package us.supercheng.emall.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.supercheng.emall.common.ServerResponse;
import us.supercheng.emall.dao.CategoryMapper;
import us.supercheng.emall.pojo.Category;
import us.supercheng.emall.service.ICategoryService;

import java.util.List;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ServerResponse<List<Category>> getCategoriesByParentId(int parentId) {
        List<Category> categoryList =  this.categoryMapper.getCategoriesByParentId(parentId);
        return ServerResponse.createServerResponseSuccess(categoryList);
    }

    @Override
    public ServerResponse<String> addCategory(Integer parentId, String categoryName) {
        Category category = new Category();
        category.setStatus(true);
        category.setParentId(parentId);
        category.setName(categoryName);
        int count = this.categoryMapper.insertSelective(category);
        if (count > 0) {
            return ServerResponse.createServerResponseSuccess("Add Category Success");
        }
        return ServerResponse.createServerResponseError("Add Category Failed");
    }
}