package com.brezze.share.communication.utils;

public class StringUtil {

    public static String getSignalLevel(String signal) {
        int signalLevel = 0;
        if (signal.startsWith("4G") || signal.startsWith("CSQ")) {
            String[] split = signal.split(":");
            if (split.length > 1) {
                int signalInt = Integer.parseInt(split[1]);
                if (signalInt >= 0 && signalInt < 16) {
                    signalLevel = 0;
                } else if (signalInt >= 16 && signalInt < 21) {
                    signalLevel = 0;
                } else if (signalInt >= 21 && signalInt < 24) {
                    signalLevel = 1;
                } else if (signalInt >= 24 && signalInt < 27) {
                    signalLevel = 1;
                } else if (signalInt >= 27) {
                    signalLevel = 2;
                }
            }
        } else if (signal.startsWith("WIFI")) {
            String[] split = signal.split(":");
            if (split.length > 1) {
                int signalInt;
                if (split[1].length() >= 3) {
                    signalInt = Integer.parseInt(split[1].substring(0, 3));
                } else {
                    signalInt = Integer.parseInt(split[1]);
                }
                if (signalInt > -35 && signalInt <= 0) {
                    signalLevel = 2;
                } else if (signalInt > -50 && signalInt <= -35) {
                    signalLevel = 1;
                } else if (signalInt > -60 && signalInt <= -50) {
                    signalLevel = 1;
                } else if (signalInt > -70 && signalInt <= -60) {
                    signalLevel = 0;
                }
            }
        }
        return String.valueOf(signalLevel);
    }
}
