package com.cs.service.impl;

import com.cs.api.mall.VO.MallIndexConfigGoodsVO;
import com.cs.common.ServiceResultEnum;
import com.cs.mapper.IndexConfigMapper;
import com.cs.mapper.MallGoodsMapper;
import com.cs.pojo.IndexConfig;
import com.cs.pojo.MallGoods;
import com.cs.service.IndexConfigService;
import com.cs.util.BeanUtil;
import com.cs.util.PageQueryUtil;
import com.cs.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AdminIndexConfigServiceImpl implements IndexConfigService {

    @Autowired
    private IndexConfigMapper indexConfigMapper;

    @Autowired
    private MallGoodsMapper mallGoodsMapper;

    @Override
    public PageResult getIndexConfigPage(PageQueryUtil pageQueryUtil) {
        List<IndexConfig> indexConfigList = indexConfigMapper.getIndexConfigList(pageQueryUtil);
        int total = indexConfigMapper.getTotalIndexConfig(pageQueryUtil);
        PageResult pageResult = new PageResult(indexConfigList, total, pageQueryUtil.getLimit(), pageQueryUtil.getPage());
        return pageResult;
    }

    @Override
    public String addIndexConfig(IndexConfig indexConfig) {
        // 查询商品在不
        if (mallGoodsMapper.getMallGoodById(indexConfig.getGoodsId())==null){
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }

        // 查询同一类别配置商品 中商品id是否存在
        if (indexConfigMapper.selectByConfigTypeAndGoodsId(indexConfig.getConfigType(), indexConfig.getGoodsId()) != null){
            return ServiceResultEnum.SAME_INDEX_CONFIG_EXIST.getResult();
        }

        if (indexConfigMapper.addIndexConfig(indexConfig) > 0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public IndexConfig selectIndexConfigById(Long id) {
        IndexConfig indexConfig = indexConfigMapper.selectIndexConfigById(id);
        return indexConfig;
    }

    @Override
    public String updateIndexConfig(IndexConfig indexConfig) {
        // 先查询商品编号是否存在
        if (mallGoodsMapper.getMallGoodById(indexConfig.getGoodsId())==null){
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }

        if (indexConfigMapper.selectIndexConfigById(indexConfig.getConfigId()) == null){
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }

        IndexConfig temp = indexConfigMapper.selectByConfigTypeAndGoodsId(indexConfig.getConfigType(), indexConfig.getGoodsId());

        // 查此配置中是否存在此商品
        if (temp != null && !temp.getConfigId().equals(indexConfig.getConfigId())){
            return ServiceResultEnum.SAME_INDEX_CONFIG_EXIST.getResult();
        }

        indexConfig.setUpdateTime(new Date());

        if (indexConfigMapper.updateIndexConfig(indexConfig) > 0){
            return ServiceResultEnum.SUCCESS.getResult();
        }

        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public int deleteIndexConfig(Long[] ids) {
        return indexConfigMapper.deleteIndexConfig(ids);
    }

    @Override
    public List<MallIndexConfigGoodsVO> selectIndexConfigForIndex(int type, int number) {
        List<IndexConfig> indexConfigList = indexConfigMapper.selectIndexConfigByTypeAndNumber(type, number);
        List<MallIndexConfigGoodsVO> mallIndexConfigGoodsVOS = new ArrayList<MallIndexConfigGoodsVO>();

        if (!CollectionUtils.isEmpty(indexConfigList)){
            // 取出所有的 indexConfig id
            List<Long> goodsIds = indexConfigList.stream().map(IndexConfig::getGoodsId).collect(Collectors.toList());
            //通过id取出所有的商品信息
            List<MallGoods> mallGoods = mallGoodsMapper.selectListByIds(goodsIds);
            mallIndexConfigGoodsVOS = BeanUtil.copyList(mallGoods, MallIndexConfigGoodsVO.class);

            for (MallIndexConfigGoodsVO mallIndexConfigGoodsVO : mallIndexConfigGoodsVOS) {
                // 判断 商品名和简介 是否过长 省略
                String goodsName = mallIndexConfigGoodsVO.getGoodsName();
                String goodsIntro = mallIndexConfigGoodsVO.getGoodsIntro();

                if (goodsName.length() > 28){
                    goodsName = goodsName.substring(0, 28) + "...";
                }
                if (goodsIntro.length() > 28){
                    goodsIntro = goodsIntro.substring(0, 28) + "...";
                }
            }
        }
        return mallIndexConfigGoodsVOS;
    }
}
