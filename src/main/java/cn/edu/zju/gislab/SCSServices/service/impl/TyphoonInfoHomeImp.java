package cn.edu.zju.gislab.SCSServices.service.impl;

import cn.edu.zju.gislab.SCSServices.mapper.*;
import cn.edu.zju.gislab.SCSServices.po.*;
import cn.edu.zju.gislab.SCSServices.service.TyphoonInfoHome;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    public List<TyphModel> getTyphForecastUSAEurope(long typhModelNum, String staTime, String modelType) {
        List<TyphModel> results = null;
        SimpleDateFormat sdfLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdfSimple = new SimpleDateFormat("yyyyMMddHH");

        try {
            Date staDateWorld = sdfLocal.parse(staTime);
            Date staDateChina = new Date(staDateWorld.getTime() - 8 * 60 * 60 * 1000);
            Date last48staDateChina = new Date(staDateChina.getTime() - 48 * 60 * 60 * 1000);
            String staTimeChina = "";

            // 找到最近的日期
            TyphModelExample typhModelExampleLast = new TyphModelExample();
            TyphModelExample.Criteria criteriaLast = typhModelExampleLast.createCriteria();
            criteriaLast.andIdxEqualTo(String.valueOf(typhModelNum));
            criteriaLast.andModelTypeEqualTo(modelType);
            criteriaLast.andStTimeGreaterThanOrEqualTo(sdfSimple.format(last48staDateChina));
            criteriaLast.andStTimeLessThanOrEqualTo(sdfSimple.format(staDateChina));
            typhModelExampleLast.setOrderByClause("st_time DESC");
            List<TyphModel> typhModelListsLast = typhModelMapper.selectByExample(typhModelExampleLast);
            if (typhModelListsLast.size() <= 0) return results;
            staTimeChina = typhModelListsLast.get(0).getStTime();

            TyphModelExample typhModelExample = new TyphModelExample();
            TyphModelExample.Criteria criteria = typhModelExample.createCriteria();
            criteria.andIdxEqualTo(String.valueOf(typhModelNum));
            criteria.andModelTypeEqualTo(modelType);
            criteria.andStTimeEqualTo(staTimeChina);
            List<TyphModel> typhModelList = typhModelMapper.selectByExample(typhModelExample);

            // 合并
            if (typhModelList.size() > 0) {
                Date staDateLast = sdfSimple.parse(staTimeChina);
                Date staDate = new Date(staDateLast.getTime() + 8 * 60 * 60 * 1000);
                Map<Integer, TyphModel> map = new HashMap<>();

                for (int i = 0; i < typhModelList.size(); i++) {
                    TyphModel now = typhModelList.get(i);
                    int key = now.getFcTime();
                    if (!map.containsKey(key)) {
                        int fctime = now.getFcTime();
                        Date endDate = new Date(staDate.getTime() + fctime * 60 * 60 * 1000);

                        now.setStTime(sdfLocal.format(staDate));
                        now.setModelType(sdfLocal.format(endDate));
                        now.setLng(now.getLng().abs().divide(new BigDecimal(10)));
                        now.setLat(now.getLat().abs().divide(new BigDecimal(10)));
                        map.put(key, now);
                    }
                }

                // 排序
                results = new ArrayList<>();
                for (Map.Entry<Integer, TyphModel> entry : map.entrySet()) {
                    int key = entry.getKey();
                    TyphModel val = entry.getValue();

                    int pos = 0;
                    for (; pos < results.size(); pos++) {
                        if (key < results.get(pos).getFcTime()) break;
                    }
                    results.add(pos, val);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return results;
    }

    @Override
    public List<Tepo> getTyphForecastTEPO(long typhModelNum, String staTime) {
        List<Tepo> results = null;

        return results;
    }

}
