package com.cs.mapper;

import com.cs.pojo.MallGoods;
import com.cs.pojo.StockNumDTO;
import com.cs.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MallGoodsMapper {

    MallGoods selectByIdAndName(@Param("goodsCategoryId") Long goodsCategoryId, @Param("goodsName") String goodsName);

    int saveGood(MallGoods mallGoods);

    List<MallGoods> findMallGoodsList(PageQueryUtil pageUtil);

    int getMallGoodsListTotal(PageQueryUtil pageUtil);

    int updateBatchSellStatus(@Param("ids") Long[] ids, @Param("sellStatus") int sellStatus);

    MallGoods getMallGoodById(@Param("id") Long id);

    int updateMallGoods(MallGoods goods);

    int recoverStockNum(StockNumDTO stockNumDTO);

    int deductStockNum(StockNumDTO stockNumDTO);

    List<MallGoods> selectListByIds(List<Long> mallGoodsIds);

    List<MallGoods> searchMallGoods(PageQueryUtil pageQueryUtil);

    int searchMallGoodsListTotal(PageQueryUtil pageQueryUtil);
}
