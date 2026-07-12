package Service;

import domain.Chat;
import domain.User;
import repository.ChatRepository;
import repository.UserRepository;
import securityEx.ChatKeyPair;
import securityEx.CipherUtil;
import securityEx.HashUtil;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;


public class ChatService {
    ChatKeyPair ckp = new ChatKeyPair();
    Scanner s = new Scanner(System.in);
    CipherUtil cu = new CipherUtil();
    ChatRepository cr = new ChatRepository();
    UserRepository ur = new UserRepository();
    User u = new User();
    HashUtil h = new HashUtil();


    public void sendChat(String senderId, String receiverId, String password) throws Exception {

        //보내는 사람과 받는 사람의 id에 대해서 찾기
        User sender = ur.findById(senderId);
        User receiver = ur.findById(receiverId);

        if (sender == null || receiver == null) {
            System.out.println("사용자가 존재하지 않습니다.");
            return;
        }

        // sender 채팅 개인키 가져와서 복호화
        byte[] encryptedPrivate = Base64.getDecoder()
                .decode(sender.getEncryptedChatPrivateKey());

        byte[] hashPwd = h.digest(password);
        SecretKey secretKey = new SecretKeySpec(hashPwd, "AES");

        byte[] decryptedPrivate = cu.decrypt(encryptedPrivate, secretKey);

        PrivateKey senderPrivateKey = KeyFactory.getInstance("EC")
                .generatePrivate(new PKCS8EncodedKeySpec(decryptedPrivate));

        // receiver 공개키
        byte[] receiverPub = Base64.getDecoder()
                .decode(receiver.getChatPublicKey());

        PublicKey receiverPublicKey = KeyFactory.getInstance("EC")
                .generatePublic(new X509EncodedKeySpec(receiverPub));

        // shared key 생성
        byte[] sharedKey = ckp.sharedSecret(senderPrivateKey, receiverPublicKey);
        SecretKey key = new SecretKeySpec(sharedKey, "AES");

        System.out.println("메시지 입력 > ");
        String text = s.nextLine();

        byte[] encrypted = cu.encrypt(text.getBytes(), key);
        String encryptedText = Base64.getEncoder().encodeToString(encrypted);

        Chat chat = new Chat(encryptedText, senderId, receiverId);
        cr.save(chat);
    }

    public void getChat(String myId, String otherId, String password) throws Exception {

        User me = ur.findById(myId);
        User other = ur.findById(otherId);

        if (me == null || other == null) {
            System.out.println("사용자가 존재하지 않습니다.");
            return;
        }

        // 내 채팅 개인키 복호화
        byte[] encryptedPrivate = Base64.getDecoder()
                .decode(me.getEncryptedChatPrivateKey());

        byte[] hashPwd = h.digest(password);
        SecretKey secretKey = new SecretKeySpec(hashPwd, "AES");

        byte[] decryptedPrivate = cu.decrypt(encryptedPrivate, secretKey);

        PrivateKey myPrivateKey = KeyFactory.getInstance("EC")
                .generatePrivate(new PKCS8EncodedKeySpec(decryptedPrivate));

        // 상대 공개키
        byte[] otherPub = Base64.getDecoder()
                .decode(other.getChatPublicKey());

        PublicKey otherPublicKey = KeyFactory.getInstance("EC")
                .generatePublic(new X509EncodedKeySpec(otherPub));

        // shared key
        byte[] sharedKey = ckp.sharedSecret(myPrivateKey, otherPublicKey);
        SecretKey key = new SecretKeySpec(sharedKey, "AES");

        List<Chat> chats = cr.findAll();

        System.out.println("===== 채팅 =====");

        for (Chat chat : chats) {

            // 나와 상대방 대화만 필터링
            if (!(
                    (chat.getSenderId().equals(myId) && chat.getReceiverId().equals(otherId)) ||
                            (chat.getSenderId().equals(otherId) && chat.getReceiverId().equals(myId))
            )) continue;

            byte[] encrypted = Base64.getDecoder().decode(chat.getEncryptedMessage());

            byte[] decrypted = cu.decrypt(encrypted, key);
            String message = new String(decrypted);

            System.out.println(chat.getSenderId() + " : " + message);
        }
    }
}
