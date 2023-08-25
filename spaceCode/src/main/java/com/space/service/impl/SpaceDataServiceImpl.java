package com.space.service.impl;

import com.space.bean.SpaceData;
import com.space.dto.PageInfo;
import com.space.mapper.SpaceDataMapper;
import com.space.service.ISpaceDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpaceDataServiceImpl implements ISpaceDataService {

    @Autowired
    private SpaceDataMapper spaceDataMapper;


    @Override
    public List<SpaceData> getList() {
        return spaceDataMapper.getList();
    }

    @Override
    public int add(SpaceData data) {
        return spaceDataMapper.add(data);
    }

    @Override
    public int addBatch(List<SpaceData> lstData) {
        return spaceDataMapper.addBatch(lstData);
    }

    @Override
    public int deleteAll() {
        return spaceDataMapper.deleteAll();
    }

    /**
     * 分页获取.
     *
     * @param pageSize
     * @param pageNumber
     * @return
     */
    @Override
    public PageInfo<SpaceData> getListPage(Integer pageSize, Integer pageNumber) {
        PageInfo<SpaceData> pageInfo = new PageInfo<>();
        pageInfo.setPageSize(pageSize);
        pageInfo.setPageIndex(pageNumber);

        Integer pageStart = pageSize * (pageNumber - 1);
        List<SpaceData> list = spaceDataMapper.getListPage(pageStart, pageSize);

        pageInfo.setData(list);

        return pageInfo;
    }
}
