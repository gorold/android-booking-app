package com.gerald.java1dapp;

import java.util.Date;

public class ViewBookingsModel extends BookingsModel{
    private String mBooker;
    private Date mStartTime;
    private Date mEndTime;
    private String mRoomId;

    public ViewBookingsModel() {}

    public ViewBookingsModel(String booker, Date startTime, Date endTime, String roomId) {
        mBooker = booker;
        mStartTime = startTime;
        mEndTime = endTime;
        mRoomId = roomId;
    }


}
