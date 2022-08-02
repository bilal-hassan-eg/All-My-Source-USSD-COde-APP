package com.example.vodafone_cash;
import com.example.vodafone_cash.AddUSSD.AddUssdCodes;
import com.example.vodafone_cash.MassTransfer.MainActivity;
import com.example.vodafone_cash.SingleTransfer.OnePay;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.vodafone_cash.MassTransfer.MainActivity;

public class DashBoard extends AppCompatActivity {
    DBConnections dbConnections;
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE , Manifest.permission.READ_PHONE_STATE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        dbConnections = new DBConnections(this);
        dbConnections.DeleteData();
        dbConnections.Create();
        //dbConnections.DropTable();
        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        Button All = findViewById(R.id.ALL);
        All.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashBoard.this, MainActivity.class);
                startActivity(intent);
            }
        });
        Button One = findViewById(R.id.ONE);
        One.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashBoard.this, OnePay.class);
                startActivity(intent);
            }
        });
        Button AddUSSD = findViewById(R.id.AddCode);
        AddUSSD.setOnClickListener(view -> {
            Intent intent = new Intent(DashBoard.this, AddUssdCodes.class);
            startActivity(intent);
        });

    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}