package com.financialcalculator.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.financialcalculator.model.MoreInfoEntity;

import java.util.List;

@Dao
public interface MoreInfoDao {
    @Query("SELECT * FROM MoreInfoEntity")
    List<MoreInfoEntity> getAll();

    @Query("SELECT * FROM MoreInfoEntity where calId=:firebaseId")
    List<MoreInfoEntity> getAllById(String firebaseId);

    /*@Query("SELECT * FROM EMISearchHistoryEntity WHERE first_name LIKE :first AND "
            + "last_name LIKE :last LIMIT 1")
    EMISearchHistoryEntity findByName(String first, String last);*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<MoreInfoEntity> moreInfoEntities);

    @Delete
    void delete(MoreInfoEntity moreInfoEntity);

    @Update
    void update(MoreInfoEntity moreInfoEntity);
}