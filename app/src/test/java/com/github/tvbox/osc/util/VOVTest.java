package com.github.tvbox.osc.util;

import android.util.Log;


import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;

public class VOVTest {

    private static final String KEY = "VheJFL";
    private static final String hexString = "0123456789ABCDEF";

    public static String d(String str) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(str.length() / 2);
        for (int i = 0; i < str.length(); i += 2) {
            baos.write((hexString.indexOf(str.charAt(i)) << 4) | hexString.indexOf(str.charAt(i + 1)));
        }
        byte[] b = baos.toByteArray();
        int len = b.length;
        int keyLen = KEY.length();
        for (int i2 = 0; i2 < len; i2++) {
            b[i2] = (byte) (b[i2] ^ KEY.charAt(i2 % keyLen));
        }
        return new String(b);
    }

    @Test
    public void ad() {
        System.out.println("--------------aaaaaaaaaa-------------");
        Double d = Math.ceil(new Date().getTime() / 1000d);
        int a = d.intValue();
        String key = "DS" + String.valueOf(a) + "DCC147D11943AF75";
        System.out.println(key);
        try {
            System.out.println(AESUtil.hexmd5(key));
        } catch (Exception e) {
            Log.e(this.getClass().getName(), "生成ekey失败", e);
        }
        System.out.println("--------------aaaaaaaaaa-------------");
    }





}
