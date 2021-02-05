package com.example.wifi_no_internet;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * @name : TCPWifiClient
 * @author : Pierre-Vincent Gouel
 * @date : 30/05/2019
 * @version v1.0
 *
 * This class is used to create a wireless communication between the phone ans the device
 * This communication is made through a Wifi-socket and the hotsport network of the phone
 * The communication is in 2 parts : sending an order, receive a data that corresponds to the order
 */
public class TCPWifiClient {
    private int SocketPort;
    private InetAddress ServerIP;
    private Socket socket;
    private InputStream input;

    /**
     * Constructor of a TCPClient
     * @param pservIF value of the IP address associated to the communication
     * @param pPort value of the Wifi port  associated to the communication
     */
    TCPWifiClient(InetAddress pservIF, int pPort){
        this.ServerIP = pservIF;
        this.SocketPort = pPort;
        Log.d("test" + ServerIP, "TCPWifiClient: ");
    }

    /**
     * This function is ths function that manages all the communication
     * @param command
     * @return
     */
    public float TCPSocketCom(String command){
        int order,NBbytesRead,j;
        float poidsmes;
        byte[] Inbuffer = new byte[255];
        String toasttxt = command;
// Selection of the order to send, depending of what called the TCPClient
        if(command.equals("MES")){order = 0x01;} // Send Measuring command to Argon

        else if(command.equals("Arg")){order = 0x10;}  // To establish connection Send Ping Test command to each Argon
        else if(command.equals("Arg2")){order = 0x10;} // seperately and wait for they reply
        else if(command.equals("Arg3")){order = 0x10;}
        else if(command.equals("Arg4")){order = 0x10;}

        else if(command.equals("TAR")){order = 0x20;}  // TARE the Argon
        else if(command.equals("BATT")){order = 0x30;} // Check Batt status the Argon
        else{return -1;} // no connection status

//Creation of the communication socket client, and send the order through it
        try {
            socket = null;
//            Log.d("TCPWifiClient" + ServerIP + SocketPort, "TCPSocketCom: ");
            socket = new Socket(this.ServerIP, this.SocketPort);
//            Log.d("TCPWifiClient", this.ServerIP+ " " + this.SocketPort);
            socket.setSoTimeout(5000);
            OutputStream envoiCom = socket.getOutputStream();
            envoiCom.write(order);
//            Log.d("TCPWifiClient", this.ServerIP+ " " + this.SocketPort);
//Wait for a response from the device and store it into a buffer
            input = null;
            input = socket.getInputStream();
///            Log.d("TCPWifiClient", this.ServerIP+ " " + this.SocketPort);
            NBbytesRead = input.read(Inbuffer,0,255);
//            Log.d("TCPWifiClient", BbytesRead+ " " + Inbuffer);

        } catch (UnknownHostException e) {
            e.printStackTrace();
            return -2;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("General I/O exception: " + e.getMessage());
            return -3;
        }
        finally {
            try {
                if(input != null){input.close();}
                if(socket != null){socket.close();}
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//Treatment of the data (rebuild all the bytes sends into one value)
//For example 123 is received as three bytes "1","2" and "3"
        poidsmes = 0;
        for (j = 0; j < NBbytesRead; j++) {
            poidsmes+=(Inbuffer[j]-48)*(Math.pow(10,NBbytesRead-(j+1)));
        }
        if(!(command.equals("BATT"))){poidsmes = poidsmes / 1000;}
        return poidsmes;
    }
}
