package us.supercheng.emall.service;

import us.supercheng.emall.common.ServerResponse;
import us.supercheng.emall.pojo.Category;
import java.util.List;
import java.util.Set;

public interface ICategoryService {
    ServerResponse<List<Category>> getCategoriesByParentId(int parentId);
    ServerResponse<String> addCategory(Integer parentId, String categoryName);
    ServerResponse<String> setCategoryName(Integer categoryId, String categoryName);
    ServerResponse<Set<String>> getDeepCategory(Integer categoryId, Set<Category> inCategorySet);
}