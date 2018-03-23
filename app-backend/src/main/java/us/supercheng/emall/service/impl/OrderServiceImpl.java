package us.supercheng.emall.service.impl;

import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.utils.ZxingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.supercheng.emall.common.Const;
import us.supercheng.emall.common.ServerResponse;
import us.supercheng.emall.dao.CartMapper;
import us.supercheng.emall.dao.OrderMapper;
import us.supercheng.emall.dao.ProductMapper;
import us.supercheng.emall.pojo.Cart;
import us.supercheng.emall.pojo.Product;
import us.supercheng.emall.service.IOrderService;
import us.supercheng.emall.util.AlipayHelper;
import us.supercheng.emall.util.BigDecimalHelper;
import us.supercheng.emall.util.QRImageHelper;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    public ServerResponse<Map> create(Integer userId, Integer shippingId, HttpServletRequest request) {

        List<Cart> carts = cartMapper.selectCheckedCartsByUserId(userId);
        if (carts.size() == 0) {
            return ServerResponse.createServerResponseError("No Checked Item(s) Found in Cart");
        }

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<>();
        List<Cart> alipayCarts = new ArrayList<>();
        BigDecimal totalPay = new BigDecimal("0.0");
        String body = "";

        for (Cart cart  : carts) {
            Product p = productMapper.selectByPrimaryKey(cart.getProductId());
            if (p.getStatus() == Const.ProductConst.PRODUCT_STATUS_1) {
                if (p.getStock() >= cart.getQuantity()) {
                    alipayCarts.add(cart);
                    totalPay = BigDecimalHelper.add(totalPay.doubleValue(), BigDecimalHelper.mul(cart.getQuantity(),
                            p.getPrice().doubleValue()).doubleValue());
                    body += p.getName() + " " + cart.getQuantity() + ";";
                    // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
                    GoodsDetail good = GoodsDetail.newInstance(cart.getProductId() + "", p.getName(),
                            p.getPrice().longValue(), cart.getQuantity());
                    goodsDetailList.add(good);
                }
            }
        }

        if(alipayCarts.size() > 0) {
            // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
            // 需保证商户系统端不能重复，建议通过数据库sequence生成，
            String outTradeNo = System.currentTimeMillis() + (long) (Math.random() * 10000000L) + "";

            // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
            String subject = "xxx品牌xxx门店当面付扫码消费";
            // (必填) 订单总金额，单位为元，不能超过1亿元
            // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
            String totalAmount = totalPay.toString();

            // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
            // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
            String undiscountableAmount = "0";

            // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
            // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
            String sellerId = "";

            // 商户操作员编号，添加此参数可以为商户操作员做销售统计
            String operatorId = "test_operator_id";

            // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
            String storeId = "test_store_id";

            // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
            ExtendParams extendParams = new ExtendParams();
            extendParams.setSysServiceProviderId("2088100200300400500");
            // 支付超时，定义为120分钟
            String timeoutExpress = "120m";

            // 创建扫码支付请求builder，设置请求参数
            AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                    .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                    .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                    .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                    .setTimeoutExpress(timeoutExpress)
                    //                .setNotifyUrl("http://www.test-notify-url.com")//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                    .setGoodsDetailList(goodsDetailList);

            AlipayF2FPrecreateResult result = AlipayHelper.TRADE_SERVICE.tradePrecreate(builder);
            switch (result.getTradeStatus()) {
                case SUCCESS:
                    //log.info("支付宝预下单成功: )");
                    AlipayTradePrecreateResponse response = result.getResponse();
                    AlipayHelper.dumpResponse(response);
                    // 需要修改为运行机器上的路径
                    String qrFilePath = request.getSession().getServletContext().getRealPath("/") + Const.APP_QR_PATH;
                    File file = new File(qrFilePath);
                    if (!file.exists()) {
                        file.mkdirs();
                    }

                    String filePath = qrFilePath + String.format("/qr-%s.png", response.getOutTradeNo());
                    //log.info("filePath:" + filePath);
                    File qrImageFile = ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);
                    String qrBase64Str = "";
                    try {
                        qrBase64Str = QRImageHelper.qRImageToBase64(qrImageFile);
                    } catch (IOException ex) {
                        return ServerResponse.createServerResponseError("Create Alipay QR Code Failed");
                    } finally {
                        QRImageHelper.deleteQRImage(qrImageFile);
                    }


                    Map returnMap = new HashMap<>();
                    returnMap.put("code", response.getCode());
                    returnMap.put("msg", response.getMsg());
                    returnMap.put("out_trade_no", response.getOutTradeNo());
                    returnMap.put("qr_code", qrBase64Str);

                    return ServerResponse.createServerResponseSuccess(returnMap);
                    // break;
                case FAILED:
                    return ServerResponse.createServerResponseError("Place Alipay Pre-Order Failed");
                    //log.error("支付宝预下单失败!!!");
                    //break;
                case UNKNOWN:
                    //log.error("系统异常，预下单状态未知!!!");
                    //break;
                    return ServerResponse.createServerResponseError("Alipay System Error");
                default:
                    //log.error("不支持的交易状态，交易返回异常!!!");
                    //break;
                    return ServerResponse.createServerResponseError("Unknown Alipay System Error");
            }
        } else {

        }
        return null;
    }

}