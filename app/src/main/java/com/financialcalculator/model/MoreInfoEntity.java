package com.financialcalculator.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Entity(tableName = "MoreInfoEntity")
public class MoreInfoEntity implements Serializable {


    /**
     * answer : At the end of current year you will get Rs $a and after retirement you will get Rs@a+1234@.
     * calId : -MIYAMFxdh-r33VoP3Lh
     * firebaseId : -MJBjkBqyizVj7esK6f5
     * ques : What is test3 question ?
     */

    @SerializedName("answer")
    private String answer;
    @SerializedName("calId")
    private String calId;


    @PrimaryKey
    @NotNull
    private String firebaseId;
    @SerializedName("ques")
    private String ques;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getCalId() {
        return calId;
    }

    public void setCalId(String calId) {
        this.calId = calId;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String getQues() {
        return ques;
    }

    public void setQues(String ques) {
        this.ques = ques;
    }
}
