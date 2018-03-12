package us.supercheng.emall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.supercheng.emall.common.ServerResponse;
import us.supercheng.emall.dao.ProductMapper;
import us.supercheng.emall.pojo.Product;
import us.supercheng.emall.service.IProductService;
import us.supercheng.emall.vo.ProductListManageVo;
import java.util.ArrayList;
import java.util.List;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    public PageInfo manageList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> products = this.productMapper.manageList();
        List<ProductListManageVo> productListManageVos = new ArrayList<>();
        for (Product p : products) {
            productListManageVos.add(this.convertToProductListManageVo(p));
        }
        PageInfo pageInfo = new PageInfo(products);
        pageInfo.setList(productListManageVos);
        return pageInfo;
    }

    @Override
    public PageInfo manageFindProductsByNameOrId(Integer pageNum, Integer pageSize, String productName, Integer productId) {
        if (StringUtils.isNotBlank(productName)) {
            productName = "%" + productName + "%";
        }

        PageHelper.startPage(pageNum, pageSize);
        List<Product> products = this.productMapper.manageFindProductsByNameOrId(productName, productId);
        List<ProductListManageVo> productListManageVos = new ArrayList<>();
        for (Product p : products) {
            productListManageVos.add(this.convertToProductListManageVo(p));
        }
        PageInfo pageInfo = new PageInfo(products);
        pageInfo.setList(productListManageVos);
        return pageInfo;
    }

    @Override
    public Product manageDetail(Integer productId) {
        return this.productMapper.selectByPrimaryKey(productId);
    }

    @Override
    public Product findProductById(Integer productId) {
        return this.productMapper.selectByPrimaryKey(productId);
    }

    @Override
    public ServerResponse manageSetSaleStatus(Product product, Integer status) {
        product.setStatus(status);
        int count = this.productMapper.updateByPrimaryKeySelective(product);
        if (count > 0) {
            return ServerResponse.createServerResponseSuccess("Update Product ProductID: " +
                    product.getId() + " Status to: " + status);
        }
        return ServerResponse.createServerResponseError("Error Update Product");
    }


    private ProductListManageVo convertToProductListManageVo(Product product) {
        ProductListManageVo productListManageVo = new ProductListManageVo();
        productListManageVo.setId(product.getId());
        productListManageVo.setCategoryId(product.getCategoryId());
        productListManageVo.setName(product.getName());
        productListManageVo.setPrice(product.getPrice());
        productListManageVo.setMainImage(product.getMainImage());
        productListManageVo.setStatus(product.getStatus());
        productListManageVo.setSubtitle(product.getSubtitle());
        return productListManageVo;
    }
}