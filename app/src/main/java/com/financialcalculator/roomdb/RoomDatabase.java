package com.financialcalculator.roomdb;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import com.financialcalculator.roomdb.dao.EMISearchHistoryDao;
import com.financialcalculator.roomdb.tables.EMISearchHistoryEntity;

@Database(entities = {EMISearchHistoryEntity.class}, version = 1)
public abstract class RoomDatabase extends android.arch.persistence.room.RoomDatabase {
    public abstract EMISearchHistoryDao emiSearchHistoryDao();

    public static final RoomDatabase APP_DATABASE = null;

    public static RoomDatabase getAppDatabase(Context context) {
        if (APP_DATABASE == null) {
            return Room.databaseBuilder(context,
                    RoomDatabase.class, "financial_cal_db")
                    //.addMigrations(MIGRATION_1_2)
                    .build();
        }
        return APP_DATABASE;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
        }
    };
}