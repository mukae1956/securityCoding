package Service;

import domain.User;
import repository.UserRepository;
import securityEx.ChatKeyPair;
import securityEx.CipherUtil;
import securityEx.DigitalSign;
import securityEx.HashUtil;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Scanner;
import java.util.Base64;

public class UserService {
    //객체 불러오기
    Scanner s = new Scanner(System.in);
    ChatKeyPair c = new ChatKeyPair();
    CipherUtil cu = new CipherUtil();
    HashUtil h = new HashUtil();
    UserRepository ur = new UserRepository();
    User u = new User();
    DigitalSign ds = new DigitalSign();
    private User currentUser;

    //회원가입 -> 회원가입 시작/종료 로그 필요!
    public void joinMember() throws Exception {
        System.out.println("회원 ID 입력 : ");
        String newId = s.nextLine();

        if (ur.existsById(newId)){
            System.out.println("이미 존재하는 ID입니다! 새로운 ID를 입력해 주세요!");
            return;
        }

        System.out.println("회원 이름 입력 : ");
        String newName = s.nextLine();
        String newPwd;
        while (true) {
            System.out.println("비밀번호 입력 : ");
            newPwd = s.nextLine();
            System.out.println("비밀번호 확인 : ");
            String checkPwd = s.nextLine();

            if (newPwd.equals(checkPwd)) {
                break;
            } else {
                System.out.println("비밀번호가 동일하지 않습니다! 다시 입력해 주세요!");
            }
        }

        // 로그인용 키 쌍
        KeyPair userKeyPair = c.generateECKeyPair();
        PrivateKey userPrivateKey = userKeyPair.getPrivate();
        PublicKey userPublicKey = userKeyPair.getPublic();

        // 채팅용 키 쌍
        KeyPair chatKeyPair = c.generateECKeyPair();
        PrivateKey chatPrivateKey = chatKeyPair.getPrivate();
        PublicKey chatPublicKey = chatKeyPair.getPublic();

        // 비밀번호 해시 → 비밀키
        byte[] hashPwd = h.digest(newPwd);
        SecretKey secretKey = new SecretKeySpec(hashPwd, "AES");
        String password = Base64.getEncoder().encodeToString(hashPwd);

        // 로그인용 공개키 (Base64 문자열)
        String publicKey = Base64.getEncoder().encodeToString(userPublicKey.getEncoded());

        // 채팅용 공개키 (Base64 문자열)
        String chatPublicKeyStr = Base64.getEncoder().encodeToString(chatPublicKey.getEncoded());

        // 로그인용 개인키 암호화
        byte[] encryptedLoginPrivate = cu.encrypt(userPrivateKey.getEncoded(), secretKey);
        String encryptedPrivateKey = Base64.getEncoder().encodeToString(encryptedLoginPrivate);

        // 채팅용 개인키 암호화  ← 이 부분이 핵심!
        byte[] encryptedChatPrivate = cu.encrypt(chatPrivateKey.getEncoded(), secretKey);
        String encryptedChatPrivateKey = Base64.getEncoder().encodeToString(encryptedChatPrivate);

        // User 생성자: (id, pwd, name, 로그인공개키, 암호화로그인개인키, 채팅공개키, 암호화채팅개인키)
        User user = new User(newId, password, newName,
                publicKey, encryptedPrivateKey,
                chatPublicKeyStr, encryptedChatPrivateKey);

        ur.save(user);
        System.out.println("회원가입이 완료되었습니다! " + newName + "님 환영합니다🤩");
    }

    //로그인 -> 로그인/로그아웃 로그 필요!
    public void login() throws Exception {
        System.out.println("회원 ID 입력 : ");
        String userId = s.nextLine();

        //id로 사용자부터 조회
        User user = ur.findById(userId);
        if (user==null) {
            System.out.println("존재하지 않는 사용자입니다! 다시 입력해 주세요!");
            return;
        }

        System.out.println("비밀번호 입력 : ");
        String pwd = s.nextLine();

        // json파일에 있는 해시값 비밀번호와 비교하기 위함
        byte[] hashPwd = h.digest(pwd);
        String jsonPwd = Base64.getEncoder().encodeToString(hashPwd);

        SecretKey secretKey= new SecretKeySpec(hashPwd, "AES");

        if(!jsonPwd.equals(user.getPassword())) {
            System.out.println("비밀번호가 틀렸습니다! 다시 입력해 주세요");
            return;
        }

        // json파일(user)에 존재하는 개인키 암호화문을 바이트로 변환
        byte[] encrypted = Base64.getDecoder().decode(user.getEncryptedPrivateKey());

        // 개인키 암호화문에 대해서 복호화
        byte[] decryptedPrivateKey = cu.decrypt(encrypted, secretKey);

        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PKCS8EncodedKeySpec keySpecPrivate = new PKCS8EncodedKeySpec(decryptedPrivateKey);

        PrivateKey privateKey = keyFactory.generatePrivate(keySpecPrivate);

        // 공개키 가져오기
        byte[] bytePublicKey = Base64.getDecoder().decode(user.getPublicKey());
        var keySpecPublic = new X509EncodedKeySpec(bytePublicKey);
        PublicKey publicKey = keyFactory.generatePublic(keySpecPublic);


        // challenge값 생성
        byte[] challenge = ds.generateChallenge(32);

        // 서명값 생성
        byte[] signature = ds.generateSignature(privateKey,challenge);
        System.out.println("서명값을 생성 중입니다...");
        // 서명에 대해 검증
        boolean isValid = ds.verifySignature(publicKey,challenge, signature);
        System.out.println("서명값에 대해 검증 중입니다...");

        if (!isValid){
            System.out.println("로그인 실패!");
            return;
        } else {
            System.out.println("로그인 성공!");
            this.currentUser = user;
        }

        System.out.println("로그인 되었습니다! " + user.getUserName() + "님 어서오세요🥰");
    }

    public void logout(){
        System.out.println("로그아웃 되었습니다! 다음에 또 뵙겠습니다😊");
        currentUser = null;
    }

    //로그인 상태를 가져오기 위한 getter 추가
    public User getCurrentUser() {
        return currentUser;
    }
}

