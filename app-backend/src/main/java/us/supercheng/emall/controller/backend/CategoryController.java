package us.supercheng.emall.controller.backend;

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

    @Autowired
    private ICategoryService iCategoryService;

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "get_category.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getCategoriesByParentId(@RequestParam(defaultValue = "0") int parentId, HttpSession session) {
        User user = this.iUserService.getCurrentUser(session);
        ServerResponse serverResponse = this.iUserService.checkAdminUser(user);
        if (serverResponse.getStatus() == ResponseCode.SUCCESS.getCode()) {
            ServerResponse<List<Category>> serverResponseCategoryList = this.iCategoryService.getCategoriesByParentId(parentId);
            if (serverResponseCategoryList.getData().size() > 0) {
                return serverResponseCategoryList;
            }
            return ServerResponse.createServerResponseError("No Such Category ID: " + parentId + " Found.");
        }
        return serverResponse;
    }

    @RequestMapping(value = "add_category.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse addCategory(@RequestParam(defaultValue = "0") Integer parentId, String categoryName, HttpSession session) {
        User user = this.iUserService.getCurrentUser(session);
        ServerResponse serverResponse = this.iUserService.checkAdminUser(user);
        if (serverResponse.getStatus() == ResponseCode.SUCCESS.getCode()) {
            return this.iCategoryService.addCategory(parentId, categoryName);
        }
        return serverResponse;
    }

    @RequestMapping(value = "set_category_name.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse setCategoryName(Integer categoryId, String categoryName, HttpSession session) {
        User user = this.iUserService.getCurrentUser(session);
        ServerResponse serverResponse = this.iUserService.checkAdminUser(user);
        if (serverResponse.getStatus() == ResponseCode.SUCCESS.getCode()) {
            return this.iCategoryService.setCategoryName(categoryId, categoryName);
        }
        return serverResponse;
    }

    @RequestMapping(value = "get_deep_category.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getDeepCategory(Integer categoryId, HttpSession session) {
        User user = this.iUserService.getCurrentUser(session);
        ServerResponse serverResponse = this.iUserService.checkAdminUser(user);
        if (serverResponse.getStatus() == ResponseCode.SUCCESS.getCode()) {
            return this.iCategoryService.getDeepCategory(categoryId, new HashSet<Category>());
        }
        return serverResponse;
    }
}