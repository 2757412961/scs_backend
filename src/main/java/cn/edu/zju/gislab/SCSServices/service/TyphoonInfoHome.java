package cn.edu.zju.gislab.SCSServices.service;

import cn.edu.zju.gislab.SCSServices.po.ScsUsers;
import cn.edu.zju.gislab.SCSServices.po.Tfybd;
import cn.edu.zju.gislab.SCSServices.po.TyphInfo;
import cn.edu.zju.gislab.SCSServices.po.TyphMonitor;

import java.util.List;

public interface TyphoonInfoHome {
    // 获取所有台风列表
    List<TyphInfo> getTyphoonList(long Year);
    TyphInfo getTyphoonOnGoing();
    List<String> getTyphoonYears();
    List<TyphMonitor> getTyphoonRoute(long typhNum);
}
