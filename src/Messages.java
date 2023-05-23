import java.io.Serializable;

public class Messages implements Serializable {
    private String sender;
    private String receiver;
    private String messageContent;
    private boolean isPrivate;

    public Messages(String sender, String messageContent) {
        this.sender = sender;
        this.messageContent = messageContent;
        isPrivate = false;
    }

    public Messages(String sender, String receiver, String messageContent) {
        this.sender = sender;
        this.receiver = receiver;
        this.messageContent = messageContent;
        isPrivate = true;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public boolean isPrivate(){
        return isPrivate;
    }
}
