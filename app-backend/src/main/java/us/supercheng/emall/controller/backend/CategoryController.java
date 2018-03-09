package us.supercheng.emall.controller.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import us.supercheng.emall.common.Const;
import us.supercheng.emall.common.ServerResponse;
import us.supercheng.emall.pojo.Category;
import us.supercheng.emall.pojo.User;
import us.supercheng.emall.service.ICategoryService;
import us.supercheng.emall.service.IUserService;
import javax.servlet.http.HttpSession;
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
        if (user == null) {
            return ServerResponse.createServerResponseError("Login Required");
        }
        if (user.getRole() == Const.ROLE_ADMIN) {
            return ServerResponse.createServerResponseError("Username: " + user.getUsername() + " is not an Admin User");
        }

        ServerResponse<List<Category>> serverResponse = this.iCategoryService.getCategoriesByParentId(parentId);

        if(serverResponse.getData().size() > 0) {
            return serverResponse;
        }
        return ServerResponse.createServerResponseError("No Such Category ID: " + parentId + " Found.");
    }


    @RequestMapping(value = "add_category.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse addCategory(@RequestParam(defaultValue = "0") Integer parentId, String categoryNames, HttpSession session) {
        User user = this.iUserService.getCurrentUser(session);
        if (user == null) {
            return ServerResponse.createServerResponseError("Login Required");
        }
        if (user.getRole() == Const.ROLE_ADMIN) {
            return ServerResponse.createServerResponseError("Username: " + user.getUsername() + " is not an Admin User");
        }
        return this.iCategoryService.addCategory(parentId, categoryNames);
    }

}