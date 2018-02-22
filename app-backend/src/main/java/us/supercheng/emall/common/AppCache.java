package us.supercheng.emall.common;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;

public class AppCache {

    private static Cache<String, String> forgetQuestionAnswerCache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES).build();


    public static String readForgetQuestionAnswerCache (String username) {
       try {
           return forgetQuestionAnswerCache.getIfPresent(username);
       } catch (Exception ex) {
           return null;
       }
    }

    public static void writeForgetQuestionAnswerCache (String username, String token) {
        forgetQuestionAnswerCache.put(username, token);
    }
}