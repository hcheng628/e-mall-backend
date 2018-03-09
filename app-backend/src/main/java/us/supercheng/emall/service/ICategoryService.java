package us.supercheng.emall.service;

import us.supercheng.emall.common.ServerResponse;
import us.supercheng.emall.pojo.Category;
import java.util.List;

public interface ICategoryService {

    public ServerResponse<List<Category>> getCategoriesByParentId(int parentId);
}