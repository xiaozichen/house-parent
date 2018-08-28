package com.mooc.house.web.controller;

import com.mooc.house.biz.service.HouseService;
import com.mooc.house.common.constants.CommonConstants;
import com.mooc.house.common.model.House;
import com.mooc.house.common.page.PageData;
import com.mooc.house.common.page.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Created by Administrator on 2018/8/28.
 */
@Controller
public class HouseController {

    @Autowired
    private HouseService houseService;
    /**
     * 1.实现分页
     * 2.支持小区搜索、类型搜索
     * 3.支持排序
     * 4.支持展示图片、价格、标题、地址等信息
     * @return
     */
    @RequestMapping("/house/list")
    public String houseList(Integer pageSize, Integer pageNum, House query, ModelMap modelMap){
        PageData<House> ps =  houseService.queryHouse(query, PageParams.build(pageSize, pageNum));
        //List<House> hotHouses =  recommendService.getHotHouse(CommonConstants.RECOM_SIZE);
      //  modelMap.put("recomHouses", hotHouses);
        modelMap.put("ps", ps);
        modelMap.put("vo", query);
        return "house/listing";
    }
}
