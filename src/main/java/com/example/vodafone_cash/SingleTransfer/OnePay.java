package com.example.vodafone_cash.SingleTransfer;


import android.net.Uri;
import android.os.Bundle;

import com.example.vodafone_cash.DBConnections;
import com.example.vodafone_cash.MassTransfer.MainActivity;
import com.romellfudi.ussdlibrary.USSDApi;
import com.romellfudi.ussdlibrary.USSDController;


import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vodafone_cash.R;
import com.romellfudi.ussdlibrary.USSDService;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;


public class OnePay extends AppCompatActivity {
    EditText mountMoney, phoneNumber , delayTime;
    Button Start, StopANDResume;
    String mountMoneyText, phoneNumberText , delayTimeText;
    static int Counter = 0, MoneySuccessInteger = 0, State_Stop_Resume = 1, AllTimeCounter = 0, MoneyAfterDevision = 0;
    String Message;
    String USSDCodeFROMDB, Response;
    static String[] responses;
    ArrayList<String> MessagesList;
    TextView USSDCode, counter, MoneySuccess;
    static int check_if_ussd_done = 0;
    static HashMap<String, HashSet<String>> map = new HashMap<String, HashSet<String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_pay);

        MessagesList = new ArrayList<String>();
        Spinner spinner = findViewById(R.id.spinner_Codes_ONEPAY);
        DBConnections dbConnections = new DBConnections(this);
        ArrayList<String> USSDCODENames = dbConnections.SelectUSSDName();
        USSDController.verifyOverLay(this);
        USSDController.verifyAccesibilityAccess(this);
        SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, USSDCODENames);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayList<String> data = dbConnections.SelectUSSDBYName(USSDCODENames.get(i));
                USSDCodeFROMDB = data.get(0);
                Response = data.get(1);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mountMoney = findViewById(R.id.mountMoneyOne);
        phoneNumber = findViewById(R.id.NumberOne);
        Start = findViewById(R.id.StartOne);
        StopANDResume = findViewById(R.id.StopANDResumeOne);
        USSDCode = findViewById(R.id.codeUSSDOne);
        counter = findViewById(R.id.counter);
        delayTime = findViewById(R.id.delayTimeOne);
        MoneySuccess = findViewById(R.id.mountMonySuccessOne);
        Start.setOnClickListener(view -> {
            if (spinner.getSelectedItem() != null) {
                Recharge charger = new Recharge();
                StopANDResume.setText("Stop");
                delayTimeText = delayTime.getText().toString();
                mountMoneyText = mountMoney.getText().toString();
                phoneNumberText = phoneNumber.getText().toString();
                AllTimeCounter = 0;
                MoneyAfterDevision = 0;
                MoneySuccessInteger = 0;
                Counter = 0;
                MoneySuccess.setText("");
                counter.setText("");
                if (Response.contains(":"))
                    responses = Response.split(":");
                else {
                    responses = new String[]{Response};
                }
                State_Stop_Resume = 0;
                if (Integer.parseInt(mountMoneyText) % 50 == 0) {
                    AllTimeCounter = Integer.parseInt(mountMoneyText) / 50;
                } else {
                    for (int i = Integer.parseInt(mountMoneyText); i > 0; i--) {
                        if (i % 50 != 0) {
                            MoneyAfterDevision++;
                        } else if (i % 50 == 0) {
                            AllTimeCounter = i / 50;
                            break;
                        }
                    }
                }
                //Toast.makeText(OnePay.this,AllTimeCounter + " " + MoneyAfterDevision + " " + mountMoney.getText().toString() , Toast.LENGTH_LONG).show();

                if (mountMoneyText.trim().length() > 0 && phoneNumberText.trim().length() > 0) {
                    if (Integer.parseInt(mountMoneyText) < 100) {

                        Thread thread = new Thread(() -> {
                            if (MoneyAfterDevision != 0) {
                                charger.run(String.valueOf(MoneyAfterDevision));
                            }
                            MoneyAfterDevision = 0;
                            for (int i = 0; i < AllTimeCounter && State_Stop_Resume != 1; i++) {
                                //Toast.makeText(OnePay.this,phoneNumberText + " " + "50 " + "  " + String.valueOf(AllTimeCounter), Toast.LENGTH_LONG ).show();

                                charger.run("50");
                                try {
                                    Thread.sleep(9*1000);

                                }
                                catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                        });

                        thread.start();


                    }
                }

            }


        });

        StopANDResume.setOnClickListener(view -> {
            Recharge charger = new Recharge();
            if(spinner.getSelectedItem() != null) {
                if(State_Stop_Resume == 0){
                    StopANDResume.setText("Resume");
                    State_Stop_Resume = 1;

                }
                else{
                    delayTimeText = delayTime.getText().toString();
                    mountMoneyText = mountMoney.getText().toString();
                    phoneNumberText = phoneNumber.getText().toString();
                    State_Stop_Resume = 0;
                    if(mountMoneyText.trim().length() > 0 && phoneNumberText.trim().length() > 0 && Response.trim().length() > 0) {
                        mountMoneyText = mountMoney.getText().toString();
                        phoneNumberText = phoneNumber.getText().toString();
                        if (Response.contains(":"))
                            responses = Response.split(":");
                        else {
                            responses = new String[]{Response};
                        }
                        //Toast.makeText(OnePay.this , String.valueOf(FinalNewCounter) , Toast.LENGTH_LONG).show();
                        State_Stop_Resume = 0;

                        Thread thread = new Thread(() -> {
                            for (int i = Counter; i < AllTimeCounter && State_Stop_Resume != 1; i++) {
                                //Toast.makeText(OnePay.this,phoneNumberText + " " + "50 " + "  " + String.valueOf(AllTimeCounter), Toast.LENGTH_LONG ).show();
                                try{
                                    charger.run("50");
                                    if(Counter >= AllTimeCounter)
                                        break;
                                    runOnUiThread(()->{Toast.makeText(OnePay.this,String.valueOf(Counter) + " " + String.valueOf(AllTimeCounter),Toast.LENGTH_SHORT).show();});
                                }catch (Exception exception) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(OnePay.this, exception.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                    });
                                }
                            }
                        });

                        thread.start();


                    }
                    StopANDResume.setText("Stop");
                }
            }

        });
        setRecyclerView();
    }

    private void setRecyclerView() {
        ArrayAdapter arrayAdapter = new ArrayAdapter(OnePay.this, android.R.layout.simple_list_item_1,MessagesList);
        ListView list = findViewById(R.id.ListResponses);
        list.setAdapter(arrayAdapter);
    }

    class Recharge{
        public synchronized void run(String Mount) {
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
                    check_if_ussd_done = 0;

                    Thread t1 = new Thread(()->{

                        HashMap<String, HashSet<String>> map = new HashMap<>();
                        map.put("KEY_LOGIN", new HashSet<>(Arrays.asList("espere", "waiting", "loading", "esperando")));
                        map.put("KEY_ERROR", new HashSet<>(Arrays.asList("problema", "problem", "error", "null")));
                        USSDApi ussdApi = USSDController.getInstance(OnePay.this);
                        String USSDCode1 = USSDCodeFROMDB.replace("p", phoneNumberText);
                        String USSDCode2 = USSDCode1.replace("m", Mount);
                        String USSDCode3 = USSDCode2.replace("#", "");

                        ussdApi.callUSSDInvoke(USSDCode3.trim() + Uri.encode("#"), map, new USSDController.CallbackInvoke() {
                            @Override
                            public void responseInvoke(String message) {
                                // message has the response string data
                                if (responses != null) {
                                    String dataToSend = null;
                                    for (int i = 0; i < responses.length; i++) {
                                        if (responses[i].trim() == "p") {
                                            dataToSend = phoneNumberText;
                                        } else if (responses[i].trim() == "m") {
                                            dataToSend = Mount;
                                        } else {
                                            dataToSend = responses[i];
                                        }

                                        String finalDataToSend = dataToSend;
                                        runOnUiThread(() -> Toast.makeText(OnePay.this, finalDataToSend, Toast.LENGTH_LONG).show());
                                        USSDService.send(dataToSend);
                                    }
                                }
                            }

                            @Override
                            public void over(String message) {
                                // message has the response string data from USSD or error
                                // response no have input text, NOT SEND ANY DATA
                                check_if_ussd_done = 1;
                                MessagesList.add((message));
                                runOnUiThread(()->{setRecyclerView();});
                                runOnUiThread(() -> Toast.makeText(OnePay.this, message, Toast.LENGTH_LONG).show());

                            }
                        });
                    });
                    t1.start();
                    int counter1 = 0 ;
                    while (check_if_ussd_done == 0){
                        counter1 ++;
                        Thread.sleep(10);
                        if(counter1 == 1000){
                            check_if_ussd_done = 1;
                            break;
                        }
                        if(check_if_ussd_done != 0){break;}
                    }
                    int counter2 = 0 ;
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
                            counter2++;
                            if(counter2 == 1000){
                                Thread.sleep(Integer.parseInt(delayTimeText) * 1000);
                                break ;
                            }
                            if(length == RealLength && arr1[length-1] != ""){
                                String state = "0";
                                if(Message.contains("تم")){
                                    state = "1";
                                }else{state="0";}
                                MessagesList.add((Message));
                                runOnUiThread(() -> {setRecyclerView();
                                    MoneySuccessInteger += Integer.parseInt(Mount);
                                    MoneySuccess.setText(String.valueOf(MoneySuccessInteger));
                                    Counter++;
                                    counter.setText(String.valueOf(Counter));
                                });
                                try {
                                    Thread.sleep(Integer.parseInt(delayTimeText) * 1000);
                                } catch (InterruptedException ex) {
                                    runOnUiThread(() -> Toast.makeText(OnePay.this, ex.getMessage(), Toast.LENGTH_LONG).show());
                                }
                                //if(t1.isAlive()){t1.stop();}
                                runOnUiThread(() ->{
                                    Toast.makeText(OnePay.this, " ", Toast.LENGTH_LONG).show();


                                } );
                                check_if_ussd_done = 0;
                                break;
                            }else{
                                break Label1;
                            }



                        } catch (Exception ex) {
                            Log.d("MassTransfer : ","[Error] " + ex.getMessage().toString());
                            runOnUiThread(() -> Toast.makeText(OnePay.this, ex.getMessage(), Toast.LENGTH_LONG).show());
                        }

                    }

                } catch (Exception ex) {
                    Log.d("MassTransfer : ","[Error] " + ex.getMessage().toString());
                    runOnUiThread(() -> Toast.makeText(OnePay.this, ex.getMessage(), Toast.LENGTH_LONG).show());
                }

            }
        }
    }

}
