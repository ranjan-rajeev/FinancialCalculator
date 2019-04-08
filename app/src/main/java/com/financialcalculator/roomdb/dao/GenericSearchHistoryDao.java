package com.financialcalculator.roomdb.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.financialcalculator.roomdb.tables.EMISearchHistoryEntity;
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