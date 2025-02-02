package chat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chat.UI.ChatUI;
import chat.info.FileInfo;
import javafx.application.Platform;
import javafx.stage.FileChooser;

public class ChatClient extends UnicastRemoteObject implements IChatClient {
    private final String username;
    private final IChatServer server;
    private ChatUI ui;
    private Map<String, List<String>> unreadMessages = new HashMap<>();

    public ChatClient(String username, IChatServer server, ChatUI ui) throws RemoteException {
        super();
        this.username = username;
        this.server = server;
        this.ui = ui;
    }

    @Override
    public boolean login(String password) throws RemoteException {
        return server.login(username, password, this);
    }

    @Override
    public void logout() throws RemoteException {
        server.logout(username);
    }

    @Override
    public void sendMessage(String recipient, String message) throws RemoteException {
        server.sendMessage(username, recipient, message);
    }

    @Override
    public java.util.List<String> getUserList() throws RemoteException {
        return server.listOnlineUsers();
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public List<String> getUnreadMessages(String sender) {
        // Retorna as mensagens não lidas do remetente, ou uma lista vazia se não houver
        return unreadMessages.getOrDefault(sender, new ArrayList<>());
    }
    
    @Override
    public void clearUnreadMessages(String sender) {
        // Remove as mensagens não lidas do remetente
        unreadMessages.remove(sender);
    }
    

    @Override
    public void receiveMessage(String sender, String message) throws RemoteException {
        Platform.runLater(() -> {
            if (ui != null) {
                // Verifica se a janela de chat com o remetente está aberta
                if (!ui.isChatWindowOpen(sender)) {
                    // Adiciona a mensagem à lista de não lidas
                    unreadMessages.computeIfAbsent(sender, k -> new ArrayList<>()).add(message);
                    // Notifica o usuário sobre a nova mensagem
                    ui.showNotification(sender, message);
                } else {
                    // Exibe a mensagem diretamente na janela de chat aberta
                    ui.displayReceivedMessage(sender, message);
                }
            } else {
                System.out.println(sender + " says: " + message);
            }
        });
    }
}
