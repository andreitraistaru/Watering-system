package com.myplant;

import java.util.Date;

public class Utils {
    private static final String serverProtocol = "http";
    private static final String serverIp = "wateringsystem.asuscomm.com";
    private static final int serverPort = 1234;
    private static final String serverFileInfo = "/info";
    private static final String serverFileWater = "/water";
    private static final int wateringTime = 5000;
    private static final int wateringTimeResult = 3000;
    private static final int maxHumidity = 1023;
    private static final int thirstyPlantThreshold = 46;
    private static final int driedPlantThreshold = 20;
    private static final int maxHistoryCapacity = 999;

    public static String getServerProtocol() {
        return serverProtocol;
    }
    public static String getServerIp() {
        return serverIp;
    }
    public static int getServerPort() {
        return serverPort;
    }
    public static String getServerFileInfo() {
        return serverFileInfo;
    }
    public static String getServerFileWater() {
        return serverFileWater;
    }
    public static int getWateringTime() {
        return wateringTime;
    }
    public static int getWateringTimeResult() {
        return wateringTimeResult;
    }
    public static int getHumidityPercentage(int sensorValue) {
        return (100 * (maxHumidity - sensorValue)) / maxHumidity;
    }
    public static Date getStatusInfo(long serverValue) {
        if (serverValue == 0) {
            return null;
        }

        return new Date(new Date().getTime() - serverValue);
    }
    public static int getThirstyPlantThreshold() {
        return thirstyPlantThreshold;
    }
    public static int getDriedPlantThreshold() {
        return driedPlantThreshold;
    }
    public static int getMaxHistoryCapacity() {
        return maxHistoryCapacity;
    }
}
