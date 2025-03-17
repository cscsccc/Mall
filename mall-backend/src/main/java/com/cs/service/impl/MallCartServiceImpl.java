package com.cs.service.impl;

import com.cs.api.mall.VO.MallCartItemVO;
import com.cs.api.mall.param.MallCartEditCountParam;
import com.cs.common.Constants;
import com.cs.common.MallException;
import com.cs.common.ServiceResultEnum;
import com.cs.mapper.MallCartMapper;
import com.cs.mapper.MallGoodsMapper;
import com.cs.pojo.MallCartItem;
import com.cs.pojo.MallGoods;
import com.cs.service.MallCartService;
import com.cs.util.BeanUtil;
import com.cs.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MallCartServiceImpl implements MallCartService {

    @Autowired
    private MallCartMapper mallCartMapper;
    @Autowired
    private MallGoodsMapper mallGoodsMapper;

    @Override
    public List<MallCartItemVO> getCartItemList(Long userId) {
        // 根据用户id查询购物车 最多返回固定条
        List<MallCartItem> mallCartItems = mallCartMapper.selectByUserId(userId, Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER);
        List<MallCartItemVO> mallCartItemVOS = new ArrayList<MallCartItemVO>();

        return getMallCartItemVOS(mallCartItemVOS, mallCartItems);
    }

    @Override
    public String addCart(Integer goodsCount, Long goodsId, Long userId) {
        // 查询商品id是否已在 购物车中
        MallCartItem mallCartItem = mallCartMapper.selectByUserIdAndGoodsId(userId, goodsId);
        if (mallCartItem != null) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_EXIST_ERROR.getResult();
        }
        MallGoods mallGoods = mallGoodsMapper.getMallGoodById(goodsId);
        if (mallGoods == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        if (goodsCount < 1){
            return ServiceResultEnum.SHOPPING_CART_ITEM_NUMBER_ERROR.getResult();
        }
        if (goodsCount >  Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER){
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        // 总购物车条目
        int totalItems = mallCartMapper.selectCountByUserId(userId);
        if (totalItems > Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER){
            return ServiceResultEnum.SHOPPING_CART_ITEM_TOTAL_NUMBER_ERROR.getResult();
        }
        // 添加商品到购物车
        MallCartItem temp = new MallCartItem();
        temp.setUserId(userId);
        temp.setGoodsId(goodsId);
        temp.setGoodsCount(goodsCount);
        if (mallCartMapper.saveCart(temp) > 0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updatecartItemCount(MallCartEditCountParam mallCartEditCountParam, Long userId) {
        MallCartItem mallCartItem = mallCartMapper.selectByPrimaryKey(mallCartEditCountParam.getCartItemId());
        if (mallCartItem == null) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_EXIST_ERROR.getResult();
        }

        if (!mallCartItem.getUserId().equals(userId)) {
            return ServiceResultEnum.NO_PERMISSION_ERROR.getResult();
        }

        if (mallCartEditCountParam.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER){
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }

        if (mallCartEditCountParam.getGoodsCount().equals(mallCartItem.getGoodsCount())){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        mallCartItem.setGoodsCount(mallCartEditCountParam.getGoodsCount());
        mallCartItem.setUpdateTime(new Date());

        if (mallCartMapper.updateByPrimaryKeySelective(mallCartItem)>0){
            return ServiceResultEnum.SUCCESS.getResult();
        }

        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public boolean deleteCartItem(Long id, Long userId) {
        MallCartItem mallCartItem = mallCartMapper.selectByPrimaryKey(id);
        if (mallCartItem == null){
            return false;
        }

        if (!mallCartItem.getUserId().equals(userId)){
            return false;
        }

        return mallCartMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public List<MallCartItemVO> getCartItemListForSettle(Long[] cartItemIds) {

        // 查询所有的购物车条目
        List<MallCartItem> mallCartItems = mallCartMapper.selectByPrimaryKeyForSettle(cartItemIds);
        List<MallCartItemVO> mallCartItemVOS = new ArrayList<>();
        return getMallCartItemVOS(mallCartItemVOS, mallCartItems);
    }

    @Override
    public List<MallCartItemVO> findCartItemListForSettle(List<Long> cartItemIds, Long userId) {
        List<MallCartItemVO> mallCartItemVOS = new ArrayList<>();
        List<MallCartItem> mallCartItems = mallCartMapper.selectByUserIDAndCartItems(cartItemIds, userId);
        if (CollectionUtils.isEmpty(mallCartItems)){
            MallException.fail("购物项不能为空了");
        }
        if (mallCartItems.size() != cartItemIds.size()){
            MallException.fail("参数异常");
        }

        return getMallCartItemVOS(mallCartItemVOS, mallCartItems);
    }

    private List<MallCartItemVO> getMallCartItemVOS(List<MallCartItemVO> mallCartItemVOS, List<MallCartItem> mallCartItems){
        if (!CollectionUtils.isEmpty(mallCartItems)){
            // 获取所有商品信息
            List<Long> mallGoodsIds = mallCartItems.stream().
                                    map(MallCartItem::getGoodsId).
                                    collect(Collectors.toList());
            List<MallGoods> mallGoods = mallGoodsMapper.selectListByIds(mallGoodsIds);

            // 将商品信息和购物车信息封装为VO
            Map<Long, MallGoods> mallGoodsMap = new HashMap<>();

            //设置键->goodsId 值->对应的mallGood
            if (!CollectionUtils.isEmpty(mallGoods)){
                mallGoodsMap = mallGoods.stream().collect(Collectors.toMap(MallGoods::getGoodsId, Function.identity(), (entity1, entity2)->entity1));
            }
            for (MallCartItem mallCartItem : mallCartItems) {

                MallCartItemVO mallCartItemVO = new MallCartItemVO();
                BeanUtil.copyProperties(mallCartItem, mallCartItemVO);

                if (mallGoodsMap.containsKey(mallCartItem.getGoodsId())) {
                    MallGoods temp = mallGoodsMap.get(mallCartItem.getGoodsId());
                    mallCartItemVO.setGoodsCoverImg(temp.getGoodsCoverImg());
                    mallCartItemVO.setGoodsName(temp.getGoodsName().length() > 28 ? temp.getGoodsName().substring(0, 28) + "..." : temp.getGoodsName());
                    mallCartItemVO.setSellingPrice(temp.getSellingPrice());

                    mallCartItemVOS.add(mallCartItemVO);

                }


            }

        }

        return mallCartItemVOS;
    }
}
