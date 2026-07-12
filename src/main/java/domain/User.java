package domain;

public class User {
    private String userId;
    private String password;
    private String userName;

    // 로그인용 키
    private String publicKey;
    private String encryptedPrivateKey;

    // 채팅용 키 추가
    private String chatPublicKey;
    private String encryptedChatPrivateKey;

    public User() {}

    public User(String userId, String password, String userName,
                String publicKey, String encryptedPrivateKey,
                String chatPublicKey, String encryptedChatPrivateKey) {

        this.userId = userId;
        this.password = password;
        this.userName = userName;
        this.publicKey = publicKey;
        this.encryptedPrivateKey = encryptedPrivateKey;
        this.chatPublicKey = chatPublicKey;
        this.encryptedChatPrivateKey = encryptedChatPrivateKey;

    }

    // getter/setter
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }

    public String getEncryptedPrivateKey() { return encryptedPrivateKey; }
    public void setEncryptedPrivateKey(String encryptedPrivateKey) {
        this.encryptedPrivateKey = encryptedPrivateKey;
    }

    public String getChatPublicKey() { return chatPublicKey; }
    public void setChatPublicKey(String chatPublicKey) {
        this.chatPublicKey = chatPublicKey;
    }

    public String getEncryptedChatPrivateKey() { return encryptedChatPrivateKey; }
    public void setEncryptedChatPrivateKey(String encryptedChatPrivateKey) {
        this.encryptedChatPrivateKey = encryptedChatPrivateKey;
    }
}