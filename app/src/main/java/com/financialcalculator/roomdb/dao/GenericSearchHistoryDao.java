package com.financialcalculator.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.financialcalculator.roomdb.tables.GenericSearchHistoryEntity;

import java.util.List;

@Dao
public interface GenericSearchHistoryDao {
    @Query("SELECT * FROM GenericSearchHistoryEntity")
    List<GenericSearchHistoryEntity> getAll();

    @Query("SELECT * FROM GenericSearchHistoryEntity WHERE type = :typeId ")
    List<GenericSearchHistoryEntity> getListByType(int... typeId);

    /*@Query("SELECT * FROM EMISearchHistoryEntity WHERE first_name LIKE :first AND "
            + "last_name LIKE :last LIMIT 1")
    EMISearchHistoryEntity findByName(String first, String last);*/

    @Insert
    void insertAll(GenericSearchHistoryEntity... genericSearchHistoryEntities);

    @Delete
    void delete(GenericSearchHistoryEntity... genericSearchHistoryEntity);

    @Update
    void update(GenericSearchHistoryEntity... genericSearchHistoryEntity);
}