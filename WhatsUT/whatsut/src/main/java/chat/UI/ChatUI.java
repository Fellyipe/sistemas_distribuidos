package chat.UI;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import chat.*;
import chat.info.*;

public class ChatUI {
    private Stage primaryStage;
    private IChatServer server; // Referência ao servidor
    private Registry registry;      // Registro RMI
    private String username;        // Nome do usuário logado (opcional, útil para exibições)
    private IChatClient client;
    private Map<String, TextArea> privateChatWindows = new HashMap<>();
    private Set<String> openChats = new HashSet<>();
    private TextArea chatArea; // Agora é um atributo da classe
    private VBox chatMessages; // Usaremos VBox para suportar botões
    private String currentWindow = null;
    private String currentRecipitent = null;
    private String currentGroup = null;


    public ChatUI(Stage primaryStage) {
        this.primaryStage = primaryStage;
        chatMessages = new VBox(10);
        try {
            // Inicializa o registro e o servidor RMI
            this.registry = LocateRegistry.getRegistry("localhost", 1099);
            this.server = (IChatServer) registry.lookup("ChatService");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erro ao conectar ao servidor. Verifique se o servidor está em execução.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }

    public IChatClient getClient() {
        return client;
    }

    public boolean isChatWindowOpen(String sender) {
        return openChats.contains(sender);
    }
    
    public void markChatAsOpen(String sender) {
        openChats.add(sender);
    }
    
    public void markChatAsClosed(String sender) {
        openChats.remove(sender);
    }
    
    // Tela de Início: Exibe opções de Login ou Registro
    public void showStartScreen() {
        currentWindow = "Start";
        VBox startLayout = new VBox(10);
        startLayout.setPadding(new Insets(10));

        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");

        startLayout.getChildren().addAll(loginButton, registerButton);

        // Ação do botão de Login
        loginButton.setOnAction(e -> showLoginScreen());

        // Ação do botão de Registro
        registerButton.setOnAction(e -> showRegisterScreen());

        Scene startScene = new Scene(startLayout, 300, 200);
        primaryStage.setScene(startScene);
        primaryStage.show();
    }

    // Tela de Login
    public void showLoginScreen() {
        currentWindow = "Login";
        
        VBox loginLayout = new VBox(10);
        loginLayout.setPadding(new Insets(10));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        Button backButton = new Button("Back");
        Label statusLabel = new Label();

        loginLayout.getChildren().addAll(new Label("Enter your credentials:"), usernameField, passwordField, loginButton, backButton, statusLabel);

        loginButton.setOnAction(e -> {
            try {
                String username = usernameField.getText();
                String password = passwordField.getText();

                client = new ChatClient(username, server, this);

                if (client.login(password)) {
                    statusLabel.setText("Login successful!");
                    this.username = username;
                    server.registerClient(username, client); 
                    showChatWindow();  // Exibe a janela de chat após login bem-sucedido
                } else {
                    statusLabel.setText("Invalid credentials. Try again.");
                    
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                statusLabel.setText("Error connecting to server.");
            }
        });

        backButton.setOnAction(e -> showStartScreen());

        // Configuração da cena de login
        Scene loginScene = new Scene(loginLayout, 300, 200);
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    // Tela de Registro
    public void showRegisterScreen() {
        currentWindow = "Register";
        
        VBox registerLayout = new VBox(10);
        registerLayout.setPadding(new Insets(10));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        Button registerButton = new Button("Register");
        Button backButton = new Button("Back");
        Label statusLabel = new Label();

        registerLayout.getChildren().addAll(new Label("Enter your details:"), usernameField, passwordField, emailField, registerButton, backButton, statusLabel);

        registerButton.setOnAction(e -> {
            try {
                String username = usernameField.getText();
                String password = passwordField.getText();
                String email = emailField.getText();

                Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                IChatServer server = (IChatServer) registry.lookup("ChatService");

                boolean success = server.registerUser(username, password, email);

                if (success) {
                    statusLabel.setText("User registered successfully. You can now log in.");
                } else {
                    statusLabel.setText("Username already exists. Please try again.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                statusLabel.setText("Error connecting to server.");
            }
        });

        backButton.setOnAction(e -> showStartScreen());

        // Configuração da cena de registro
        Scene registerScene = new Scene(registerLayout, 300, 250);
        primaryStage.setScene(registerScene);
        primaryStage.show();
    }

    // Tela de Lista de Usuários
    public void showUserList() {
        currentWindow = "User List";
        
        try {
            // Obtém as listas do servidor
            List<String> allUsers = server.listUsers();
            List<String> onlineUsers = server.listOnlineUsers();
            
            VBox userListLayout = new VBox(10);
            userListLayout.setPadding(new Insets(10));
            
            Label title = new Label("Lista de Usuários:");
            title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            
            Label loggedInUserLabel = new Label("Você está logado como: " + username);
            loggedInUserLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            
            // Exibe a lista de usuários cadastrados
            Label allUsersLabel = new Label("Usuários cadastrados:");
            ListView<String> allUsersView = new ListView<>(FXCollections.observableArrayList(allUsers));
            
            // Exibe a lista de usuários online
            Label onlineUsersLabel = new Label("Usuários online:");
            ListView<String> onlineUsersView = new ListView<>(FXCollections.observableArrayList(onlineUsers));
            
            Button backButton = new Button("Voltar");
            backButton.setOnAction(e -> showChatWindow());
            
            // Ação para iniciar o chat privado
            Button privateChatButton = new Button("Iniciar Chat Privado");
            privateChatButton.setOnAction(e -> {
                String selectedUser = onlineUsersView.getSelectionModel().getSelectedItem();
                if (selectedUser != null && !selectedUser.equals(username)) {
                    showPrivateChatWindow(selectedUser); // Abre a janela de chat privado
                } else {
                    showError("Selecione um usuário válido.");
                }
            });

            userListLayout.getChildren().addAll(
                title,
                loggedInUserLabel,
                allUsersLabel, allUsersView,
                onlineUsersLabel, onlineUsersView,
                privateChatButton, backButton
            );

            

            Scene userListScene = new Scene(userListLayout, 400, 500);
            primaryStage.setScene(userListScene);
            primaryStage.show();
        } catch (RemoteException e) {
            showError("Erro ao obter a lista de usuários.");
        }
    }

    // Janela do Chat (após o login)
    public void showChatWindow() {
        currentWindow = "Main";
        
        VBox chatLayout = new VBox(10);
        chatLayout.setPadding(new Insets(10));
    
        Label welcomeLabel = new Label("Welcome to WhatsUT!");
        Button userListButton = new Button("Ver Lista de Usuários");
        Button groupListButton = new Button("Ver Lista de Grupos");
        Button backButton = new Button("Sair");
    
        userListButton.setOnAction(e -> showUserList());
        groupListButton.setOnAction(e -> showGroupList());
        backButton.setOnAction(e -> {
            try {
                if (client != null) {
                    client.logout(); // Realiza logout
                }
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
            showStartScreen(); // Volta para a tela inicial
        });
    
        chatLayout.getChildren().addAll(welcomeLabel, userListButton, groupListButton, backButton);
    
        Scene chatScene = new Scene(chatLayout, 400, 300);
        primaryStage.setScene(chatScene);
        primaryStage.show();
    }

    public void showPrivateChatWindow(String recipient) {
        currentWindow = "Private Chat";
        currentRecipitent = recipient;
        
        VBox chatLayout = new VBox(10);
        chatLayout.setPadding(new Insets(10));
    
        Label title = new Label("Chat com " + recipient);
        chatMessages = new VBox(5);
        chatMessages.setPrefHeight(300);
        
        chatArea = new TextArea(); // Usa o atributo da classe
        chatArea.setEditable(false);
    
        chatMessages = new VBox(10); // Suportará mensagens e botões de download
        ScrollPane chatScroll = new ScrollPane(chatMessages);
        chatScroll.setFitToWidth(true);
    
        try {
            List<MessageInfo> history = server.getMessageHistory(username, recipient);
            for (MessageInfo msg : history) {
                if (msg.isFile()) {
                    addFileToChat(msg.getSender(), msg.getRecipient(), msg.getFile().getFileName());
                } else {
                    chatMessages.getChildren().add(new Label(msg.toString()));
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    
        TextField messageField = new TextField();
        Button sendButton = new Button("Enviar");
        Button sendFileButton = new Button("📎 Enviar Arquivo");
        Button backButton = new Button("Voltar");
    
        sendButton.setOnAction(e -> {
            String message = messageField.getText();
            if (!message.isEmpty()) {
                try {
                    server.sendMessage(username, recipient, message);
                    // addMessageToChat("📩 Você: " + message);
                    updateChat(recipient, false); // ADICIONEI ISSO
                    messageField.clear();
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });
    
        sendFileButton.setOnAction(e -> sendFileToRecipient(recipient));
        backButton.setOnAction(e -> showUserList());
    
        VBox inputLayout = new VBox(10, messageField, sendButton, sendFileButton, backButton);
        chatLayout.getChildren().addAll(title, chatScroll, inputLayout);
    
        Scene chatScene = new Scene(chatLayout, 400, 500);
        primaryStage.setScene(chatScene);
        primaryStage.show();
    }
    
    
    // Exibe mensagens recebidas na janela apropriada
    public void displayReceivedMessage(String sender, String message) {
        TextArea chatArea = privateChatWindows.get(sender);

        if (chatArea != null) {
            // Atualiza o TextArea da janela de chat com o remetente
            chatArea.appendText(sender + ": " + message + "\n");
        } else {
            // Opcional: Notificação ou log caso não haja janela aberta
            System.out.println("Nova mensagem de " + sender + ": " + message);
        }
    }
    
    public void showGroupList() {
        currentWindow = "Group List";
        
        VBox groupListLayout = new VBox(10);
        groupListLayout.setPadding(new Insets(10));
    
        Label titleLabel = new Label("Lista de Grupos Disponíveis:");
        ListView<String> groupListView = new ListView<>();
        Button joinGroupButton = new Button("Solicitar entrada");
        joinGroupButton.setDisable(true); // Desativado inicialmente
        Button enterGroupButton = new Button("Entrar no Grupo");
        enterGroupButton.setDisable(true); // Desativado inicialmente
        Button createGroupButton = new Button("Criar Grupo");
        Button backButton = new Button("Voltar");
    
        try {
            List<String> groups = server.listGroups();
            groupListView.getItems().addAll(groups);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    
        // Habilitar botão de entrar se o usuário for membro do grupo
        groupListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                try {
                    GroupInfo group = server.getGroupInfo(newValue);
                    joinGroupButton.setDisable(group == null || (group.isMember(username)));
                    enterGroupButton.setDisable(group == null || !(group.isMember(username)));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    
        joinGroupButton.setOnAction(e -> {
            String selectedGroup = groupListView.getSelectionModel().getSelectedItem();
            if (selectedGroup != null) {
                try {
                    if (server.requestJoinGroup(selectedGroup, username)) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Solicitação enviada.", ButtonType.OK);
                        alert.showAndWait();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Não foi possível solicitar entrada.", ButtonType.OK);
                        alert.showAndWait();
                    }
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });
    
        enterGroupButton.setOnAction(e -> {
            String selectedGroup = groupListView.getSelectionModel().getSelectedItem();
            if (selectedGroup != null) {
                showGroupChatWindow(selectedGroup);
            }
        });

        createGroupButton.setOnAction(e -> showCreateGroupScreen());
    
        backButton.setOnAction(e -> showChatWindow());
    
        groupListLayout.getChildren().addAll(titleLabel, groupListView, joinGroupButton, enterGroupButton, createGroupButton, backButton);
    
        Scene groupListScene = new Scene(groupListLayout, 400, 300);
        primaryStage.setScene(groupListScene);
        primaryStage.show();
    }
    
    public void showCreateGroupScreen() {
        currentWindow = "Create Group";

        VBox createGroupLayout = new VBox(10);
        createGroupLayout.setPadding(new Insets(10));
        
        Label titleLabel = new Label("Criar um Novo Grupo");
        TextField groupNameField = new TextField();
        groupNameField.setPromptText("Nome do Grupo");
        
        TextField groupDescriptionField = new TextField();
        groupDescriptionField.setPromptText("Descrição do Grupo");
        
        Button createButton = new Button("Criar");
        Button backButton = new Button("Voltar");
        
        Label statusLabel = new Label();
        
        createButton.setOnAction(e -> {
            String groupName = groupNameField.getText().trim();
            String groupDescription = groupDescriptionField.getText().trim();
            
            if (groupName.isEmpty() || groupDescription.isEmpty()) {
                statusLabel.setText("Por favor, preencha todos os campos.");
            } else {
                try {
                    boolean success = server.createGroup(groupName, groupDescription, username);
                    if (success) {
                        statusLabel.setText("Grupo criado com sucesso!");
                    } else {
                        statusLabel.setText("Não foi possível criar o grupo. Nome já em uso?");
                    }
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                    statusLabel.setText("Erro ao criar o grupo.");
                }
            }
        });
        
        backButton.setOnAction(e -> showChatWindow());
        
        createGroupLayout.getChildren().addAll(titleLabel, groupNameField, groupDescriptionField, createButton, backButton, statusLabel);
        
        Scene createGroupScene = new Scene(createGroupLayout, 400, 300);
        primaryStage.setScene(createGroupScene);
        primaryStage.show();
    }
    
    public void showGroupChatWindow(String groupName) {
        currentWindow = "Group Chat";
        currentGroup = groupName;
        
        VBox groupChatLayout = new VBox(10);
        groupChatLayout.setPadding(new Insets(10));
    
        Label titleLabel = new Label("Grupo: " + groupName);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
    
        chatMessages = new VBox(10); // ADICIONEI ISSO
        chatMessages.setPrefHeight(300); // ADICIONEI ISSO
        chatArea = new TextArea(); // ADICIONEI ISSO
        chatArea.setEditable(false); // ADICIONEI ISSO
        ScrollPane chatScroll = new ScrollPane(chatMessages); // ADICIONEI ISSO
        chatScroll.setFitToWidth(true); // ADICIONEI ISSO

        ListView<String> messageListView = new ListView<>();
        TextField messageField = new TextField();
        messageField.setPromptText("Digite sua mensagem...");
    
        Button sendMessageButton = new Button("Enviar");
        Button leaveGroupButton = new Button("Sair do Grupo");
        Button manageRequestsButton = new Button("Gerenciar Solicitações");
        manageRequestsButton.setVisible(false);
        Button manageMembersButton = new Button("Gerenciar Membros");
        manageMembersButton.setVisible(false);
        Button backButton = new Button("Voltar");
    
    
        try {
            List<String> messages = server.getGroupMessages(groupName);
            for (String message : messages) { //ADICIONEI ISSO
                chatMessages.getChildren().add(new Label(message)); // ADICIONEI ISSO
            }
        
            // messageListView.getItems().addAll(messages);
            
            // Verifica se o usuário é dono do grupo para mostrar botão de gerenciar
            GroupInfo group = server.getGroupInfo(groupName);
            if (group != null && group.getOwner().equals(username)) {
                manageRequestsButton.setVisible(true);
                manageMembersButton.setVisible(true);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    
        sendMessageButton.setOnAction(e -> {
            String message = messageField.getText().trim();
            if (!message.isEmpty()) {
                try {
                    server.sendGroupMessage(groupName, username, message);
                    // messageListView.getItems().add(username + ": " + message);
                    updateChat(groupName, true); // ADICIONEI ISSO
                    messageField.clear();
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });
    
        leaveGroupButton.setOnAction(e -> {
            
            try {   
                GroupInfo group = server.getGroupInfo(groupName);
                Alert alert = new Alert(null);

                if(group.getOwner().equals(username) && group.getMembers().size() == 1) {
                    alert = new Alert(Alert.AlertType.INFORMATION, "Tem certeza que quer sair do grupo?\nO grupo será excluído após sua saída!", ButtonType.OK, ButtonType.CANCEL);    
                } else if (group.getOwner().equals(username)) {
                    alert = new Alert(Alert.AlertType.INFORMATION, "Tem certeza que quer sair do grupo?\nUm novo dono será escolhido para o grupo.", ButtonType.OK, ButtonType.CANCEL);
                } else {
                    alert = new Alert(Alert.AlertType.INFORMATION, "Tem certeza que quer sair do grupo?", ButtonType.OK, ButtonType.CANCEL);
                }

                Optional<ButtonType> result = alert.showAndWait();

                if (result.isPresent() && result.get() == ButtonType.OK) {
                    boolean success;
                    if (group != null && group.getOwner().equals(username)) {
                        handleGroupOwnerExit(groupName);
                        success = true;
                    } else {
                        success = server.leaveGroup(groupName, username);
                    }
                    if (success) {
                        showGroupList();
                    } else {
                        System.out.println("Erro ao sair do grupo.");
                    }
                }
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        });
    
        manageRequestsButton.setOnAction(e -> showGroupRequests(groupName)); // Chama a tela de gerenciamento
    
        manageMembersButton.setOnAction(e -> showManageMembersScreen(groupName));

        backButton.setOnAction(e -> showGroupList());
    
        VBox inputLayout = new VBox(10, messageField, sendMessageButton, leaveGroupButton, manageRequestsButton, manageMembersButton, backButton); // ADICIONEI ISSO AQUI
        groupChatLayout.getChildren().addAll(titleLabel, chatScroll, inputLayout); // MUDEI ISSO AQUI
    
        Scene groupChatScene = new Scene(groupChatLayout, 500, 400);
        primaryStage.setScene(groupChatScene);
        primaryStage.show();
    }
    
    public void showGroupRequests(String groupName) {
        currentWindow = "Group Requests";
        currentGroup = groupName;

        VBox requestLayout = new VBox(10);
        requestLayout.setPadding(new Insets(10));
    
        Label titleLabel = new Label("Solicitações Pendentes - " + groupName);
        ListView<String> requestListView = new ListView<>();
        Button approveButton = new Button("Aprovar");
        approveButton.setDisable(true); // Desativado inicialmente
        Button rejectButton = new Button("Rejeitar");
        rejectButton.setDisable(true); // Desativado inicialmente
        Button backButton = new Button("Voltar");
    
        try {
            GroupInfo group = server.getGroupInfo(groupName);
            if (group != null && group.getOwner().equals(username)) {
                requestListView.getItems().addAll(server.getPendingRequests(groupName));
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Você não é o dono deste grupo.", ButtonType.OK);
                alert.showAndWait();
                return;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        requestListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                approveButton.setDisable(false);
                rejectButton.setDisable(false);
            }
        });
    
        approveButton.setOnAction(e -> {
            String selectedUser = requestListView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                try {
                    if (server.approveJoinRequest(groupName, username, selectedUser, true)) {
                        requestListView.getItems().remove(selectedUser);
                    }
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });
    
        rejectButton.setOnAction(e -> {
            String selectedUser = requestListView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                try {
                    if (server.approveJoinRequest(groupName, username, selectedUser, false)) {
                        requestListView.getItems().remove(selectedUser);
                    }
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });
    
        backButton.setOnAction(e -> showGroupChatWindow(groupName));
    
        requestLayout.getChildren().addAll(titleLabel, requestListView, approveButton, rejectButton, backButton);
    
        Scene requestScene = new Scene(requestLayout, 400, 300);
        primaryStage.setScene(requestScene);
        primaryStage.show();
    }
    
    public void showManageMembersScreen(String groupName) {
        currentWindow = "Group Chat";
        currentGroup = groupName;

        VBox manageMembersLayout = new VBox(10);
        manageMembersLayout.setPadding(new Insets(10));
    
        Label titleLabel = new Label("Gerenciar Membros - " + groupName);
        ListView<String> memberListView = new ListView<>();
        Button removeMemberButton = new Button("Expulsar Membro");
        removeMemberButton.setDisable(true);
        Button backButton = new Button("Voltar");
    
        try {
            GroupInfo group = server.getGroupInfo(groupName);
            if (group != null && group.getOwner().equals(username)) {
                memberListView.getItems().addAll(group.getMembers());
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Você não é o dono deste grupo.", ButtonType.OK);
                alert.showAndWait();
                return;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    
        memberListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                removeMemberButton.setDisable(false);
            }
        });
    
        removeMemberButton.setOnAction(e -> {
            String selectedUser = memberListView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                try {
                    if (server.removeUserFromGroup(groupName, selectedUser)) {
                        memberListView.getItems().remove(selectedUser);
                    }
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });
    
        backButton.setOnAction(e -> showGroupChatWindow(groupName));
    
        manageMembersLayout.getChildren().addAll(titleLabel, memberListView, removeMemberButton, backButton);
    
        Scene manageMembersScene = new Scene(manageMembersLayout, 400, 300);
        primaryStage.setScene(manageMembersScene);
        primaryStage.show();
    }
    
    private void handleGroupOwnerExit(String groupName) {
        GroupInfo group = null;
        try {
            group = server.getGroupInfo(groupName);
            server.removeUserFromGroup(groupName, group.getOwner());
            group = server.getGroupInfo(groupName);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    
        if (group == null) return; // Se o grupo não existir, sai da função

        List<String> members = new ArrayList<>(group.getMembers()); // Obtém a lista de membros
        System.out.println(group.getMembers());

        if (members.size() > 0) { 
            // Se ainda houver membros, escolhemos o primeiro como novo dono
            String newOwner = members.get(0);
            group.setOwner(newOwner);
            try {
                if(server.changeGroupOwner(groupName, newOwner)) {
                    System.out.println("Novo dono do grupoooooo " + groupName + ": " + newOwner);
                }
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
            System.out.println("Novo dono do grupo " + groupName + ": " + newOwner);
            System.out.println(group.getOwner());
        } else { 
            // Se não houver mais membros, excluímos o grupo
            try {
                server.deleteGroup(groupName);
                System.out.println("Grupo " + groupName + " foi removido por não ter mais membros.");
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void sendFileToRecipient(String recipient) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Todos os Arquivos", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
    
        if (selectedFile != null) {
            try {
                byte[] fileData = Files.readAllBytes(selectedFile.toPath());
                String fileName = selectedFile.getName();
    
                FileInfo fileInfo = new FileInfo(fileName, fileData);
                server.sendFile(username, recipient, fileInfo);
    
                addFileToChat(username, recipient, fileName);
    
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    

    // public void receiveFileFromSender(String sender) {
    //     try {
    //         // Solicita o arquivo do servidor
    //         FileInfo fileInfo = server.receiveFile(sender, username);
    
    //         if (fileInfo != null) {
    //             // Abre um diálogo para o usuário escolher onde salvar o arquivo
    //             FileChooser fileChooser = new FileChooser();
    //             fileChooser.setInitialFileName(fileInfo.getFileName());
    
    //             File savedFile = fileChooser.showSaveDialog(primaryStage);
    //             if (savedFile != null) {
    //                 // Salva os dados no local escolhido
    //                 Files.write(savedFile.toPath(), fileInfo.getFileData());
    //                 System.out.println("Você recebeu um arquivo: " + fileInfo.getFileName() + "\n");
    //             }
    //         } else {
    //             System.out.println("Nenhum arquivo recebido.\n");
    //         }
    
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }

    public void receiveFileFromSender(String sender, String fileName) {
        try {
            FileInfo fileInfo = server.receiveFile(sender, username, fileName);
    
            if (fileInfo != null) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialFileName(fileInfo.getFileName());
    
                File savedFile = fileChooser.showSaveDialog(primaryStage);
                if (savedFile != null) {
                    Files.write(savedFile.toPath(), fileInfo.getFileData());
                    addMessageToChat("📥 Arquivo " + fileInfo.getFileName() + " baixado com sucesso!");
                }
            } else {
                addMessageToChat("❌ Nenhum arquivo disponível para baixar.");
            }
    
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    private void addMessageToChat(String message) {
        Label messageLabel = new Label(message);
        chatMessages.getChildren().add(messageLabel); // Agora adiciona corretamente
    }
    
    private void addFileToChat(String sender, String recipient, String fileName) {
        HBox fileBox = new HBox(10);
        Label fileLabel = new Label();
        fileLabel.setText((sender.equals(username)) ? "Você enviou: " + fileName : sender + " ennviou: " + fileName);


        Button downloadButton = new Button("Baixar");
        System.out.println("Addfile - sender: " + sender + " - recipient: " + recipient);

        downloadButton.setOnAction(e -> {
            try {
                FileInfo file = server.receiveFile(sender, recipient, fileName);
                if (file != null) {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setInitialFileName(file.getFileName());
                    File saveFile = fileChooser.showSaveDialog(primaryStage);
    
                    if (saveFile != null) {
                        Files.write(saveFile.toPath(), file.getFileData());
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Arquivo salvo com sucesso!", ButtonType.OK);
                        alert.showAndWait();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Arquivo não encontrado.", ButtonType.OK);
                    alert.showAndWait();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    
        fileBox.getChildren().addAll(fileLabel, downloadButton);
        chatMessages.getChildren().add(fileBox);
    }
    
    
    private void saveFileLocally(FileInfo file) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(file.getFileName());
        File selectedFile = fileChooser.showSaveDialog(primaryStage);
        
        if (selectedFile != null) {
            try (FileOutputStream fos = new FileOutputStream(selectedFile)) {
                fos.write(file.getFileData());
                System.out.println("📂 Arquivo salvo: " + selectedFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void updateChat(String recipientOrGroup, boolean isGroup) {
        Platform.runLater(() -> {
            
            try {    
                List<String> messages;
                if (isGroup && currentWindow.equals("Group Chat") && recipientOrGroup.equals(currentGroup)) {
                    chatMessages.getChildren().clear(); // Limpa o chat atual
                    messages = server.getGroupMessages(recipientOrGroup);
                    for (String message : messages) { //ADICIONEI ISSO
                        chatMessages.getChildren().add(new Label(message)); // ADICIONEI ISSO
                    }
                } else if (currentWindow.equals("Private Chat") && recipientOrGroup.equals(currentRecipitent)) {
                    chatMessages.getChildren().clear(); // Limpa o chat atual
                    List<MessageInfo> history = server.getMessageHistory(username, recipientOrGroup);
                    for (MessageInfo msg : history) {
                        if (msg.isFile()) {
                            addFileToChat(msg.getSender(), msg.getRecipient(), msg.getFile().getFileName());
                        } else {
                            chatMessages.getChildren().add(new Label(msg.toString()));
                        }
                    }
                }

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    public void showNotification(String msg, String content, String groupName) {
        

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
            alert.showAndWait();
            if (content.equals("joinapproval") && currentWindow.equals("Group List")) {
                showGroupList();
            }
            if (content.equals("groupremoval") && currentWindow.equals("Group Chat") && currentGroup.equals(groupName)) {
                showGroupList();
            }
            if (content.equals("newowne") && currentWindow.equals("Group Chat") && currentGroup.equals(groupName)) {
                showGroupChatWindow(groupName);
            }
        });
    }


}
