package org.damein.miaosha.util;


import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {

    public static String md5(String src){//转16进制
        return DigestUtils.md2Hex(src);
    }

    private static final String salt="1a2b3c4d";

    public static String inputPassToFormPass(String inputPass){
       String str = ""+salt.charAt(0)+salt.charAt(2)+inputPass+salt.charAt(5)+salt.charAt(4);
       return md5(str);
    }

    public static String fromPassToDBPass(String formPass,String salt){
        String str =  ""+salt.charAt(0)+salt.charAt(2)+formPass+salt.charAt(5)+salt.charAt(4);
        return md5(str);
    }

    public static String inputPassToDBPass(String input,String saltDB){
        String formPass = inputPassToFormPass(input);
        String dbPass = fromPassToDBPass(formPass,saltDB);
        return  dbPass;
    }


//    public static void main(String[] args) {
////        System.out.println(inputPassToFormPass("123456"));
////        System.out.println(fromPassToDBPass(inputPassToFormPass("123456"),"1a2b3c4d"));
//        System.out.println(inputPassToDBPass("123456", "1a2b3c4d"));
//    }
}
