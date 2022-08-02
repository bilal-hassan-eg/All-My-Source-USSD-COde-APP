package com.example.vodafone_cash.SingleTransfer;

import java.security.PublicKey;

public class responseMessageObject {
    public String Message;
    public int Id;
    public responseMessageObject(int Id,String Message){
        this.Id = Id;
        this.Message = Message;
    }
}
