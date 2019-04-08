package com.what2e.dao;

import com.what2e.entity.LinkMapping;
import org.apache.ibatis.annotations.Param;

public interface LinkMappingDao {
    void insert(@Param("tableName") String tableName,
                   @Param("shortLink") String shortLink,
                   @Param("longLink") String longLink,
                   @Param("createTime") String createTime,
                   @Param("failureTime") String failureTime);

    LinkMapping select(@Param("tableName") String tableName, @Param("shortLink") String shortLink);

    void createTable(@Param("tableName") String tableName);

    void upVisits(@Param("tableName") String tableName, @Param("shortLink") String shortLink);

    int selectVisits(@Param("tableName") String tableName, @Param("shortLink") String shortLink);
}
