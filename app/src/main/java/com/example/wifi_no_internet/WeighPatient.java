package com.example.wifi_no_internet;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WeighPatient extends AppCompatActivity {

    Button btntar;
    ImageButton btnWeighing,btnBack,btnSetting,btnBatt,addPatient;
    TextView tvWeigh,tvWeigh2,tvWeigh3,tvWeigh4,tvWeighfinal,etDateW,tvId;
    EditText etName,etAge,etSize,etLastWeigh;
    Spinner spin;
    float Weigh_value= (float) 0.0,Weigh_value2 = (float) 0.0,Weigh_value3 =  (float)  0.0 , Weigh_value4 =  (float) 0.0 ; //added 20203112
    float Weigh_valuefinal = (float) 0.0 , variable=  (float) 0.0, variable2 = (float) 0.0,  variable3 = (float) 0.0, variable4 =(float) 0.0, bat_val = (float) 0.0;//added 20203112
    float registered = (float) 0.0;
    //float registered2, registered3, registered4;
    boolean tar_flag = false;
    int Wifiport;
    InetAddress ipaddr,ipaddr2,ipaddr3,ipaddr4;
    DatabaseHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheighing);

        database = new DatabaseHelper(this);
        database.open();

        btntar = (Button) findViewById(R.id.btn_tar);

        btnWeighing = (ImageButton) findViewById(R.id.imbt_weighing);

        btnBack = (ImageButton) findViewById(R.id.imbtn_back);
        btnSetting = (ImageButton) findViewById(R.id.imbtn_setting);
        btnBatt = (ImageButton) findViewById(R.id.imbtn_batt);
