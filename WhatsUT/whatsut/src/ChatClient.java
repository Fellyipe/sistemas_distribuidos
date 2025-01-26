import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ChatClient extends UnicastRemoteObject implements IChatClient {
    private final String username;

    public ChatClient(String username) throws RemoteException {
        super();
        this.username = username;
    }

    @Override
    public void receiveMessage(String sender, String message) throws RemoteException {
        System.out.println(sender + " says: " + message);
    }
}
