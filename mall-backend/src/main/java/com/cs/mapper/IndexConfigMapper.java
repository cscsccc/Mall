package com.cs.mapper;

import com.cs.pojo.IndexConfig;
import com.cs.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IndexConfigMapper {

    List<IndexConfig> getIndexConfigList(PageQueryUtil pageQueryUtil);

    int getTotalIndexConfig(PageQueryUtil pageQueryUtil);

    IndexConfig selectByConfigTypeAndGoodsId(@Param("configType") Byte configType, @Param("goodsId") Long goodsId);

    int addIndexConfig(IndexConfig indexConfig);

    IndexConfig selectIndexConfigById(Long configId);

    int updateIndexConfig(IndexConfig indexConfig);

    int deleteIndexConfig(Long[] ids);

    List<IndexConfig> selectIndexConfigByTypeAndNumber(int type, int number);
}
