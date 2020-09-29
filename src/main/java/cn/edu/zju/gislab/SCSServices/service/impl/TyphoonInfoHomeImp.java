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
            Date staDateChina = sdfLocal.parse(staTime);
            Date staDateWorld = new Date(staDateChina.getTime() - 8 * 60 * 60 * 1000);
            Date last48staDateWorld = new Date(staDateWorld.getTime() - 48 * 60 * 60 * 1000);
            String staTimeWorld = "";

            // 找到最近的日期
            TyphModelExample typhModelExampleLast = new TyphModelExample();
            TyphModelExample.Criteria criteriaLast = typhModelExampleLast.createCriteria();
            criteriaLast.andIdxEqualTo(String.valueOf(typhModelNum));
            criteriaLast.andModelTypeEqualTo(modelType);
            criteriaLast.andStTimeGreaterThanOrEqualTo(sdfSimple.format(last48staDateWorld));
            criteriaLast.andStTimeLessThanOrEqualTo(sdfSimple.format(staDateWorld));
            typhModelExampleLast.setOrderByClause("st_time DESC");
            List<TyphModel> typhModelListsLast = typhModelMapper.selectByExample(typhModelExampleLast);
            if (typhModelListsLast.size() <= 0) return results;
            staTimeWorld = typhModelListsLast.get(0).getStTime();

            TyphModelExample typhModelExample = new TyphModelExample();
            TyphModelExample.Criteria criteria = typhModelExample.createCriteria();
            criteria.andIdxEqualTo(String.valueOf(typhModelNum));
            criteria.andModelTypeEqualTo(modelType);
            criteria.andStTimeEqualTo(staTimeWorld);
            List<TyphModel> typhModelList = typhModelMapper.selectByExample(typhModelExample);

            // 合并
            if (typhModelList.size() > 0) {
                Date staDateLast = sdfSimple.parse(staTimeWorld);
                Date staDate = new Date(staDateLast.getTime() + 8 * 60 * 60 * 1000);
                Map<Integer, TyphModel> map = new HashMap<>();
                Map<Integer, Integer> con = new HashMap<>();

                // 计算总和
                for (int i = 0; i < typhModelList.size(); i++) {
                    TyphModel now = typhModelList.get(i);
                    int key = now.getFcTime();
                    if (!map.containsKey(key)) {
                        int fctime = now.getFcTime();
                        Date endDate = new Date(staDate.getTime() + fctime * 60 * 60 * 1000);

                        now.setStTime(sdfLocal.format(staDate));
                        now.setLocation(sdfLocal.format(endDate));
                        now.setLng(now.getLng().abs());
                        now.setLat(now.getLat().abs());

                        map.put(key, now);
                        con.put(key, 1);
                    } else {
                        TyphModel model = map.get(key);
                        model.setLng(model.getLng().add(now.getLng().abs()));
                        model.setLat(model.getLat().add(now.getLat().abs()));
                        model.setSpeed(model.getSpeed().add(now.getSpeed()));
                        model.setPressure(model.getPressure().add(now.getPressure()));

                        map.put(key, model);
                        con.put(key, con.get(key) + 1);
                    }
                }

                // 排序 map转换为list
                results = new ArrayList<>();
                for (Map.Entry<Integer, TyphModel> entry : map.entrySet()) {
                    int key = entry.getKey();
                    TyphModel val = entry.getValue();
                    int count = con.get(key);
                    val.setLng(val.getLng().divide(new BigDecimal(count * 10), 4));
                    val.setLat(val.getLat().divide(new BigDecimal(count * 10), 3));
                    val.setSpeed(val.getSpeed().divide(new BigDecimal(count), 3));
                    val.setPressure(val.getPressure().divide(new BigDecimal(count), 4));

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
        SimpleDateFormat sdfLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdfSimple = new SimpleDateFormat("yyyyMMddHH");

        try {
            Date staDateChina = sdfLocal.parse(staTime);
            Date staDateWorld = new Date(staDateChina.getTime() - 8 * 60 * 60 * 1000);
            Date last48staDateWorld = new Date(staDateWorld.getTime() - 48 * 60 * 60 * 1000);

            // 找到最近的日期
            TepoExample tepoExampleLast = new TepoExample();
            TepoExample.Criteria criteriaLast = tepoExampleLast.createCriteria();
            criteriaLast.andIdxEqualTo(String.valueOf(typhModelNum));
            criteriaLast.andStTimeGreaterThanOrEqualTo(sdfSimple.format(last48staDateWorld));
            criteriaLast.andStTimeLessThanOrEqualTo(sdfSimple.format(staDateWorld));
            tepoExampleLast.setOrderByClause("st_time DESC");
            List<Tepo> tepoListLast = tepoMapper.selectByExample(tepoExampleLast);
            if (tepoListLast.size() <= 0) return results;
            String timeLast = tepoListLast.get(0).getStTime();
            int predict_num = tepoListLast.get(0).getPredictNum();

            TepoExample tepoExample = new TepoExample();
            TepoExample.Criteria criteria = tepoExample.createCriteria();
            criteria.andIdxEqualTo(String.valueOf(typhModelNum));
            criteria.andStTimeEqualTo(timeLast);
            criteria.andPredictNumEqualTo(predict_num);
            tepoExample.setOrderByClause("fc_time");
            List<Tepo> tepoList = tepoMapper.selectByExample(tepoExample);

            // 合并
            results = new ArrayList<>();
            if (tepoList.size() > 0) {
                Date staDateLast = sdfSimple.parse(timeLast);
                Date staDate = new Date(staDateLast.getTime() + 8 * 60 * 60 * 1000);
                for (int i = 0; i < tepoList.size(); i++) {
                    Tepo tepo = tepoList.get(i);
                    int fctime = tepo.getFcTime();
                    Date endDate = new Date(staDate.getTime() + fctime * 60 * 60 * 1000);

                    tepo.setStTime(sdfLocal.format(staDate));
                    tepo.setLocation(sdfLocal.format(endDate));
                    tepo.setLng(tepo.getLng().abs().divide(new BigDecimal(10), 3));
                    tepo.setLat(tepo.getLat().abs().divide(new BigDecimal(10), 3));
                    results.add(tepo);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return results;
    }

}
