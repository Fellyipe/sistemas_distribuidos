package chat.main;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import chat.ChatClient;
import chat.IChatServer;
import chat.UI.*;

public class ChatApp extends Application {
    private ChatUI chatUI; // Classe para gerenciar a interface

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        chatUI = new ChatUI(primaryStage); // Inicializa a interface
        chatUI.showStartScreen(); // Exibe a tela inicial
    }
}