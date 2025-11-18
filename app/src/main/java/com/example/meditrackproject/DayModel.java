package com.example.meditrackproject;

public class DayModel {
    public String dayName, fullDayName;
    public int dayNumber;
    public boolean isSelected;

    public DayModel(String dayName,String fullDayName, int dayNumber, boolean isSelected) {
        this.dayName = dayName;
        this.dayNumber = dayNumber;
        this.fullDayName = fullDayName;
        this.isSelected = isSelected;
    }
}

