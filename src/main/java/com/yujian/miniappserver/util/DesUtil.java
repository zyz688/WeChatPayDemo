package com.yujian.miniappserver.util;


import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Random;

public class DesUtil {
    private static final String[] PASSWORD = {"EAE11974", "01A345DB", "ED55134D", "87478290", "F3B3C3D9", "8599AE65",
            "D89DAFB2", "377D2364", "9EF94DD3", "84B5C94E", "82FE310C", "040D4439", "81EE5A91", "4ABA444A",
            "EDCD918F", "8A4FE733", "E9C572F0", "B5972754", "6DE43039", "5692D219", "4D33498C", "67EBC74A",
            "3D5D2679", "04424295", "DC9A4771", "C008C093", "798383CA", "4A5F796D", "5E6F5F6E", "F4C85E50",
            "263ABB6C", "9F75CACF", "933373B0", "D934EF33", "B976893B", "1B3A9C93", "658C5C75", "C6BC7755",
            "96626501", "67DB93E5", "8D08FFF1", "39D37086", "73FEAC02", "BFA0FF08", "96AD77B9", "1E322B2D",
            "AF3D528A", "52A0C790", "9194393B", "B1030F3D", "B40DDA2A", "598542BB", "6C07F00B", "ED541401",
            "0AD4FF2A", "E5BF5E25", "B3FB599D", "138A3283", "761DD440", "F9308643", "01646FA2", "847AAD06",
            "597F33CA", "55E46FBC", "B01F148A", "B027AE56", "36BBD18A", "BDD082BC", "87540218", "5BF257B7",
            "8E3A01C2", "0785DD79", "BA318419", "7CB8EA26", "FE6D6C59", "30842E06", "DAB672E8", "85DEBAB7",
            "ADD3A6CC", "FEEAB3FA", "B70F6BBB", "714C44C5", "E404831F", "D39DC3D1", "4CAB0417", "D4E6E086",
            "032BA7F5", "984B9B26", "C4500E96", "61492391", "582F6CD8", "DE2426FD", "4C7F2F9E", "40E338E6",
            "79DC863C", "529A07D4", "B3F72A85", "FCE954AC", "82C3F8A8", "15E8FA0D", "C3FF6914", "C43109E8",
            "04874051", "FEB6462A", "0B825EE2", "3C4D5B84", "46B4D03F", "7E334B36", "8D954B4D", "F843B564",
            "433A9BB5", "4AF4C66C", "B17BCE11", "2D8DD8A4", "AB0A449E", "1961CBA5", "F8AC6739", "5F90EF18",
            "A8A5D53A", "23750D2D", "A563C9AB", "31123B67", "BE1EF7AD", "3B893BD5", "F2AB7BA7", "E7E707D0",
            "5A82A236", "26272C4D", "D6CD101D", "E8346ECB", "13C22710", "9B1DC6E2", "468070A6", "62D6DF78",
            "71AE8F18", "A28FB859", "28D7EC15", "857D4881", "6E502FFA", "7C7DBA65", "0DF249D5", "2D54F856",
            "7D87E0EA", "CB409929", "AF653745", "D0F8AADF", "7DCEECFF", "F9680AD4", "6098677F", "D576DA45",
            "D42ED4C5", "229E6059", "8865B425", "959EE00B", "78DBFD31", "F31CEA9C", "BA8EDB8C", "9445EE6E",
            "0C85F0FA", "BF18DC49", "D14CB6F3", "BE244015", "7C4DE1D3", "299ADFC7", "C47E307C", "1117BDD1",
            "1E2470E1", "35E1CB8E", "B190389D", "60151972", "BF69F311", "3D70F8ED", "7D2F979E", "2CE810DE",
            "0D01D86F", "20D901E4", "ED21A2CF", "F90AFF9F", "691CA6FE", "F2D2D564", "CF115BBF", "7755C32E",
            "6B8F2B14", "E9AAE99E", "6D545A96", "7ABA9F2E", "B005CFC0", "9A9AFC84", "95535ECA", "B7ADC826",
            "953CE1A3", "73061E35", "11210FBA", "109D154C", "DB02704F", "BAF6EF22", "82449E04", "2980C9EB",
            "F9BAA2C4", "4A5A6593", "815A5B84", "8AD26590", "7753EDB9", "82A58E4F", "AC323F40", "90F009E2",
            "C85BC2AA", "460F2646", "82982FEE", "57CFE72E", "CA78ABFE", "73B61F43", "837554EC", "3B95EACB",
            "41F197BE", "5A19ADB9", "3A4BF6F0", "90148683", "F3D2B008", "86CF4614", "30E6FAB7", "2E631BF8",
            "4AEDF714", "C11EE3B5", "AC4FB2B1", "C7143875", "01646081", "B7D12949", "1F1FFDC3", "586B3673",
            "27D1A813", "0E0326E2", "9A8CBA24", "26A92D3A", "8660222B", "9F1336E3", "6A005A5C", "FDABA133",
            "D5D449A6", "44D4E86E", "2A5C3250", "53D4586B", "E6C6154F", "62D0EC90", "0D7A2D58", "45F762A5",
            "E16AC9D3", "5B2DA72E", "02FE4916", "F9878746", "44B8DFEC", "F943EEDF", "B0ADAB2B", "2F420F5A",
            "656A20C4", "F8A92434"};
    private static final String CHARSET = "UTF-8";
    private DesUtil() {}
    /**
     * 从密钥表中获取8位明文密钥
     *
     * @return 明文密钥 password
     */
    public static String getPassword(int index) {
        if (index <= 255 && index >= 0) {
            return PASSWORD[index];
        }
        return null;
    }
    /**
     * MD5加密
     *
     * @param password 明文密钥
     * @return 密文密钥
     */
    public static String MD5encode(String password) {
        String result = "";
        if (password.length()==0) {
            return null;
        }
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(password.getBytes());
            for (byte b : bytes) {
                //不管十进制数的改变，而只保证二进制数的一致性
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 随机生成8位加密随机数
     *
     * @return 加密随机数 data
     */
    public static String getData() {
        String[] strings = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index = getIndex(strings.length);
            sb.append(strings[index]);
        }
        return sb.toString();
    }
    /**
     * DES算法，加密
     *
     * @param data 8位加密随机数
     * @param key  8位大写密文密钥
     * @return 加密后的16个字节的十六进制字符串，作为密文1
     */
    public static String DESEncode(String key, String data) {
        if (data == null) {
            return null;
        }
        try {
            String key1= DesUtil.MD5encode(key).substring(0,8).toUpperCase();
            DESKeySpec dks = new DESKeySpec(key1.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            Key secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(key1.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] bytes = cipher.doFinal(data.getBytes("GBK"));
            return byte2String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return data;
        }
    }

    /**
     * DES解密字符串
     *
     * @param key 解密密码，长度不能够小于8位
     * @param data 待解密字符串
     * @return 解密后内容
     */
    public static String decrypt(String key, String data) {
        if (key== null || key.length() < 8||data == null)
            return "";
        try {
            String key1=MD5encode(key).substring(0,8).toUpperCase();
            DESKeySpec dks = new DESKeySpec(key1.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            Key secretKey = keyFactory.generateSecret(dks);


            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(key1.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

            byte[] bytes = hex2byte(data);

            BASE64Encoder encoder = new BASE64Encoder();
            new BASE64Encoder().encodeBuffer(bytes);
            String s = new BASE64Encoder().encodeBuffer(bytes);//new String(Base64.getEncoder().encode(bytes));

            // byte[] bytes =  Base64.getDecoder().decode(data.getBytes("GBK"));
            // 真正开始解密操作
            return new String(cipher.doFinal((new BASE64Decoder()).decodeBuffer(s)), CHARSET);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Web返回参数解密
     * @param strCipher 密文
     * @param random 解密的随机数
     * @return 返回解密后的明文
     */
    public static String webReturnParameterDecrypt(String strCipher,String random){
        //加密得到新的解密密钥KEY
        String strNewKey =  DesUtil.DESEncode("YuJian@2018",random);
        //用新的密钥KEY去解密
        String strDecryptTxt =  DesUtil.decrypt(strNewKey,strCipher);
        return strDecryptTxt;
    }


    /**
     * 对16字节密文1以4字节为单位，让个单位中的各字节依次与4字节加密随机数的各字节进行异或操作，再重新组合得到新的16字节密文2；
     * <p>
     * 例如：密文1 = 1A 37 CD D1 A2 68 35 67 BB 3C B7 32 54 3F 76 92； 加密随机数 = AC 90 1A 8D；
     * 1A 37 CD D1 异或 AC 90 1A 8D
     * + A2 68 35 67 异或 AC 90 1A 8D
     * + BB 3C B7 32异或 AC 90 1A 8D
     * + 54 3F 76 92异或 AC 90 1A 8D = B6 A7 D7 5C 0E F8 2F EA 17 AC AD BF F8 AF 6C 1F
     * <p>
     * 对密文2各字节进行高低位对调最终生成加密密钥KEY。
     * <p>
     * 例如： B6 A7 D7 5C 0E F8 2F EA 17 AC AD BF F8 AF 6C 1F
     * -> 6B 7A 7D C5 E0 8F F2 AE 71 CA DA FB 8F FA C6 F1 ;
     *
     * @param cipher 16字节密文1
     * @param data   8位加密随机数
     * @return 16字节加密密钥KEY
     */
    public static String cipherXor(String cipher, String data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            String parent = cipher.substring(i * 8, (i + 1) * 8);
            for (int j = 0; j < 4; j++) {
                String child = parent.substring(j * 2, (j + 1) * 2);
                String dataChild = data.substring(j * 2, (j + 1) * 2);
                int a = Integer.parseInt(child, 16);
                int b = Integer.parseInt(dataChild, 16);
                String result = Integer.toString(a ^ b, 16);
                if (result.length() < 2) {
                    result = "0" + result;
                }
                //高低位互换
                sb.append(result.charAt(1));
                sb.append(result.charAt(0));
            }
        }
        return sb.toString().toUpperCase();
    }
    /**
     * AES算法，加密
     *
     * @param key  16字节加密密钥KEY
     * @param data 待加密明文
     * @return 加密后的16字节十六进制字符串
     */
    public static String AESEncode(String key, String data) {
        return AES128(key, data, Cipher.ENCRYPT_MODE);
    }
    /**
     * AES算法，解密
     *
     * @param key  16字节加密密钥KEY
     * @param data 待解密密文
     * @return 解密后的16字节十六进制字符串
     */
    public static String AESDecode(String key, String data) {
        return AES128(key, data, Cipher.DECRYPT_MODE);
    }
    /**
     * AES算法
     *
     * @param key    16字节加密密钥KEY
     * @param data   待处理数据
     * @param opmode 这个密码的操作模式
     * @return 处理后的16字节十六进制字符串
     */
    private static String AES128(String key, String data, int opmode) {
        if (data == null) {
            return null;
        }
        try {
            SecretKeySpec secretKey = new SecretKeySpec(hex2byte(key), "AES");
            if (opmode == Cipher.ENCRYPT_MODE) {
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(opmode, secretKey);
                byte[] bytes = cipher.doFinal(hex2byte(data));
                return byte2String(bytes);
            } else if (opmode == Cipher.DECRYPT_MODE) {
                Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
                cipher.init(opmode, secretKey);
                byte[] bytes = cipher.doFinal(hex2byte(data));
                return byte2String(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
    /**
     * 随机生成索引
     *
     * @param length 数组长度
     * @return 索引 index
     */
    public static int getIndex(int length) {
        Random random = new Random();
        return random.nextInt(length);
    }
    /**
     * 字节数组转hex字符串
     */
    public static String byte2String(byte[] b) {
        StringBuilder sb = new StringBuilder();
        String stmp;
        for (int n = 0; b != null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            if (stmp.length() == 1) sb.append('0');
            sb.append(stmp);
        }
        return sb.toString().toUpperCase(Locale.CHINA);
    }
    /**
     * 将hex字符串转换成字节数组
     */
    public static byte[] hex2byte(String inputString) {
        if (inputString == null || inputString.length() < 2) {
            return new byte[0];
        }
        inputString = inputString.toLowerCase();
        int l = inputString.length() / 2;
        byte[] result = new byte[l];
        for (int i = 0; i < result.length; i++) {
            String tmp = inputString.substring(2 * i, 2 * i + 2);
            result[i] = (byte) (Integer.parseInt(tmp, 16) & 0xFF);
        }
        return result;
    }
    //获取16位随机数
    public static String getDataforWeb() {
        String[] strings = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            int index = getIndex(strings.length);
            sb.append(strings[index]);
        }
        return sb.toString();
    }

    /**
     * 获取一定长度的随机字符串
     *
     * @param length 指定字符串长度
     * @return 一定长度的字符串
     */
    public static String getRandomStringByLength(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
}
