package us.supercheng.emall.test.common;

import org.junit.Test;
import us.supercheng.emall.common.AppCache;

public class AppCacheTest {

    @Test
    public void mainTest() throws Exception{
        String key = "hcheng";
        String value = "UUID-UUID-UUID-UUID";
        String flag = AppCache.readForgetQuestionAnswerCache(key);
        System.out.println("Before Flag: " + flag);
        AppCache.writeForgetQuestionAnswerCache(key, value);
        flag = AppCache.readForgetQuestionAnswerCache(key);
        System.out.println("After Flag: " + flag);
        Thread.sleep(3000); // Set CacheTimeout less than 3 seconds
        flag = AppCache.readForgetQuestionAnswerCache(key);
        System.out.println("After 3 Seconds Flag: " + flag);
    }
}