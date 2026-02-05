import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class ServerGUI extends JFrame {
    private JTextField messageField;
    private JTextField keyField;
    private JTextArea chatArea;
    private PrintWriter out;
    private static final int PORT = 5000;

    public ServerGUI() {
        setTitle("Vigenere Server");
        setSize(500, 450); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

       
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
        new Thread(this::startServer).start();
    }

    private void startServer() {
        appendText(">>> Serveur prêt...\n");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            Socket socket = serverSocket.accept();
            appendText(">>> Client connecté !\n");

            out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String encryptedMsg;
            while ((encryptedMsg = in.readLine()) != null) {
                appendText("\n[Client - Chiffré]: " + encryptedMsg + "\n");
                
                
                String currentKey = keyField.getText();
                String decryptedMsg = VigenereLogic.decrypt(encryptedMsg, currentKey);
                
                appendText("[Client - Déchiffré]: " + decryptedMsg + "\n");
            }
        } catch (IOException e) {
            appendText("Erreur: " + e.getMessage());
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
        SwingUtilities.invokeLater(() -> {
            chatArea.append(text);
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        new ServerGUI();
    }
}