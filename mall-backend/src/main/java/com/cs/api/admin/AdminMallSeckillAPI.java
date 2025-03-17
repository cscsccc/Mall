package com.cs.api.admin;

import com.cs.api.admin.param.BatchIdParam;
import com.cs.common.Constants;
import com.cs.common.ServiceResultEnum;
import com.cs.config.annotation.TokenToAdminUser;
import com.cs.config.annotation.TokenToMallUser;
import com.cs.mapper.MallSecKillMapper;
import com.cs.pojo.AdminUserToken;
import com.cs.pojo.MallSeckill;
import com.cs.pojo.MallUserToken;
import com.cs.redis.RedisCache;
import com.cs.service.MallSecKillService;
import com.cs.util.PageQueryUtil;
import com.cs.util.PageResult;
import com.cs.util.Result;
import com.cs.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AdminMallSeckillAPI {
    @Autowired
    private MallSecKillService mallSecKillService;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private MallSecKillMapper mallSecKillMapper;

    @GetMapping("/seckill/list")
    public Result getSecKillList(@RequestParam(required = false) Integer pageNumber,
                                 @RequestParam(required = false) Integer pageSize, @TokenToAdminUser AdminUserToken adminUserToken){

        if (pageNumber == null || pageNumber < 1 || pageSize == null || pageSize < 10)  {
            return ResultGenerator.genFailResult("参数异常");
        }
        Map params = new HashMap(8);
        params.put("page", pageNumber);
        params.put("limit", pageSize);

        PageQueryUtil pageQueryUtil = new PageQueryUtil(params);

        PageResult result = mallSecKillService.findSecKillListForAdminPage(pageQueryUtil);

        return ResultGenerator.genSuccessResult(result);
    }

    @GetMapping("/seckill/{seckillId}")
    public Result getSecKillById(@PathVariable Long seckillId, @TokenToAdminUser AdminUserToken adminUserToken){
        if (seckillId == null){
            return ResultGenerator.genFailResult("参数异常");
        }

        return ResultGenerator.genSuccessResult(mallSecKillService.findSecKillByPrimaryKey(seckillId));
    }

    @PutMapping("/seckill")
    public Result updateSeckill(@RequestBody MallSeckill mallSeckill, @TokenToAdminUser AdminUserToken adminUserToken){
        if (mallSeckill == null || mallSeckill.getSeckillId() == null || mallSeckill.getSeckillId() < 1 || mallSeckill.getSeckillNum() < 1 || mallSeckill.getSeckillPrice() < 1){
            return ResultGenerator.genFailResult("参数异常");
        }

        boolean result = mallSecKillService.updateSeckill(mallSeckill);

        if (result){
            System.out.println(mallSeckill.getSeckillNum());
            redisCache.setCacheObject(Constants.SECKILL_GOODS_STOCK_KEY + mallSeckill.getSeckillId(), mallSeckill.getSeckillNum());
            redisCache.deleteObject(Constants.SECKILL_GOODS_DETAIL + mallSeckill.getSeckillId());
            redisCache.deleteObject(Constants.SECKILL_GOODS_LIST);
            redisCache.deleteObject(Constants.SECKILL_KEY + mallSeckill.getSeckillId());
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult(ServiceResultEnum.DB_ERROR.getResult());

    }

    @DeleteMapping("/seckill")
    public Result deleteSeckill(@RequestBody BatchIdParam batchIdParam, @TokenToAdminUser AdminUserToken adminUserToken){
        if (batchIdParam == null || batchIdParam.getIds().length< 1){
            return ResultGenerator.genFailResult("参数异常");
        }

        if (mallSecKillService.deleteSeckillByPrimaryKeys(batchIdParam.getIds())){
            for(Long id : batchIdParam.getIds()){
                redisCache.deleteObject(Constants.SECKILL_GOODS_DETAIL + id);
            }
            redisCache.deleteObject(Constants.SECKILL_GOODS_LIST);
            return ResultGenerator.genSuccessResult();
        }

        return ResultGenerator.genFailResult(ServiceResultEnum.DB_ERROR.getResult());

    }

    @PostMapping("/seckill")
    public Result saveSeckill(@RequestBody MallSeckill mallSeckill, @TokenToAdminUser AdminUserToken adminUserToken){
        if (mallSeckill == null || mallSeckill.getGoodsId() < 1 || mallSeckill.getSeckillNum() < 1 || mallSeckill.getSeckillPrice() < 1){
            return ResultGenerator.genFailResult("参数异常");
        }

        boolean result = mallSecKillService.saveSeckill(mallSeckill);

        if (result){
            // 通过goodsId查找 secKILLID
            Long seckillId = mallSecKillMapper.getPrimaryKryByGoodsId(mallSeckill.getGoodsId());
            System.out.println(mallSeckill.getSeckillNum());
            redisCache.setCacheObject(Constants.SECKILL_GOODS_STOCK_KEY + seckillId, mallSeckill.getSeckillNum());
            redisCache.deleteObject(Constants.SECKILL_GOODS_DETAIL + mallSeckill.getSeckillId());
            redisCache.deleteObject(Constants.SECKILL_GOODS_LIST);
            return ResultGenerator.genSuccessResult();
        }

        return ResultGenerator.genFailResult(ServiceResultEnum.DB_ERROR.getResult());
    }

}
