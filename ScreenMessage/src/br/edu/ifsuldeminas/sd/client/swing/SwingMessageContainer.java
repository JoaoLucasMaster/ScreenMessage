package br.edu.ifsuldeminas.sd.client.swing;

import br.edu.ifsuldeminas.sd.chat.MessageContainer;

public class SwingMessageContainer implements MessageContainer {
    private ChatUI chatUI;

    public void setChatUI(ChatUI chatUI) {
        this.chatUI = chatUI;
    }

    @Override
    public void newMessage(String message) {
        if (message == null || message.equals("")) {
            return;
        }
        chatUI.displayMessage(message);
    }
}
