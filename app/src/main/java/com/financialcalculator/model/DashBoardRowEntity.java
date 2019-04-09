package com.financialcalculator.model;

import java.util.List;

/**
 * Created by Rajeev Ranjan -  ABPB on 09-04-2019.
 */
public class DashBoardRowEntity {

    int id;
    String title;
    List<DashboardEntity> dashboardEntities;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DashBoardRowEntity(int id, String title, List<DashboardEntity> dashboardEntities) {
        this.id = id;
        this.title = title;
        this.dashboardEntities = dashboardEntities;
    }

    public String getTitle() {
        return title;

    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<DashboardEntity> getDashboardEntities() {
        return dashboardEntities;
    }

    public void setDashboardEntities(List<DashboardEntity> dashboardEntities) {
        this.dashboardEntities = dashboardEntities;
    }
}
