package com.cs.service;

import com.cs.api.mall.VO.MallIndexCategoryVO;
import com.cs.pojo.GoodsCategory;
import com.cs.util.PageQueryUtil;
import com.cs.util.PageResult;
import org.springframework.stereotype.Service;

import java.util.List;

public interface MallCategoryService {

    PageResult getCategories(PageQueryUtil pageUtil);

    String saveCategory(GoodsCategory goodsCategory);

    GoodsCategory selectByPrimaryKey(Long id);

    String updateCategory(GoodsCategory category);

    boolean deleteBatch(Long[] ids);

    List<MallIndexCategoryVO> getCategoriesForIndex();
}


