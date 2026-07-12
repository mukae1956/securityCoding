package securityEx;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.SecretKey;

import java.security.SecureRandom;
import java.util.Arrays;

public class CipherUtil {
    final static String alg = "AES/GCM/NoPadding";

    private static final int KEY_SIZE = 256;
    private static final int IV_SIZE = 12;
    private static final int TAG_LENGTH = 128;
    private static final int SEED_SIZE = 16; // seed 크기 (조정 가능)

    // 암호화
    public static byte[] encrypt(byte[] plaintext, SecretKey key) throws Exception {
        // 랜덤 seed 생성
        byte[] seed = new byte[SEED_SIZE];
        new SecureRandom().nextBytes(seed);

        // seed + plaintext 합치기

        // seed와 평문의 길이를 가지는 바이트 생성
        byte[] seededPlaintext = new byte[SEED_SIZE + plaintext.length];
        System.arraycopy(seed, 0, seededPlaintext, 0, SEED_SIZE);
        System.arraycopy(plaintext, 0, seededPlaintext, SEED_SIZE, plaintext.length);

        // IV 생성
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance(alg);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

        byte[] ciphertext = cipher.doFinal(seededPlaintext); // seed 포함해서 암호화

        // 4. IV + 암호문 반환
        byte[] result = new byte[IV_SIZE + ciphertext.length];
        System.arraycopy(iv, 0, result, 0, IV_SIZE);
        System.arraycopy(ciphertext, 0, result, IV_SIZE, ciphertext.length);
        return result;
    }

    // 복호화
    public static byte[] decrypt(byte[] encrypted, SecretKey key) throws Exception {
        // 1. IV 분리
        byte[] iv = new byte[IV_SIZE];
        byte[] ciphertext = new byte[encrypted.length - IV_SIZE];
        System.arraycopy(encrypted, 0, iv, 0, IV_SIZE);
        System.arraycopy(encrypted, IV_SIZE, ciphertext, 0, ciphertext.length);

        Cipher cipher = Cipher.getInstance(alg);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);

        byte[] seededPlaintext = cipher.doFinal(ciphertext);

        // 2. 앞의 seed(16바이트) 제거하고 실제 데이터만 반환
        return Arrays.copyOfRange(seededPlaintext, SEED_SIZE, seededPlaintext.length);
    }
}