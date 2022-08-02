package com.example.vodafone_cash.AddUSSD;

public class USSDCodeObject {
    public String USSDCode ;
    public String Responses ;
    public String name;
    public USSDCodeObject(String USSDCode, String Responses,String name){
        this.name = name;
        this.USSDCode = USSDCode;
        this.Responses = Responses;
    }
}
