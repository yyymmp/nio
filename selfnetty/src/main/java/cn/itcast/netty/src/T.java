package cn.itcast.netty.src;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jlz
 * @date 2022年04月08日 15:14
 */
public class T {

    public static final String KEY_ALGORITHM = "RSA";
    public static final String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";
    public static final String PUBLIC_KEY = "your_public_key";
    public static final String PRIVATE_KEY = "your_private_key";

    public static final String ENCODE_ALGORITHM = "SHA-256";
    public static final String SIGNATURE_ALGORITHM = "SHA256withRSA";




    /**
     * 签名
     *
     * @param privateKey 私钥
     * @param plain_text 明文
     * @return
     */
    public static String sign(PrivateKey privateKey, String plain_text) {
        MessageDigest messageDigest;
        byte[] signed = null;
        try {
            messageDigest = MessageDigest.getInstance(ENCODE_ALGORITHM);
            messageDigest.update(plain_text.getBytes());
            byte[] outputDigest_sign = messageDigest.digest();
//            System.out.println("SHA-256加密后-----》" +bytesToHexString(outputDigest_sign));
            Signature Sign = Signature.getInstance(SIGNATURE_ALGORITHM);
            Sign.initSign(privateKey);
            Sign.update(outputDigest_sign);
            signed = Sign.sign();
//            System.out.println("SHA256withRSA签名后-----》" + bytesToHexString(signed));
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return Base64.getEncoder().encodeToString(signed);

//        return Base64Util.encode(signed);
    }



    /**
     * bytes[]换成16进制字符串
     *
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static Map<String, String> createEncryKeyStr(PublicKey puKey, PrivateKey priKey) throws Exception {
        //通过对象 KeyPair 获取RSA公私钥对象RSAPublicKey RSAPrivateKey
        RSAPublicKey publicKey = (RSAPublicKey) puKey;
        RSAPrivateKey privateKey = (RSAPrivateKey) priKey;

        String pubKeyStr = new String(Base64.getEncoder().encodeToString(publicKey.getEncoded()));
        String pkcs8Str = new String(Base64.getEncoder().encodeToString(privateKey.getEncoded()));


        //私钥加密
//        String privateKeyStr = LukeRsa.privateKeyPwdToPKCS1(privateKey, privateKeyPwd);//使用BC加密私钥格式会被转为PKSC#1格式

        //公私钥对象存入map中
        Map<String, String> keyMap = new HashMap<String, String>(2);
        keyMap.put("publicKeyStr", pubKeyStr);
        keyMap.put("privateKeyStr", pkcs8Str);
        return keyMap;
    }


    public static void main(String[] args) {
        String privateKey = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCymyGp5BYDnm5/2/oAKTvmy1X8ZvBQh0dZXGAFF9ubFQL4z0hLMUk2m+At3rjGOF2A6cS7zoIVrypoKtv4Q2VCgyXGw99i//Q61+RWIWuNjg8vQNDZfClq6ITA2vYPFUAHjOO5hc0oUM/fZF9PmQvDMSavfvTPavov2onREvFHaDr9uVvGV0Ub33roNNN5uXEzaNlPQUdalz/FsUIdFa5Qv89X/VIKEvRdg42WufLMdmaNvzKSeNxlDOFCI5C4qdXMThHzOjMstrdp3PwWSMmomdWfo+lOdw5A+yUeb3toZkMJrY1uSYE8R0ysDdJwMCYd3P49LsMOR7ggwJcRUNYVAgMBAAECggEBAJ0pLKIV9dkRXCJeKsphfjhDzFHhMq1pnDrhsgxJRhBcmNTfcLnZkJbg6o7yYJzrlDuPeHHZ8VWrYSFwNUdr5np0dJVGXhb7Kqlst5uipMVWr73gxuXY/4j3OXmKMuJqzIDYVLo3JqPHGGN2Kvgbr+H+KhBbIogxlhztrD+w72bosmX6efdw1Qnv/7tZr2rEvrT1q+G0Bo7Huu9XyNU/ZhfDzpj5j3P+AZqzGGAP7+FfxogZAvpNSqv5jZ6pz9YQ4ref5lqaz1CnWfVml44buDyyEYKST6UubEZJnvZdmLE0ODPJMiiENOkYC4Pz2hAGXzZkqKa5lQj83q8eQZ3pojECgYEA7Hz1ceEtyhm1D/jsyqsPGaxXYTPIn6j5HXJRe8diL/qRoH2w6256EQk1TFiuqEk7tm5qgN5/JX5ienTxvcVtExkkvZ6hB48FlwlgX6oBtGTRebJcFy7SO/3a35gBZBuTfR9FCZGKHRN/61+AHhMPQypigEBG9NtFCzj6EJ9C5NMCgYEAwVeZ4i4hT2vCyBJzi0hH9mz/4t0y1YeKqj2OgojgEOe08l6akH0+jU/qqTZWFUENFsHg9ZcjHniN+8xKow+AxAL3ei1Hkbzg+DCcqGfcBE4f6Hyq6WexGkNQHuLnsaFAwKjclS9yyDIGoIx/pmPEgE7/Z/jh7dSbVQtx23/6qHcCgYEAtUdQalwfWoV8OuMouG6J47ctQBCO4HyJgV4JQiXjiT289daDkOUd4wM4/O4Z7Fw+zqgzMdfchSQtXwtX5414ACTM+iGn9IBY259PZeWO6vZBcNUdLJQyidQdPDpsDo3o8AfRRvVudr1k2EtbjTI1B9psqT0cZ0PR2DlzztwWTgsCgYEAg+NgI7xYGLdn3ddIXoDmJiHgxD8kSCj33H21UzJPtvh4pMUIJvxoyTj02I0qV8FSTm3fKWj6GfUe1d9cIrOWoR9s9jkhEzeHN4bhtrdyjLVGau9wH1wOod29L/SVHMTlSQdnwZTmdc46Wxa8lSKFjwlUjPZoGDbp5y+xcYxxzq0CgYAjdKdky3xfZ7IvM4PzS3YDxsdHSavDl3e2e/Wnja3Lj+D4vrS40U/y0BCo+eFBgJY0haKL61mPuFsP67C9Z4VjHSZzbsNPmxwkoSYW2fT0m8Q5BtzRuUGGB71cviuuNbk1WqoQlFsVV9R6ZgF+tLPq5CPFawblvEvsP1QsFvg44Q==";

        String encodeValue = "app_id=787ED78C01800006&biz_content=[{\"vehicleno\":\"川AXC999\"}]&charset=utf-8&mcode=200103&sign_type=RSA2&timestamp=2022-04-12 10:28:05&version=1.0";

        String sign = sign(getPrivateKey(privateKey), encodeValue);
        System.out.println(String.format("sign:%s",sign));


        System.out.println("待签名参数:");
    }

    public static PrivateKey getPrivateKey(String key) {
        try {
            byte[] byteKey = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(byteKey);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

            return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Long getDelay(Date startDate, Date endDate) {
        if (null == startDate || null == endDate) {
            return null;
        }

        Instant startInstant = startDate.toInstant();
        Instant endInstant = endDate.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();


        final LocalDate start = startInstant.atZone(zoneId).toLocalDate();
        final LocalDate end = endInstant.atZone(zoneId).toLocalDate();
        if (start != null && end != null) {
            return end.toEpochDay() - start.toEpochDay();
        }
        return null;
    }
}
