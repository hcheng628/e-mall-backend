package us.supercheng.emall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.supercheng.emall.common.ServerResponse;
import us.supercheng.emall.dao.ShippingMapper;
import us.supercheng.emall.pojo.Shipping;
import us.supercheng.emall.service.IShippingService;
import java.util.Date;
import java.util.List;

@Service("iAddressService")
public class ShippingServiceImpl implements IShippingService {
    private static final Logger logger = LoggerFactory.getLogger(ShippingServiceImpl.class);

    @Autowired
    private ShippingMapper shippingMapper;

    public ServerResponse<Shipping> add(Integer userId, Shipping shipping) {
        logger.info("Enter add userId: " + userId + " shippingId: " + shipping.getId());
        shipping.setId(null);
        shipping.setUserId(userId);
        shipping.setCreateTime(new Date()); // To trigger Mybstis using now()
        int count = this.shippingMapper.insertSelective(shipping);
        if (count <0 ) {
            logger.error("Exit add --- Add Shipping Address Failed");
            return ServerResponse.createServerResponseError("Add Shipping Address Failed");
        }
        logger.info("Exit add");
        return ServerResponse.createServerResponseSuccess(shipping);
    }

    public ServerResponse<String> del(Integer shippingId) {
        logger.info("Enter del shippingId: " + shippingId);
        int count = this.shippingMapper.deleteByPrimaryKey(shippingId);
        if (count < 0 ) {
            logger.error("Exit del --- Delete Shipping Address AddressID: " + shippingId + " Failed");
            return ServerResponse.createServerResponseError("Delete Shipping Address AddressID: " + shippingId + " Failed");
        }
        logger.info("Exit del");
        return ServerResponse.createServerResponseSuccess("Delete Shipping Address AddressID: " + shippingId + " Success");
    }

    public ServerResponse<String> update(Integer userId, Shipping shipping) {
        logger.info("Enter update userId: " + userId + " shippingId: " + shipping.getId());
        shipping.setUserId(userId);
        shipping.setUpdateTime(new Date()); // To trigger Mybstis using now()
        int count = this.shippingMapper.updateByPrimaryKeyAndUserIdSelective(shipping);
        if (count > 0 ) {
            logger.info("Exit update ");
            return ServerResponse.createServerResponseError("Update Shipping Address AddressID: " + shipping.getId() + " Success");
        }
        logger.error("Exit Update Shipping Address AddressID: " + shipping.getId() + " Failed");
        return ServerResponse.createServerResponseError("Update Shipping Address AddressID: " + shipping.getId() + " Failed");
    }

    public ServerResponse<Shipping> select(Integer shippingId) {
        logger.info("Enter select shippingId: " + shippingId);
        Shipping shipping = this.shippingMapper.selectByPrimaryKey(shippingId);
        if (shipping != null) {
            logger.info("Exit select");
            return ServerResponse.createServerResponseSuccess(shipping);
        }
        logger.error("Exit select --- No Such Shipping Address AddressID: " + shippingId);
        return ServerResponse.createServerResponseError("No Such Shipping Address AddressID: " + shippingId);
    }

    public ServerResponse<PageInfo> list(Integer pageNum, Integer pageSize, Integer userId) {
        logger.info("Enter list pageNum: " + pageNum + " pageSize: " + pageSize + " userId: " + userId);
        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippingList = this.shippingMapper.selectShippingsByUserId(userId, null);
        PageInfo pageInfo = new PageInfo(shippingList);
        logger.info("Exit list");
        return ServerResponse.createServerResponseSuccess(pageInfo);
    }
}