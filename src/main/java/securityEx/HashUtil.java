package securityEx;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {

    final static String HASH_ALGORITHM = "SHA-256";

    /* public static String hexEncoding(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    } */

    public static byte[] digest(String sharedKey) {
        byte[] secretKey;
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);

            secretKey = md.digest(sharedKey.getBytes());
            md.reset();
            for (int i = 0; i < 1000; i++) {
                secretKey = md.digest(secretKey);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("해싱 알고리즘을 사용할 수 없습니다!");
        }
        return secretKey;
    }


}
