package com.cs.api.mall;

import com.cs.api.mall.VO.MallIndexCategoryVO;
import com.cs.common.ServiceResultEnum;
import com.cs.config.annotation.TokenToMallUser;
import com.cs.pojo.MallUserToken;
import com.cs.service.MallCategoryService;
import com.cs.util.Result;
import com.cs.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MallGoodsCategoryAPI {
    @Autowired
    private MallCategoryService mallCategoryService;

    @GetMapping("/categories")
    public Result getCategories(){
        List<MallIndexCategoryVO> mallIndexCategoryVOS = mallCategoryService.getCategoriesForIndex();
        System.out.println(mallIndexCategoryVOS);

        if (!CollectionUtils.isEmpty(mallIndexCategoryVOS)){
            return ResultGenerator.genSuccessResult(mallIndexCategoryVOS);
        }
        return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
    }
}
