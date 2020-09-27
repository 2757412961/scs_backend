/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: PredictionPaperServiceImp
 * Author:   zhangzhe
 * Date:     2020/9/27 18:18
 * Description: 预报单服务
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cn.edu.zju.gislab.SCSServices.service.impl;

import cn.edu.zju.gislab.SCSServices.mapper.HqybdMapper;
import cn.edu.zju.gislab.SCSServices.service.PredictionPaperService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 〈预报单服务〉
 *
 * @author zhangzhe
 * @create 2020/9/27
 * @since 1.0.0
 */
public class PredictionPaperServiceImp implements PredictionPaperService {

    @Autowired
    private HqybdMapper hqybdMapper;

    /**
     *  获取有海区预报的年、月、日
     * @param year
     * @param month
     * @return   year month 都为空，返回 {year:xx, month: xx , day: xx}
     *           year 为空 返回{month:xx , day : xx}
     *           year month 都不为空，返回 {day : xx}
     *           year month day 都不为空，返回 {fileName: xxx}
     */
    @Override
    public JSONObject getSeaAreaForecastConditon(String year, String month, String day) {
        // 如果year为空，检索所有年份
        JSONObject result = new JSONObject();
        if ("".equals(year)){
            String sqlStr = "select distinct YEAR from HQYBD order by year ASC";
            List<String> yearStrList = hqybdMapper.selectSingleStringList(sqlStr);
            result.put("year",yearStrList); //添加year 集合
            year = yearStrList.get(yearStrList.size()-1); // 获取最新的一年
        }
        // 如果月也为空，就根据year检索 month
        if ("".equals(month)){
            String sqlStr = "select distinct MONTH from HQYBD WHERE ( YEAR = "+ year + " ) order by month ASC";
            List<String> monthStrList = hqybdMapper.selectSingleStringList(sqlStr);
            result.put("month",monthStrList); //添加year 集合
            month = monthStrList.get(monthStrList.size()-1); // 获取最新的月份
        }
        // 根据 year 和 month 检索 day
        if ("".equals(day)){
            String sqlStr = "select distinct DAY from HQYBD WHERE ( YEAR = "+ year + " and MONTH = " + month + " ) order by day ASC";
            List<String> dayStrList = hqybdMapper.selectSingleStringList(sqlStr);
            // 获取单独的海区预报 day List
            result.put("day",dayStrList); //添加year 集合
        }
        // 如果year month day都不为空,检索文件名
        if ( !("".equals(day)) && null != day ){
            String sqlStr = "select FILENAME from HQYBD WHERE ( YEAR = "+ year + " and MONTH = " + month + " and DAY = "+ day + " )";
            List<String> fileName = hqybdMapper.selectSingleStringList(sqlStr);
            // 获取单独的海区预报 day List
            result.put("fileName",fileName); //添加year 集合
        }
        return result;
    }
}
