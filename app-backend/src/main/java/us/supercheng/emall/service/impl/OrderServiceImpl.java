package us.supercheng.emall.service.impl;

import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.omg.CORBA.ORB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.supercheng.emall.common.Const;
import us.supercheng.emall.common.ServerResponse;
import us.supercheng.emall.dao.CartMapper;
import us.supercheng.emall.dao.OrderItemMapper;
import us.supercheng.emall.dao.OrderMapper;
import us.supercheng.emall.dao.ProductMapper;
import us.supercheng.emall.pojo.Cart;
import us.supercheng.emall.pojo.Order;
import us.supercheng.emall.pojo.OrderItem;
import us.supercheng.emall.pojo.Product;
import us.supercheng.emall.service.IOrderService;
import us.supercheng.emall.util.AlipayHelper;
import us.supercheng.emall.util.BigDecimalHelper;
import us.supercheng.emall.util.QRImageHelper;
import us.supercheng.emall.vo.OrderCartVo;
import us.supercheng.emall.vo.OrderItemVo;
import us.supercheng.emall.vo.OrderVo;
import java.math.BigDecimal;
import java.util.*;

@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    public ServerResponse<OrderVo> create(Integer userId, Integer shippingId) {
        List<Cart> carts = this.cartMapper.selectCheckedCartsByUserId(userId);
        if (carts.size() == 0) {
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
                // This needs to be improved to insert batch
                orderItems.add(orderItem);
                this.orderItemMapper.insert(orderItem);
            }
            OrderVo orderVo = this.transformToCartVo(order, orderItems, shippingId);
            return ServerResponse.createServerResponseSuccess(orderVo);
        } else {
            return ServerResponse.createServerResponseError("No Items to Generate Order");
        }
    }

    public ServerResponse<OrderCartVo> getOrderCart(Integer userId) {
        List<Cart> carts = this.cartMapper.selectCheckedCartsByUserId(userId);
        if (carts.size() == 0) {
            return ServerResponse.createServerResponseError("No Checked Item(s) Found in Cart");
        }

        OrderCartVo orderCartVo = this.transformToOrderCartVoFromCarts(carts);
        return ServerResponse.createServerResponseSuccess(orderCartVo);
    }

    public ServerResponse<PageInfo> list(Integer userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<OrderVo> orderVos = new ArrayList<>();
        List<Order> orders = this.orderMapper.selectOrdersByUserId(userId);
        PageInfo pageInfo = new PageInfo(orders);
        if (orders.size() == 0) {
            return ServerResponse.createServerResponseError("No Order Found for UserID: " + userId);
        }
        for(Order eachOrder : orders) {
            List<OrderItem> orderItems = this.orderItemMapper.selectByOrderNoAndUserId(eachOrder.getOrderNo(), userId);
            OrderVo orderVo = this.transformToOrderVoFromOrderItems(orderItems, eachOrder);
            orderVos.add(orderVo);
        }

        pageInfo.setList(orderVos);
        return ServerResponse.createServerResponseSuccess(pageInfo);
    }

    public ServerResponse<PageInfo> listAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orders = this.orderMapper.selectAllOrders();
        PageInfo pageInfo = new PageInfo(orders);
        List<OrderVo> orderVos = new ArrayList<>();
        for (Order order : orders) {
            List<OrderItem> orderItems = this.orderItemMapper.selectByOrderNo(order.getOrderNo());
            OrderVo orderVo = this.transformToOrderVoFromOrderItems(orderItems, order);
            orderVos.add(orderVo);
        }
        pageInfo.setList(orderVos);
        return ServerResponse.createServerResponseSuccess(pageInfo);
    }

    public ServerResponse<OrderVo> detail(Long orderNo, Integer userId) {
        Order order = this.orderMapper.selectByOrderNoAndUserId(orderNo, userId);
        if (order == null) {
            return ServerResponse.createServerResponseError("Order Number: " + orderNo + " Not Found");
        }
        List<OrderItem> orderItems = this.orderItemMapper.selectByOrderNoAndUserId(orderNo, userId);
        OrderVo orderVo = this.transformToOrderVoFromOrderItems(orderItems, order);
        return ServerResponse.createServerResponseSuccess(orderVo);
    }

    public ServerResponse<OrderVo> detailAdmin(Long orderNo) {
        Order order = this.orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return ServerResponse.createServerResponseError("Order Number: " + orderNo + " Not Found");
        }
        List<OrderItem> orderItems = this.orderItemMapper.selectByOrderNo(orderNo);
        OrderVo orderVo = this.transformToOrderVoFromOrderItems(orderItems, order);
        return ServerResponse.createServerResponseSuccess(orderVo);
    }

    public ServerResponse<String> shipGoods(Long orderNo) {
        Order order = this.orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return ServerResponse.createServerResponseError("No Such Order OrderID: " + orderNo);
        }
        if (order.getStatus() == Const.PaymentSystem.OrderStatusEnum.PAID.getCode()) {
            order.setStatus(Const.PaymentSystem.OrderStatusEnum.SHIPPED.getCode());
            int count = this.orderMapper.updateByPrimaryKeySelective(order);
            if (count > 0) {
                return ServerResponse.createServerResponseSuccess("Order Shipment Success");
            } else {
                return ServerResponse.createServerResponseError("Order Shipment Fail");
            }
        } else {
            return ServerResponse.createServerResponseError("Payment for OrderID: " + orderNo + " "
                    + Const.PaymentSystem.OrderStatusEnum.UNPAID.getVal());
        }
    }

    public ServerResponse<Map> pay(Long orderNo, Integer userId) {
        Order dbOrder = this.orderMapper.selectByOrderNoAndUserId(orderNo, userId);
        if (dbOrder == null) {
            return ServerResponse.createServerResponseError("Order Number: " + orderNo + " Not Found");
        }
        List<OrderItem> dbOrderItems = this.orderItemMapper.selectByOrderNoAndUserId(dbOrder.getOrderNo(), userId);
        if (dbOrderItems.size() == 0) {
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
            return ServerResponse.createServerResponseError("No Available or not Enough Product(s) Order Number: " + orderNo);
        }

        String outTradeNo = orderNo + "";
        String subject = "xxxBrandxxxStore QR Pay";
        String totalAmount = totalPay.toString();
        // Optional if use Total Amt - Discount Amt will be Discount Amt
        String undiscountableAmount = "0";
        // If Empty then this will be APPID'S PID
        String sellerId = "";
        String operatorId = "test_operator_id";
        String storeId = "test_store_id";
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");
        String timeoutExpress = "120m";
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress).setGoodsDetailList(goodsDetails);
        //.setNotifyUrl("http://www.test-notify-url.com")//支付宝服务器主动通知商户服务器里指定的页面http路径
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
                return ServerResponse.createServerResponseSuccess(returnMap);
            case FAILED:
                //log.error("支付宝预下单失败!!!");
                return ServerResponse.createServerResponseError("Place Alipay Pre-Order Failed");
            case UNKNOWN:
                //log.error("系统异常，预下单状态未知!!!");
                return ServerResponse.createServerResponseError("Alipay System Error");
            default:
                //log.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.createServerResponseError("Unknown Alipay System Error");
        }
    }

    private OrderVo transformToCartVo(Order order, List<OrderItem> orderItems, Integer shippingId) {
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
        return orderVo;
    }

    private OrderCartVo transformToOrderCartVoFromCarts(List<Cart> carts) {
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

        return orderCartVo;
    }

    private OrderVo transformToOrderVoFromOrderItems(List<OrderItem> orderItems, Order order) {
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
        return orderVo;
    }
}