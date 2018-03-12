package us.supercheng.emall.controller.portal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import us.supercheng.emall.common.ServerResponse;
import us.supercheng.emall.pojo.Product;

@Controller
@RequestMapping(value = "/product")
public class ProductController {

    @RequestMapping(value = "list.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Product> list(String categoryId, String keyword,
                                        @RequestParam(defaultValue = "1") String pageNum,
                                        @RequestParam(defaultValue = "10") String pageSize,
                                        @RequestParam(defaultValue = "") String orderBy) {
        System.out.println("CategoryId: " + categoryId);
        System.out.println("Keyword: " + keyword);
        System.out.println("PageNum: " + pageNum);
        System.out.println("PageSize: " + pageSize);
        System.out.println("OrderBy: " + orderBy);
        return null;
    }

    @RequestMapping(value = "detail.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Product> detail(String productId) {
        System.out.println("ProductId: " + productId);
        return null;
    }
}