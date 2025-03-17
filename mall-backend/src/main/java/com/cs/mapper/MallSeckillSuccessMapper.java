package com.cs.mapper;

import com.cs.pojo.MallSeckillSuccess;

public interface MallSeckillSuccessMapper {
    MallSeckillSuccess findByUserIdAndSeckillId(Long userId, Long seckillId);

    MallSeckillSuccess findByUserIdAndSeckillSuccessId(Long userId, Long seckillSuccessId);
}
