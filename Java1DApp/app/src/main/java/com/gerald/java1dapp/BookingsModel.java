package com.gerald.java1dapp;

import java.util.Date;

public abstract class BookingsModel {
    private String mBooker;
    private Date mStartTime;
    private Date mEndTime;
    private String mRoomId;

    public String getBooker() {return mBooker;}

    public Date getStartTime() {return mStartTime;}

    public Date getEndTime() {return mEndTime;}

    public String getRoomId() {return mRoomId;}

    public void setBooker(String booker) {mBooker = booker;}

    public void setStartTime(Date startTime) {mStartTime = startTime;}

    public void setEndTime(Date endTime) {mEndTime = endTime;}

    public void setRoomId(String roomId) {mRoomId = roomId;}
}
