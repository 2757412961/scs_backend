package cn.edu.zju.gislab.SCSServices.service.impl;

import cn.edu.zju.gislab.SCSServices.mapper.*;
import cn.edu.zju.gislab.SCSServices.po.*;
import cn.edu.zju.gislab.SCSServices.service.TyphoonInfoHome;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Aauthor:zjh
 */

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
            String sqlStr = "select max(st_time) from typh_model " +
                    " WHERE st_time <= " + sdfSimple.format(staDateWorld) +
                    " and st_time >= " + sdfSimple.format(last48staDateWorld);
            List<TyphModel> typhModelListLast = typhModelMapper.selectSingleStringList(sqlStr);
            if (typhModelListLast.size() <= 0) return results;

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
            Date staDateWorld = new Date(staDateChina.getTime() - 18 * 60 * 60 * 1000);
            Date last48staDateWorld = new Date(staDateWorld.getTime() - 58 * 60 * 60 * 1000);

            String sqlStr = "select distinct st_time, predict_num from TEPO " +
                    " WHERE st_time <= " + sdfSimple.format(staDateWorld) +
                    " and st_time >= " + sdfSimple.format(last48staDateWorld);
            List<Tepo> tepoListLast = tepoMapper.selectSingleStringList(sqlStr);
            if (tepoListLast.size() <= 0) return results;

            // 找到最近的日期
            Date maxStaDateChina = new Date();
            Date maxStaDateWorld = new Date();
            int predict_num = 0;
            for (int i = 0; i < tepoListLast.size(); i++) {
                Tepo tepo = tepoListLast.get(i);
                Date stDateChina = sdfSimple.parse(tepo.getStTime());
                if (tepo.getPredictNum() == 1) {
                    stDateChina = new Date(stDateChina.getTime() + 18 * 60 * 60 * 1000);
                } else if (tepo.getPredictNum() == 2) {
                    stDateChina = new Date(stDateChina.getTime() + 20 * 60 * 60 * 1000);
                } else if (tepo.getPredictNum() == 3) {
                    stDateChina = new Date(stDateChina.getTime() + 26 * 60 * 60 * 1000);
                }

                if (stDateChina.compareTo(staDateChina) <= 0) {
                    if (predict_num == 0 || stDateChina.compareTo(maxStaDateChina) > 0) {
                        predict_num = tepo.getPredictNum();
                        maxStaDateChina = stDateChina;
                        maxStaDateWorld = sdfSimple.parse(tepo.getStTime());
                    }
                }
            }

            int staHour = (int)(staDateChina.getTime() - maxStaDateWorld.getTime()) / 60 / 60 / 1000 - 8;
            TepoExample tepoExample = new TepoExample();
            TepoExample.Criteria criteria = tepoExample.createCriteria();
            criteria.andIdxEqualTo(String.valueOf(typhModelNum));
            criteria.andStTimeEqualTo(sdfSimple.format(maxStaDateWorld));
            criteria.andPredictNumEqualTo(predict_num);
            criteria.andFcTimeBetween(staHour, 120);
            tepoExample.setOrderByClause("fc_time");
            List<Tepo> tepoList = tepoMapper.selectByExample(tepoExample);

            // 合并
            results = new ArrayList<>();
            if (tepoList.size() > 0) {
                Date staDate = new Date(maxStaDateWorld.getTime() + 8 * 60 * 60 * 1000);
                BigDecimal zero = new BigDecimal(0);
                for (int i = 0; i < tepoList.size(); i++) {
                    Tepo tepo = tepoList.get(i);
                    int fctime = tepo.getFcTime();
                    Date endDate = new Date(staDate.getTime() + fctime * 60 * 60 * 1000);

                    tepo.setStTime(sdfLocal.format(staDate));
                    tepo.setLocation(sdfLocal.format(endDate));
                    tepo.setLng((tepo.getLng().compareTo(zero) >= 0 ? tepo.getLng() : tepo.getLng().add(new BigDecimal(3600)))
                            .divide(new BigDecimal(10), 1, RoundingMode.HALF_UP));
                    tepo.setLat(tepo.getLat().divide(new BigDecimal(10), 1, RoundingMode.HALF_UP));
                    results.add(tepo);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return results;
    }

}
