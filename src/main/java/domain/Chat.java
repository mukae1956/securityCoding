package domain;

public class Chat {
    private String encryptedMessage;
    private String senderId;
    private String receiverId;

    public Chat() {}

    public Chat(String encryptedMessage, String senderId, String receiverId) {
        this.encryptedMessage = encryptedMessage;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public String getEncryptedMessage() { return encryptedMessage; }
    public void setEncryptedMessage(String encryptedMessage) {
        this.encryptedMessage = encryptedMessage;
    }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }

}