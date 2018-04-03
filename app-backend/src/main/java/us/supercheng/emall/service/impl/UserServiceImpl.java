package us.supercheng.emall.service.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.supercheng.emall.common.AppCache;
import us.supercheng.emall.common.Const;
import us.supercheng.emall.common.ResponseCode;
import us.supercheng.emall.common.ServerResponse;
import us.supercheng.emall.dao.UserMapper;
import us.supercheng.emall.pojo.User;
import us.supercheng.emall.service.IUserService;
import us.supercheng.emall.util.MD5Helper;
import javax.servlet.http.HttpSession;
import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        logger.info("Enter login username: " + username + " password: " + password);
        int resultCount = this.userMapper.checkUsername(username);
        if (resultCount > 0) {
            User user = this.userMapper.selectLogin(username, this.saltAndMD5Passwd(password));
            if (user == null) {
                logger.error("Exit login --- Password Not Match");
                return ServerResponse.createServerResponseError("Password Not Match");
            } else {
                user.setPassword(StringUtils.EMPTY);
                logger.info("Exit login");
                return ServerResponse.createServerResponseSuccess(user);
            }
        }
        logger.error("Exit login --- No Such Username");
        return ServerResponse.createServerResponseError("No Such Username");
    }

    @Override
    public ServerResponse<String> check_valid(String inText, String acctType) {
        logger.info("Enter check_valid inText: " + inText + " acctType: " + acctType);
        int resultCount = -2;
        if (StringUtils.equals(acctType, Const.USERNAME)) {
            resultCount = this.userMapper.checkUsername(inText);
        } else if (StringUtils.equals(acctType, Const.E_MAIL)) {
            resultCount = this.userMapper.checkEmail(inText);
        } else {
            logger.error("Exit check_valid --- Invalid Account Type: " + acctType);
            return ServerResponse.createServerResponseError("Invalid Account Type: " + acctType);
        }
        if (resultCount > 0) {
            logger.error("Exit check_valid --- " + acctType + ": " + inText
                    + " Already Exist Please Try with a Different One");
            return ServerResponse.createServerResponseError(acctType + ": " + inText
                    + " Already Exist Please Try with a Different One");
        } else {
            logger.info("Exit check_valid");
            return ServerResponse.createServerResponse(ResponseCode.SUCCESS.getCode(),
                    "Valid " + acctType + ": " + inText);
        }
    }

    @Override
    public ServerResponse<String> register(User inUser) {
        logger.info("Enter register userId: " + inUser.getId());
        if (this.userMapper.checkUsername(inUser.getUsername()) > 0) {
            logger.error("Exit register --- Username: " + inUser.getUsername()
                    + " Already Exist Please Try with a Different One");
            return ServerResponse.createServerResponseError("Username: " + inUser.getUsername()
                    + " Already Exist Please Try with a Different One");
        }
        if (this.userMapper.checkEmail(inUser.getEmail()) > 0) {
            logger.error("Exit register --- E-mail: " + inUser.getEmail() +
                    " Already Exist Please Try with a Different One");
            return ServerResponse.createServerResponseError("E-mail: " + inUser.getEmail() +
                    " Already Exist Please Try with a Different One");
        }
        inUser.setPassword(this.saltAndMD5Passwd(inUser.getPassword()));
        inUser.setRole(Const.ROLE_CUSTOMER);
        if (this.userMapper.insert(inUser) > 0) {
            logger.info("Exit register");
            return ServerResponse.createServerResponseSuccess("Account Register Success");
        }
        logger.error("Exit register --- Account Register Fail");
        return ServerResponse.createServerResponseError("Account Register Fail");
    }

    @Override
    public ServerResponse<String> getUserQuestion(String username) {
        logger.info("Enter getUserQuestion username: " + username);
        int resultCount = this.userMapper.checkUsername(username);
        if (resultCount > 0) {
            logger.info("Exit getUserQuestion");
            return ServerResponse.createServerResponseSuccess(this.userMapper.selectUserQuestion(username));
        }
        logger.error("Exit getUserQuestion --- No Such Username: " + username + " Found");
        return ServerResponse.createServerResponseError("No Such Username: " + username + " Found");
    }

    @Override
    public ServerResponse<String> checkQuestionAnswer(String username, String question, String answer) {
        logger.info("Enter checkQuestionAnswer username: " + username + " question: " + question + " answer: " + answer);
        int resultCount = this.userMapper.checkUsername(username);
        if (resultCount > 0) {
            resultCount = this.userMapper.checkUserAnswer(username, question, answer);
            if (resultCount > 0) {
                String token = UUID.randomUUID().toString().toUpperCase();
                AppCache.writeForgetQuestionAnswerCache(username, token);
                logger.info("Exit checkQuestionAnswer");
                return ServerResponse.createServerResponseSuccess(token);
            }
            logger.error("Exit checkQuestionAnswer --- Incorrect Answer: " + answer + " for " + question);
            return ServerResponse.createServerResponseError("Incorrect Answer: " + answer + " for " + question);
        }
        logger.error("Exit checkQuestionAnswer --- No Such Username: " + username + " Found");
        return ServerResponse.createServerResponseError("No Such Username: " + username + " Found");
    }

    @Override
    public ServerResponse<String> resetPassword(String username, String passwordNew, String forgetToken) {
        logger.info("Enter resetPassword username: " + username + " passwordNew: " + passwordNew + " forgetToken: " + forgetToken);
        if (this.userMapper.checkUsername(username) > 0) {
            if (StringUtils.equals(AppCache.readForgetQuestionAnswerCache(username), forgetToken)) {
                int count = this.userMapper.updatePasswordByUsername(username, this.saltAndMD5Passwd(passwordNew));
                if (count > 0) {
                    logger.info("Exit resetPassword");
                    return ServerResponse.createServerResponseSuccess("Reset Password Success");
                }
                logger.error("Exit resetPassword --- Reset Password Failed");
                return ServerResponse.createServerResponseError("Reset Password Failed");
            }
            logger.error("Exit resetPassword --- Invalid Reset Password Token");
            return ServerResponse.createServerResponseError("Invalid Reset Password Token");
        }
        logger.error("Exit resetPassword --- No Such Username: " + username + " Found");
        return ServerResponse.createServerResponseError("No Such Username: " + username + " Found");
    }

    @Override
    public ServerResponse<String> resetPassword(Integer id, String newPassword) {
        logger.info("Enter resetPassword id: " + id + " newPassword: " + newPassword);
        User user = new User();
        user.setId(id);
        user.setPassword(this.saltAndMD5Passwd(newPassword));
        int count = this.userMapper.updateByPrimaryKeySelective(user);
        if (count > 0) {
            logger.info("Exit resetPassword");
            return ServerResponse.createServerResponseSuccess("Reset Password Success");
        }
        logger.info("Exit resetPassword --- Reset Password Failed");
        return ServerResponse.createServerResponseError("Reset Password Failed");
    }

    @Override
    public ServerResponse<User> updateInfo(Integer id, String email, String phone, String question, String answer) {
        logger.info("Enter updateInfo id: " + id + " email: " + email + " phone: " + phone +
                " question: " + question + " answer: " + answer);
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setPhone(phone);
        user.setQuestion(question);
        user.setAnswer(answer);
        int count = this.userMapper.updateByPrimaryKeySelective(user);
        if (count > 0) {
            logger.info("Exit updateInfo");
            return ServerResponse.createServerResponseSuccess(user);
        }
        logger.info("Exit updateInfo --- Update User Info Failed");
        return ServerResponse.createServerResponseError("Update User Info Failed");
    }

    private String saltAndMD5Passwd(String passwd) {
        logger.info("Enter saltAndMD5Passwd passwd: " + passwd);
        logger.info("Exit saltAndMD5Passwd");
        return MD5Helper.MD5Encode(Const.SALT_PASSWD_PREFIX + passwd + Const.SALT_PASSWD_SUFFIX,
                Const.APP_DATETIME_FORMAT);
    }

    public User getCurrentUser(HttpSession session) {
        logger.info("Enter getCurrentUser");
        logger.info("Exit getCurrentUser");
        return (User) session.getAttribute(Const.CURRENT_USER);
    }

    @Override
    public ServerResponse checkAdminUser(User user) {
        logger.info("Enter checkAdminUser userId: " + user.getId());
        if (user == null) {
            logger.error("Exit checkAdminUser --- " + ResponseCode.LOGIN_REQUIRED.getDesc() + " Code: " + ResponseCode.LOGIN_REQUIRED.getCode());
            return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
        }
        if (user.getRole() != Const.ROLE_ADMIN) {
            logger.error("Exit checkAdminUser --- Username: " + user.getUsername() + " is not an Admin User");
            return ServerResponse.createServerResponseError("Username: " + user.getUsername() + " is not an Admin User");
        }
        logger.info("Exit checkAdminUser");
        return ServerResponse.createServerResponseSuccess("Username: " + user.getUsername() + " is an Admin");
    }
}