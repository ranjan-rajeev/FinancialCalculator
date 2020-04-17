package com.financialcalculator.roomdb.tables;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "EMISearchHistoryEntity")
public class EMISearchHistoryEntity implements Parcelable {
    @PrimaryKey
    @ColumnInfo(name = "historyID")
    private long historyID;

    @ColumnInfo(name = "type")
    private int type;

    @ColumnInfo(name = "updatedTime")
    private long updatedTime;

    @ColumnInfo(name = "principalAmt")
    String  principalAmt;

    @ColumnInfo(name = "roi")
    String roi;

    @ColumnInfo(name = "loanTenure")
    String loanTenure;

    @ColumnInfo(name = "loanTenureTYpe")
    String loanTenureTYpe;



    public EMISearchHistoryEntity(int type, String principalAmt, String roi, String loanTenure, String loanTenureTYpe,long updatedTime) {
        this.historyID = updatedTime;
        this.type = type;
        this.updatedTime = updatedTime;
        this.principalAmt = principalAmt;
        this.roi = roi;
        this.loanTenure = loanTenure;
        this.loanTenureTYpe = loanTenureTYpe;
    }

    public long getHistoryID() {
        return historyID;
    }

    public void setHistoryID(long historyID) {
        this.historyID = historyID;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getPrincipalAmt() {
        return principalAmt;
    }

    public void setPrincipalAmt(String principalAmt) {
        this.principalAmt = principalAmt;
    }

    public String getRoi() {
        return roi;
    }

    public void setRoi(String roi) {
        this.roi = roi;
    }

    public String getLoanTenure() {
        return loanTenure;
    }

    public void setLoanTenure(String loanTenure) {
        this.loanTenure = loanTenure;
    }

    public String getLoanTenureTYpe() {
        return loanTenureTYpe;
    }

    public void setLoanTenureTYpe(String loanTenureTYpe) {
        this.loanTenureTYpe = loanTenureTYpe;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.historyID);
        dest.writeInt(this.type);
        dest.writeLong(this.updatedTime);
        dest.writeString(this.principalAmt);
        dest.writeString(this.roi);
        dest.writeString(this.loanTenure);
        dest.writeString(this.loanTenureTYpe);
    }

    protected EMISearchHistoryEntity(Parcel in) {
        this.historyID = in.readLong();
        this.type = in.readInt();
        this.updatedTime = in.readLong();
        this.principalAmt = in.readString();
        this.roi = in.readString();
        this.loanTenure = in.readString();
        this.loanTenureTYpe = in.readString();
    }

    public static final Parcelable.Creator<EMISearchHistoryEntity> CREATOR = new Parcelable.Creator<EMISearchHistoryEntity>() {
        @Override
        public EMISearchHistoryEntity createFromParcel(Parcel source) {
            return new EMISearchHistoryEntity(source);
        }

        @Override
        public EMISearchHistoryEntity[] newArray(int size) {
            return new EMISearchHistoryEntity[size];
        }
    };
}
