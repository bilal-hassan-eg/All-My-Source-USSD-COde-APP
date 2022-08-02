package com.example.vodafone_cash.MassTransfer;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vodafone_cash.DBConnections;
import com.example.vodafone_cash.R;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class DATAEntry extends AppCompatActivity {
    Button saveData,EnterExel;
    EditText mountMoney,delayTime;
    String USSDCode , Response;
    DBConnections dbConnections;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dataentry);
        dbConnections = new DBConnections(this);
        ArrayList<String> CodesItems = dbConnections.SelectUSSDName();
        Spinner codesSpinner = findViewById(R.id.spinner_codes);
        //SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this,R.id.spinner_codes,CodesItems);
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,CodesItems);
        codesSpinner.setAdapter(adapter);
        codesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayList<String> data = dbConnections.SelectUSSDBYName(CodesItems.get(i));
                //Toast.makeText(DATAEntry.this,data.get(0) + " " + data.get(1), Toast.LENGTH_LONG).show();
                USSDCode = data.get(0);
                Response = data.get(1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        saveData = (Button) findViewById(R.id.save_data);
        saveData.setOnClickListener(view -> {
            mountMoney = (EditText)findViewById(R.id.money);
            String mountMoneyText = mountMoney.getText().toString();
            delayTime = (EditText)findViewById(R.id.delay);
            String delayTimeText = delayTime.getText().toString();
            Intent dataToSend = new Intent();
            if(mountMoneyText.trim().length() > 0 && delayTimeText.trim().length() > 0){
                dataToSend.putExtra("USSDCode",USSDCode);
                dataToSend.putExtra("Responses",Response);
                dataToSend.putExtra("mountMoney",mountMoney.getText().toString());
                dataToSend.putExtra("delayTime",delayTime.getText().toString());
                if(Integer.parseInt(mountMoney.getText().toString()) < 50){
                    setResult(RESULT_OK,dataToSend);
                    finish();
                }else{
                    Toast.makeText(DATAEntry.this,"Must money be less than 200",Toast.LENGTH_LONG).show();
                }

            }else{
                Toast.makeText(DATAEntry.this,"Enter All Data",Toast.LENGTH_SHORT).show();
            }

        });
        EnterExel = findViewById(R.id.enter_Exel);
        EnterExel.setOnClickListener(view -> {
            Intent readFile = new Intent(Intent.ACTION_GET_CONTENT);
            readFile.setType("*/*");
            startActivityForResult(readFile,100);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK){
            String Filepath = null;
            TextView path = findViewById(R.id.pathFile);
            Toast.makeText(DATAEntry.this,data.getDataString(),Toast.LENGTH_LONG).show();
            DBConnections db = new DBConnections(this);

            try{
                if(data.getData().getPath().contains("root")){
                    Filepath = data.getData().getPath().replace("/root","");
                }else{
                    Filepath = data.getData().getPath();
                }
                if(Filepath.contains("primary")){
                    String[] arr = Filepath.split(":");
                    Filepath = "/storage/emulated/0/"+arr[1];
                }
                path.setText(Filepath);
                FileInputStream file = new FileInputStream(Filepath);
                Workbook workbook = new XSSFWorkbook(file);
                DataFormatter dataFormatter = new DataFormatter();
                Iterator<Sheet> sheets = workbook.sheetIterator();
                while (sheets.hasNext()){
                    Sheet sh = sheets.next();
                    Iterator<Row> iterator = sh.iterator();
                    while (iterator.hasNext()){
                        Row row = iterator.next();
                        Iterator<Cell> cellIterator = row.iterator();
                        while (cellIterator.hasNext()){
                            Cell cell = cellIterator.next();
                            int i = 0;
                            String cellValue = dataFormatter.formatCellValue(cell);
                            PhoneObject contact = new PhoneObject(i,cellValue,"0");
                            db.InsertData(contact);
                            i++;
                        }
                    }
                }
                workbook.close();
            }catch (Exception e){
                Toast.makeText(DATAEntry.this,e.getMessage().toString(),Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }
}