//        low

        tvWeigh = (TextView) findViewById(R.id.tv_wheighmes);
        tvWeigh2 = (TextView) findViewById(R.id.tv_wheighmes_2);
        tvWeigh3 = (TextView) findViewById(R.id.tv_wheighmes_3);
        tvWeigh4 = (TextView) findViewById(R.id.tv_wheighmes_4);
        tvWeighfinal = (TextView) findViewById(R.id.tv_wheighmes_final);

        etLastWeigh = (EditText) findViewById(R.id.et_lastweigh);

        etDateW = (TextView) findViewById(R.id.tv_dateW);
        spin = (Spinner) findViewById(R.id.spinner_patient);
        addPatient = (ImageButton) findViewById(R.id.imbtn_addPat);
        tvId = (TextView) findViewById(R.id.tv_id);

        refreshSpinner();

        btnWeighing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAWeighing();
                doAWeighing2();
                doAWeighing3();
                doAWeighing4();
                doAWeighingfinal();
            }
        });

        btntar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tarBalance();
                tarBalance2();
                tarBalance3();
                tarBalance4();
                tarBalancefinal();
            }
        });

        btnBatt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBatteryDetails();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeighPatient.this,network_mana.class);
                WeighPatient.this.startActivity(intent);
            }
        });

        addPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(WeighPatient.this);
                View diagView = li.inflate(R.layout.add_patient,null);
                final EditText etName = (EditText) diagView.findViewById(R.id.et_diag_name);
                final EditText etWeight = (EditText) diagView.findViewById(R.id.et_diag_weight);

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(WeighPatient.this);
                builder.setTitle("Add a new client");
                builder.setView(diagView);

                builder.setPositiveButton("save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Date date = Calendar.getInstance().getTime();
                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        String strDate = dateFormat.format(date);
                        database.add(new Patient(etName.getText().toString(), strDate,Float.parseFloat(etWeight.getText().toString())));
                        refreshSpinner();
                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"Cancel",Toast.LENGTH_LONG).show();
                    }
                });

                android.app.AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = database.getAllprod();
                c.moveToFirst();
                while(!c.isAfterLast()){
                    if(c.getString(1).equals(spin.getSelectedItem().toString())){
//                        etName.setText(spin.getSelectedItem().toString());
                        etLastWeigh.setText(c.getString(2));
                        etDateW.setText(c.getString(3));
                        tvId.setText(Long.toString(c.getLong(0)));
                        return;
                    }
                    c.moveToNext();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        checkWifiParam();
        generalBattery();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    /**
     * Send an order to the device
     * Update the user interface and set the actual value to 0
     */
    public void tarBalance(){
        final  Handler handler = new Handler();
        final TCPWifiClient tcpWifiClient = new TCPWifiClient(ipaddr,Wifiport);
//        Toast.makeText(WeighPatient.this,WeighPatient.this.getString(R.string.sTarAct),Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final float value = tcpWifiClient.TCPSocketCom("TAR");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(value >-1) {
                            tvWeigh.setText("0.0");
                            variable = 1;
//                            Toast.makeText(WeighPatient.this, "Tar done", Toast.LENGTH_SHORT).show();
                        }
                        else{
//                            Toast.makeText(WeighPatient.this, "Error while doing TAR", Toast.LENGTH_SHORT).show();
//                            Log.d("tarBal", "tarBalance1: ");
                        }
                    }
                });
            }
        }).start();
    }

    public void tarBalance2(){
        final  Handler handler = new Handler();
        final TCPWifiClient2 tcpWifiClient2 = new TCPWifiClient2(ipaddr2,Wifiport);
//        Toast.makeText(WeighPatient.this,WeighPatient.this.getString(R.string.sTarAct),Toast.LENGTH_SHORT).show();
//        Log.d("tarring123456789", "tarBalance2: ");
        new Thread(new Runnable() {
            @Override
            public void run() {
//                Log.d("tarBal2", "tarBalance123: ");

//                error here
                final float value2 = tcpWifiClient2.TCPSocketCom("TAR");
//                Log.d("tarBal2", "tarBalance2: ");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
//                        Log.d("tarBal2", "tarBalance2: ");
                        if(value2 >-1) {
                            tvWeigh2.setText("0.0");
                            variable2 = 1;
//                            Toast.makeText(WeighPatient.this, "Tar done", Toast.LENGTH_SHORT).show();
                        }
                        else{
//                            Toast.makeText(WeighPatient.this, "Error while doing TAR", Toast.LENGTH_SHORT).show();
//                            Log.d("tarBal2", "tarBalance2: ");
                        }
                    }
                });
            }
        }).start();
    }

    public void tarBalance3(){
        final  Handler handler = new Handler();
        final TCPWifiClient3 tcpWifiClient3 = new TCPWifiClient3(ipaddr3,Wifiport);
//        Toast.makeText(WeighPatient.this,WeighPatient.this.getString(R.string.sTarAct),Toast.LENGTH_SHORT).show();
//        Log.d("tarring123456789", "tarBalance3: ");
        new Thread(new Runnable() {
            @Override
            public void run() {
//                Log.d("tarBalance3", "tarBalance3: ");

//                error here
                final float value3 = tcpWifiClient3.TCPSocketCom("TAR");
//                Log.d("tarBalance3", "tarBalance3: ");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
//                        Log.d("tarBalance3", "tarBalance3: ");
                        if(value3 >-1) {
                            tvWeigh3.setText("0.0");
                            variable3 = 1;
//                            Toast.makeText(WeighPatient.this, "Tar done", Toast.LENGTH_SHORT).show();
                        }
                        else{
//                            Toast.makeText(WeighPatient.this, "Error while doing TAR", Toast.LENGTH_SHORT).show();
//                            Log.d("tarBalance3", "tarBalance3: ");
                        }
                    }
                });
            }
        }).start();
    }

    public void tarBalance4(){
        final  Handler handler = new Handler();
        final TCPWifiClient4 tcpWifiClient4 = new TCPWifiClient4(ipaddr4,Wifiport);
//        Toast.makeText(WeighPatient.this,WeighPatient.this.getString(R.string.sTarAct),Toast.LENGTH_SHORT).show();
        Log.d("tarring123456789", "tarBalance4: ");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("tarBalance4", "tarBalance4: ");

//                error here
                final float value4 = tcpWifiClient4.TCPSocketCom("TAR");
//                Log.d("tarBalance4", "tarBalance4: ");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
//                        Log.d("tarBalance4", "tarBalance4: ");
                        if(value4 >-1) {
                            tvWeigh4.setText("0.0");
                            variable4 = 1;
//                            Toast.makeText(WeighPatient.this, "Tar done", Toast.LENGTH_SHORT).show();
                        }
                        else{
//                            Toast.makeText(WeighPatient.this, "Error while doing TAR", Toast.LENGTH_SHORT).show();
//                            Log.d("tarBalance4", "tarBalance4: ");
                        }
                    }
                });
            }
        }).start();
    }

    public void tarBalancefinal(){
        final  Handler handler = new Handler();
        Toast.makeText(WeighPatient.this,WeighPatient.this.getString(R.string.sTarAct),Toast.LENGTH_SHORT).show();
        Log.d("tarBalancefinal", "tarBalancefinal: ");
//        tvWeighfinal.setText("0.0");
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (variable == 1 && variable2 == 1 && variable3 == 1 && variable4 == 1){
                    tvWeighfinal.setText("0.0");
                    registered = Weigh_valuefinal;
                    tar_flag = true ;
                    variable  = 0;
                    variable2 = 0;
                    variable3 = 0;
                    variable4 = 0;
                }
                else {
                    Toast.makeText(WeighPatient.this, "Error while doing TAR", Toast.LENGTH_SHORT).show();
                    tvWeighfinal.setText("NRdy");//Not ready THEF 2021/02/05ded THEF
                }
            }
        }).start();
    }




    /**
     * Save the last weight value (if there is) and its date
     * Get a new value from the device
     * Save the value and update the user interface
     */

    public void doAWeighing(){
        final Handler handler = new Handler();
        final TCPWifiClient tcpWifiClient = new TCPWifiClient(ipaddr,Wifiport);
//        Log.d("IPADDR ERROR", "doAWeighing: ");
        new Thread(new Runnable() {
            //            @Override
            public void run() {
//                Log.d("IPADDR ERROR", "doAWeighing: ");
                Weigh_value = tcpWifiClient.TCPSocketCom("MES");
//                Log.d("IPADDR ERROR", "doAWeighing: " + Weigh_value);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
//                        Date c = Calendar.getInstance().getTime();
//                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/ yyyy");
//                        Weigh_value += 0.309;
//                        Weigh_value /= 10.422;
//                        Weigh_value *= 10;
                        tvWeigh.setText(Float.toString(Weigh_value));
                        variable = 2;
//                        Toast.makeText(WeighPatient.this,"Weigh done",Toast.LENGTH_SHORT).show();
//                        Log.d("doAWeighing" + Long.parseLong(tvId.getText().toString()), "doAWeighing");
//                        database.update(new Patient(Long.parseLong(tvId.getText().toString()),spin.getSelectedItem().toString(),df.format(c),Float.parseFloat(tvWeigh.getText().toString())));
                    }
                });
            }
        }).start();
    }

    public void doAWeighing2(){
        final Handler handler = new Handler();
        final TCPWifiClient2 tcpWifiClient2 = new TCPWifiClient2(ipaddr2,Wifiport);
//        Log.d("IPADDR2 ERROR", "doAWeighing2: ");
        new Thread(new Runnable() {
            //            @Override
            public void run() {
//                Log.d("IPADDR ERROR", "doAWeighing: ");
                Weigh_value2 = tcpWifiClient2.TCPSocketCom("MES");
//                Log.d("IPADDR2 ERROR", "doAWeighing: " + Weigh_value2);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
//                        Date c = Calendar.getInstance().getTime();
//                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/ yyyy");
//                        Weigh_value += 0.309;
//                        Weigh_value /= 10.422;
//                        Weigh_value *= 10;
                        tvWeigh2.setText(Float.toString(Weigh_value2));
                        variable2 = 2;
//                        Toast.makeText(WeighPatient.this,"Weigh done",Toast.LENGTH_SHORT).show();
//                        Log.d("doAWeighing2" + Long.parseLong(tvId.getText().toString()), "doAWeighing2");
//                        database.update(new Patient(Long.parseLong(tvId.getText().toString()),spin.getSelectedItem().toString(),df.format(c),Float.parseFloat(tvWeigh.getText().toString())));
                    }
                });
            }
        }).start();
    }

    public void doAWeighing3(){
        final Handler handler = new Handler();
        final TCPWifiClient3 tcpWifiClient3 = new TCPWifiClient3(ipaddr3,Wifiport);
//        Log.d("IPADDR3 ERROR", "doAWeighing3: ");
        new Thread(new Runnable() {
            //            @Override
            public void run() {
//                Log.d("IPADDR ERROR", "doAWeighing: ");
                Weigh_value3 = tcpWifiClient3.TCPSocketCom("MES");
                Log.d("IPADDR3 ERROR", "doAWeighing: " + Weigh_value3);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
//                        Date c = Calendar.getInstance().getTime();
//                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/ yyyy");
//                        Weigh_value += 0.309;
//                        Weigh_value /= 10.422;
//                        Weigh_value *= 10;
                        tvWeigh3.setText(Float.toString(Weigh_value3));
                        variable3 = 2;
//                        Toast.makeText(WeighPatient.this,"Weigh done",Toast.LENGTH_SHORT).show();
//                        Log.d("doAWeighing3" + Long.parseLong(tvId.getText().toString()), "doAWeighing3");
//                        database.update(new Patient(Long.parseLong(tvId.getText().toString()),spin.getSelectedItem().toString(),df.format(c),Float.parseFloat(tvWeigh.getText().toString())));
                    }
                });
            }
        }).start();
    }

    public void doAWeighing4(){
        final Handler handler = new Handler();
        final TCPWifiClient4 tcpWifiClient4 = new TCPWifiClient4(ipaddr4,Wifiport);
//        Log.d("IPADDR4 ERROR", "doAWeighing4: ");
        new Thread(new Runnable() {
            //            @Override
            public void run() {
//                Log.d("IPADDR ERROR", "doAWeighing: ");
                Weigh_value4 = tcpWifiClient4.TCPSocketCom("MES");
//                Log.d("IPADDR4 ERROR", "doAWeighing4: " + Weigh_value4);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
//                        Date c = Calendar.getInstance().getTime();
//                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/ yyyy");
//                        Weigh_value += 0.309;
//                        Weigh_value /= 10.422;
//                        Weigh_value *= 10;
                        tvWeigh4.setText(Float.toString(Weigh_value4));
                        variable4 = 2;
//                        Toast.makeText(WeighPatient.this,"Weigh done",Toast.LENGTH_SHORT).show();
//                        Log.d("doAWeighing4" + Long.parseLong(tvId.getText().toString()), "doAWeighing4");
//                        database.update(new Patient(Long.parseLong(tvId.getText().toString()),spin.getSelectedItem().toString(),df.format(c),Float.parseFloat(tvWeigh.getText().toString())));
                    }
                });
            }
        }).start();
    }
    //Addded Weigh_valuefinal=Math.round(Weigh_valuefinal * 100) / 100.0F; thef 05/01/2021
    public void doAWeighingfinal(){
        final Handler handler = new Handler();
//        Log.d("IPADDRfinal ERROR", "doAWeighing: ");
        new Thread(new Runnable() {
            //            @Override
            public void run() {
//                Log.d("doAWeighingfinal ERROR", "doAWeighingfinal: ");
//                Log.d("IPADDRfinal ERROR", "doAWeighingfinal: " + Weigh_valuefinal);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (variable == 2 && variable2 == 2 && variable3 == 2 && variable4 == 2) {
                            //reset the state
                            variable  = 0;
                            variable2 = 0;
                            variable3 = 0;
                            variable4 = 0;

                            if (Weigh_value <0)
                                Weigh_value = 0;
                            if (Weigh_value2<0)
                                Weigh_value2 = 0;
                            if (Weigh_value3 <0)
                                Weigh_value3= 0;
                            if (Weigh_value4<0)
                                Weigh_value4 = 0;

                            Weigh_valuefinal = Weigh_value + Weigh_value2 + Weigh_value3 + Weigh_value4 - registered;
                            if (tar_flag)
                            {
                                registered = Weigh_valuefinal;
                                tar_flag = false;
                            }

//                            if (Weigh_valuefinal >= 1) {
//                                if (Weigh_valuefinal <= 10) {
//                                        Weigh_valuefinal = (float) (-0.0004 * (Weigh_valuefinal * Weigh_valuefinal) + 1.228 * (Weigh_valuefinal) + 0.0427);
//                                }
//
//                                else if (Weigh_valuefinal <= 20) {
//                                    Weigh_valuefinal = (float) (-0.0004 * (Weigh_valuefinal * Weigh_valuefinal) + 1.255 * (Weigh_valuefinal) + 0.0427);
//                                }
//
//                                else if (Weigh_valuefinal <= 25) {
//                                    Weigh_valuefinal = (float) (-0.0004 * (Weigh_valuefinal * Weigh_valuefinal) + 1.26 * (Weigh_valuefinal) + 0.0427);
//                                }
//
//                                else if (Weigh_valuefinal <= 35) {
//                                    Weigh_valuefinal = (float) (-0.0004 * (Weigh_valuefinal * Weigh_valuefinal) + 1.259 * (Weigh_valuefinal) + 0.0427);
//                                }
//
//                                else if (Weigh_valuefinal <= 45) {
//                                    Weigh_valuefinal = (float) (-0.0004 * (Weigh_valuefinal * Weigh_valuefinal) + 1.254 * (Weigh_valuefinal) + 0.0427);
//                                }
//
//                                else if (Weigh_valuefinal <= 50) {
//                                    Weigh_valuefinal = (float) (-0.0004 * (Weigh_valuefinal * Weigh_valuefinal) + 1.242 * (Weigh_valuefinal) + 0.0427);
//                                }
//
//                                else if (Weigh_valuefinal <= 60) {
//                                    Weigh_valuefinal = (float) (-0.0004 * (Weigh_valuefinal * Weigh_valuefinal) + 1.235 * (Weigh_valuefinal) + 0.0427);
//                                }
//
//                                else if (Weigh_valuefinal <= 70) {
//                                    Weigh_valuefinal = (float) (-0.0004 * (Weigh_valuefinal * Weigh_valuefinal) + 1.23 * (Weigh_valuefinal) + 0.0427);
//                                }
//
//                                else if (Weigh_valuefinal <= 80) {
//                                    Weigh_valuefinal = (float) (-0.0004 * (Weigh_valuefinal * Weigh_valuefinal) + 1.225 * (Weigh_valuefinal) + 0.0427);
//                                }
//
//                                else if (Weigh_valuefinal <= 86) {
//                                    Weigh_valuefinal = (float) (-0.0004 * (Weigh_valuefinal * Weigh_valuefinal) + 1.2277 * (Weigh_valuefinal) + 0.0427);
//                                }
//
//                            }
//
//                            else{
//
//                            }
                            Weigh_valuefinal=Math.round(Weigh_valuefinal * 100) / 100.0F; //addded thef 05/01/2021
                            tvWeighfinal.setText(Float.toString(Weigh_valuefinal));
                            Date c = Calendar.getInstance().getTime();
                            SimpleDateFormat df = new SimpleDateFormat("dd/MM/ yyyy");
                            Toast.makeText(WeighPatient.this, "Weigh done", Toast.LENGTH_SHORT).show();
                            Log.d("doAWeighingfinal" + Long.parseLong(tvId.getText().toString()), "doAWeighingfinal");
//                            database.update(new Patient(Long.parseLong(tvId.getText().toString()), spin.getSelectedItem().toString(), df.format(c), Float.parseFloat(tvWeigh3.getText().toString())));
                        }
                    }
                });
            }
        }).start();
    }




    /**
     * Verify if the parameters of the wifi connection are valid
     * Notify the user if not
     */
    private void checkWifiParam(){
        SharedPreferences mypref = getSharedPreferences("WifiPref",MODE_PRIVATE);
        Wifiport = mypref.getInt("PORT",-1);
        String sIP = mypref.getString("IPADDR","");
        String sIP2 = mypref.getString("IPADDR2","");
        String sIP3 = mypref.getString("IPADDR3","");
        String sIP4 = mypref.getString("IPADDR4","");
        if (sIP.equals("")||(sIP.length()>15) || Wifiport==-1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(WeighPatient.this);
            builder.setMessage("No Wifi adress or port are defined, connection will no be possible.\nDo you want to open the settings ?")
                    .setTitle("Wifi Error")
                    .setPositiveButton("Open it", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(WeighPatient.this,network_mana.class);
                            WeighPatient.this.startActivity(intent);
                        }
                    });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(WeighPatient.this, "You will not be able to connect the device without that", Toast.LENGTH_SHORT).show();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else{
            try {
                ipaddr = InetAddress.getByName(sIP);
                ipaddr2 = InetAddress.getByName(sIP2);
                ipaddr3 = InetAddress.getByName(sIP3);
                ipaddr4 = InetAddress.getByName(sIP4);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Send an order to the device, then get a 2-bytes datas that contains all the battery levels
     *
     */
    private void generalBattery(){
        final Handler handler = new Handler();
        final TCPWifiClient tcpWifiClient = new TCPWifiClient(ipaddr,Wifiport);

        new Thread(new Runnable() {
            @Override
            public void run() {
//Get the data from the device
                bat_val = tcpWifiClient.TCPSocketCom("BATT");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
//Exctract all baterry levels from this data
                        int val,varg,vxe1,vxe2,vxe3;
                        val = Math.round(bat_val);
                        varg = Math.round(val/1000000);
                        val = val-(varg*1000000);
                        vxe1 = Math.round(val/10000);
                        val = val-(vxe1*10000);
                        vxe2 = Math.round(val/100);
                        val = val-(vxe2*100);
                        vxe3 = val;
//Check if there is one part with a low battery level
                        //added 20203112
                        if(varg<=200 || vxe1 <=200|| vxe2 <=200|| vxe3 <=200){
                            btnBatt.setImageResource(R.drawable.low_batt);
                            Toast.makeText(WeighPatient.this,"WARNING : Low battery",Toast.LENGTH_LONG).show();
                        }
//Save te values of the battery
                        else{btnBatt.setImageResource(R.drawable.battery);}
                        SharedPreferences mypref = getSharedPreferences("BatteryPref",MODE_PRIVATE);
                        SharedPreferences.Editor editor = mypref.edit();
                        editor.putInt("BATTARG",varg); //Argon battery
                        editor.putInt("BATTXE1",vxe1); //Xenon battery
                        editor.putInt("BATTXE2",vxe2); //Xenon battery
                        editor.putInt("BATTXE3",vxe3); // Xenon battery
                        editor.apply();
                    }
                });
            }
        }).start();
    }

    /**
     * Open the dialog that shows the details of the battery levels
     */
    public void showBatteryDetails(){
        batteryDialog dialog = new batteryDialog();
        dialog.show(getSupportFragmentManager(),null);
    }

    public void refreshSpinner(){
        List<String> lNames = getNamesList();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item,lNames);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        spin.setAdapter(dataAdapter);
    }

    public List<String> getNamesList(){
        Cursor c = database.getAllprod();
        List<String> list = new ArrayList<String>();
        c.moveToFirst();
        while(!c.isAfterLast()){
            list.add(c.getString(1));
            c.moveToNext();
        }
        c.close();
        return list;
    }

}
