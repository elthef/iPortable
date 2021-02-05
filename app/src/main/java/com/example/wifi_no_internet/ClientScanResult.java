package com.example.wifi_no_internet;

/**
 * @Name : ClientScanResult
 * @Author : Pierre-Vincent Gouel
 * @date : 30/05/2019
 *
 * This class is used to get and modify parameters of each nod of the network the phone is connected to
 * This class just contains attributs, a constructor that set the attributs and all getters and setters for attributs
 */
public class ClientScanResult {
    private String IpAddr;
    private String MACAddr;
    private String Device;
    private boolean isReachable;

    public ClientScanResult(String IP,String MAC,String dev,boolean reach){
        this.IpAddr=IP;
        this.MACAddr=MAC;
        this.Device=dev;
        this.isReachable=reach;
    }

    public String getIpAddr() {
        return IpAddr;
    }

    public void setIpAddr(String ipAddr) {
        IpAddr = ipAddr;
    }

    public String getMACAddr() {
        return MACAddr;
    }

    public void setMACAddr(String MACAddr) {
        this.MACAddr = MACAddr;
    }

    public String getDevice() {
        return Device;
    }

    public void setDevice(String device) {
        Device = device;
    }

    public boolean isReachable() {
        return isReachable;
    }

    public void setReachable(boolean reachable) {
        isReachable = reachable;
    }
}

