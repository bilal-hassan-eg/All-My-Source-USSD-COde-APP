package com.example.vodafone_cash.AddUSSD;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.vodafone_cash.DBConnections;
import com.example.vodafone_cash.R;

import java.util.ArrayList;

public class listViewAdapter extends BaseAdapter {
    DBConnections dbConnections;
    ArrayList<USSDCodeObject> ussds;
    AddUssdCodes context;
    public listViewAdapter(ArrayList<USSDCodeObject> ussds,Context context){
        this.context = (AddUssdCodes) context;
        this.ussds = ussds;
    }
    @Override
    public int getCount() {
        return ussds.size();
    }

    @Override
    public Object getItem(int i) {
        return ussds.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        dbConnections = new DBConnections(context);
        LayoutInflater ly = context.getLayoutInflater();
        View vi = ly.inflate(R.layout.add_ussd_item_listview,null);
        TextView ussd = vi.findViewById(R.id.Code);
        TextView response = vi.findViewById(R.id.responses);
        TextView Name = vi.findViewById(R.id.codeName);
        Button delete = vi.findViewById(R.id.delete);
        Name.setText(ussds.get(i).name);
        ussd.setText(ussds.get(i).USSDCode);
        response.setText(ussds.get(i).Responses);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbConnections.DeleteUSSDCode(ussds.get(i).name);
                context.UpdateData();
            }
        });
        return vi;
    }
}
