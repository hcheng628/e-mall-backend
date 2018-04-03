package us.supercheng.emall.util;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.supercheng.emall.common.Const;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class FTPHelper {
    private static final Logger logger = LoggerFactory.getLogger(FTPHelper.class);

    private FTPClient ftpClient;

    private FTPHelper () {
        this.ftpClient = new FTPClient();
    }

    private FTPClient getFtpClient() {
        return this.ftpClient;
    }

    private boolean doConnect(FTPClient ftpClient) {
        try {
            logger.debug("doConnect FTP IP: " + Const.FTP_IP + " Port: " + Const.FTP_PORT);
            ftpClient.connect(Const.FTP_IP, Const.FTP_PORT);
            return true;
        } catch (IOException ex) {
            logger.error("doConnect\r\n" + ex);
            return false;
        }
    }

    private boolean doLogin(FTPClient ftpClient) {
        try {
            logger.debug("doConnect FTP USER: " + Const.FTP_USER + " PASS: " + Const.FTP_PASS);
            ftpClient.login(Const.FTP_USER, Const.FTP_PASS);
            return true;
        }catch (IOException ex) {
            logger.error("doLogin\r\n" + ex);
            return false;
        }
    }

    public static String doUpload(String remoteDir, File file) {
        logger.debug("Enter doUpload RemoteDir: " + remoteDir + " File: " + file.getName());
        FTPHelper ftpHelper = new FTPHelper();
        String fileName = null;
        if (ftpHelper.doConnect(ftpHelper.getFtpClient())) {
            if (ftpHelper.doLogin(ftpHelper.getFtpClient())) {
                String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
                logger.info("Extension: " + extension);
                ftpHelper.getFtpClient().enterLocalPassiveMode();
                InputStream inputStream = null;
                try {
                    ftpHelper.getFtpClient().setFileType(FTP.BINARY_FILE_TYPE);
                    ftpHelper.getFtpClient().setControlEncoding(Const.FTPConst.ENCODING);
                    ftpHelper.getFtpClient().setBufferSize(Const.FTPConst.BUFFER_SIZE);
                    ftpHelper.getFtpClient().changeWorkingDirectory(remoteDir);
                    inputStream = new FileInputStream(file);
                    fileName = UUID.randomUUID().toString() + "." + extension;
                    logger.info("Filename: " + fileName);
                    ftpHelper.getFtpClient().storeFile(fileName, inputStream);
                } catch (IOException ex) {
                    logger.error("doUpload\r\n" + ex);
                    return null;
                }finally {
                   try {
                       inputStream.close();
                       if (ftpHelper.getFtpClient().isConnected()) {
                            ftpHelper.getFtpClient().logout();
                            ftpHelper.getFtpClient().disconnect();
                       }
                   } catch (IOException e) {
                       logger.error("doUpload Closing\r\n" + e);
                   }
                }
            }
        }
        return fileName;
    }
}