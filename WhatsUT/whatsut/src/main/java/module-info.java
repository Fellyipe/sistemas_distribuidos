module chat {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;

    opens chat to javafx.fxml;
    exports chat;
    exports chat.main;
    exports chat.info;
    exports chat.UI;
    exports chat.utils;
}
