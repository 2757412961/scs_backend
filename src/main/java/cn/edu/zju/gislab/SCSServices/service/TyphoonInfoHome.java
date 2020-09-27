package cn.edu.zju.gislab.SCSServices.service;

import cn.edu.zju.gislab.SCSServices.po.*;

import java.util.List;

public interface TyphoonInfoHome {
    // 获取所有台风列表
    List<TyphInfo> getTyphoonList(long Year);
    TyphInfo getTyphoonOnGoing();
    List<String> getTyphoonYears();
    List<TyphMonitorWeb> getTyphoonRoute(long typhNum);
    TyphInfo getTyphoonInfo(long typhNum);

//    TyphForecastWeb getTyphForecastChinaJapan();
//    getTyphForecastUSAEurope();
//    getTyphForecastTEPO();
}
