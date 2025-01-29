package chat.info;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class GroupInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name; // Nome do grupo
    private String description; // Descrição do grupo
    private String owner; // Criador do grupo
    private Set<String> members; // Membros do grupo
    private Set<String> pendingRequests; // Solicitações pendentes

    public GroupInfo(String name, String description, String owner) {
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.members = new HashSet<>();
        this.pendingRequests = new HashSet<>();
        this.members.add(owner); // O criador do grupo é automaticamente membro
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getOwner() {
        return owner;
    }

    public Set<String> getMembers() {
        return members;
    }

    public Set<String> getPendingRequests() {
        return pendingRequests;
    }

    public boolean addMember(String username) {
        return members.add(username);
    }

    public boolean removeMember(String username) {
        return members.remove(username);
    }

    public void addPendingRequest(String username) {
        pendingRequests.add(username);
    }

    public void removePendingRequest(String username) {
        pendingRequests.remove(username);
    }

    @Override
    public String toString() {
        return "Group{name='" + name + "', owner='" + owner + "', members=" + members.size() + "}";
    }
}
