package com.financialcalculator.roomdb.tables;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Rajeev Ranjan -  ABPB on 08-04-2019.
 */
@Entity(tableName = "GenericSearchHistoryEntity")
public class GenericSearchHistoryEntity implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @ColumnInfo(name = "type")
    private int type;

    @ColumnInfo(name = "createdTime")
    private long createdTime;

    @ColumnInfo(name = "updatedTime")
    private long updatedTime;


    @ColumnInfo(name = "listKeyValues")
    private String listKeyValues;

    public String getListKeyValues() {
        return listKeyValues;
    }

    public void setListKeyValues(String listKeyValues) {
        this.listKeyValues = listKeyValues;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public GenericSearchHistoryEntity() {

    }

    public GenericSearchHistoryEntity(long updatedTime, String listKeyValues) {
        this.createdTime = Calendar.getInstance().getTimeInMillis();
        this.updatedTime = updatedTime;
        this.listKeyValues = listKeyValues;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeInt(this.type);
        dest.writeLong(this.createdTime);
        dest.writeLong(this.updatedTime);
        dest.writeString(this.listKeyValues);
    }

    protected GenericSearchHistoryEntity(Parcel in) {
        this.id = in.readLong();
        this.type = in.readInt();
        this.createdTime = in.readLong();
        this.updatedTime = in.readLong();
        this.listKeyValues = in.readString();
    }

    public static final Parcelable.Creator<GenericSearchHistoryEntity> CREATOR = new Parcelable.Creator<GenericSearchHistoryEntity>() {
        @Override
        public GenericSearchHistoryEntity createFromParcel(Parcel source) {
            return new GenericSearchHistoryEntity(source);
        }

        @Override
        public GenericSearchHistoryEntity[] newArray(int size) {
            return new GenericSearchHistoryEntity[size];
        }
    };
}
