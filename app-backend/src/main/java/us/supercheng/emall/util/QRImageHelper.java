package us.supercheng.emall.util;

import org.apache.commons.codec.binary.Base64;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class QRImageHelper {

    public static String qRImageToBase64(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] byteArr = new byte[(int) file.length()];
        fis.read(byteArr);
        return Base64.encodeBase64String(byteArr);
    }

    public static boolean deleteQRImage(File file) {
        try {
            if (file.delete()) {
                System.out.println(file.getName() + " is deleted!");
                return true;
            } else {
                System.err.println("Delete operation is failed.");
            }
        } catch (Exception e) {
            System.err.println("Delete Exception: " + e);
            e.printStackTrace();
        }
        return false;
    }
}