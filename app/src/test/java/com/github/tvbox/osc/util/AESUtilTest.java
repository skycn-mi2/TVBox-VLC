package com.github.tvbox.osc.util;


import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Formattable;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtilTest {

    /**
     * 1.电码本模式（Electronic Codebook Book (ECB)）不支持；
     * 2.密码分组链接模式（Cipher Block Chaining (CBC)）默认的加密算法；
     * 3.计算器模式（Counter (CTR)）；
     * 4.密码反馈模式（Cipher FeedBack (CFB)）；
     * 5.输出反馈模式（Output FeedBack (OFB)）。
     */
    // 指定加密的算法、工作模式和填充方式
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    /**
     * 偏移量
     */
    private static final String IV_STRING = "123456cbae567890";

    /**
     * 将byte[]转为各种进制的字符串
     *
     * @param bytes byte[]
     * @param radix 可以转换进制的范围，从Character.MIN_RADIX到Character.MAX_RADIX，超出范围后变为10进制
     * @return 转换后的字符串
     */
    public static String binary(byte[] bytes, int radix) {
        return new BigInteger(1, bytes).toString(radix);// 这里的1代表正数
    }

    /**
     * base 64 encode
     *
     * @param bytes 待编码的byte[]
     * @return 编码后的base 64 code
     */
    public static String base64Encode(byte[] bytes) {
        return Base64.getEncoder().withoutPadding().encodeToString(bytes);
        //   .encodeToString(bytes, Base64.DEFAULT | Base64.URL_SAFE | Base64.NO_WRAP);
    }

    /**
     * base 64 decode
     *
     * @param base64Code 待解码的base 64 code
     * @return 解码后的byte[]
     */
    public static byte[] base64Decode(String base64Code) {
        return isEmpty(base64Code) ? null : Base64.getUrlDecoder().decode(base64Code);
    }

    /**
     * 获取byte[]的md5值
     *
     * @param bytes byte[]
     * @return md5
     * @throws Exception
     */
    public static byte[] md5(byte[] bytes) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(bytes);
        return md.digest();
    }

    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * 获取字符串md5值
     *
     * @param msg
     * @return md5
     * @throws Exception
     */
    public static byte[] md5(String msg) throws Exception {
        return isEmpty(msg) ? null : md5(msg.getBytes());
    }

    /**
     * 结合base64实现md5加密
     *
     * @param msg 待加密字符串
     * @return 获取md5后转为base64
     * @throws Exception
     */
    public static String md5Encrypt(String msg) throws Exception {
        return isEmpty(msg) ? null : base64Encode(md5(msg));
    }

    /**
     * AES加密
     *
     * @param content    待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的byte[]
     * @throws Exception
     */
    public static byte[] aesEncryptToBytes(String content, String encryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(encryptKey.getBytes());
        kgen.init(128, random);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(kgen.generateKey().getEncoded(), "AES"));
        return cipher.doFinal(content.getBytes("utf-8"));
    }

    /**
     * AES加密为base 64 code
     *
     * @param content    待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的base 64 code
     * @throws Exception
     */
    public static String aesEncrypt(String content, String encryptKey) throws Exception {
        return base64Encode(aesEncryptToBytes(content, encryptKey));
    }

    /**
     * AES解密
     *
     * @param encryptBytes 待解密的byte[]
     * @param decryptKey   解密密钥
     * @return 解密后的String
     * @throws Exception
     */
    public static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey) throws
            Exception {
//        KeyGenerator kgen = KeyGenerator.getInstance("AES");
//        kgen.init(128, new SecureRandom(decryptKey.getBytes()));
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(decryptKey.getBytes());
        kgen.init(128, random);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(kgen.generateKey().getEncoded(), "AES"));
        byte[] decryptBytes = cipher.doFinal(encryptBytes);

        return new String(decryptBytes);
    }

    /**
     * 将base 64 code AES解密
     *
     * @param encryptStr 待解密的base 64 code
     * @param decryptKey 解密密钥
     * @return 解密后的string
     */
    public static String aesDecrypt(String encryptStr, String decryptKey) throws Exception {
        return isEmpty(encryptStr) ? null : aesDecryptByBytes(base64Decode(encryptStr),
                decryptKey);
    }


    public static String encryptAES(String content, String key, String iv_string)
            throws Exception {
        byte[] byteContent = content.getBytes("UTF-8");
        return encryptAES(byteContent, key, iv_string);
    }

    public static String encryptAES(byte[] byteContent, String key, String iv_string)
            throws Exception {
        // 注意，为了能与 iOS 统一
        // 这里的 key 不可以使用 KeyGenerator、SecureRandom、SecretKey 生成
        byte[] enCodeFormat = key.getBytes();
        SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
        byte[] initParam = iv_string.getBytes();
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
        // 指定加密的算法、工作模式和填充方式
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] encryptedBytes = cipher.doFinal(byteContent);
        // 同样对加密后数据进行 base64 编码
        return base64Encode(encryptedBytes);
    }

    /**
     * @param content
     * @param key     将AES秘钥改成规定的16位即可。
     * @return
     * @throws Exception 解密（使用这个）
     */
    public static String decryptAES(String content, String key, String iv_string)
            throws Exception {
        // base64 解码
        byte[] encryptedBytes = base64Decode(content);
        return decryptAES(encryptedBytes, key, iv_string);
    }

    public static String decryptAES(byte[] encryptedBytes, String key, String iv_string)
            throws Exception {
        // base64 解码
        byte[] enCodeFormat = key.getBytes();
        SecretKeySpec secretKey = new SecretKeySpec(enCodeFormat, "AES");
        byte[] initParam = iv_string.getBytes();
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        byte[] result = cipher.doFinal(encryptedBytes);
        return new String(result, "UTF-8");
    }


    //        @Test
    public void t() throws Exception {
        String key = "skycnapplive9876";
        String v = "563456cbae567890";
        File in = new File("D:\\soft\\Android\\AndroidStudioProjects\\TVBoxOSC-main2\\app\\src\\main\\res\\raw\\live.txt");
        File out = new File("D:\\soft\\Android\\AndroidStudioProjects\\TVBoxOSC-main2\\app\\src\\main\\res\\raw\\live_aes.txt");

        try (
                FileInputStream fis = new FileInputStream(in);
                FileWriter fos = new FileWriter(out);
        ) {
            byte[] fileBytes = new byte[(int) in.length()];
            fis.read(fileBytes);
            String sql = encryptAES(fileBytes, key, v);
            fos.write("encrypt");
            fos.write(sql);
        } finally {
        }
        System.out.println("ok");
    }


