package us.supercheng.emall.vo;

import java.math.BigDecimal;
import java.util.List;

public class CartProductVo {
    private List<CartVo> cartVos;
    private boolean allChecked;
    private BigDecimal cartTotalPrice;

    public List<CartVo> getCartVos() {
        return cartVos;
    }

    public void setCartVos(List<CartVo> cartVos) {
        this.cartVos = cartVos;
    }

    public boolean isAllChecked() {
        return allChecked;
    }

    public void setAllChecked(boolean allChecked) {
        this.allChecked = allChecked;
    }

    public BigDecimal getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(BigDecimal cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }
}