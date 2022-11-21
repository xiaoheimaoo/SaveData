package cn.mcfun.savedata;

import android.os.Environment;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;


public class FileUtil {
    final static String key = "b5nHjsMrqaeNliSs3jyOzgpD";
    final static String keyiv = "wuD6keVr";
    public static void saveFile(String str, String fileName) throws Exception {
        byte[] head = Base64.decode("+AE=",Base64.NO_WRAP);
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        FileOutputStream outStream = new FileOutputStream(file);
        outStream.write(head);
        outStream.write(str.getBytes());
        outStream.close();
    }
    public static void deletefile(String fileName) {
        try {
            File path = new File(Environment.getExternalStorageDirectory(), fileName);
            File[] files = path.listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String getFile(String fileName) throws Exception {
        File file = new File(Environment.getExternalStorageDirectory(),fileName);
        FileInputStream fis = new FileInputStream(file);
        byte[] b = new byte[1024];
        int len = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while ((len = fis.read(b)) != -1) {
            baos.write(b, 2, len);
        }
        byte[] data = baos.toByteArray();
        baos.close();
        fis.close();
        return new String(data);

    }
    public static String decrypt(String data) throws  Exception{
        byte[] keyB = key.getBytes();
        byte[] keyivB = keyiv.getBytes();
        byte[] dataByte = Base64.decode(data,Base64.NO_WRAP);
        DESedeKeySpec spec = new DESedeKeySpec(keyB);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        Key deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(keyivB);
        cipher.init(2, deskey, ips);
        byte[] bOut = cipher.doFinal(dataByte);
        String result = new String(bOut, "UTF-8");
        return result;
    }
    public static String encrypt(String data) {
        byte[] keyB = key.getBytes();
        byte[] keyivB = keyiv.getBytes();
        byte[] dataByte = data.getBytes();
        String result = null;
        try {
            DESedeKeySpec spec = new DESedeKeySpec(keyB);
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
            Key deskey = keyfactory.generateSecret(spec);
            Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
            IvParameterSpec ips = new IvParameterSpec(keyivB);
            cipher.init(1, deskey, ips);
            byte[] bOut = cipher.doFinal(dataByte);
            result = Base64.encodeToString(bOut,Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}