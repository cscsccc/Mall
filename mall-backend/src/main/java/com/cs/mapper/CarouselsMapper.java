package com.cs.mapper;


import com.cs.pojo.Carousels;
import com.cs.util.PageQueryUtil;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CarouselsMapper {
    List<Carousels> selectCarouselsList(PageQueryUtil pageUtil);

    int getTotalCarousels(PageQueryUtil pageQueryUtil);

    int insertCarousel(Carousels carousel);

    int updateByBatchId(@Param("ids") Long[] ids);

    List<Carousels> selectCarouselForIndex(int indexCarouselNumber);
}
