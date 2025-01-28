package chat.UI;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

import chat.*;

public class ChatUI {
    private Stage primaryStage;
    private IChatServer server; // Referência ao servidor
    private Registry registry;      // Registro RMI
    private String username;        // Nome do usuário logado (opcional, útil para exibições)
    private IChatClient client;

    public ChatUI(Stage primaryStage) {
        this.primaryStage = primaryStage;

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
    
    // Tela de Início: Exibe opções de Login ou Registro
    public void showStartScreen() {
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

                client = new ChatClient(username, server);

                if (client.login(password)) {
                    statusLabel.setText("Login successful!");
                    this.username = username;
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

            userListLayout.getChildren().addAll(
                title,
                loggedInUserLabel,
                allUsersLabel, allUsersView,
                onlineUsersLabel, onlineUsersView,
                backButton
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
        VBox chatLayout = new VBox(10);
        chatLayout.setPadding(new Insets(10));
    
        Label welcomeLabel = new Label("Welcome to WhatsUT!");
        Button userListButton = new Button("Ver Lista de Usuários");
        Button backButton = new Button("Sair");
    
        userListButton.setOnAction(e -> showUserList());
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
    
        chatLayout.getChildren().addAll(welcomeLabel, userListButton, backButton);
    
        Scene chatScene = new Scene(chatLayout, 400, 300);
        primaryStage.setScene(chatScene);
        primaryStage.show();
    }
    
}
