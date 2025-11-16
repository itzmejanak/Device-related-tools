package com.brezze.share.communication.oo.ybt;

import java.io.Serializable;

public class VersionInfo implements Serializable {
    private static final long serialVersionUID = 5359709211352400086L;


    private String androidRelease = "";

    private String androidTest = "";

    private String mcuRelease = "";

    private String mcuTest = "";

    private String chipRelease = "";

    private String chipTest = "";

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getAndroidRelease() {
        return androidRelease;
    }

    public void setAndroidRelease(String androidRelease) {
        this.androidRelease = androidRelease;
    }

    public String getAndroidTest() {
        return androidTest;
    }

    public void setAndroidTest(String androidTest) {
        this.androidTest = androidTest;
    }

    public String getMcuRelease() {
        return mcuRelease;
    }

    public void setMcuRelease(String mcuRelease) {
        this.mcuRelease = mcuRelease;
    }

    public String getMcuTest() {
        return mcuTest;
    }

    public void setMcuTest(String mcuTest) {
        this.mcuTest = mcuTest;
    }

    public String getChipRelease() {
        return chipRelease;
    }

    public void setChipRelease(String chipRelease) {
        this.chipRelease = chipRelease;
    }

    public String getChipTest() {
        return chipTest;
    }

    public void setChipTest(String chipTest) {
        this.chipTest = chipTest;
    }
}
