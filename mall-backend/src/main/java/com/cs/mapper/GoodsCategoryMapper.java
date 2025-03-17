package com.cs.mapper;

import com.cs.pojo.GoodsCategory;
import com.cs.util.PageQueryUtil;

import java.util.List;

public interface GoodsCategoryMapper {

    GoodsCategory selectByLevelAndName(Byte categoryLevel, String categoryName);

    GoodsCategory selectByPrimaryKey(Long goodsCategoryId);

    List<GoodsCategory> getGoodsCategoryList(PageQueryUtil pageUtil);

    int getTotalGoodsCategories(PageQueryUtil pageUtil);

    int insertSelective(GoodsCategory goodsCategory);

    int updateCategory(GoodsCategory category);

    int deleteBatch(Long[] ids);

    List<GoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int categoryLevel, int number);
}
