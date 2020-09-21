package com.lxm.utils;

import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * 
 * ClassName: com.lxm.utils.RSAUtils <br/>
 * Function: rsa工具 <br/>
 * Date: 2020年9月19日 <br/>
 * @author liuxiangming
 */
public class RSAUtils {

    
    private static int KEY_SIZE = 2048; // 密钥长度(bit)

    /**
     * RSA最大解密密文大小
     */
    private static final int    MAX_DECRYPT_BLOCK  = KEY_SIZE / 8;
    
    /**
     * RSA最大加密明文大小。
     * 
     * RSA加密常用的填充模式有三种：RSA_PKCS1_PADDING， RSA_PKCS1_OAEP_PADDING， RSA_NO_PADDING
     * RSA_PKCS1_PADDING（java默认rsa填充模式）： 输入明文，必须 比 RSA 钥模长(modulus) 短至少11个字节, 也就是　RSA_size(rsa) – 11；输出密文和modulus一样长
     * RSA_PKCS1_OAEP_PADDING： 输入明文长度，RSA_size(rsa) – 41； 输出密文和modulus一样长
     * RSA_NO_PADDING： 输入明文可以和RSA钥模长一样长，如果输入的明文过长，必须切割，然后填充；输出密文和modulus一样长
     * 
     */
    private static final int    MAX_ENCRYPT_BLOCK  = MAX_DECRYPT_BLOCK - 11;

    public static final String  SIGNATURE_ALGO     = "SHA1WithRSA";

    public static final String  SIGNATURE_ALGO_256 = "SHA256WithRSA";

    public static final String  ALGORITHM_RSA      = "RSA";

    /**
     * 生成公钥
     * 
     * @param algorithm
     * @param publicKey
     * @return
     */
    public static PublicKey getPublicKeyFromX509(String algorithm, String publicKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            byte[] encodedKey = Base64.decode(publicKey);
            return keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 生成私钥
     * 
     * @param algorithm
     * @param privateKey
     * @return
     */
    public static PrivateKey getPrivateKeyFromPKCS8(String algorithm, String privateKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            byte[] encodedKey = Base64.decode(privateKey);
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 公钥加密
     * 
     * @param content
     * @param publicKey
     * @return
     */
    public static byte[] rsaEncrypt(byte[] content, String publicKey) {
        PublicKey pubKey = getPublicKeyFromX509(ALGORITHM_RSA, publicKey);
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int len = content.length;
            int offSet = 0;
            while (len - offSet > 0) {
                byte[] cache;
                if (len - offSet > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(content, offSet, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(content, offSet, len - offSet);
                }
                out.write(cache, 0, cache.length);
                offSet += MAX_ENCRYPT_BLOCK;
            }
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 私钥解密
     * 
     * @param content
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static byte[] rsaDecrypt(byte[] content, String privateKey) {
        PrivateKey priKey = getPrivateKeyFromPKCS8(ALGORITHM_RSA, privateKey);
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            int len = content.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            while (len - offSet > 0) {
                byte[] cache;
                if (len - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(content, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(content, offSet, len - offSet);
                }
                out.write(cache, 0, cache.length);
                offSet += MAX_DECRYPT_BLOCK;
            }
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 私钥生成签名
     * 
     * @param data
     * @param privateKey
     * @param algorithm
     * @return
     */
    public static byte[] sign(byte[] data, String privateKey, String algorithm) {
        PrivateKey priKey = getPrivateKeyFromPKCS8(ALGORITHM_RSA, privateKey);
        try {
            Signature signature = Signature.getInstance(algorithm);
            signature.initSign(priKey);
            signature.update(data);
            return signature.sign();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 公钥验证签名
     * 
     * @param data
     * @param sign
     * @param publicKey
     * @param algorithm
     * @return
     */
    public static boolean verifySign(byte[] data, byte[] sign, String publicKey, String algorithm) {
        PublicKey pubKey = getPublicKeyFromX509(ALGORITHM_RSA, publicKey);
        try {
            Signature signature = Signature.getInstance(algorithm);
            signature.initVerify(pubKey);
            signature.update(data);
            return signature.verify(sign);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
