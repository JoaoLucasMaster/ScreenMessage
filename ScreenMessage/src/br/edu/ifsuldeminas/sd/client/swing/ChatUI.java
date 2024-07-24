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
        frame.setSize(600, 400);

        // Top panel with user info
        JPanel topPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel nameLabel = new JLabel("Name:");
        JLabel localPortLabel = new JLabel("Local Port:");
        JLabel serverPortLabel = new JLabel("Remote Port:");

        nameField = new JTextField();
        localPortField = new JTextField();
        serverPortField = new JTextField();

        connectButton = new JButton("Connect");
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
        topPanel.add(new JLabel());
        topPanel.add(connectButton);

        messageArea = new JTextPane();
        messageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageArea);

        messageInput = new JTextField();
        messageInput.setEnabled(false);
        messageInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        sendButton = new JButton("Send");
        sendButton.setEnabled(false);
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(messageInput, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        frame.getContentPane().add(topPanel, BorderLayout.NORTH);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
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
            public void run() {
                new ChatUI();
            }
        });
    }
}
