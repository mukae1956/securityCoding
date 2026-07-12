package securityEx;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;

public class DigitalSign {
    ChatKeyPair c = new ChatKeyPair();

    public static byte[] generateChallenge(int length){
        byte[] randomBytes = new byte[length];
        SecureRandom sr = new SecureRandom();
        sr.nextBytes(randomBytes);
        return randomBytes;
    }
    // 전자서명 생성
    public static byte[] generateSignature(PrivateKey privateKey, byte[] challenge) throws Exception {
        Signature signer = Signature.getInstance("SHA256withECDSA");
        signer.initSign(privateKey);
        signer.update(challenge);
        byte[] signature = signer.sign();
        return signature;
    }

    // 전자서명 검증
    public static boolean verifySignature(PublicKey publicKey, byte[] challenge, byte[] signature) throws Exception {
        Signature verifier = Signature.getInstance("SHA256withECDSA");
        verifier.initVerify(publicKey);
        verifier.update(challenge);
        boolean isValid = verifier.verify(signature);
        return isValid;
    }
}
