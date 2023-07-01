package com.financialcalculator.roomdb;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.financialcalculator.model.MoreInfoEntity;
import com.financialcalculator.roomdb.dao.EMISearchHistoryDao;
import com.financialcalculator.roomdb.dao.GenericSearchHistoryDao;
import com.financialcalculator.roomdb.dao.MoreInfoDao;
import com.financialcalculator.roomdb.tables.EMISearchHistoryEntity;
import com.financialcalculator.roomdb.tables.GenericSearchHistoryEntity;

@Database(entities = {EMISearchHistoryEntity.class, GenericSearchHistoryEntity.class,
        MoreInfoEntity.class}, version = 1)
public abstract class RoomDatabase extends androidx.room.RoomDatabase {
    public abstract EMISearchHistoryDao emiSearchHistoryDao();

    public abstract GenericSearchHistoryDao genericSearchHistoryDao();

    public static final RoomDatabase APP_DATABASE = null;

    public static RoomDatabase getAppDatabase(Context context) {
        if (APP_DATABASE == null) {
            return Room.databaseBuilder(context,
                    RoomDatabase.class, "financial_cal_db")
                    .fallbackToDestructiveMigration()
                    //.addMigrations(MIGRATION_1_2)
                    .build();
        }
        return APP_DATABASE;
    }

    public abstract MoreInfoDao moreInfoDao();

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
        }
    };
}