//        @Test
    public void t2() throws Exception {
        String key = "skycnapplive9876";
        String v = "563456cbae567890";
        File in = new File("D:\\soft\\Android\\AndroidStudioProjects\\TVBoxOSC-main2\\app\\src\\main\\res\\raw\\live.txt");
        File out = new File("D:\\soft\\Android\\AndroidStudioProjects\\TVBoxOSC-main2\\app\\src\\main\\res\\raw\\live_decrypt.txt");
        System.out.println("文件存在：" + in.exists());
        try (
                FileInputStream fis = new FileInputStream(in);
                FileWriter fos = new FileWriter(out);
        ) {
            int of = "encrypt".getBytes().length;
            byte[] fileBytes = new byte[(int) in.length() - of];
            System.out.println("of:" + of + ",fileBytes:" + fileBytes.length + ",in:" + in.length());
            fis.skip(of);
            fis.read(fileBytes);
            String sql = decryptAES(Base64.getDecoder().decode(fileBytes), key, v);
            fos.write(sql);
        }

    }

    @Test
    public void encryptFiles() {
        String key = "skycnapplive9876";
        String v = "563456cbae567890";
        File dirs = new File("D:\\soft\\Android\\AndroidStudioProjects\\TVBoxOSC-main2-vlc\\config\\res\\");
        if (!dirs.exists() || dirs.isFile()) {
            return;
        }
        String outdir = "D:\\soft\\Android\\AndroidStudioProjects\\TVBoxOSC-main2-vlc\\app\\src\\main\\res\\raw\\";
        for (File f : dirs.listFiles()) {
            try (
                    FileInputStream fis = new FileInputStream(f);
                    FileWriter fos = new FileWriter(new File(outdir + f.getName()));
            ) {
                byte[] fileBytes = new byte[(int) f.length()];
                fis.read(fileBytes);
                String sql = encryptAES(fileBytes, key, v);
                fos.write("encrypt");
                fos.write(sql);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
            }
            System.out.println("加密"+f.getName()+ "... ok");
        }
    }
}
