package com.example.wifi_no_internet;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import static android.content.Context.MODE_PRIVATE;

/**
 * Name : batteryDialog
 * @author : Pierre-Vincent Gouel
 * @date : 30/05/2019
 *
 * This class is used to manage the dialog that contains the differents battery levels of the system
 *
 */
public class batteryDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //Create the view that will contains the dialog
        View v =getActivity().getLayoutInflater().inflate(R.layout.battery_dialog, new LinearLayout(getActivity()),false);

        //Affect the different values to each level of battery
        SharedPreferences mypref = getActivity().getSharedPreferences("BatteryPref",MODE_PRIVATE);
        TextView battArg = (TextView) v.findViewById(R.id.tv_valarg);
        TextView battXe1 = (TextView) v.findViewById(R.id.tv_valXe1);
        TextView battXe2 = (TextView) v.findViewById(R.id.tv_valXe2);
        TextView battXe3 = (TextView) v.findViewById(R.id.tv_valXe3);
        battArg.setText(Integer.toString(mypref.getInt("BATTARG",-1))+"%");
        battXe1.setText(Integer.toString(mypref.getInt("BATTXE1",-1))+"%");
        battXe2.setText(Integer.toString(mypref.getInt("BATTXE2",-1))+"%");
        battXe3.setText(Integer.toString(mypref.getInt("BATTXE3",-1))+"%");

        //Configure points of the dialog like the title
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v)
        .setTitle("System's battery level details")
                .setNeutralButton("Quit details", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }
}
