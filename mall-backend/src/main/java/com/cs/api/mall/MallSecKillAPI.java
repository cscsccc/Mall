package com.cs.api.mall;

import com.cs.api.mall.VO.ExposerVO;
import com.cs.api.mall.VO.MallSeckillGoodsVO;
import com.cs.api.mall.VO.SeckillSuccessVO;
import com.cs.common.Constants;
import com.cs.common.MallException;
import com.cs.common.SeckillStatusEnum;
import com.cs.config.annotation.TokenToAdminUser;
import com.cs.config.annotation.TokenToMallUser;
import com.cs.mapper.MallGoodsMapper;
import com.cs.pojo.MallGoods;
import com.cs.pojo.MallSeckill;
import com.cs.pojo.MallUserToken;
import com.cs.redis.RedisCache;
import com.cs.service.MallSecKillService;
import com.cs.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MallSecKillAPI {

    @Autowired
    private MallSecKillService mallSecKillService;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private MallGoodsMapper mallGoodsMapper;

    @GetMapping("/seckill/list")
    public Result getSecKillList(@RequestParam(required = false) Integer pageNumber,
                                @RequestParam(required = false) Integer pageSize, @TokenToMallUser MallUserToken mallUserToken){

        if (pageNumber == null || pageNumber < 1 || pageSize == null || pageSize < 10)  {
            return ResultGenerator.genFailResult("参数异常");
        }
        Map params = new HashMap(8);
        params.put("page", pageNumber);
        params.put("limit", pageSize);

        PageQueryUtil pageQueryUtil = new PageQueryUtil(params);
        PageResult pageResult = mallSecKillService.findSecKillList(pageQueryUtil);

        return ResultGenerator.genSuccessResult(pageResult);
    }

    @GetMapping("/seckill/{seckillId}")
    public Result getSecKillInfo(@PathVariable("seckillId") Long seckillId, @TokenToMallUser MallUserToken mallUserToken) {
        MallSeckillGoodsVO mallSeckillGoodsVO = redisCache.getCacheObject(Constants.SECKILL_GOODS_DETAIL + seckillId);
        if (mallSeckillGoodsVO == null) {
            MallSeckill mallSeckill = mallSecKillService.findSecKillByPrimaryKey(seckillId);
            if (mallSeckill == null || !mallSeckill.getSeckillStatus()) {
                return ResultGenerator.genFailResult("商品已下架");
            }
            mallSeckillGoodsVO = new MallSeckillGoodsVO();
            BeanUtil.copyProperties(mallSeckill, mallSeckillGoodsVO);
            MallGoods mallGood = mallGoodsMapper.getMallGoodById(mallSeckillGoodsVO.getGoodsId());
            mallSeckillGoodsVO.setGoodsName(mallGood.getGoodsName());
            mallSeckillGoodsVO.setGoodsIntro(mallGood.getGoodsIntro());
            mallSeckillGoodsVO.setGoodsCoverImg(mallGood.getGoodsCoverImg());
            mallSeckillGoodsVO.setGoodsDetailContent(mallGood.getGoodsDetailContent());
            Date seckillBegin = mallSeckillGoodsVO.getSeckillBegin();
            Date seckillEnd = mallSeckillGoodsVO.getSeckillEnd();
            mallSeckillGoodsVO.setStartDate(seckillBegin.getTime());
            mallSeckillGoodsVO.setEndDate(seckillEnd.getTime());

            redisCache.setCacheObject(Constants.SECKILL_GOODS_DETAIL + seckillId, mallSeckillGoodsVO);
        }
        System.out.println(mallSeckillGoodsVO);
        return ResultGenerator.genSuccessResult(mallSeckillGoodsVO);
    }

    @PostMapping("/seckillExecution/{seckillId}/{md5}")
    public Result execute(@PathVariable("seckillId") Long seckillId, @PathVariable("md5") String md5, @TokenToMallUser MallUserToken mallUserToken){
        if (seckillId == null || md5 == null || !md5.equals(MD5Util.MD5Encode(seckillId.toString() + Constants.SECKILL_ORDER_SALT, "UTF-8")))  {
            MallException.fail("秒杀商品不存在");
        }
        SeckillSuccessVO seckillSuccessVO = mallSecKillService.executeSeckill(seckillId, mallUserToken.getUserId());

        return ResultGenerator.genSuccessResult(seckillSuccessVO);
    }

    @PostMapping("/seckill/{seckillId}/exposer")
    public Result exposer(@PathVariable("seckillId") Long seckillId, @TokenToMallUser MallUserToken userToken){
        MallSeckillGoodsVO mallSeckillGoodsVO = redisCache.getCacheObject(Constants.SECKILL_GOODS_DETAIL + seckillId);
        Date beginDate = mallSeckillGoodsVO.getSeckillBegin();
        Date endDate = mallSeckillGoodsVO.getSeckillEnd();
        Date now = new Date();
        if (now.getTime() < beginDate.getTime() || now.getTime() > endDate.getTime()){
            return ResultGenerator.genSuccessResult(new ExposerVO(SeckillStatusEnum.NOT_START, seckillId, now.getTime(), beginDate.getTime(), endDate.getTime()));
        }
        // 检查库存
        Integer stock = redisCache.getCacheObject(Constants.SECKILL_GOODS_STOCK_KEY + seckillId);
        if (stock == null || stock < 0){
            return ResultGenerator.genSuccessResult(new ExposerVO(SeckillStatusEnum.STARTED_SHORTAGE_STOCK, seckillId));
        }
        String md5 = MD5Util.MD5Encode(seckillId.toString() + Constants.SECKILL_ORDER_SALT, "UTF-8");
        return ResultGenerator.genSuccessResult(new ExposerVO(SeckillStatusEnum.START,md5, seckillId));
    }

}
