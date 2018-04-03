package us.supercheng.emall.controller.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import us.supercheng.emall.common.ResponseCode;
import us.supercheng.emall.common.ServerResponse;
import us.supercheng.emall.pojo.Category;
import us.supercheng.emall.pojo.User;
import us.supercheng.emall.service.ICategoryService;
import us.supercheng.emall.service.IUserService;
import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.List;

@Controller
@RequestMapping("/manage/category")
public class CategoryController {
    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);


    @Autowired
    private ICategoryService iCategoryService;

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "get_category.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getCategoriesByParentId(@RequestParam(defaultValue = "0") int parentId, HttpSession session) {
        logger.info("Enter getCategoriesByParentId parentId: " + parentId);
        User user = this.iUserService.getCurrentUser(session);
        ServerResponse serverResponse = this.iUserService.checkAdminUser(user);
        if (serverResponse.getStatus() == ResponseCode.SUCCESS.getCode()) {
            ServerResponse<List<Category>> serverResponseCategoryList = this.iCategoryService.getCategoriesByParentId(parentId);
            if (serverResponseCategoryList.getData().size() > 0) {
                logger.info("Exit getCategoriesByParentId");
                return serverResponseCategoryList;
            }
            logger.error("Exit getCategoriesByParentId --- No Such Category ID: " + parentId + " Found");
            return ServerResponse.createServerResponseError("No Such Category ID: " + parentId + " Found");
        }
        logger.error("Exit getCategoriesByParentId --- Not Admin User");
        return serverResponse;
    }

    @RequestMapping(value = "add_category.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse addCategory(@RequestParam(defaultValue = "0") Integer parentId, String categoryName, HttpSession session) {
        logger.info("Enter addCategory parentId: " + parentId + " categoryName: " + categoryName);
        User user = this.iUserService.getCurrentUser(session);
        ServerResponse serverResponse = this.iUserService.checkAdminUser(user);
        if (serverResponse.getStatus() == ResponseCode.SUCCESS.getCode()) {
            logger.info("Exit addCategory");
            return this.iCategoryService.addCategory(parentId, categoryName);
        }
        logger.info("Exit addCategory --- Not Admin User");
        return serverResponse;
    }

    @RequestMapping(value = "set_category_name.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse setCategoryName(Integer categoryId, String categoryName, HttpSession session) {
        logger.info("Enter setCategoryName categoryId: " + categoryId + " categoryName: " + categoryName);
        User user = this.iUserService.getCurrentUser(session);
        ServerResponse serverResponse = this.iUserService.checkAdminUser(user);
        if (serverResponse.getStatus() == ResponseCode.SUCCESS.getCode()) {
            logger.info("Exit setCategoryName");
            return this.iCategoryService.setCategoryName(categoryId, categoryName);
        }
        logger.info("Exit setCategoryName --- Not Admin User");
        return serverResponse;
    }

    @RequestMapping(value = "get_deep_category.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getDeepCategory(Integer categoryId, HttpSession session) {
        logger.info("Enter getDeepCategory categoryId: " + categoryId);
        User user = this.iUserService.getCurrentUser(session);
        ServerResponse serverResponse = this.iUserService.checkAdminUser(user);
        if (serverResponse.getStatus() == ResponseCode.SUCCESS.getCode()) {
            logger.info("Exit getDeepCategory");
            return this.iCategoryService.getDeepCategory(categoryId, new HashSet<Category>());
        }
        logger.info("Exit getDeepCategory --- Not Admin User");
        return serverResponse;
    }
}