package com.gerald.java1dapp;

public class RoomsModel {
    private String block;
    private String capacity;
    private String level;
    private boolean lock;
    private String name;
    private String type;
    private String unit;
    private String roomId;
    private boolean available;

    public RoomsModel() {}

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean getAvailable() {return available;}

    public String getBlock() {
        return block;
    }

    public String getCapacity() {
        return capacity;
    }

    public String getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getUnit() {
        return unit;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}

