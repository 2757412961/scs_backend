package cn.edu.zju.gislab.SCSServices.service;

import com.alibaba.fastjson.JSONObject;

public interface PredictionPaperService {
    /**
     *  查询海区预报单条件：年、月、日
     * @return
     */
    public JSONObject getSeaAreaForecastConditon(String year, String month, String day);

}
