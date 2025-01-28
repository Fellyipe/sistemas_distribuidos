package chat.UI;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import chat.*;

public class ChatUI {
    private Stage primaryStage;

    public ChatUI(Stage primaryStage) {
        this.primaryStage = primaryStage;
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

                Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                IChatServer server = (IChatServer) registry.lookup("ChatService");

                IChatClient client = new ChatClient(username, server);

                if (client.login(password)) {
                    statusLabel.setText("Login successful!");
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

    // Janela do Chat (após o login)
    public void showChatWindow() {
        VBox chatLayout = new VBox(10);
        chatLayout.setPadding(new Insets(10));

        Label welcomeLabel = new Label("Welcome to WhatsUT!");
        Button backButton = new Button("Back");
        chatLayout.getChildren().addAll(welcomeLabel, backButton);

        backButton.setOnAction(e -> showStartScreen());

        Scene chatScene = new Scene(chatLayout, 400, 300);
        primaryStage.setScene(chatScene);
        primaryStage.show();
    }
}
