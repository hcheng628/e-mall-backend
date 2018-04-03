package us.supercheng.emall.service.impl;

import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.supercheng.emall.common.Const;
import us.supercheng.emall.common.ServerResponse;
import us.supercheng.emall.dao.*;
import us.supercheng.emall.pojo.*;
import us.supercheng.emall.service.IOrderService;
import us.supercheng.emall.util.*;
import us.supercheng.emall.vo.OrderCartVo;
import us.supercheng.emall.vo.OrderItemVo;
import us.supercheng.emall.vo.OrderVo;
import java.math.BigDecimal;
import java.util.*;

@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private PayInfoMapper payInfoMapper;

    @Autowired
    private ShippingMapper shippingMapper;

    public ServerResponse<OrderVo> create(Integer userId, Integer shippingId) {
        logger.info("Enter create userId: " + userId + " shippingId: " + shippingId);
        List<Cart> carts = this.cartMapper.selectCheckedCartsByUserId(userId);
        String ids = "";
        if (carts.size() == 0) {
            logger.info("Exit create --- cart size: " + carts.size());
            return ServerResponse.createServerResponseError("No Checked Item(s) Found in Cart");
        }
        List<Cart> alipayCarts = new ArrayList<>();
        Map<Integer, Product> products = new HashMap<>();
        BigDecimal totalPay = new BigDecimal("0.0");
        for (Cart cart : carts) {
            Product p = this.productMapper.selectByPrimaryKey(cart.getProductId());
            if (p.getStatus() == Const.ProductConst.PRODUCT_STATUS_1) {
                if (p.getStock() >= cart.getQuantity()) {
                    alipayCarts.add(cart);
                    totalPay = BigDecimalHelper.add(totalPay.doubleValue(), BigDecimalHelper.mul(cart.getQuantity(),
                            p.getPrice().doubleValue()).doubleValue());
                    products.put(p.getId(), p);
                }
            }
            ids += cart.getId() + ",";
        }
        if(carts.size() > 1) {
            ids = ids.substring(0, ids.length()-1);
        }
        if (alipayCarts.size() > 0) {
            String outTradeNo = System.currentTimeMillis() + (long) (Math.random() * 10000000L) + "";
            String totalAmount = totalPay.toString();
            Order order = new Order();
            order.setOrderNo(Long.parseLong(outTradeNo));
            order.setUserId(userId);
            order.setShippingId(shippingId);
            order.setPayment(new BigDecimal(totalAmount));
            order.setPaymentType(Const.PaymentSystem.ALIPAY);
            order.setStatus(Const.PaymentSystem.OrderStatusEnum.UNPAID.getCode());
            this.orderMapper.insertSelective(order);
            List<OrderItem> orderItems = new ArrayList<>();
            for (Cart each : alipayCarts) {
                OrderItem orderItem = new OrderItem();
                orderItem.setUserId(userId);
                orderItem.setOrderNo(Long.parseLong(outTradeNo));
                orderItem.setProductId(each.getProductId());
                orderItem.setProductName(products.get(each.getProductId()).getName());
                orderItem.setProductImage(products.get(each.getProductId()).getMainImage());
                orderItem.setCurrentUnitPrice(products.get(each.getProductId()).getPrice());
                orderItem.setQuantity(each.getQuantity());
                orderItem.setTotalPrice(BigDecimalHelper.mul(orderItem.getCurrentUnitPrice().doubleValue(),
                        orderItem.getQuantity()));
                orderItem.setCreateTime(new Date());
                orderItems.add(orderItem);
            }
            int count = this.orderItemMapper.insertBatch(orderItems);
            if (count != orderItems.size()) {
                logger.info("Exit create --- orderItems size: " + orderItems.size());
                return ServerResponse.createServerResponseError("Processing Order Item(s) Failed");
            }
            OrderVo orderVo = this.transformToCartVo(order, orderItems, shippingId);

            if (this.cartMapper.deleteBatch(ids) == carts.size()) {
                System.err.println("Could not Clear Cart after Placing Order");
            }
            logger.info("Exit create");
            return ServerResponse.createServerResponseSuccess(orderVo);
        } else {
            logger.info("Exit create --- No Items to Generate Order");
            return ServerResponse.createServerResponseError("No Items to Generate Order");
        }
    }

    public ServerResponse<Boolean> cancel(Long orderNo, Integer userId) {
        Order order = this.orderMapper.selectByOrderNoAndUserId(orderNo, userId);
        if (order == null) {
            logger.info("Exit cancel Order Number: " + orderNo + " Not Found");
            return ServerResponse.createServerResponseError("Order Number: " + orderNo + " Not Found");
        }
        if (order.getStatus() != Const.PaymentSystem.OrderStatusEnum.UNPAID.getCode()) {
            logger.info("Exit cancel --- Cannot Cancel Order at" +
                    Const.PaymentSystem.OrderStatusEnum.codeOf(order.getStatus()).getVal() + " Stage.");
            return ServerResponse.createServerResponseError("Cannot Cancel Order at " +
                    Const.PaymentSystem.OrderStatusEnum.codeOf(order.getStatus()).getVal() + " Stage.");
        }
        order.setStatus(Const.PaymentSystem.OrderStatusEnum.CANCELED.getCode());
        int count = this.orderMapper.updateByPrimaryKeySelective(order);
        if (count > 0) {
            logger.info("Exit cancel count: " + count);
            return ServerResponse.createServerResponseSuccess(true);
        }
        logger.info("Exit cancel --- Fail to Cancel Order OrderID:" + orderNo);
        return ServerResponse.createServerResponseError("Fail to Cancel Order OrderID: " + orderNo);
    }

    public ServerResponse<OrderCartVo> getOrderCart(Integer userId) {
        logger.info("Enter getOrderCart userId: " + userId);
        List<Cart> carts = this.cartMapper.selectCheckedCartsByUserId(userId);
        if (carts.size() == 0) {
            logger.info("Exit getOrderCart carts size: " + carts.size());
            return ServerResponse.createServerResponseError("No Checked Item(s) Found in Cart");
        }

        OrderCartVo orderCartVo = this.transformToOrderCartVoFromCarts(carts);
        logger.info("Exit getOrderCart");
        return ServerResponse.createServerResponseSuccess(orderCartVo);
    }

    public ServerResponse<PageInfo> list(Integer userId, Integer pageNum, Integer pageSize) {
        logger.info("Enter list userId: " + userId + " pageNum: " + pageNum + " pageSize: " + pageSize);
        PageHelper.startPage(pageNum, pageSize);
        List<OrderVo> orderVos = new ArrayList<>();
        List<Order> orders = this.orderMapper.selectOrdersByUserId(userId);
        PageInfo pageInfo = new PageInfo(orders);
        if (orders.size() == 0) {
            logger.info("Exit list orders size: " + orders.size());
            return ServerResponse.createServerResponseError("No Order Found for UserID: " + userId);
        }
        for (Order eachOrder : orders) {
            List<OrderItem> orderItems = this.orderItemMapper.selectByOrderNoAndUserId(eachOrder.getOrderNo(), userId);
            OrderVo orderVo = this.transformToOrderVoFromOrderItems(orderItems, eachOrder, false);
            orderVos.add(orderVo);
        }
        pageInfo.setList(orderVos);
        logger.info("Exit list");
        return ServerResponse.createServerResponseSuccess(pageInfo);
    }

    public ServerResponse<PageInfo> listAdmin(Integer pageNum, Integer pageSize) {
        logger.info("Exit listAdmin pageNum: " + pageNum + " pageSize: " + pageSize);
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orders = this.orderMapper.selectAllOrders();
        PageInfo pageInfo = new PageInfo(orders);
        List<OrderVo> orderVos = new ArrayList<>();
        for (Order order : orders) {
            List<OrderItem> orderItems = this.orderItemMapper.selectByOrderNo(order.getOrderNo());
            OrderVo orderVo = this.transformToOrderVoFromOrderItems(orderItems, order, false);
            orderVos.add(orderVo);
        }
        pageInfo.setList(orderVos);
        logger.info("Exit listAdmin");
        return ServerResponse.createServerResponseSuccess(pageInfo);
    }

    public ServerResponse<OrderVo> detail(Long orderNo, Integer userId) {
        logger.info("Exit detail orderNo: " + orderNo + " userId: " + userId);
        Order order;
        List<OrderItem> orderItems;
        if (userId != null) {
            order = this.orderMapper.selectByOrderNoAndUserId(orderNo, userId);
            if (order == null) {
                logger.info("Exit detail detail is null ");
                return ServerResponse.createServerResponseError("Order Number: " + orderNo + " Not Found");
            }
            orderItems = this.orderItemMapper.selectByOrderNoAndUserId(orderNo, userId);
        } else {
            order = this.orderMapper.selectByOrderNo(orderNo);
            if (order == null) {
                logger.info("Exit detail detail is null ");
                return ServerResponse.createServerResponseError("Order Number: " + orderNo + " Not Found");
            }
            orderItems = this.orderItemMapper.selectByOrderNo(orderNo);
        }
        OrderVo orderVo = this.transformToOrderVoFromOrderItems(orderItems, order, true);
        logger.info("Exit detail");
        return ServerResponse.createServerResponseSuccess(orderVo);
    }

    public ServerResponse<OrderVo> detailAdmin(Long orderNo) {
        logger.info("Enter detailAdmin orderNo: " + orderNo);
        logger.info("Exit detailAdmin");
        return this.detail(orderNo, null);
    }

    public ServerResponse<PageInfo> searchAdmin(Long orderNo, Integer pageNum, Integer pageSize) {
        logger.info("Enter searchAdmin orderNo: " + orderNo + " pageNum: " + pageNum + " pageSize: " + pageSize);
        PageHelper.startPage(pageNum, pageSize);
        Order order = this.orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            PageInfo pageInfo = new PageInfo();
            logger.info("Exit searchAdmin order is null");
            return ServerResponse.createServerResponseSuccess(pageInfo);
        }
        // for now only has search by orderNo, when this returns a list this will not be a problem
        List<OrderVo> orderVos = new ArrayList<>();
        List<OrderItem> orderItems = this.orderItemMapper.selectByOrderNo(orderNo);
        OrderVo orderVo = this.transformToOrderVoFromOrderItems(orderItems, order, false);
        orderVos.add(orderVo);
        PageInfo pageInfo = new PageInfo(orderVos);
        logger.info("Exit searchAdmin order is null");
        return ServerResponse.createServerResponseSuccess(pageInfo);
    }

    public ServerResponse<String> shipGoods(Long orderNo) {
        logger.info("Enter shipGoods orderNo: " + orderNo);
        Order order = this.orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            logger.info("Exit shipGoods order is null");
            return ServerResponse.createServerResponseError("No Such Order OrderID: " + orderNo);
        }
        if (order.getStatus() == Const.PaymentSystem.OrderStatusEnum.PAID.getCode()) {
            order.setStatus(Const.PaymentSystem.OrderStatusEnum.SHIPPED.getCode());
            int count = this.orderMapper.updateByPrimaryKeySelective(order);
            if (count > 0) {
                logger.info("Exit shipGoods count: " + count);
                return ServerResponse.createServerResponseSuccess("Order Shipment Success");
            } else {
                logger.info("Exit shipGoods count: " + count);
                return ServerResponse.createServerResponseError("Order Shipment Fail");
            }
        } else {
            logger.info("Exit shipGoods Payment for OrderID: " + orderNo + " "
                    + Const.PaymentSystem.OrderStatusEnum.UNPAID.getVal());
            return ServerResponse.createServerResponseError("Payment for OrderID: " + orderNo + " "
                    + Const.PaymentSystem.OrderStatusEnum.UNPAID.getVal());
        }
    }

    public ServerResponse<Map> pay(Long orderNo, Integer userId) {
        logger.info("Enter pay orderNo: " + orderNo + " userId: " + userId);
        Order dbOrder = this.orderMapper.selectByOrderNoAndUserId(orderNo, userId);
        if (dbOrder == null) {
            logger.info("Exit pay dbOrder is null");
            return ServerResponse.createServerResponseError("Order Number: " + orderNo + " Not Found");
        }
        List<OrderItem> dbOrderItems = this.orderItemMapper.selectByOrderNoAndUserId(dbOrder.getOrderNo(), userId);
        if (dbOrderItems.size() == 0) {
            logger.info("Exit pay dbOrderItems size: " + dbOrderItems.size());
            return ServerResponse.createServerResponseError("No Order Item(s) in Order Number: " + orderNo);
        }
        List<GoodsDetail> goodsDetails = new ArrayList<>();
        BigDecimal totalPay = new BigDecimal("0.0");
        String body = "";
        for (OrderItem eachItem : dbOrderItems) {
            Product p = this.productMapper.selectByPrimaryKey(eachItem.getProductId());
            if (p.getStatus() == Const.ProductConst.PRODUCT_STATUS_1) {
                if (p.getStock() >= eachItem.getQuantity()) {
                    totalPay = BigDecimalHelper.add(totalPay.doubleValue(), BigDecimalHelper.mul(eachItem.getQuantity(),
                            p.getPrice().doubleValue()).doubleValue());
                    body += p.getName() + " " + eachItem.getQuantity() + ";";
                    GoodsDetail good = GoodsDetail.newInstance(eachItem.getProductId() + "", p.getName(),
                            p.getPrice().longValue(), eachItem.getQuantity());
                    goodsDetails.add(good);
                }
            }
        }
        if (goodsDetails.size() == 0) {
            logger.info("Exit pay goodsDetails size: " + goodsDetails.size());
            return ServerResponse.createServerResponseError("No Available or not Enough Product(s) Order Number: " + orderNo);
        }
        String outTradeNo = orderNo + "";
        String subject = Const.APP_STORE_NAME;
        String totalAmount = totalPay.toString();
        String undiscountableAmount = "0";
        String operatorId = Const.APP_STORE_OPERATOR_ID;
        String storeId = Const.APP_STORE_ID;
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId(Const.PaymentSystem.AlipayConst.ALIBABA_SYS_PROVIDER_ID);
        String timeoutExpress = Const.PaymentSystem.AlipayConst.ALIBABA_CALL_TIMEOUT;
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(Const.PaymentSystem.AlipayConst.SELLER_ID).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress).setGoodsDetailList(goodsDetails)
                .setNotifyUrl(Const.PaymentSystem.AlipayConst.ALIBABA_CALLBACK_DOMAIN +
                        Const.PaymentSystem.AlipayConst.ALIBABA_CALLBACK_URI); //支付宝服务器主动通知商户服务器里指定的页面http路径
        AlipayF2FPrecreateResult result = AlipayHelper.TRADE_SERVICE.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                //log.info("支付宝预下单成功: )");
                AlipayTradePrecreateResponse response = result.getResponse();
                AlipayHelper.dumpResponse(response);
                String qrBase64Str = QRImageHelper.getQRCodeImage256(response.getQrCode());
                if (qrBase64Str == null) {
                    ServerResponse.createServerResponseError("Generate Alipay QR Code Fail");
                }
                Map returnMap = new HashMap<>();
                returnMap.put("orderNo", response.getOutTradeNo());
                returnMap.put("qrBase64", qrBase64Str);
                logger.info("Exit pay");
                return ServerResponse.createServerResponseSuccess(returnMap);
            case FAILED:
                logger.error("Exit pay --- Alipay Placing Order Fail");
                return ServerResponse.createServerResponseError("Place Alipay Pre-Order Failed");
            case UNKNOWN:
                logger.error("Exit pay --- Alipay Order Unknown Status");
                return ServerResponse.createServerResponseError("Alipay System Error");
            default:
                logger.error("Exit pay --- Alipay Unknown System Error");
                return ServerResponse.createServerResponseError("Unknown Alipay System Error");
        }
    }

    public String alipayCallback(Map<String, String> params) {
        logger.info("Enter alipayCallback");
        boolean senderVerifyFlag = false;
        try {
            senderVerifyFlag = AlipaySignature.rsaCheckV1(params,
                    PropHelper.getValue(Const.ALIPAY_PROP_FILE, Const.PaymentSystem.AlipayConst.ALIBABA_PUBLIC_KEY),
                    Const.APP_DEFAULT_ENCODING, Const.PaymentSystem.AlipayConst.ALIBABA_RSA2);
        } catch (Exception ex) {
            logger.error("Alipay Signature RSA2 Check Fail");
        }
        if (!senderVerifyFlag) {
            logger.error("Exit alipayCallback senderVerifyFlag: " + senderVerifyFlag);
            return Const.PaymentSystem.AlipayConst.ALIBABA_CALLBACK_FAIL;
        }
        logger.info("Alipay Callback --- Sender Verification Success");
        Long tradeNo = Long.parseLong(params.get(Const.PaymentSystem.AlipayConst.OUT_TRADE_NO));
        if (tradeNo == null) {
            logger.info("Exit alipayCallback --- tradeNo is null");
            return Const.PaymentSystem.AlipayConst.ALIBABA_CALLBACK_FAIL;
        }
        logger.info("Trade Number: " + tradeNo);
        Order order = this.orderMapper.selectByOrderNo(tradeNo);
        if (order == null) {
            logger.error("Exit alipayCallback --- DB Order: Null");
            return Const.PaymentSystem.AlipayConst.ALIBABA_CALLBACK_FAIL;
        }
        logger.info("DB Order: " + order.getId());
        if (order.getStatus() >= Const.PaymentSystem.OrderStatusEnum.PAID.getCode()) {
            logger.error("Exit alipayCallback --- Order Already has been Paid");
            return Const.PaymentSystem.AlipayConst.ALIBABA_CALLBACK_FAIL;
        }
        logger.info("Order Waiting for Pay: " + order.getStatus());
        BigDecimal totalPrice = order.getPayment();
        BigDecimal alipayTotal = new BigDecimal(params.get(Const.PaymentSystem.AlipayConst.TOTAL_AMT));
        if (BigDecimalHelper.sub(totalPrice.doubleValue(), alipayTotal.doubleValue()).intValue() != 0) {
            logger.info("Exit alipayCallback User Payment: " + alipayTotal + " does not Equal to System Payment: " + totalPrice);
            return Const.PaymentSystem.AlipayConst.ALIBABA_CALLBACK_FAIL;
        }
        logger.info("User Payment does Equal to System Payment.");
        String tradeStatus = params.get(Const.PaymentSystem.AlipayConst.TRADE_STATUS);
        logger.info("Trade Status: " + tradeStatus);
        if(tradeStatus.equalsIgnoreCase(Const.PaymentSystem.AlipayConst.TRADE_SUCCESS)) {
            logger.info("Update Order: " + order.getOrderNo());
            order.setStatus(Const.PaymentSystem.OrderStatusEnum.PAID.getCode());
            logger.info("set Order PaymentTime Str: " + params.get(Const.PaymentSystem.AlipayConst.GMT_PAYMENT));
            logger.info("set Order PaymentTime Date: " + DateTimeHelper.toAppDateTime(params.get(Const.PaymentSystem.AlipayConst.GMT_PAYMENT)));
            order.setPaymentTime(DateTimeHelper.toAppDateTime(params.get(Const.PaymentSystem.AlipayConst.GMT_PAYMENT)));
            int count = this.orderMapper.updateByPrimaryKeySelective(order);
            logger.info("Update Order: " + count);
            if (count <= 0) {
                System.err.println("Update Order OrderID: " + order.getId() + "to TRADE_SUCCESS Fail");
                logger.info("Exit alipayCallback count: " + count);
                return Const.PaymentSystem.AlipayConst.ALIBABA_CALLBACK_FAIL;
            }
        }
        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Const.PaymentSystem.ALIPAY);
        payInfo.setPlatformNumber(params.get(Const.PaymentSystem.AlipayConst.TRADE_NO));
        payInfo.setPlatformStatus(tradeStatus);
        payInfo.setCreateTime(new Date());
        payInfo.setUpdateTime(new Date());
        int count = this.payInfoMapper.insertSelective(payInfo);
        logger.info("Insert PayInfo: " + count);
        if (count <= 0) {
            System.err.println("Insert PayInfo to Trade Status: " + tradeStatus + " Fail");
            logger.info("Exit alipayCallback count: " + count);
            return Const.PaymentSystem.AlipayConst.ALIBABA_CALLBACK_FAIL;
        }
        logger.info("Exit alipayCallback count: " + count);
        return Const.PaymentSystem.AlipayConst.ALIBABA_CALLBACK_SUCCESS;
    }

    public ServerResponse queryOrderPayStatus(Long orderNo, Integer userId) {
        logger.info("Exit queryOrderPayStatus orderNo: " + orderNo + " userId: " + userId);
        Order order = this.orderMapper.selectByOrderNoAndUserId(orderNo, userId);
        if (order == null) {
            logger.info("Exit queryOrderPayStatus order is null");
            return ServerResponse.createServerResponseError("Order Number: " + orderNo + " Not Found");
        }
        if (order.getStatus() >= Const.PaymentSystem.OrderStatusEnum.PAID.getCode()) {
            logger.info("Exit queryOrderPayStatus Order Status is greater or equal to PAID status");
            return ServerResponse.createServerResponseSuccess("true");
        }
        logger.info("Exit queryOrderPayStatus");
        return ServerResponse.createServerResponseError("false");
    }

    private OrderVo transformToCartVo(Order order, List<OrderItem> orderItems, Integer shippingId) {
        logger.info("Enter transformToCartVo orderId: " + order.getId() + " orderItems size: " + orderItems.size() + " shippingId: " + shippingId);
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPostage(order.getPostage());
        orderVo.setStatus(order.getStatus());
        orderVo.setPaymentTime(order.getPaymentTime());
        orderVo.setSendTime(order.getSendTime());
        orderVo.setEndTime(order.getEndTime());
        orderVo.setCloseTime(order.getCloseTime());
        orderVo.setCreateTime(order.getCreateTime());
        orderVo.setOrderItemVos(new ArrayList<OrderItemVo>());
        for (OrderItem item : orderItems) {
            OrderItemVo orderItemVo = new OrderItemVo();
            orderItemVo.setOrderNo(order.getOrderNo());
            orderItemVo.setProductId(item.getProductId());
            orderItemVo.setProductName(item.getProductName());
            orderItemVo.setProductImage(item.getProductImage());
            orderItemVo.setCurrentUnitPrice(item.getCurrentUnitPrice());
            orderItemVo.setQuantity(item.getQuantity());
            orderItemVo.setTotalPrice(item.getTotalPrice());
            orderItemVo.setCreateTime(item.getCreateTime());
            orderVo.getOrderItemVos().add(orderItemVo);
        }
        orderVo.setShippingId(shippingId);
        logger.info("Exit transformToCartVo");
        return orderVo;
    }

    private OrderCartVo transformToOrderCartVoFromCarts(List<Cart> carts) {
        logger.info("Enter transformToOrderCartVoFromCarts cart size: " + carts.size());
        OrderCartVo orderCartVo = new OrderCartVo();
        BigDecimal totalPrice = new BigDecimal("0.0");
        List<OrderItemVo> orderItemVos = new ArrayList<>();
        for (Cart c : carts) {
            Product p = this.productMapper.selectByPrimaryKey(c.getProductId());
            OrderItemVo orderItemVo = new OrderItemVo();
            orderItemVo.setOrderNo(null);
            orderItemVo.setProductId(c.getProductId());
            orderItemVo.setProductName(p.getName());
            orderItemVo.setProductImage(p.getMainImage());
            orderItemVo.setCurrentUnitPrice(p.getPrice());
            orderItemVo.setQuantity(c.getQuantity());
            orderItemVo.setTotalPrice(BigDecimalHelper.mul(p.getPrice().doubleValue(), c.getQuantity()));
            orderItemVo.setCreateTime(null);
            totalPrice = BigDecimalHelper.add(totalPrice.doubleValue(), orderItemVo.getTotalPrice().doubleValue());
            orderItemVos.add(orderItemVo);
        }
        orderCartVo.setOrderItemVoList(orderItemVos);
        orderCartVo.setImageHost("xxx");
        orderCartVo.setProductTotalPrice(totalPrice);
        logger.info("Exit transformToOrderCartVoFromCarts");
        return orderCartVo;
    }

    private OrderVo transformToOrderVoFromOrderItems(List<OrderItem> orderItems, Order order, boolean hasShipping) {
        logger.info("Enter transformToOrderVoFromOrderItems orderItems size: " + orderItems.size() + " orderId: "
                + order.getId() + " hasShipping: " + hasShipping);
        OrderVo orderVo = new OrderVo();
        List<OrderItemVo> orderItemVos = new ArrayList<>();
        BigDecimal totalPrice = new BigDecimal("0.0");
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        // PaymentTypeDesc
        orderVo.setPostage(order.getPostage());
        orderVo.setStatus(order.getStatus());
        // StatusDesc
        orderVo.setPaymentTime(order.getPaymentTime());
        orderVo.setSendTime(order.getSendTime());
        orderVo.setEndTime(order.getEndTime());
        orderVo.setCloseTime(order.getCloseTime());
        orderVo.setCreateTime(order.getCreateTime());
        orderVo.setShippingId(order.getShippingId());
        for (OrderItem each : orderItems) {
            OrderItemVo orderItemVo = new OrderItemVo();
            orderItemVo.setOrderNo(each.getOrderNo());
            orderItemVo.setProductId(each.getProductId());
            orderItemVo.setProductName(each.getProductName());
            orderItemVo.setProductImage(each.getProductImage());
            orderItemVo.setCurrentUnitPrice(each.getCurrentUnitPrice());
            orderItemVo.setQuantity(each.getQuantity());
            orderItemVo.setTotalPrice(each.getTotalPrice());
            orderItemVo.setCreateTime(each.getCreateTime());
            totalPrice = BigDecimalHelper.add(totalPrice.doubleValue(), each.getTotalPrice().doubleValue());
            orderItemVos.add(orderItemVo);
        }
        orderVo.setOrderItemVos(orderItemVos);
        if (hasShipping) {
            Shipping shipping = this.shippingMapper.selectByPrimaryKey(order.getShippingId());
            orderVo.setShipping(shipping);
        }
        logger.info("Exit transformToOrderVoFromOrderItems");
        return orderVo;
    }
}