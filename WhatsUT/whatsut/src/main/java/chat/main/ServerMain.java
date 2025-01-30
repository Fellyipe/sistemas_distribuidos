package chat.main;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import chat.ChatServer;

public class ServerMain {
    public static void main(String[] args) {
        try {
            ChatServer server = new ChatServer();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("ChatService", server);

            System.out.println("Server is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
