package cn.edu.zju.gislab.SCSServices.controller;

import cn.edu.zju.gislab.SCSServices.po.Tfybd;
import cn.edu.zju.gislab.SCSServices.po.TyphInfo;
import cn.edu.zju.gislab.SCSServices.po.TyphMonitor;
import cn.edu.zju.gislab.SCSServices.po.TyphMonitorWeb;
import cn.edu.zju.gislab.SCSServices.service.TyphoonInfoHome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class TyphoonController {

    @Autowired
    private TyphoonInfoHome typhoonInfoHome;

    // 获取特定年份的所有台风
    @RequestMapping("/typhoonList")
    public @ResponseBody List<TyphInfo> queryTyphoonList( long year ) {
        List<TyphInfo> result = typhoonInfoHome.getTyphoonList( year );
        return result;
    }

    // 获取现有台风的所有年份
    @RequestMapping("/typhoonYear")
    public @ResponseBody List<String> queryTyphoonYears() {
        List<String> result = typhoonInfoHome.getTyphoonYears();
        return result;
    }

    // 获取正在进行的台风
    @RequestMapping("/typhoonOngoing")
    public @ResponseBody TyphInfo queryTyphoonOngoing() {
        TyphInfo result = typhoonInfoHome.getTyphoonOnGoing();
        return result;
    }

    // 获取Ongoing或所选择的台风的行进路线
    @RequestMapping("/typhoonRoute")
    public @ResponseBody List<TyphMonitorWeb> queryTyphoonRoute(long typhNum ) {
        List<TyphMonitorWeb> result = typhoonInfoHome.getTyphoonRoute(typhNum);
        return result;
    }
}
