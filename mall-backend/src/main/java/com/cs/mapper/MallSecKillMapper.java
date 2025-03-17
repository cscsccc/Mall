package com.cs.mapper;

import com.cs.pojo.MallSeckill;
import com.cs.util.PageQueryUtil;

import java.util.List;
import java.util.Map;

public interface MallSecKillMapper {
    List<MallSeckill> findSecKillList();

    int findSecKillListTotal(PageQueryUtil pageUtil);

    MallSeckill findSeckillByPrimaryKey(Long seckillId);

    int updateSecKillBySelective(MallSeckill mallSeckill);

    int deleteSeckillByPrimaryKeys(Long[] seckillIds);

    int saveSeckill(MallSeckill mallSeckill);

    List<MallSeckill> findSecKillListForAdmin(PageQueryUtil pageUtil);

    void killByProcedure(Map params);

    Long getPrimaryKryByGoodsId(Long goodsId);

    MallSeckill findSecKillByGoodsId(Long goodsId);
}
