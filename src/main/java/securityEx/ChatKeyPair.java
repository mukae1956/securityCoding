package securityEx;

import javax.crypto.KeyAgreement;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class ChatKeyPair {

    //상수
    private static final String EC_ALGORITHM = "EC";
    private static final String CURVE = "secp256r1";
    private static final String ECDH = "ECDH";

    //EC 키 쌍 생성
    public static KeyPair generateECKeyPair() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(EC_ALGORITHM);
        kpg.initialize(new ECGenParameterSpec(CURVE), new SecureRandom());
        return kpg.generateKeyPair();
    }

    //ECDH 공유키(비밀키) 계산
    public static byte[] sharedSecret(PrivateKey myPriv, PublicKey peerPub) throws Exception {
        KeyAgreement ka = KeyAgreement.getInstance(ECDH);
        ka.init(myPriv);
        ka.doPhase(peerPub, true);
        return ka.generateSecret();
    }

}
