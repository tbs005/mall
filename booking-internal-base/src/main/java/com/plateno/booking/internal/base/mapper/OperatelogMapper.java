package com.plateno.booking.internal.base.mapper;

import com.plateno.booking.internal.base.pojo.Operatelog;
import com.plateno.booking.internal.base.pojo.OperatelogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface OperatelogMapper extends BaseMapper {
    int countByExample(OperatelogExample example);

    int deleteByExample(OperatelogExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Operatelog record);

    int insertSelective(Operatelog record);

    List<Operatelog> selectByExampleWithRowbounds(OperatelogExample example, RowBounds rowBounds);

    List<Operatelog> selectByExample(OperatelogExample example);

    Operatelog selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Operatelog record, @Param("example") OperatelogExample example);

    int updateByExample(@Param("record") Operatelog record, @Param("example") OperatelogExample example);

    int updateByPrimaryKeySelective(Operatelog record);

    int updateByPrimaryKey(Operatelog record);
}