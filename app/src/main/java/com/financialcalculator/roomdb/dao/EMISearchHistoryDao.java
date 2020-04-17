package com.financialcalculator.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.financialcalculator.roomdb.tables.EMISearchHistoryEntity;

import java.util.List;

@Dao
public interface EMISearchHistoryDao {
    @Query("SELECT * FROM emisearchhistoryentity")
    List<EMISearchHistoryEntity> getAll();

    @Query("SELECT * FROM emisearchhistoryentity WHERE historyID IN (:userIds)")
    List<EMISearchHistoryEntity> loadAllByIds(long[] userIds);

    /*@Query("SELECT * FROM EMISearchHistoryEntity WHERE first_name LIKE :first AND "
            + "last_name LIKE :last LIMIT 1")
    EMISearchHistoryEntity findByName(String first, String last);*/

    @Insert
    void insertAll(EMISearchHistoryEntity... emiSearchHistoryEntityList);

    @Delete
    void delete(EMISearchHistoryEntity emiSearchHistoryEntity);

    @Update
    void update(EMISearchHistoryEntity emiSearchHistoryEntity);
}