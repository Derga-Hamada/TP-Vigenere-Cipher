import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class ClientGUI extends JFrame {
    private JTextField messageField;
    private JTextField keyField; 
    private JTextArea chatArea;
    private PrintWriter out;
    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    public ClientGUI() {
        setTitle("Vigenere Client");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // لوحة علوية للمفتاح
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Clé secrète:"));
        keyField = new JTextField("MASTERKEY", 15);
        topPanel.add(keyField);
        add(topPanel, BorderLayout.NORTH);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        JButton sendButton = new JButton("Envoyer");

        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());

        setVisible(true);
        new Thread(this::connectToServer).start();
    }

    private void connectToServer() {
        appendText(">>> Connexion...\n");
        try {
            Socket socket = new Socket(HOST, PORT);
            appendText(">>> Connecté au serveur !\n");

            out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String encryptedResponse;
            while ((encryptedResponse = in.readLine()) != null) {
                appendText("\n[Serveur - Chiffré]: " + encryptedResponse + "\n");
                
                // استخدام المفتاح المدخل في الحقل
                String currentKey = keyField.getText();
                String decryptedResponse = VigenereLogic.decrypt(encryptedResponse, currentKey);
                
                appendText("[Serveur - Déchiffré]: " + decryptedResponse + "\n");
            }
        } catch (IOException e) {
            appendText("Erreur de connexion.\n");
        }
    }

    private void sendMessage() {
        String msg = messageField.getText();
        if (!msg.isEmpty() && out != null) {
            String currentKey = keyField.getText();
            
            if (currentKey.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer une clé !");
                return;
            }

            String encryptedMsg = VigenereLogic.encrypt(msg, currentKey);
            out.println(encryptedMsg);
            
            appendText("\n[Moi]: " + msg + "\n");
            messageField.setText("");
        }
    }
    
    private void appendText(String text) {
        SwingUtilities.invokeLater(() -> chatArea.append(text));
    }

    public static void main(String[] args) {
        new ClientGUI();
    }
}