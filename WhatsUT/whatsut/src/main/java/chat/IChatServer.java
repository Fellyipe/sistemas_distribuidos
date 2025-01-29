package chat;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import chat.info.GroupInfo;
import chat.info.MessageInfo;

public interface IChatServer extends Remote {
    public boolean registerUser(String username, String password, String email) throws RemoteException;
    boolean login(String username, String password, IChatClient client) throws RemoteException;
    void logout(String username) throws RemoteException;
    List<String> listUsers() throws RemoteException; // Retorna todos os usuários
    List<String> listOnlineUsers() throws RemoteException; // Retorna apenas os usuários online
    void sendMessage(String sender, String receiver, String message) throws RemoteException;
    public List<MessageInfo> getMessageHistory(String user1, String user2) throws RemoteException;
    public boolean createGroup(String groupName, String description, String owner) throws RemoteException;
    public boolean requestJoinGroup(String groupName, String username) throws RemoteException;
    public boolean approveJoinRequest(String groupName, String owner, String username, boolean approve) throws RemoteException;
    public List<GroupInfo> listGroups() throws RemoteException;
}
