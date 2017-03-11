package com.franklyn.mobileswitch.helper.pojo;

/**
 * Created by AGBOMA franklyn on 1/30/17.
 */

public class SwitchContent {

    private String state, deviceName,lastTimeSet;

    public SwitchContent(){}

    public SwitchContent(String deviceName, String state, String lastTimeSet) {

        this.deviceName = deviceName;
        this.state = state;
        this.lastTimeSet = lastTimeSet;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setLastTimeSet(String lastTimeSet) {
        this.lastTimeSet = lastTimeSet;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getLastTimeSet() {
        return lastTimeSet;
    }

    public String getState() {
        return state;
    }
}
