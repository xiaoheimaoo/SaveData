package cn.mcfun.savedata;

import android.os.Environment;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.Key;


public class FileUtil {
    final static String key = "b5nHjsMrqaeNliSs3jyOzgpD";
    final static String keyiv = "wuD6keVr";
    public static void saveFileWithRoot(String head, String str, String fileName) throws Exception {
        // 将 head 解码
        byte[] headBytes = Base64.decode(head, Base64.NO_WRAP);

        // 目标文件路径
        String path = fileName;  // 你可以替换成需要的路径

        // 创建新文件
        createFileWithRoot(path);

        // 写入数据
        writeToFileWithRoot(path, headBytes, str.getBytes());
    }
    /**
     * 使用 Root 权限创建文件
     */
    private static void createFileWithRoot(String path) throws Exception {
        String cmd = "touch " + path;
        Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", cmd});
        process.waitFor();
    }

    /**
     * 使用 Root 权限将数据写入文件
     */
    private static void writeToFileWithRoot(String path, byte[] headBytes, byte[] contentBytes) throws Exception {
        // 使用 Root 权限打开文件输出流并写入数据
        String cmd = "cat > " + path;
        Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", cmd});

        // 通过输出流写入文件
        try (OutputStream outputStream = process.getOutputStream()) {
            // 先写入 headBytes
            outputStream.write(headBytes);
            // 然后写入 contentBytes
            outputStream.write(contentBytes);
            outputStream.flush(); // 确保数据被写入
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("写入文件失败", e);
        }

        // 等待命令执行完成
        process.waitFor();
    }
    public static void deletefile(String fileName) {
        try {
            File path = new File(Environment.getExternalStorageDirectory(), fileName);
            File[] files = path.listFiles();
            for (File file : files) {
                if (file.isFile() && !file.getName().equals("54cc790bf952ea710ed7e8be08049531")) {
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