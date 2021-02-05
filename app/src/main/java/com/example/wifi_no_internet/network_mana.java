package com.example.wifi_no_internet;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * @author Pierre-Vincent Gouel
 * This class is used to manage the settings activity that allows user to see and manage the connection parameters
 */
public class network_mana extends AppCompatActivity {

    ImageButton saveIpPort,pingDevice,setHotSpot,searchDevices,backsetting;
    EditText IPaddr,IPaddr2,IPaddr3,IPaddr4,WifiPort;
    TextView tvScanRes,tvArgok,tvArg2ok,tvArg3ok,tvArg4ok,tvGlobalCo;
    ImageView imGlobalCo;

    float retourSocket,retourSocket2,retourSocket3,retourSocket4;
    HotSpot_manager APmanager;
    SharedPreferences mypref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);

        saveIpPort = (ImageButton) findViewById(R.id.imbtn_save_net);
        backsetting = (ImageButton) findViewById(R.id.imgbtn_backset);
        pingDevice = (ImageButton) findViewById(R.id.imbtn_ping);
        setHotSpot = (ImageButton) findViewById(R.id.imbtn_hotspot);
        searchDevices = (ImageButton) findViewById(R.id.imbtn_scan);

        IPaddr = (EditText) findViewById(R.id.et_IP);
        IPaddr2 = (EditText) findViewById(R.id.et_IP2);
        IPaddr3 = (EditText) findViewById(R.id.et_IP3);
        IPaddr4 = (EditText) findViewById(R.id.et_IP4);

        WifiPort = (EditText) findViewById(R.id.et_port);
        tvScanRes = (TextView) findViewById(R.id.tv_scanres);

        tvArgok = (TextView) findViewById(R.id.tv_okArg);
        tvArg2ok = (TextView) findViewById(R.id.tv_okArg2);
        tvArg3ok = (TextView) findViewById(R.id.tv_okArg3);
        tvArg4ok = (TextView) findViewById(R.id.tv_okArg4);

        tvGlobalCo = (TextView) findViewById(R.id.tv_connect_state);
        imGlobalCo = (ImageView) findViewById(R.id.img_connect_state);

        tvArgok.setText("-");
        tvArg2ok.setText("-");
        tvArg3ok.setText("-");
        tvArg4ok.setText("-");

        tvGlobalCo.setText("");

        tvArgok.setBackgroundColor(getColor(R.color.grey));
        tvArg2ok.setBackgroundColor(getColor(R.color.grey));
        tvArg3ok.setBackgroundColor(getColor(R.color.grey));
        tvArg4ok.setBackgroundColor(getColor(R.color.grey));

        mypref = getSharedPreferences("WifiPref",MODE_PRIVATE);

        IPaddr.setText(mypref.getString("IPADDR",""));
        IPaddr2.setText(mypref.getString("IPADDR2",""));
        IPaddr3.setText(mypref.getString("IPADDR3",""));
        IPaddr4.setText(mypref.getString("IPADDR4",""));

        APmanager = new HotSpot_manager();

        tvScanRes.setMovementMethod(new ScrollingMovementMethod());

        saveIpPort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mypref = getSharedPreferences("WifiPref",MODE_PRIVATE);
                SharedPreferences.Editor editor = mypref.edit();

                editor.putString("IPADDR",IPaddr.getText().toString());
                editor.putString("IPADDR2",IPaddr2.getText().toString());
                editor.putString("IPADDR3",IPaddr3.getText().toString());
                editor.putString("IPADDR4",IPaddr4.getText().toString());

                editor.putInt("PORT",Integer.valueOf(WifiPort.getText().toString()));
                editor.apply();
                Toast.makeText(network_mana.this,network_mana.this.getString(R.string.sSave),Toast.LENGTH_SHORT).show();
            }
        });

        backsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setHotSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifHotSpot();
            }
        });
        searchDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanHotSpot();
            }
        });

        pingDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvGlobalCo.setText(network_mana.this.getString(R.string.sConnection));
                ipingDevice("Arg");
                sleepThread();
                ipingDevice2("Arg2");
                sleepThread();
                ipingDevice3("Arg3");
                sleepThread();
                ipingDevice4("Arg4");
            }
        });

    }

    /**
     * This function is used to open the view in the phone settings that allows user to enable or disable the hotspot
     */
    public void verifHotSpot(){
        String Sres = "";
        if(APmanager.isHotSport(network_mana.this)){
            Sres = "HotSpot Activated";
        }
        else{
            Sres = "HotSpot Not Activated";
        }
        Toast.makeText(this,Sres,Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Intent.ACTION_MAIN,null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cn = new ComponentName("com.android.settings","com.android.settings.TetherSettings");
        intent.setComponent(cn);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * this function call the class and functions to get the differents clients connected to the network
     * Then, print in a text all the devices and their details to the user
     */
    public void scanHotSpot(){
        Toast.makeText(network_mana.this,network_mana.this.getString(R.string.sScan),Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                HotSpot_manager HSMana = new HotSpot_manager();
                final ArrayList<ClientScanResult> clients = HSMana.getClientList(false,10000);
                tvScanRes.post(new Runnable() {
                    @Override
                    public void run() {
                        int comptClients = 0;
                        tvScanRes.setText("Clients : \n");
                        String lastIP = "";
                        for (ClientScanResult clientScanResult : clients) {
                            tvScanRes.append("*************\n");
                            tvScanRes.append("IPAddr : " + clientScanResult.getIpAddr() + " \n");
                            tvScanRes.append("MACAddr : " + clientScanResult.getMACAddr() + " \n");
                            lastIP = clientScanResult.getIpAddr();
                            comptClients++;
                            if(comptClients==1){IPaddr.setText(lastIP);}
                            if(comptClients==2){IPaddr2.setText(lastIP);}
                            if(comptClients==3){IPaddr3.setText(lastIP);}
                        }
                        if(comptClients==4){IPaddr4.setText(lastIP);}
                        else{Toast.makeText(getApplicationContext(),"Check your devices and connect again",Toast.LENGTH_SHORT).show();}
                    }
                });
            }
        }).start();
    }

    /**
     * This function send a data to the selected device
     * Then, in fuction of the response (or the lack of response) it modify the user interface
     * This function is called each time by part of the device to ping
     * @param sParam used to select which part of the device is ping
     */
    public void ipingDevice(final String sParam){
        InetAddress ipadd = null;
        final Handler handler = new Handler();
        try {
            ipadd = InetAddress.getByName(IPaddr.getText().toString());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        final TCPWifiClient tcpWifiClient = new TCPWifiClient(ipadd,Integer.parseInt(WifiPort.getText().toString()));
////create a new thread to do the communication n parallel
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                retourSocket = tcpWifiClient.TCPSocketCom(sParam);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        TextView txt;
//Select the view to modify in function of the part to ping
                        switch (sParam){
                            case "Arg":
                                txt = tvArgok;
                                break;
                            case "Arg2":
                                txt = tvArgok;
                                break;
                            case "Arg3":
                                txt = tvArgok;
                                break;
                            case "Arg4":
                                txt = tvArgok;
                                break;
                            default:
                                txt = tvArgok;
                                break;
                        }
//Modify the view in function of the result of the communication
                        if(retourSocket == 1){
                            txt.setText("OK");
                            txt.setBackgroundColor(getColor(R.color.StronGreen));
                        }
                        else if(retourSocket == -1){
                            Toast.makeText(network_mana.this,"ERROR : Socket Connection problem",Toast.LENGTH_SHORT).show();
                            txt.setText("NOK");
                            txt.setBackgroundColor(getColor(R.color.StrongRed));
                        }
                        else{
                            Toast.makeText(network_mana.this,"Connection problem",Toast.LENGTH_SHORT).show();
                            txt.setText("NOK");
                            txt.setBackgroundColor(getColor(R.color.StrongRed));
                        }
//If it is the last part to ping, give a global status of the connection
//                        if(sParam.equals("Arg4")){
//                            if(tvArgok.getText()=="OK"&&tvArg2ok.getText()=="OK"&&tvArg3ok.getText()=="OK"&&tvArg4ok.getText()=="OK"){
//                                tvGlobalCo.setText(network_mana.this.getString(R.string.sConnected));
//                                imGlobalCo.setImageDrawable(network_mana.this.getDrawable(R.drawable.checked));
//                            }
//                            else{
//                                tvGlobalCo.setText(network_mana.this.getString(R.string.sDisconnected));
//                                imGlobalCo.setImageDrawable(network_mana.this.getDrawable(R.drawable.unchecked));
//                            }
//                        }
                    }
                });
            }
        });
        t.start();
    }

    public void ipingDevice2(final String sParam) {
        InetAddress ipadd2 = null;
        final Handler handler = new Handler();
        try {
            ipadd2 = InetAddress.getByName(IPaddr2.getText().toString());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        final TCPWifiClient2 tcpWifiClient2 = new TCPWifiClient2(ipadd2, Integer.parseInt(WifiPort.getText().toString()));
        Thread o = new Thread(new Runnable() {
            @Override
            public void run() {
                retourSocket2 = tcpWifiClient2.TCPSocketCom(sParam);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        TextView another;
                        switch (sParam) {
                            case "Arg":
                                another = tvArg2ok;
                                break;
                            case "Arg2":
                                another = tvArg2ok;
                                break;
                            case "Arg3":
                                another = tvArg2ok;
                                break;
                            case "Arg4":
                                another = tvArg2ok;
                                break;
                            default:
                                another = tvArg2ok;
                                break;
                        }

                        if (retourSocket2 == 1) {
                            another.setText("OK");
                            another.setBackgroundColor(getColor(R.color.StronGreen));
                        } else if (retourSocket2 == -1) {
                            another.setText("NOK");
                            another.setBackgroundColor(getColor(R.color.StrongRed));
                        } else {
                            another.setText("NOK");
                            another.setBackgroundColor(getColor(R.color.StrongRed));
                            Toast.makeText(network_mana.this, "Connection error", Toast.LENGTH_SHORT);
                        }
                    }
                });
            }
        });
        o.start();
    }



    public void ipingDevice3(final String sParam) {
        InetAddress ipadd3 = null;
        final Handler handler = new Handler();
        try {
            ipadd3 = InetAddress.getByName(IPaddr3.getText().toString());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        final TCPWifiClient3 tcpWifiClient3 = new TCPWifiClient3(ipadd3, Integer.parseInt(WifiPort.getText().toString()));
        Thread r = new Thread(new Runnable() {
            @Override
            public void run() {
                retourSocket3 = tcpWifiClient3.TCPSocketCom(sParam);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        TextView highschool;
                        switch (sParam) {
                            case "Arg":
                                highschool = tvArg3ok;
                                break;
                            case "Arg2":
                                highschool = tvArg3ok;
                                break;
                            case "Arg3":
                                highschool = tvArg3ok;
                                break;
                            case "Arg4":
                                highschool = tvArg3ok;
                                break;
                            default:
                                highschool = tvArg3ok;
                                break;
                        }

                        if (retourSocket3 == 1) {
                            highschool.setText("OK");
                            highschool.setBackgroundColor(getColor(R.color.StronGreen));
                        } else if (retourSocket3 == -1) {
                            highschool.setText("NOK");
                            highschool.setBackgroundColor(getColor(R.color.StrongRed));
                        } else {
                            highschool.setText("NOK");
                            highschool.setBackgroundColor(getColor(R.color.StrongRed));
                            Toast.makeText(network_mana.this, "Connection error", Toast.LENGTH_SHORT);
                        }
                    }
                });
            }
        });
        r.start();
    }


    public void ipingDevice4(final String sParam) {
        InetAddress ipadd4 = null;
        final Handler handler = new Handler();
        try {
            ipadd4 = InetAddress.getByName(IPaddr4.getText().toString());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        final TCPWifiClient4 tcpWifiClient4 = new TCPWifiClient4(ipadd4, Integer.parseInt(WifiPort.getText().toString()));
        Thread f = new Thread(new Runnable() {
            @Override
            public void run() {
                retourSocket4 = tcpWifiClient4.TCPSocketCom(sParam);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        TextView omega;
                        switch (sParam) {
                            case "Arg":
                                omega = tvArg4ok;
                                break;
                            case "Arg2":
                                omega = tvArg4ok;
                                break;
                            case "Arg3":
                                omega = tvArg4ok;
                                break;
                            case "Arg4":
                                omega = tvArg4ok;
                                break;
                            default:
                                omega = tvArg4ok;
                                break;
                        }

                        if (retourSocket4 == 1) {
                            omega.setText("OK");
                            omega.setBackgroundColor(getColor(R.color.StronGreen));
                        } else if (retourSocket4 == -1) {
                            omega.setText("NOK");
                            omega.setBackgroundColor(getColor(R.color.StrongRed));
                        } else {
                            omega.setText("NOK");
                            omega.setBackgroundColor(getColor(R.color.StrongRed));
                            Toast.makeText(network_mana.this, "Connection error", Toast.LENGTH_SHORT);
                        }
                    }
                });
            }
        });
        f.start();
    }




    public void sleepThread(){
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
