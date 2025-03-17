package com.cs.service;

import com.cs.api.mall.VO.MallIndexConfigGoodsVO;
import com.cs.pojo.IndexConfig;
import com.cs.util.PageQueryUtil;
import com.cs.util.PageResult;

import java.util.List;

public interface IndexConfigService {
    PageResult getIndexConfigPage(PageQueryUtil pageQueryUtil);

    String addIndexConfig(IndexConfig indexConfig);

    IndexConfig selectIndexConfigById(Long id);

    String updateIndexConfig(IndexConfig indexConfig);

    int deleteIndexConfig(Long[] ids);

    List<MallIndexConfigGoodsVO> selectIndexConfigForIndex(int type, int number);
}
