package com.example.wifi_no_internet;

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
public class TCPWifiClient3 {
    private int SocketPort;
    private InetAddress ServerIP;
    private Socket socket;
    private InputStream input;

    /**
     * Constructor of a TCPClient
     * @param pservIF value of the IP address associated to the communication
     * @param pPort value of the Wifi port  associated to the communication
     */
    TCPWifiClient3(InetAddress pservIF, int pPort){
        this.ServerIP = pservIF;
        this.SocketPort = pPort;
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
        if(command.equals("MES")){order = 0x01;}
        else if(command.equals("Arg")){order = 0x12;}
        else if(command.equals("Arg2")){order = 0x12;}
        else if(command.equals("Arg3")){order = 0x12;}
        else if(command.equals("Arg4")){order = 0x12;}
        else if(command.equals("TAR")){order = 0x20;}
        else if(command.equals("BATT")){order = 0x30;}
        else{return -1;}

//Creation of the communication socket client, and send the order through it
        try {
            socket = null;
            socket = new Socket(this.ServerIP, this.SocketPort);
            socket.setSoTimeout(5000);
            OutputStream envoiCom = socket.getOutputStream();
            envoiCom.write(order);

//Wait for a response from the device and store it into a buffer
            input = null;
            input = socket.getInputStream();
            NBbytesRead = input.read(Inbuffer,0,255);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return -1;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
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
