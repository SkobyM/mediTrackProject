package com.example.meditrackproject;

public class DayModel {
    public String dayName, fullDayName, fullDate;
    public int dayNumber;
    public boolean isSelected;

    public DayModel(String dayName,String fullDayName,String fullDate, int dayNumber, boolean isSelected) {
        this.dayName = dayName;
        this.dayNumber = dayNumber;
        this.fullDayName = fullDayName;
        this.fullDate = fullDate;
        this.isSelected = isSelected;
    }
}

