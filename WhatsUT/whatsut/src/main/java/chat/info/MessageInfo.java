package chat.info;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String sender;
    private String message;
    private long timestamp;

    public MessageInfo(String sender, String message) {
        this.sender = sender;
        this.message = message;
        this.timestamp = System.currentTimeMillis(); // Armazena a hora da mensagem
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String horaFormatada = sdf.format(new Date(timestamp));
        return sender + ": " + message + " [" + horaFormatada + "]";
    }
}

