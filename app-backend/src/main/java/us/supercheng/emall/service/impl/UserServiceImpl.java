package us.supercheng.emall.service.impl;

import org.apache.commons.lang.StringUtils;
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
import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService{

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = this.userMapper.checkUsername(username);
        if (resultCount > 0) {
            User user = this.userMapper.selectLogin(username, MD5Helper.MD5Encode(password, Const.DEFAULT_ENCODING));
            if (user == null) {
                return ServerResponse.createServerResponse(ResponseCode.ERROR.getCode(), "Password Not Match");
            } else {
                user.setPassword(StringUtils.EMPTY);
                return ServerResponse.createServerResponseSuccess(user);
            }
        }
        return ServerResponse.createServerResponse(ResponseCode.ERROR.getCode(), "No Such Username");
    }

    @Override
    public ServerResponse<String> check_valid(String inText, String acctType) {
        int resultCount = -2;
        if (StringUtils.equals(acctType, Const.USERNAME)) {
            resultCount = this.userMapper.checkUsername(inText);
        } else if (StringUtils.equals(acctType, Const.E_MAIL)) {
            resultCount = this.userMapper.checkEmail(inText);
        } else {
            return ServerResponse.createServerResponse(ResponseCode.ERROR.getCode(),
                    "Invalid Account Type: " + acctType);
        }
        if (resultCount > 0) {
            return ServerResponse.createServerResponse(ResponseCode.ERROR.getCode(),
                    acctType + ": " + inText + " Already Exist Please Try with a Different One");
        } else {
            return ServerResponse.createServerResponse(ResponseCode.SUCCESS.getCode(),
                    "Valid " + acctType + ": " + inText);
        }
    }

    @Override
    public ServerResponse<String> register(User inUser) {
        if (this.userMapper.checkUsername(inUser.getUsername()) > 0) {
            return ServerResponse.createServerResponse(ResponseCode.ERROR.getCode(),
                    "Username: " + inUser.getUsername() + " Already Exist Please Try with a Different One");        }
        if (this.userMapper.checkEmail(inUser.getEmail()) > 0) {
            return ServerResponse.createServerResponse(ResponseCode.ERROR.getCode(),
                    "E-mail: " + inUser.getEmail() + " Already Exist Please Try with a Different One");
        }
        inUser.setPassword(MD5Helper.MD5Encode(inUser.getPassword(), Const.DEFAULT_ENCODING));
        inUser.setRole(Const.ROLE_CUSTOMER);
        if (this.userMapper.insert(inUser) > 0) {
            return ServerResponse.createServerResponseSuccess("Account Register Success");
        }
        return ServerResponse.createServerResponse(ResponseCode.ERROR.getCode(), "Account Register Fail");
    }

    @Override
    public ServerResponse<String> getUserQuestion(String username) {
        int resultCount = this.userMapper.checkUsername(username);
        if (resultCount > 0) {
            return ServerResponse.createServerResponseSuccess(this.userMapper.selectUserQuestion(username));
        }
        return ServerResponse.createServerResponseError("No Such Username: " + username + " Found");
    }

    @Override
    public ServerResponse<String> checkQuestionAnswer(String username, String question, String answer) {
        int resultCount = this.userMapper.checkUsername(username);
        if (resultCount > 0) {
            resultCount = this.userMapper.checkUserAnswer(username, question, answer);
            if (resultCount > 0) {
                String token = UUID.randomUUID().toString().toUpperCase();
                AppCache.writeForgetQuestionAnswerCache(username, token);
                return ServerResponse.createServerResponseSuccess(token);
            }
            return ServerResponse.createServerResponseError("Incorrect Answer: " + answer + " for " + question);
        }
        return ServerResponse.createServerResponseError("No Such Username: " + username + " Found");
    }

    @Override
    public ServerResponse<String> resetPassword(String username, String passwordNew, String forgetToken) {
        if(this.userMapper.checkUsername(username) > 0) {
            if (StringUtils.equals(AppCache.readForgetQuestionAnswerCache(username), forgetToken)) {
                this.userMapper.updatePasswordByUsername(username, MD5Helper.MD5Encode(passwordNew, Const.DEFAULT_ENCODING));
                return ServerResponse.createServerResponseSuccess("Reset Password Success");
            }
            return ServerResponse.createServerResponseError("Invalid Reset Password Token");
        }
        return ServerResponse.createServerResponseError("No Such Username: " + username + " Found");
    }

    @Override
    public ServerResponse<String> resetPassword(Integer id, String newPassword) {
        User user = new User();
        user.setId(id);
        user.setPassword(MD5Helper.MD5Encode(newPassword, Const.DEFAULT_ENCODING));
        this.userMapper.updateByPrimaryKeySelective(user);
        return ServerResponse.createServerResponseSuccess("Reset Password Success");
    }

    @Override
    public ServerResponse<User> updateInfo(Integer id, String email, String phone, String question, String answer) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setPhone(phone);
        user.setQuestion(question);
        user.setAnswer(answer);
        this.userMapper.updateByPrimaryKeySelective(user);
        return ServerResponse.createServerResponseSuccess(user);
    }
}