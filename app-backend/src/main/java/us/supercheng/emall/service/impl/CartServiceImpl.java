package us.supercheng.emall.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.supercheng.emall.dao.CartMapper;
import us.supercheng.emall.dao.ProductMapper;
import us.supercheng.emall.pojo.Cart;
import us.supercheng.emall.pojo.Product;
import us.supercheng.emall.vo.CartProductVo;
import us.supercheng.emall.vo.CartVo;

import java.math.BigDecimal;
import java.util.List;

@Service("iCartService")
public class CartServiceImpl {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    public CartProductVo list(Integer userId) {
        List<Cart> carts = this.cartMapper.selectCartsByUserId(userId);
        return null;
    }


    private CartVo cartToCartVo(Cart cart) {
        CartVo cartVo = new CartVo();
        cartVo.setId(cart.getId());
        cartVo.setUserId(cart.getUserId());
        cartVo.setQuantity(cart.getQuantity());
        cartVo.setQuantity(cart.getQuantity());
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
        }

        cartVo.setProductTotalPrice(new BigDecimal("50000"));
        cartVo.setLimitQuantity("??????");

        return cartVo;
    }

}