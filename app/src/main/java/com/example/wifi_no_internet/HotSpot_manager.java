package com.example.wifi_no_internet;

import android.content.Context;
import android.net.wifi.WifiManager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * @Name : HotSpot_manager
 * @author : Pierre-Vincent Gouel, based on a work found on the website stackOverflow
 * @date : 30/05/2019
 */
public class HotSpot_manager {

    /**
     * This function is used to determine if the hotspot of the phone is activated or not
     * @param context the context of th application from which the function is called
     * @return nothing
     */
    public static boolean isHotSport(Context context){
        WifiManager Wmanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        try{
            Method method = Wmanager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(Wmanager);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * This function is used to scan the network the device is attached to and get all the other devices attached
     * @param onlyReachables a boolean to determines if only the reachables clients are taken
     * @param reachableTimeout the time until which the scan can be
     * @return The List that contains all the clients found and their parameters
     */
    public ArrayList<ClientScanResult> getClientList(boolean onlyReachables, int reachableTimeout){

        BufferedReader br = null;
        ArrayList<ClientScanResult> resultList = null;

        try{
            resultList = new ArrayList<ClientScanResult>();
            //Get the data from the command arp, this command is used also on windows to scan a network
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            //Read each data-line from the buffer and split them into different elements
            while((line = br.readLine())!=null){
                String[] splitted = line.split(" +");
                if((splitted !=null)&&(splitted.length>=4)){
                    String mac = splitted[3];
                    //Test the MAC to verify that the line has the good format
                    if(mac.matches("..:..:..:..:..:..")){
                        boolean isReachable = InetAddress.getByName(splitted[0]).isReachable(reachableTimeout);
                        if(!onlyReachables || isReachable){
                            //Add a new client to the list, created from the different parts of the line
                            resultList.add(new ClientScanResult(splitted[0],splitted[3],InetAddress.getByName(splitted[0]).getCanonicalHostName(),isReachable));
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultList;
    }
}
