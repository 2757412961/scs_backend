package cn.edu.zju.gislab.SCSServices.service.impl;

import cn.edu.zju.gislab.SCSServices.mapper.*;
import cn.edu.zju.gislab.SCSServices.po.*;
import cn.edu.zju.gislab.SCSServices.service.TyphoonInfoHome;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TyphoonInfoHomeImp implements TyphoonInfoHome {

    @Autowired
    private TyphInfoMapper typhInfoMapper;

    @Autowired
    private TyphMonitorWebMapper typhMonitorWebMapper;

    @Autowired
    private TyphForecastWebMapper typhForecastWebMapper;

    @Autowired
    private TyphModelMapper typhModelMapper;

    @Autowired
    private TepoMapper tepoMapper;

    // 获取特定年份的所有台风
    @Override
    public List<TyphInfo> getTyphoonList(long Year) {
        List<TyphInfo> result;
        try {
            TyphInfoExample typhInfoExample = new TyphInfoExample();
            TyphInfoExample.Criteria criteria = typhInfoExample.createCriteria();
            criteria.andTyphNumBetween(Year * 100, Year * 100 + 100);

            List<TyphInfo> typhInfoList = typhInfoMapper.selectByExample(typhInfoExample);
            if (typhInfoList != null && typhInfoList.size() > 0) {
                result = typhInfoList;
            } else {
                return null;
            }
        } catch (Error e) {
            return null;
        }
        return result;
    }

    // 获取正在进行的台风
    @Override
    public TyphInfo getTyphoonOnGoing() {
        TyphInfo result;
        try {
            TyphInfoExample typhInfoExample = new TyphInfoExample();
            TyphInfoExample.Criteria criteria = typhInfoExample.createCriteria();
            criteria.andIsongoingEqualTo("1");
            List<TyphInfo> typhInfoList = typhInfoMapper.selectByExample(typhInfoExample);
            if (typhInfoList.size() > 0) {
                result = typhInfoList.get(0);
            } else {
                return null;
            }
        } catch (Error e) {
            return null;
        }
        return result;
    }

    // 获取现有台风的所有年份
    @Override
    public List<String> getTyphoonYears() {
        List<String> result;
        try {
            TyphInfoExample typhInfoExample = new TyphInfoExample();
            List<TyphInfo> typhInfoList = typhInfoMapper.selectByExample(typhInfoExample);

            if (typhInfoList.size() > 0) {
                result = new ArrayList<String>();
                for (int i = 0; i < typhInfoList.size(); i++) {
                    String typhNum = String.valueOf(typhInfoList.get(i).getTyphNum());
                    String year = typhNum.substring(0, 4);
                    if (!result.contains(year)) {
                        result.add(year);
                    }
                }
                if (result.size() <= 0) {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Error e) {
            return null;
        }
        return result;
    }

    // 获取Ongoing或所选择的台风的行进路线
    @Override
    public List<TyphMonitorWeb> getTyphoonRoute(long typhNum) {
        List<TyphMonitorWeb> result;
        try {
            TyphMonitorWebExample typhMonitorWebExample = new TyphMonitorWebExample();
            TyphMonitorWebExample.Criteria criteria = typhMonitorWebExample.createCriteria();
            criteria.andTyphNumEqualTo(typhNum);

            result = typhMonitorWebMapper.selectByExample(typhMonitorWebExample);
            if (result.size() <= 0) result = null;
        } catch (Error e) {
            return null;
        }

        return result;
    }

    @Override
    public TyphInfo getTyphoonInfo(long typhNum) {
        TyphInfo result = null;

        TyphInfoExample typhInfoExample = new TyphInfoExample();
        TyphInfoExample.Criteria criteria = typhInfoExample.createCriteria();
        criteria.andTyphNumEqualTo(typhNum);
        List<TyphInfo> typhInfoList = typhInfoMapper.selectByExample(typhInfoExample);

        if (typhInfoList.size() > 0) result = typhInfoList.get(0);

        return result;
    }

    @Override
    public List<TyphForecastWeb> getTyphForecastChinaJapan(long typhNum, String staTime) {
        List<TyphForecastWeb> results = null;

        try {
            Date staDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(staTime);

            TyphForecastWebExample typhForecastWebExample = new TyphForecastWebExample();
            TyphForecastWebExample.Criteria criteria = typhForecastWebExample.createCriteria();
            criteria.andTyphNumEqualTo(typhNum);
            criteria.andQbsjEqualTo(staDate);
            List<TyphForecastWeb> typhForecastWebList = typhForecastWebMapper.selectByExample(typhForecastWebExample);

            if (typhForecastWebList.size() > 0) results = typhForecastWebList;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return results;
    }

    @Override
    public List<TyphModel> getTyphForecastUSAEurope(long typhModelNum, String staTime) {
        List<TyphModel> results = null;

        TyphModelExample typhModelExample = new TyphModelExample();
        TyphModelExample.Criteria criteria = typhModelExample.createCriteria();



        return results;
    }

}
