package us.supercheng.emall.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.supercheng.emall.common.ServerResponse;
import us.supercheng.emall.dao.CategoryMapper;
import us.supercheng.emall.pojo.Category;
import us.supercheng.emall.service.ICategoryService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ServerResponse<List<Category>> getCategoriesByParentId(int parentId) {
        List<Category> categoryList = this.categoryMapper.getCategoriesByParentId(parentId);
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

    @Override
    public ServerResponse<String> setCategoryName(Integer categoryId, String categoryName) {
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int count = this.categoryMapper.updateByPrimaryKeySelective(category);
        if (count > 0) {
            return ServerResponse.createServerResponseSuccess("Set Category Name Success");
        }
        return ServerResponse.createServerResponseError("Set Category Name Failed");
    }

    @Override
    public ServerResponse<Set<String>> getDeepCategory(Integer categoryId, Set<Category> inCategorySet) {
        Set<String> categorySet = new HashSet<>();
        categorySet = getDeepCategoryHelper(categoryId, categorySet);
        return ServerResponse.createServerResponseSuccess(categorySet);
    }


    private Set<String> getDeepCategoryHelper(Integer categoryId, Set<String> inCategorySet) {
        if (categoryId != 0) {
            Category category = this.categoryMapper.selectByPrimaryKey(categoryId);
            inCategorySet.add(category.getId() + "");
        }
        List<Category> categories = this.getCategoriesByParentId(categoryId).getData();
        if (categories.size() > 0) {
            for (Category each : categories) {
                inCategorySet.add(each.getId() + "");
                this.getDeepCategoryHelper(each.getId(), inCategorySet);
            }
        }
        return inCategorySet;
    }
}