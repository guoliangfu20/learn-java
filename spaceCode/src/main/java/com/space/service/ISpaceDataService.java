package com.space.service;

import com.space.bean.SpaceData;
import com.space.dto.PageInfo;

import java.util.List;

public interface ISpaceDataService {

    /**
     * 获取所有.
     *
     * @return
     */
    List<SpaceData> getList();

    /**
     * 添加单个》
     *
     * @param data
     * @return
     */
    int add(final SpaceData data);

    /**
     * 批量添加.
     *
     * @param lstData
     * @return
     */
    int addBatch(final List<SpaceData> lstData);

    /**
     * 删除所有
     * 使用 truncate .
     *
     * @return 执行结果
     */
    int deleteAll();

    /**
     * 分页获取.
     *
     * @param pageStart
     * @param pageSize
     * @return
     */
    PageInfo<SpaceData> getListPage(Integer pageStart, Integer pageSize);
}
