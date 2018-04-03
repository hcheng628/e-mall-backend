package us.supercheng.emall.util;

import com.alipay.api.AlipayResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.service.AlipayMonitorService;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayMonitorServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeWithHBServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import us.supercheng.emall.common.Const;

@Configuration
public class AlipayHelper {
    private static final Logger logger = LoggerFactory.getLogger(AlipayHelper.class);

    // 支付宝当面付2.0服务
    public static AlipayTradeService TRADE_SERVICE;

    // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
    public static AlipayTradeService   TRADE_SERVICE_HB;

    // 支付宝交易保障接口服务，供测试接口api使用，请先阅读readme.txt
    public static AlipayMonitorService MONITOR_SERVICE;

    static {
        if (Const.APP_USE_HTTP_PROXY_FLAG) {
            System.setProperty("http.proxyHost", "10.110.17.6");
            System.setProperty("http.proxyPort", "8080");
            System.setProperty("https.proxyHost", "10.110.17.6");
            System.setProperty("https.proxyPort", "8080");
        }

        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        TRADE_SERVICE = new AlipayTradeServiceImpl.ClientBuilder().build();

        // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
        TRADE_SERVICE_HB = new AlipayTradeWithHBServiceImpl.ClientBuilder().build();

        /** 如果需要在程序中覆盖Configs提供的默认参数, 可以使用ClientBuilder类的setXXX方法修改默认参数 否则使用代码中的默认设置 */
        MONITOR_SERVICE = new AlipayMonitorServiceImpl.ClientBuilder()
                .setGatewayUrl("http://mcloudmonitor.com/gateway.do")
                .setCharset("GBK")
                .setFormat("json").build();
    }

    public static void dumpResponse(AlipayResponse response) {
        if (response != null) {
            logger.debug(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                logger.debug(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            logger.debug("body:" + response.getBody());
        }
    }

}