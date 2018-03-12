package us.supercheng.emall.service;

import com.github.pagehelper.PageInfo;
import us.supercheng.emall.common.ServerResponse;
import us.supercheng.emall.pojo.Product;

public interface IProductService {
    PageInfo manageList(Integer pageNum, Integer pageSize);
    PageInfo manageFindProductsByNameOrId(Integer pageNum, Integer pageSize, String productName, Integer productId);
    Product manageDetail(Integer productId);
    Product findProductById(Integer productId);
    ServerResponse manageSetSaleStatus(Product product, Integer status);
}