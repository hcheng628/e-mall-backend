package us.supercheng.emall.controller.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import us.supercheng.emall.common.ServerResponse;
import us.supercheng.emall.service.ICategoryService;

@Controller
@RequestMapping("/manage/category")
public class CategoryController {

    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping(value = "get_category.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getCategoriesByParentId(@RequestParam(defaultValue = "0") int parentId) {
        return this.iCategoryService.getCategoriesByParentId(parentId);
    }

}