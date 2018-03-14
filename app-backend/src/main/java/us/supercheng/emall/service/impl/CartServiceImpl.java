package us.supercheng.emall.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.supercheng.emall.common.Const;
import us.supercheng.emall.dao.CartMapper;
import us.supercheng.emall.dao.ProductMapper;
import us.supercheng.emall.pojo.Cart;
import us.supercheng.emall.pojo.Product;
import us.supercheng.emall.service.ICartService;
import us.supercheng.emall.util.BigDecimalHelper;
import us.supercheng.emall.vo.CartProductVo;
import us.supercheng.emall.vo.CartVo;

import java.math.BigDecimal;
import java.util.*;

@Service("iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    public CartProductVo list(Integer userId) {
        List<Cart> carts = this.cartMapper.selectCartsByUserId(userId);
        List<CartVo> cartVos = new ArrayList<>();
        for (Cart c : carts) {
            cartVos.add(this.cartToCartVo(c));
        }
        CartProductVo cartProductVo = new CartProductVo();
        cartProductVo.setAllChecked(true);              // default to true
        BigDecimal totalPrice = new BigDecimal("0");
        cartProductVo.setCartVos(cartVos);
        for (CartVo c : cartVos) {
            if (c.getProductChecked() == Const.CartConst.PRODUCT_UNCHECKED) {
                cartProductVo.setAllChecked(false);
            } else {
                totalPrice = BigDecimalHelper.add(totalPrice.doubleValue(), c.getProductTotalPrice().doubleValue());
            }
        }
        cartProductVo.setCartTotalPrice(totalPrice);
        return cartProductVo;
    }

    @Override
    public Map upsert(Integer productId, Integer count, Integer userId) {
        Cart cart = new Cart();
        Map<String, String> returnMap = new HashMap<>();
        Product p = this.productMapper.selectByPrimaryKey(productId);
        if (p != null) {
            cart.setChecked(1);
            cart.setUserId(userId);
            cart.setProductId(p.getId());
            cart.setCreateTime(new Date());
            cart.setUpdateTime(new Date());
            if (p.getStock() < count) {
                cart.setQuantity(p.getStock());
                returnMap.put(Const.CartConst.LIMIT_NUM_SUCCESS, Const.CartConst.LIMIT_NUM_FAIL);
                returnMap.put("count", "1");
            } else {
                cart.setQuantity(count);
                returnMap.put(Const.CartConst.LIMIT_NUM_SUCCESS, Const.CartConst.LIMIT_NUM_SUCCESS);
                returnMap.put("count", "1");
            }
            int countDb = -1;
            Cart existCart = this.cartMapper.selectByProductIdAndUserId(productId, userId);
            if (existCart == null) {
                countDb = this.cartMapper.insertSelective(cart);
            } else {
                cart.setId(existCart.getId());
                countDb = this.cartMapper.updateByPrimaryKeySelective(cart);
            }
            if (countDb > 0) {
                returnMap.put("countDb", countDb + "");
            } else {
                returnMap.put("countDb", "0");
            }
        } else {
            returnMap.put("count", "0");
        }
        return returnMap;
    }

    @Override
    public int delete(String productIds) {
        if (productIds != null) {
            productIds = productIds.replaceAll("[^0-9,]", "");
        }
        return this.cartMapper.deleteCartsByProductIds(productIds);
    }

    private CartVo cartToCartVo(Cart cart) {
        CartVo cartVo = new CartVo();
        cartVo.setId(cart.getId());
        cartVo.setUserId(cart.getUserId());
        cartVo.setProductChecked(cart.getChecked());
        cartVo.setProductId(cart.getProductId());
        Product p = this.productMapper.selectByPrimaryKey(cart.getProductId());
        if (p !=  null) {
            cartVo.setProductName(p.getName());
            cartVo.setProductSubtitle(p.getSubtitle());
            cartVo.setProductMainImage(p.getMainImage());
            cartVo.setProductPrice(p.getPrice());
            cartVo.setProductStatus(p.getStatus());
            cartVo.setProductStock(p.getStock());
            if (cart.getQuantity() >= p.getStock()) {
                cartVo.setQuantity(cart.getQuantity());
                cartVo.setLimitQuantity(Const.CartConst.LIMIT_NUM_SUCCESS);
            } else {
                cartVo.setQuantity(p.getStatus());
                cartVo.setLimitQuantity(Const.CartConst.LIMIT_NUM_FAIL);
            }
        }
        cartVo.setProductTotalPrice(BigDecimalHelper.mul(cartVo.getProductPrice().doubleValue(), cartVo.getQuantity()));
        return cartVo;
    }
}