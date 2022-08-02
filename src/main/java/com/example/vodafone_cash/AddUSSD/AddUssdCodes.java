package com.example.vodafone_cash.AddUSSD;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.vodafone_cash.DBConnections;
import com.example.vodafone_cash.R;

import java.util.ArrayList;

public class AddUssdCodes extends AppCompatActivity {
    ListView listView;
    DBConnections dbConnections;
    ArrayList<USSDCodeObject> items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ussd_codes);
        EditText UssdCode = findViewById(R.id.USSDCodeEnter);
        EditText Response = findViewById(R.id.ResponsesText);
        EditText Name = findViewById(R.id.CodeNameEnter);
        dbConnections = new DBConnections(this);

        dbConnections.Create();
        Button save = findViewById(R.id.InsertUSSD);
        save.setOnClickListener(view -> {
            String Ussd = UssdCode.getText().toString();
            String name = Name.getText().toString();
            String response = Response.getText().toString();
            if(Ussd.trim().length() > 0 && response.trim().length() > 0){
                if(!items.contains(new USSDCodeObject(Ussd,response,name))){
                    dbConnections.InsertUSSDCode(new USSDCodeObject(Ussd,response,name));
                    items.add(new USSDCodeObject(Ussd,response,name));
                    UpdateData();
                }

            }else{
                Toast.makeText(this,"Enter Data",Toast.LENGTH_LONG).show();
            }
        });
        UpdateData();
    }
    public void UpdateData(){
        items = dbConnections.SelectUSSDCode();
        listViewAdapter adapter = new listViewAdapter(items, this);
        listView = findViewById(R.id.listViewCodes);
        listView.setAdapter(adapter);
    }
}