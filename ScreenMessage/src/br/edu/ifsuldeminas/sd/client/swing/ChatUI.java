package br.edu.ifsuldeminas.sd.client.swing;

import br.edu.ifsuldeminas.sd.chat.ChatException;
import br.edu.ifsuldeminas.sd.chat.ChatFactory;
import br.edu.ifsuldeminas.sd.chat.MessageContainer;
import br.edu.ifsuldeminas.sd.chat.Sender;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatUI {
    private static final String KEY_TO_EXIT = "q";
    private JFrame frame;
    private JTextArea messageArea;
    private JTextField messageInput;
    private JButton sendButton;
    private Sender sender;
    private String from;

    public ChatUI(Sender sender, String from) {
        this.sender = sender;
        this.from = from;
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("Chat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageArea);

        messageInput = new JTextField();
        messageInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(messageInput, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);

        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(panel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private void sendMessage() {
        String message = messageInput.getText();
        if (message.equals(KEY_TO_EXIT)) {
            System.exit(0);
        }
        if (!message.trim().isEmpty()) {
            try {
                message = String.format("%s%s%s", from, MessageContainer.FROM, message);
                sender.send(message);
                displayMessage(message);  // Exibe a mensagem no terminal local
                messageInput.setText("");
            } catch (ChatException chatException) {
                JOptionPane.showMessageDialog(frame, "Error sending message: " + chatException.getMessage());
            }
        }
    }

    public void displayMessage(String message) {
        messageArea.append(message + "\n");
    }

    public static void main(String[] args) {
        String serverName = "localhost";
        int localPort = Integer.parseInt(JOptionPane.showInputDialog("Enter local port:"));
        int serverPort = Integer.parseInt(JOptionPane.showInputDialog("Enter server port:"));
        String from = JOptionPane.showInputDialog("Enter your name:");

        try {
            SwingMessageContainer messageContainer = new SwingMessageContainer();
            Sender sender = ChatFactory.build(serverName, serverPort, localPort, messageContainer);
            ChatUI chatUI = new ChatUI(sender, from);
            messageContainer.setChatUI(chatUI);
        } catch (ChatException e) {
            JOptionPane.showMessageDialog(null, "Error initializing chat: " + e.getMessage());
            System.exit(1);
        }
    }
}
