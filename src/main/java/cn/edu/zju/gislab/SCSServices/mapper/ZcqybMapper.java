package cn.edu.zju.gislab.SCSServices.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ZcqybMapper {
    List<String> selectSingleStringList(@Param(value="sqlStr") String sqlStr);
}
