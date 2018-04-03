package us.supercheng.emall.common;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;

public class AppCache {
    private static final Logger logger = LoggerFactory.getLogger(AppCache.class);

    private static Cache<String, String> forgetQuestionAnswerCache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES).build();


    public static String readForgetQuestionAnswerCache (String username) {
        logger.debug("readForgetQuestionAnswerCache username: " + username);
       try {
           return forgetQuestionAnswerCache.getIfPresent(username);
       } catch (Exception ex) {
           logger.error("Error: " + ex);
           return null;
       }
    }

    public static void writeForgetQuestionAnswerCache (String username, String token) {
        logger.debug("writeForgetQuestionAnswerCache username: " + username + " token: " + token);
        forgetQuestionAnswerCache.put(username, token);
    }
}