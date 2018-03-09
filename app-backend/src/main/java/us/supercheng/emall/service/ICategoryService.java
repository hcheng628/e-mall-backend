package us.supercheng.emall.service;

import us.supercheng.emall.common.ServerResponse;
import us.supercheng.emall.pojo.Category;
import java.util.List;

public interface ICategoryService {

    ServerResponse<List<Category>> getCategoriesByParentId(int parentId);
    ServerResponse<String> addCategory(Integer parentId, String categoryName);
}