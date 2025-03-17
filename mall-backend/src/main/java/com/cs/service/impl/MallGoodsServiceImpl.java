package com.cs.service.impl;

import com.cs.api.mall.VO.MallGoodsDetailVO;
import com.cs.api.mall.VO.MallSearchGoodsVO;
import com.cs.common.Constants;
import com.cs.common.MallCategoryLevelEnum;
import com.cs.common.MallException;
import com.cs.common.ServiceResultEnum;
import com.cs.mapper.CarouselsMapper;
import com.cs.mapper.GoodsCategoryMapper;
import com.cs.mapper.MallGoodsMapper;
import com.cs.pojo.GoodsCategory;
import com.cs.pojo.MallGoods;
import com.cs.service.MallGoodsService;
import com.cs.util.BeanUtil;
import com.cs.util.PageQueryUtil;
import com.cs.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MallGoodsServiceImpl implements MallGoodsService {

    @Autowired
    private GoodsCategoryMapper goodsCategoryMapper;
    @Autowired
    private MallGoodsMapper goodsMapper;
    @Autowired
    private CarouselsMapper carouselsMapper;

    @Override
    public String saveGood(MallGoods mallGoods) {
        GoodsCategory goodsCategory = goodsCategoryMapper.selectByPrimaryKey(mallGoods.getGoodsCategoryId());

        if (goodsCategory == null || goodsCategory.getCategoryLevel().intValue() != MallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
            return ServiceResultEnum.GOODS_CATEGORY_ERROR.getResult();
        }

        // 查找有无重名商品 分类id name
        if (goodsMapper.selectByIdAndName(goodsCategory.getCategoryId(), mallGoods.getGoodsName()) != null){
            return ServiceResultEnum.SAME_GOODS_EXIST.getResult();
        }

        if (goodsMapper.saveGood(mallGoods) > 0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public PageResult getMallGoodsPage(PageQueryUtil pageUtil) {
        List<MallGoods> mallGoodsList = goodsMapper.findMallGoodsList(pageUtil);
        int total = goodsMapper.getMallGoodsListTotal(pageUtil);
        PageResult pageResult = new PageResult(mallGoodsList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public int updateBatchSellStatus(Long[] ids, int sellStatus) {
        return goodsMapper.updateBatchSellStatus(ids, sellStatus);
    }

    @Override
    public MallGoods getOneById(Long id) {
        MallGoods mallGoods = goodsMapper.getMallGoodById(id);
        return mallGoods;
    }

    @Override
    public GoodsCategory getCategoryById(Long id) {
        GoodsCategory goodsCategory = goodsCategoryMapper.selectByPrimaryKey(id);
        return goodsCategory;
    }

    @Override
    public String updateMallGoods(MallGoods goods) {
        // 1. 查询分类
        GoodsCategory goodsCategory = goodsCategoryMapper.selectByPrimaryKey(goods.getGoodsCategoryId());
        if (goodsCategory == null || goodsCategory.getCategoryLevel().intValue() != MallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
            return ServiceResultEnum.GOODS_CATEGORY_ERROR.getResult();
        }
        // 2. 查询商品是否存在
        if (goodsMapper.getMallGoodById(goods.getGoodsId()) == null) {
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        // 3. 查询同一类别下是否有同名商品 这种情况下货物id需一致， 保证无重名
        MallGoods temp = goodsMapper.selectByIdAndName(goods.getGoodsCategoryId(), goods.getGoodsName());
        if (temp != null && !temp.getGoodsId().equals(goods.getGoodsId())) {
            return ServiceResultEnum.SAME_GOODS_EXIST.getResult();
        }
        // 4. 更新数据
        goods.setUpdateTime(new Date());
        if (goodsMapper.updateMallGoods(goods) > 0){
            return ServiceResultEnum.SUCCESS.getResult();
        }

        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public MallGoodsDetailVO getDetail(Long goodsId) {
        // 获取商品信息
        MallGoods mallGoods = goodsMapper.getMallGoodById(goodsId);
        if (mallGoods == null){
            MallException.fail(ServiceResultEnum.GOODS_NOT_EXIST.getResult());
        }
        if (mallGoods.getGoodsSellStatus() != Constants.SELL_STATUS_UP){
            MallException.fail(ServiceResultEnum.GOODS_PUT_DOWN.getResult());
        }
        MallGoodsDetailVO mallGoodsDetailVO = new MallGoodsDetailVO();
        BeanUtil.copyProperties(mallGoods, mallGoodsDetailVO);
        // 获取轮播图列表
        mallGoodsDetailVO.setGoodsCarouselList(mallGoods.getGoodsCarousel().split(","));

        return mallGoodsDetailVO;
    }

    @Override
    public PageResult searchMallGoods(PageQueryUtil pageQueryUtil) {
        List<MallGoods> mallGoodsList = goodsMapper.searchMallGoods(pageQueryUtil);
        int total = goodsMapper.searchMallGoodsListTotal(pageQueryUtil);
        List<MallSearchGoodsVO> mallSearchGoodsVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(mallGoodsList)){
            mallSearchGoodsVOS = BeanUtil.copyList(mallGoodsList, MallSearchGoodsVO.class);
            for (MallSearchGoodsVO mallSearchGoodsVO : mallSearchGoodsVOS){
                if (mallSearchGoodsVO.getGoodsName().length() > 28){
                    mallSearchGoodsVO.setGoodsName(mallSearchGoodsVO.getGoodsName().substring(0, 28)+"...");
                }
                if (mallSearchGoodsVO.getGoodsIntro().length() > 30){
                    mallSearchGoodsVO.setGoodsIntro(mallSearchGoodsVO.getGoodsIntro().substring(0, 30)+"...");
                }
            }
        }

        return new PageResult(mallSearchGoodsVOS, total, pageQueryUtil.getLimit(), pageQueryUtil.getPage());
    }
}
