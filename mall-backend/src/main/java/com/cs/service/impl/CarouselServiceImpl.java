package com.cs.service.impl;

import com.cs.api.mall.VO.MallIndexCarouselVO;
import com.cs.common.ServiceResultEnum;
import com.cs.mapper.CarouselsMapper;
import com.cs.pojo.Carousels;
import com.cs.service.CarouselService;
import com.cs.util.BeanUtil;
import com.cs.util.PageQueryUtil;
import com.cs.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CarouselServiceImpl implements CarouselService {

    @Autowired
    private CarouselsMapper carouselsMapper;

    @Override
    public PageResult getCarousels(PageQueryUtil pageQueryUtil) {
        List<Carousels> carouselsList = carouselsMapper.selectCarouselsList(pageQueryUtil);
        int total = carouselsMapper.getTotalCarousels(pageQueryUtil);
        PageResult pageResult = new PageResult(carouselsList, total, pageQueryUtil.getLimit(), pageQueryUtil.getPage());
        return pageResult;
    }

    @Override
    public String insertCarousel(Carousels carousel) {
        if (carouselsMapper.insertCarousel(carousel)>0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateByBatchId(Long[] ids) {
        if (carouselsMapper.updateByBatchId(ids) > 0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public List<MallIndexCarouselVO> selectCarouselList(int indexCarouselNumber) {
        List<Carousels> carouselsList = carouselsMapper.selectCarouselForIndex(indexCarouselNumber);
        List<MallIndexCarouselVO> mallIndexCarouselVOList = new ArrayList<>();

        if (!CollectionUtils.isEmpty(carouselsList)){
            mallIndexCarouselVOList = BeanUtil.copyList(carouselsList, MallIndexCarouselVO.class);
        }

        return mallIndexCarouselVOList;
    }
}
