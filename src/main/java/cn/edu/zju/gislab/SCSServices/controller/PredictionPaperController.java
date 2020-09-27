/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: PredictionPaperController
 * Author:   zhangzhe
 * Date:     2020/9/27 19:26
 * Description: 预报单控制器
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cn.edu.zju.gislab.SCSServices.controller;

import cn.edu.zju.gislab.SCSServices.service.PredictionPaperService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 〈预报单控制器〉
 *
 * @author zhangzhe
 * @create 2020/9/27
 * @since 1.0.0
 */
@Controller
public class PredictionPaperController {
    @Autowired
    private PredictionPaperService predictionPaperService;

    // 获取海区预报单 返回 对应的 year month day
    /*           year month 都为空，返回 {year:xx, month: xx , day: xx}
     *           year 为空 返回{month:xx , day : xx}
     *           year month 都不为空，返回 {day : xx}
     *           year month day 都不为空，返回 {fileName: xxx}
     * */
    @RequestMapping("/getSeaAreaPrediction")
    @ResponseBody
    public JSONObject getSeaAreaPrediction(String year, String month,String day) {
        JSONObject result = predictionPaperService.getSeaAreaForecastConditon(year, month, day);
        return result;
    }

}
