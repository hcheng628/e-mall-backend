package us.supercheng.emall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.supercheng.emall.common.Const;
import us.supercheng.emall.common.ServerResponse;
import us.supercheng.emall.dao.ProductMapper;
import us.supercheng.emall.pojo.Product;
import us.supercheng.emall.service.IProductService;
import us.supercheng.emall.vo.ProductListManageVo;
import java.util.ArrayList;
import java.util.List;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductMapper productMapper;

    public PageInfo manageList(Integer pageNum, Integer pageSize) {
        logger.info("Enter manageList pageNum: " + pageNum + " pageSize: " + pageSize);
        PageHelper.startPage(pageNum, pageSize);
        List<Product> products = this.productMapper.manageList();
        List<ProductListManageVo> productListManageVos = new ArrayList<>();
        for (Product p : products) {
            productListManageVos.add(this.convertToProductListManageVo(p));
        }
        PageInfo pageInfo = new PageInfo(products);
        pageInfo.setList(productListManageVos);
        logger.info("Exit manageList");
        return pageInfo;
    }

    @Override
    public PageInfo manageFindProductsByNameOrId(Integer pageNum, Integer pageSize, String productName, Integer productId) {
        logger.info("Enter manageFindProductsByNameOrId pageNum: " + pageNum + " pageSize: " + pageSize +
        " productName: " + productName + " productId: " + productId);
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
        logger.info("Exit manageFindProductsByNameOrId");
        return pageInfo;
    }

    @Override
    public Product manageDetail(Integer productId) {
        logger.info("Enter manageDetail productId: " + productId);
        logger.info("Exit manageDetail");
        return this.productMapper.selectByPrimaryKey(productId);
    }

    @Override
    public Product findProductById(Integer productId) {
        logger.info("Enter findProductById productId: " + productId);
        logger.info("Exit findProductById");
        return this.productMapper.selectByPrimaryKey(productId);
    }

    @Override
    public ServerResponse manageSetSaleStatus(Product product, Integer status) {
        logger.info("Enter manageSetSaleStatus productId: " + product.getId() + " status: " + status);
        product.setStatus(status);
        int count = this.productMapper.updateByPrimaryKeySelective(product);
        if (count > 0) {
            logger.info("Exit manageSetSaleStatus count: " + count);
            return ServerResponse.createServerResponseSuccess("Update Product ProductID: " +
                    product.getId() + " Status to: " + status);
        }
        logger.info("Exit manageSetSaleStatus");
        return ServerResponse.createServerResponseError("Error Update Product");
    }

    @Override
    public ServerResponse update(Product product) {
        logger.info("Enter update productId: " + product.getId());
        int count = this.productMapper.updateByPrimaryKeySelective(product);
        if (count > 0) {
            logger.info("Exit update count: " + count);
            return ServerResponse.createServerResponseSuccess("Update Product Success");
        }
        logger.info("Exit update");
        return ServerResponse.createServerResponseError("Update Product Failed");
    }

    @Override
    public ServerResponse insert(Product product) {
        logger.info("Enter insert productId: " + product.getId());
        int count = this.productMapper.insert(product);
        if (count > 0) {
            logger.info("Exit insert count: " + count);
            return ServerResponse.createServerResponseSuccess("Insert Product Success");
        }
        logger.info("Exit insert");
        return ServerResponse.createServerResponseError("Insert Product Failed");
    }

    @Override
    public ServerResponse upsert(Product product) {
        logger.info("Enter upsert productId: " + product.getId());
        Product p = this.productMapper.selectByPrimaryKey(product.getId());
        if (p != null) {
            logger.info("Exit upsert p is not null");
            return this.update(product);
        }
        logger.info("Exit upsert");
        return this.insert(product);

    }

    @Override
    public PageInfo findProductsByKeywordsOrCategoryId(Integer pageNum, Integer pageSize, String keywords, Integer categoryId,
                                                       String orderBy) {
        logger.info("Enter findProductsByKeywordsOrCategoryId pageNum: " + pageNum + " pageSize: " + pageSize +
         " keywords: " + keywords + " categoryId: " + categoryId + " orderBy: " + orderBy);
        PageHelper.startPage(pageNum, pageSize);
        if (StringUtils.isNotBlank(keywords)) {
            keywords = "%" + keywords + "%";
        }
        if (StringUtils.isNotBlank(orderBy)) {
            orderBy = orderBy.replace('_', ' '); // Process Order By
            PageHelper.orderBy(orderBy);
        }
        List<Product> products = this.productMapper.findProductsByKeywordsOrCategoryId(keywords, categoryId, orderBy);
        List<ProductListManageVo> productListManageVos = new ArrayList<>();
        PageInfo pageInfo = new PageInfo(products);
        for (Product p : products) {
            logger.info("Product Name: " + p.getName());
            if (p.getStatus() == Const.ProductConst.PRODUCT_STATUS_1) {
                productListManageVos.add(this.convertToProductListManageVo(p));
            }
        }
        pageInfo.setList(productListManageVos);
        logger.info("Exit findProductsByKeywordsOrCategoryId");
        return pageInfo;
    }

    private ProductListManageVo convertToProductListManageVo(Product product) {
        logger.info("Enter convertToProductListManageVo productId: " + product.getId());
        ProductListManageVo productListManageVo = new ProductListManageVo();
        productListManageVo.setId(product.getId());
        productListManageVo.setCategoryId(product.getCategoryId());
        productListManageVo.setName(product.getName());
        productListManageVo.setPrice(product.getPrice());
        productListManageVo.setMainImage(product.getMainImage());
        productListManageVo.setStatus(product.getStatus());
        productListManageVo.setSubtitle(product.getSubtitle());
        logger.info("Exit convertToProductListManageVo");
        return productListManageVo;
    }
}