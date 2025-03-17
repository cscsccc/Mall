package com.cs.service;

import com.cs.api.mall.VO.MallGoodsDetailVO;
import com.cs.pojo.GoodsCategory;
import com.cs.pojo.MallGoods;
import com.cs.util.PageQueryUtil;
import com.cs.util.PageResult;

public interface MallGoodsService {
    String saveGood(MallGoods mallGoods);

    PageResult getMallGoodsPage(PageQueryUtil pageUtil);

    int updateBatchSellStatus(Long[] ids, int sellStatus);

    MallGoods getOneById(Long id);

    GoodsCategory getCategoryById(Long id);

    String updateMallGoods(MallGoods goods);

    MallGoodsDetailVO getDetail(Long goodsId);

    PageResult searchMallGoods(PageQueryUtil pageQueryUtil);
}
