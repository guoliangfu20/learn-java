package com.space.mapper;

import com.space.bean.SpaceData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SpaceDataMapper {
    List<SpaceData> getList();

    int add(SpaceData data);

    int addBatch(final List<SpaceData> lstData);

    int deleteAll();

    List<SpaceData> getListPage(Integer pageStart,Integer pageSize);
}
