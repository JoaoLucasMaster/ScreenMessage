package br.edu.ifsuldeminas.sd.client.swing;

import br.edu.ifsuldeminas.sd.chat.ChatException;
import br.edu.ifsuldeminas.sd.chat.ChatFactory;
import br.edu.ifsuldeminas.sd.chat.MessageContainer;
import br.edu.ifsuldeminas.sd.chat.Sender;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatUI {
    private static final String KEY_TO_EXIT = "q";
    private JFrame frame;
    private JTextPane messageArea;
    private JTextField messageInput;
    private JButton sendButton;
    private Sender sender;
    private JTextField nameField;
    private JTextField localPortField;
    private JTextField serverPortField;
    private JButton connectButton;

    public ChatUI() {
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("Chat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 400);
        ImageIcon iconeEnviar = new ImageIcon(getClass().getResource("/img/enviar.png"));
        ImageIcon iconeConectar = new ImageIcon(getClass().getResource("/img/conectar.png"));
        
        // Main panel with rounded corners
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(242, 242, 242));
        mainPanel.setBorder(new RoundedBorder(10));

        // Top panel with user info - Flat design
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel("Nome:");
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        nameField = new JTextField(15);
        nameField.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JLabel localPortLabel = new JLabel("Porta Local:");
        localPortLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        localPortField = new JTextField(5);
        localPortField.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JLabel serverPortLabel = new JLabel("Porta Remota:");
        serverPortLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        serverPortField = new JTextField(5);
        serverPortField.setFont(new Font("SansSerif", Font.PLAIN, 14));

        connectButton = new JButton(" ");
        connectButton.setHorizontalTextPosition(SwingConstants.CENTER);
        connectButton.setAlignmentY(0.0f);
        connectButton.setIconTextGap(5);
        connectButton.setIcon(iconeConectar);
        connectButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        connectButton.setBackground(new Color(66, 133, 244)); // Light blue
        connectButton.setForeground(Color.WHITE);
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connect();
            }
        });

        topPanel.add(nameLabel);
        topPanel.add(nameField);
        topPanel.add(localPortLabel);
        topPanel.add(localPortField);
        topPanel.add(serverPortLabel);
        topPanel.add(serverPortField);
        topPanel.add(connectButton);

        // Message area with clean borders
        messageArea = new JTextPane();
        messageArea.setEditable(false);
        messageArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        messageArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(messageArea);

        // Message input and send button - Flat design
        messageInput = new JTextField();
        messageInput.setEnabled(false);
        messageInput.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sendButton = new JButton("");
        sendButton.setIcon(iconeEnviar);
        sendButton.setEnabled(false);
        sendButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        sendButton.setBackground(new Color(66, 133, 244));
        sendButton.setForeground(Color.WHITE);
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);

        bottomPanel.add(messageInput, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        frame.getContentPane().add(mainPanel);
        frame.setVisible(true);

        // Modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connect() {
        String from = nameField.getText();
        int localPort = Integer.parseInt(localPortField.getText());
        int serverPort = Integer.parseInt(serverPortField.getText());

        try {
            SwingMessageContainer messageContainer = new SwingMessageContainer();
            sender = ChatFactory.build("localhost", serverPort, localPort, messageContainer);
            messageContainer.setChatUI(this);

            // Disable input fields and connect button
            nameField.setEnabled(false);
            localPortField.setEnabled(false);
            serverPortField.setEnabled(false);
            connectButton.setEnabled(false);

            // Enable chat input and send button
            messageInput.setEnabled(true);
            sendButton.setEnabled(true);
        } catch (ChatException e) {
            JOptionPane.showMessageDialog(frame, "Error initializing chat: " + e.getMessage());
        }
    }

    private void sendMessage() {
        String message = messageInput.getText();
        if (message.equals(KEY_TO_EXIT)) {
            System.exit(0);
        }
        if (!message.trim().isEmpty()) {
            try {
                String formattedMessage = String.format("%s%s%s", nameField.getText(), MessageContainer.FROM, message);
                sender.send(formattedMessage);
                displayMessage(formattedMessage, Color.BLACK); // Local message in black
                messageInput.setText("");
            } catch (ChatException chatException) {
                JOptionPane.showMessageDialog(frame, "Error sending message: " + chatException.getMessage());
            }
        }
    }

    public void displayMessage(String message, Color color) {
        try {
            StyledDocument doc = messageArea.getStyledDocument();
            Style style = messageArea.addStyle("Style", null);
            StyleConstants.setForeground(style, color);
            doc.insertString(doc.getLength(), message + "\n", style);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run(){
                new ChatUI();
            }
        });
    }
}
