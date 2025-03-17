package com.cs.service;

import com.cs.api.mall.VO.MallSeckillGoodsVO;
import com.cs.api.mall.VO.SeckillSuccessVO;
import com.cs.pojo.MallSeckill;
import com.cs.util.PageQueryUtil;
import com.cs.util.PageResult;

import java.util.List;

public interface MallSecKillService {
    PageResult findSecKillList(PageQueryUtil pageUtil);

    PageResult findSecKillListForAdminPage(PageQueryUtil pageUtil);

    MallSeckill findSecKillByPrimaryKey(Long seckillId);

    boolean updateSeckill(MallSeckill mallSeckill);

    boolean deleteSeckillByPrimaryKeys(Long[] ids);

    boolean saveSeckill(MallSeckill mallSeckill);

    SeckillSuccessVO executeSeckill(Long seckillId, Long userId);
}
