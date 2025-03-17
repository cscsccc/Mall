package com.cs.api.mall;

import com.cs.api.mall.VO.IndexInfoVO;
import com.cs.api.mall.VO.MallIndexCarouselVO;
import com.cs.api.mall.VO.MallIndexConfigGoodsVO;
import com.cs.common.Constants;
import com.cs.common.IndexConfigTypeEnum;
import com.cs.service.IndexConfigService;
import com.cs.service.CarouselService;
import com.cs.util.Result;
import com.cs.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MallIndexAPI {

    @Autowired
    private CarouselService carouselService;
    @Autowired
    private IndexConfigService indexConfigService;

    @GetMapping("/index-infos")
    public Result<IndexInfoVO> getHome(){
        // 获取轮播图 新品 热门 推荐信息
        List<MallIndexCarouselVO> carousels = carouselService.selectCarouselList(Constants.INDEX_CAROUSEL_NUMBER);
        List<MallIndexConfigGoodsVO> newGoods = indexConfigService.selectIndexConfigForIndex(IndexConfigTypeEnum.INDEX_GOODS_NEW.getType(), Constants.INDEX_GOODS_NEW_NUMBER);
        List<MallIndexConfigGoodsVO> hotGoods = indexConfigService.selectIndexConfigForIndex(IndexConfigTypeEnum.INDEX_GOODS_HOT.getType(), Constants.INDEX_GOODS_HOT_NUMBER);
        List<MallIndexConfigGoodsVO> recommendGoods = indexConfigService.selectIndexConfigForIndex(IndexConfigTypeEnum.INDEX_GOODS_RECOMMOND.getType(), Constants.INDEX_GOODS_RECOMMOND_NUMBER);

        IndexInfoVO indexInfoVO = new IndexInfoVO();
        indexInfoVO.setCarousels(carousels);
        indexInfoVO.setNewGoodses(newGoods);
        indexInfoVO.setHotGoodses(hotGoods);
        indexInfoVO.setRecommendGoodses(recommendGoods);

        return ResultGenerator.genSuccessResult(indexInfoVO);
    }
}
