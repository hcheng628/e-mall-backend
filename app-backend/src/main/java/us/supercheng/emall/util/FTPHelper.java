package us.supercheng.emall.util;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import us.supercheng.emall.common.Const;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class FTPHelper {

    private FTPClient ftpClient;

    private FTPHelper () {
        this.ftpClient = new FTPClient();
    }

    private FTPClient getFtpClient() {
        return this.ftpClient;
    }

    private boolean doConnect(FTPClient ftpClient) {
        try {
            ftpClient.connect(Const.FTP_IP, Const.FTP_PORT);
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private boolean doLogin(FTPClient ftpClient) {
        try {
            ftpClient.login(Const.FTP_USER, Const.FTP_PASS);
            return true;
        }catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static String doUpload(String remoteDir, File file) {
        //System.out.println("Init " + );
        FTPHelper ftpHelper = new FTPHelper();
        String fileName = null;
        if (ftpHelper.doConnect(ftpHelper.getFtpClient())) {
            if (ftpHelper.doLogin(ftpHelper.getFtpClient())) {
                String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
                System.out.println("Extension: " + extension);
                ftpHelper.getFtpClient().enterLocalPassiveMode();
                InputStream inputStream = null;
                try {
                    ftpHelper.getFtpClient().setFileType(FTP.BINARY_FILE_TYPE);
                    ftpHelper.getFtpClient().setControlEncoding(Const.FTPConst.ENCODING);
                    ftpHelper.getFtpClient().setBufferSize(Const.FTPConst.BUFFER_SIZE);
                    ftpHelper.getFtpClient().changeWorkingDirectory(remoteDir);
                    inputStream = new FileInputStream(file);
                    fileName = UUID.randomUUID().toString() + "." + extension;
                    System.out.println("Filename: " + fileName);
                    ftpHelper.getFtpClient().storeFile(fileName, inputStream);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return null;
                }finally {
                   try {
                       inputStream.close();
                       if (ftpHelper.getFtpClient().isConnected()) {
                            ftpHelper.getFtpClient().logout();
                            ftpHelper.getFtpClient().disconnect();
                       }
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
                }
            }
        }
        return fileName;
    }
}