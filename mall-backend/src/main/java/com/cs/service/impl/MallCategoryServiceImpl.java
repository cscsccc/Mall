package com.cs.service.impl;

import com.cs.api.mall.VO.MallIndexCategoryVO;
import com.cs.api.mall.VO.SecondLevelCategoryVO;
import com.cs.api.mall.VO.ThirdLevelCategoryVO;
import com.cs.common.Constants;
import com.cs.common.MallCategoryLevelEnum;
import com.cs.common.ServiceResultEnum;
import com.cs.mapper.GoodsCategoryMapper;
import com.cs.pojo.GoodsCategory;
import com.cs.service.MallCategoryService;
import com.cs.util.BeanUtil;
import com.cs.util.PageQueryUtil;
import com.cs.util.PageResult;
import com.cs.util.ResultGenerator;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MallCategoryServiceImpl implements MallCategoryService {

    @Autowired
    private GoodsCategoryMapper goodsCategoryMapper;

    @Override
    public PageResult getCategories(PageQueryUtil pageUtil) {

        List<GoodsCategory> goodsCategories = goodsCategoryMapper.getGoodsCategoryList(pageUtil);
        int total = goodsCategoryMapper.getTotalGoodsCategories(pageUtil);
        PageResult pageResult = new PageResult(goodsCategories, total, pageUtil.getLimit(), pageUtil.getPage());

        return pageResult;
    }

    @Override
    public String saveCategory(GoodsCategory goodsCategory) {
        // 1.查询是否有此类别(根据 分类级别 和 名称 查询)
        if (goodsCategoryMapper.selectByLevelAndName(goodsCategory.getCategoryLevel(), goodsCategory.getCategoryName()) != null){
            return ServiceResultEnum.SAME_CATEGORY_EXIST.getResult();
        }

        // 2.插入此类别信息
        if (goodsCategoryMapper.insertSelective(goodsCategory) > 0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public GoodsCategory selectByPrimaryKey(Long id) {
        return goodsCategoryMapper.selectByPrimaryKey(id);
    }

    @Override
    public String updateCategory(GoodsCategory category) {
        if (goodsCategoryMapper.selectByPrimaryKey(category.getCategoryId())==null){
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }

        GoodsCategory temp = goodsCategoryMapper.selectByLevelAndName(category.getCategoryLevel(), category.getCategoryName());

        if (temp != null && !temp.getCategoryId().equals(category.getCategoryId())){
            return ServiceResultEnum.SAME_CATEGORY_EXIST.getResult();
        }
        category.setUpdateTime(new Date());
        if (goodsCategoryMapper.updateCategory(category) > 0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public boolean deleteBatch(Long[] ids) {
        return goodsCategoryMapper.deleteBatch(ids) > 0;
    }

    @Override
    public List<MallIndexCategoryVO> getCategoriesForIndex() {
        List<MallIndexCategoryVO> mallIndexCategoryVOS = new ArrayList<MallIndexCategoryVO>();
        // 获取一集标题
        List<GoodsCategory> firstLevelCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), MallCategoryLevelEnum.LEVEL_ONE.getLevel(), Constants.INDEX_CATEGORY_NUMBER);
        if (!CollectionUtils.isEmpty(firstLevelCategories)){
            List<Long> firstCategoryIds = firstLevelCategories.stream().map(GoodsCategory::getCategoryId).collect(Collectors.toList());
            List<GoodsCategory> secondLevelCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(firstCategoryIds, MallCategoryLevelEnum.LEVEL_TWO.getLevel(), 0);
            if (!CollectionUtils.isEmpty(secondLevelCategories)){
                List<Long> secondCategoryIds = secondLevelCategories.stream().map(GoodsCategory::getCategoryId).collect(Collectors.toList());
                List<GoodsCategory> thirdLevelCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(secondCategoryIds, MallCategoryLevelEnum.LEVEL_THREE.getLevel(), 0);

                if (!CollectionUtils.isEmpty(thirdLevelCategories)){

                    // 先通过parentId对三级目录进行分组 好放入相应的二级目录
                    Map<Long, List<GoodsCategory>> thirdParentIdMap = thirdLevelCategories.stream().collect(Collectors.groupingBy(GoodsCategory::getParentId));
                    //处理二级目录
                    List<SecondLevelCategoryVO> secondLevelCategoryVOS = new ArrayList<>();
                    for (GoodsCategory goodsCategory : secondLevelCategories){
                        SecondLevelCategoryVO secondLevelCategoryVO = new SecondLevelCategoryVO();
                        BeanUtil.copyProperties(goodsCategory, secondLevelCategoryVO);
                        // 对每个二级目录进行子目录查询 并封装为VO添加到VOS
                        if (thirdParentIdMap.containsKey(goodsCategory.getCategoryId())){
                            List<GoodsCategory> thirdCategoryListForParentId = thirdParentIdMap.get(goodsCategory.getCategoryId());
                            List<ThirdLevelCategoryVO> thirdLevelCategoryVOS = BeanUtil.copyList(thirdCategoryListForParentId, ThirdLevelCategoryVO.class);

                            secondLevelCategoryVO.setThirdLevelCategoryVOS(thirdLevelCategoryVOS);
                            secondLevelCategoryVOS.add(secondLevelCategoryVO);
                        }
                    }

                    // 处理一级目录
                    Map<Long, List<SecondLevelCategoryVO>> secondParentIdMap = secondLevelCategoryVOS.stream().collect(Collectors.groupingBy(SecondLevelCategoryVO::getParentId));
                    for (GoodsCategory goodsCategory : firstLevelCategories){
                        MallIndexCategoryVO mallIndexCategoryVO = new MallIndexCategoryVO();
                        BeanUtil.copyProperties(goodsCategory, mallIndexCategoryVO);
                        if (secondParentIdMap.containsKey(goodsCategory.getCategoryId())){
                            List<SecondLevelCategoryVO> secondCategoryVOListForParentId = secondParentIdMap.get(goodsCategory.getCategoryId());

                            mallIndexCategoryVO.setSecondLevelCategoryVOS(secondCategoryVOListForParentId);
                            mallIndexCategoryVOS.add(mallIndexCategoryVO);
                        }


                    }
                }

            }
            return mallIndexCategoryVOS;
        }


        return null;
    }
}
