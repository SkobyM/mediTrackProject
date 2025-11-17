package com.example.meditrackproject;

public class DayModel {
    public String dayName;
    public int dayNumber;
    public boolean isSelected;

    public DayModel(String dayName, int dayNumber, boolean isSelected) {
        this.dayName = dayName;
        this.dayNumber = dayNumber;
        this.isSelected = isSelected;
    }
}

