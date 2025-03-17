package com.cs.api.mall.param;
import lombok.Data;

@Data
public class MallCartEditCountParam {
    private Long cartItemId;
    private Integer goodsCount;
}
