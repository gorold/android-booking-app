package com.gerald.java1dapp;

import java.util.Date;

public class ViewTasksModel extends BookingsModel{
    private String mBooker;
    private Date mStartTime;
    private Date mEndTime;
    private String mRoomId;

    public ViewTasksModel() {}

    public ViewTasksModel(String booker, Date startTime, Date endTime, String roomId) {
        mBooker = booker;
        mStartTime = startTime;
        mEndTime = endTime;
        mRoomId = roomId;
    }
}
