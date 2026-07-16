package securityEx;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.SecretKey;

import java.security.SecureRandom;

public class CipherUtil {
    final static String ALG = "AES/GCM/NoPadding";

    private static final int IV_SIZE = 12;      // GCM 권장 12 bytes
    private static final int TAG_LENGTH = 128;  // 인증 태그 길이

    // 🔐 암호화
    public static byte[] encrypt(byte[] plaintext, SecretKey key) throws Exception {

        //IV 생성
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);

        //Cipher 초기화
        Cipher cipher = Cipher.getInstance(ALG);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

        // 3. 암호화
        byte[] ciphertext = cipher.doFinal(plaintext);

        // 4. IV + ciphertext 합치기
        byte[] result = new byte[IV_SIZE + ciphertext.length];
        System.arraycopy(iv, 0, result, 0, IV_SIZE);
        System.arraycopy(ciphertext, 0, result, IV_SIZE, ciphertext.length);

        return result;
    }

    // 🔓 복호화
    public static byte[] decrypt(byte[] encrypted, SecretKey key) throws Exception {

        // 1. IV 분리
        byte[] iv = new byte[IV_SIZE];
        byte[] ciphertext = new byte[encrypted.length - IV_SIZE];

        System.arraycopy(encrypted, 0, iv, 0, IV_SIZE);
        System.arraycopy(encrypted, IV_SIZE, ciphertext, 0, ciphertext.length);

        // 2. Cipher 초기화
        Cipher cipher = Cipher.getInstance(ALG);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);

        // 3. 복호화
        return cipher.doFinal(ciphertext);
    }
}