package com.financialcalculator.model;

public class DashboardEntity {
    int id;
    String name;
    int icon;

    public int getId() {
        return id;
    }

    public DashboardEntity() {
    }

    public DashboardEntity(int id, String name, int icon) {
        this.id = id;
        this.name = name;
        this.icon = icon;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }


}
