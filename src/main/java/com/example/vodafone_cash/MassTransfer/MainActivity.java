package com.example.vodafone_cash.MassTransfer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityService.GestureResultCallback;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityWindowInfo;
import android.widget.Button;
import android.widget.Toast;

import com.example.vodafone_cash.DBConnections;
import com.example.vodafone_cash.R;
import com.romellfudi.ussdlibrary.USSDApi;
import com.romellfudi.ussdlibrary.USSDController;
import com.romellfudi.ussdlibrary.USSDInterface;
import com.romellfudi.ussdlibrary.USSDService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {
    public Object look = new Object();
    DBConnections dbConnections ;
    phoneObjectAdapter adapter;
    String[] responses = new String[] {};
    String USSDCode;
    String Responses ;
    String Message;
    String delayTime;
    String mountMoney;
    static  String PhoneNumber;
    static int State_Stop_Resume = 0;
    static int state_Invoke = 0;
    ArrayList<PhoneObject> contacts ;
    static int check_if_ussd_done = 0;
    boolean x = false;

    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbConnections = new DBConnections(this);
        USSDCode = null;
        Responses = null;
        Message = null;
        delayTime = null;
        mountMoney = null;
        PhoneNumber = null;
        responses = new String[] {};
        recyclerView = findViewById(R.id.row_table);
        USSDController.verifyAccesibilityAccess(MainActivity.this);
        USSDController.verifyOverLay(MainActivity.this);
        setRecyclerView();
        Button bt_enter_data = findViewById(R.id.BT_ENTER_data);
        bt_enter_data.setOnClickListener(view -> {
            dbConnections.DeleteData();
            dbConnections.Create();
            Intent dataEntry = new Intent(MainActivity.this,DATAEntry.class);
            startActivityForResult(dataEntry,100);
        });
        State_Stop_Resume=1;

        Button stopANDresume = findViewById(R.id.stopANDresume);
        stopANDresume.setOnClickListener(view -> {
            Recharge charger = new Recharge();
            if(State_Stop_Resume == 0){
                stopANDresume.setText("Start");
                State_Stop_Resume = 1;

            }
            else{
                State_Stop_Resume = 0;
                ArrayList<PhoneObject> contacts = dbConnections.ReadDataWhereStateZero();

                Thread thread = new Thread(() -> {
                    for(int i = 0 ; i < contacts.size() && State_Stop_Resume != 1; i ++) {
                        try{
                            PhoneNumber = contacts.get(i).number;
                            charger.run(i);
                            runOnUiThread(() -> {setRecyclerView();});
                        }catch (Exception ex){

                            runOnUiThread(() -> Toast.makeText(MainActivity.this,ex.getMessage().toString(),Toast.LENGTH_LONG).show());
                        }
                    }
                    ArrayList<PhoneObject> Failds = dbConnections.ReadFaildNumbers();
                    for(int i = 0 ; i < Failds.size() && State_Stop_Resume != 1; i ++) {
                        try{
                            PhoneNumber = Failds.get(i).number;
                            charger.run(i);
                            runOnUiThread(() -> {setRecyclerView();});
                        }catch (Exception ex){

                            runOnUiThread(() -> Toast.makeText(MainActivity.this,ex.getMessage().toString(),Toast.LENGTH_LONG).show());
                        }
                    }
                });
                thread.start();
                stopANDresume.setText("Stop");
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && resultCode == RESULT_OK){
            setRecyclerView();
            USSDCode = data.getExtras().getString("USSDCode");
            Responses = data.getExtras().getString("Responses");
            if(Responses.contains("m")){Responses.replace("m",mountMoney);}
            if(Responses.contains(":")){
                responses = Responses.split(":");
            }else{
                responses = new String[] {Responses};
            }
            delayTime = data.getExtras().getString("delayTime");
            mountMoney = data.getExtras().getString("mountMoney");
        }
    }

    public void setRecyclerView(){
        try{
            Thread t = new Thread(()->{
               contacts = dbConnections.ReadData();
            });
            t.start();
            Thread.sleep(250);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new phoneObjectAdapter(MainActivity.this,contacts);
            recyclerView.setAdapter(adapter);
        }catch (Exception ex){
            Log.d("MassTransfer : ","[Error] " + ex.getMessage().toString());
            Toast.makeText(MainActivity.this,ex.getMessage().toString(),Toast.LENGTH_LONG).show();
        }

    }



    class Recharge{
        public synchronized void run(int i) {
            if(State_Stop_Resume == 0) {
                try {
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(()-> {
                        try {
                            Thread.sleep(2500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                    dbConnections.UpdateState("", PhoneNumber,"0");
                    check_if_ussd_done = 0;
                    Thread t1 = new Thread(()->{

                        HashMap<String, HashSet<String>> map = new HashMap<>();
                        map.put("KEY_LOGIN", new HashSet<>(Arrays.asList("espere", "waiting", "loading", "esperando")));
                        map.put("KEY_ERROR", new HashSet<>(Arrays.asList("problema", "problem", "error", "null")));
                        USSDApi ussdApi = USSDController.getInstance(MainActivity.this);
                        String USSDCode1 = USSDCode.replace("p", PhoneNumber);
                        String USSDCode2 = USSDCode1.replace("m", mountMoney);
                        String USSDCode3 = USSDCode2.replace("#", "");

                        ussdApi.callUSSDInvoke(USSDCode3.trim() + Uri.encode("#"), map, new USSDController.CallbackInvoke() {
                            @Override
                            public void responseInvoke(String message) {
                                // message has the response string data
                                if (responses != null) {
                                    String dataToSend = null;
                                    for (int i = 0; i < responses.length; i++) {
                                        if (responses[i].trim() == "p") {
                                            dataToSend = PhoneNumber;
                                        } else if (responses[i].trim() == "m") {
                                            dataToSend = mountMoney;
                                        } else {
                                            dataToSend = responses[i];
                                        }

                                        Toast.makeText(MainActivity.this, dataToSend, Toast.LENGTH_LONG).show();
                                        USSDService.send(dataToSend);
                                    }
                                }
                            }

                            @Override
                            public void over(String message) {
                                // message has the response string data from USSD or error
                                // response no have input text, NOT SEND ANY DATA
                                check_if_ussd_done = 1;
                                String state = "0";
                                if(message.contains("تم")){
                                    state = "1";
                                }else{state = "0";}
                                dbConnections.UpdateState(message, PhoneNumber,state);
                                runOnUiThread(()->{setRecyclerView();});
                                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();

                            }
                        });
                    });
                    t1.start();
                    int counter = 0 ;
                    while (check_if_ussd_done == 0){
                        counter ++;
                        Thread.sleep(10);
                        if(counter == 1000){
                            check_if_ussd_done = 1;
                            break;
                        }
                        if(check_if_ussd_done != 0){break;}
                    }
                    int counter1 = 0 ;
                    while (check_if_ussd_done != 0) {
                        Label1 : try {
                            Process process = Runtime.getRuntime().exec("logcat -d");
                            BufferedReader bufferedReader = new BufferedReader(
                                    new InputStreamReader(process.getInputStream()));

                            StringBuilder log = new StringBuilder();
                            String line = "";
                            while ((line = bufferedReader.readLine()) != null) {
                                if (line.contains("onAccessibilityEvent:") && line.contains("[text]") && line.contains("AlertDialog"))
                                    log.append(line);
                            }

                            String[] arr = log.toString().split("text]");
                            String message1 = arr[arr.length - 1].replace("[", "");
                            String message2 = message1.replace("]", "");
                            String[] arr1 = message2.split(",");
                            Message = arr1[0];
                            int RealLength = ((0+1) * 2);
                            int length = arr1.length;

                            Thread.sleep(10);
                            counter1++;
                            if(counter1 == 1000){
                                Thread.sleep(Integer.parseInt(delayTime) * 1000);
                                break ;
                            }
                            if(length == RealLength && arr1[length-1] != ""){
                                String state = "0";
                                if(Message.contains("تم")){
                                    state = "1";
                                }else{state="0";}
                                dbConnections.UpdateState(Message, PhoneNumber,state);
                                runOnUiThread(() -> setRecyclerView());
                                try {
                                    Thread.sleep(Integer.parseInt(delayTime) * 1000);
                                } catch (InterruptedException ex) {
                                    runOnUiThread(() -> Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show());
                                }
                                //if(t1.isAlive()){t1.stop();}
                                runOnUiThread(() -> Toast.makeText(MainActivity.this, " ", Toast.LENGTH_LONG).show());
                                check_if_ussd_done = 0;
                                break;
                            }else{
                                break Label1;
                            }



                        } catch (Exception ex) {
                            Log.d("MassTransfer : ","[Error] " + ex.getMessage().toString());
                            runOnUiThread(() -> Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show());
                        }

                    }

                } catch (Exception ex) {
                    Log.d("MassTransfer : ","[Error] " + ex.getMessage().toString());
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show());
                }

            }
        }
    }

}