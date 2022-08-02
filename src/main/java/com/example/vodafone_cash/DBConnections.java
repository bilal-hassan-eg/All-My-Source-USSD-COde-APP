package com.example.vodafone_cash;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.vodafone_cash.AddUSSD.USSDCodeObject;
import com.example.vodafone_cash.MassTransfer.PhoneObject;

import java.util.ArrayList;

public class DBConnections extends SQLiteOpenHelper {
    public  static  final String DBName = "numbers.db";
    public static  final int Version = 1;
    public DBConnections(Context context) {
        super(context,DBName,null,Version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS numbersTable (id INTEGER PRIMARY KEY AUTOINCREMENT,number  TEXT,state TEXT,Success Text)");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS ussdCodes (id INTEGER PRIMARY KEY AUTOINCREMENT,Name Text,Code TEXT,Response TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS numbersTable");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS ussdCodes");
        this.onCreate(sqLiteDatabase);
    }

    public void Create(){
        onCreate(this.getWritableDatabase());
    }

    public void InsertData(PhoneObject contact){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //contentValues.put("id",Integer.toString(contact.id));
        contentValues.put("number",contact.number);
        contentValues.put("state",contact.state);
        contentValues.put("Success","0");
        db.insert("numbersTable",null,contentValues);
    }
    public ArrayList<PhoneObject> ReadData(){
        ArrayList<PhoneObject> contacts  = new ArrayList<PhoneObject>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM numbersTable",null);
        data.moveToFirst();
        while(data.isAfterLast() == false){
            PhoneObject phone = new PhoneObject(data.getInt(0),data.getString(1),data.getString(2));

            contacts.add(phone);
            data.moveToNext();
        }
        return contacts;
    }
    public void UpdateState(String message,String PhoneNmuber,String State){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE numbersTable SET state='"+message+"', Success='"+ State +"' WHERE number='"+PhoneNmuber+"'");
        //ContentValues contentValues = new ContentValues();
        //contentValues.put("state",message);
        //db.update("numbersTable",contentValues,"state = ?", new String[] {message});
    }
    public void DeleteData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS numbersTable");
        //db.execSQL("DELETE FROM numbersTable");
    }
    public ArrayList<PhoneObject> ReadDataWhereStateZero(){
        ArrayList<PhoneObject> contacts = new ArrayList<PhoneObject>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM numbersTable WHERE state='0'",null);
        data.moveToNext();
        while (data.isAfterLast() == false){
            PhoneObject phone = new PhoneObject(data.getInt(0),data.getString(1),data.getString(2));
            contacts.add(phone);
            data.moveToNext();
        }
        return  contacts;
    }
    public ArrayList<PhoneObject> ReadFaildNumbers(){
        ArrayList<PhoneObject> contacts = new ArrayList<PhoneObject>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM numbersTable WHERE Success='0'",null);
        data.moveToNext();
        while (data.isAfterLast() == false){
            PhoneObject phone = new PhoneObject(data.getInt(0),data.getString(1),data.getString(2));
            contacts.add(phone);
            data.moveToNext();
        }
        return  contacts;
    }



    public void DeleteUSSDCode(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM ussdCodes WHERE Name='"+name+"'");
    }
    public ArrayList<USSDCodeObject> SelectUSSDCode(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<USSDCodeObject> items = new ArrayList<USSDCodeObject>();
        Cursor cursor = db.rawQuery("SELECT * FROM ussdCodes",null);
        cursor.moveToNext();
        while (cursor.isAfterLast() == false){
            USSDCodeObject ussd = new USSDCodeObject(cursor.getString(2),cursor.getString(3),cursor.getString(1));
            items.add(ussd);
            cursor.moveToNext();
        }
        return items;
    }
    public ArrayList<String> SelectUSSDName(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> items = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT Name FROM ussdCodes",null);
        cursor.moveToNext();
        while (cursor.isAfterLast() == false){
            items.add(cursor.getString(0));
            cursor.moveToNext();
        }
        return  items;
    }
    public void InsertUSSDCode(USSDCodeObject ussd){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put("Code",ussd.USSDCode);
        content.put("Response",ussd.Responses);
        content.put("Name",ussd.name);
        db.insert("ussdCodes",null,content);
    }
    public ArrayList<String> SelectUSSDBYName(String Name){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> item = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM ussdCodes WHERE Name='"+Name+"'",null);
        cursor.moveToNext();
        item.add(cursor.getString(2));
        item.add(cursor.getString(3));
        return item;
    }

}
