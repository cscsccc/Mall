package com.cs.service;

import com.cs.api.mall.VO.MallIndexCarouselVO;
import com.cs.pojo.Carousels;
import com.cs.util.PageQueryUtil;
import com.cs.util.PageResult;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CarouselService {

    PageResult getCarousels(PageQueryUtil pageQueryUtil);

    String insertCarousel(Carousels carousel);

    String updateByBatchId(Long[] ids);

    List<MallIndexCarouselVO> selectCarouselList(int indexCarouselNumber);
}
