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

    //@Value("${ftp.server.ip}")
    private String ip;

    //@Value("${ftp.server.port}")
    private String port;

    //@Value("${ftp.server.user}")
    private String user; // = Const.FTP_USER;

    //@Value("${ftp.server.pass}")
    private String pass; // = Const.FTP_PASS;
    private FTPClient ftpClient;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }

    public boolean doConnect() {
        this.ftpClient = new FTPClient();
        try {
            this.ftpClient.connect(this.ip, Integer.parseInt(this.port));
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean doLogin() {
        try {
            this.ftpClient.login(this.user, this.pass);
            return true;
        }catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static String doUpload(String remoteDir, File file) {
        FTPHelper ftpHelper = new FTPHelper();
        ftpHelper.setIp(PropHelper.getValue("ftp.server.ip"));
        ftpHelper.setPort(PropHelper.getValue("ftp.server.port"));
        ftpHelper.setUser(PropHelper.getValue("ftp.server.user"));
        ftpHelper.setPass(PropHelper.getValue("ftp.server.pass"));
        System.out.println("Init " + ftpHelper.toString());

        String fileName = null;
        if (ftpHelper.doConnect()) {
            if (ftpHelper.doLogin()) {
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

    @Override
    public String toString() {
        return "FTPHelper{" +
                "ip='" + ip + '\'' +
                ", port='" + port + '\'' +
                ", user='" + user + '\'' +
                ", pass='" + pass + '\'' +
                '}';
    }
}