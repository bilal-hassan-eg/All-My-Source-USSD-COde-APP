package com.example.vodafone_cash.MassTransfer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vodafone_cash.R;

import java.util.List;


public class phoneObjectAdapter extends RecyclerView.Adapter<phoneObjectAdapter.ViewHolder> {
    Context context;
    List<PhoneObject> contacts ;
    phoneObjectAdapter(Context context,List<PhoneObject> contacts){
        this.contacts = contacts;
        this.context = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull phoneObjectAdapter.ViewHolder holder, int position) {
        try{
            if(contacts != null && contacts.size() > 0){
                PhoneObject phone = contacts.get(position);
                holder.id.setText(String.valueOf(phone.id));
                holder.number.setText(phone.number);
                holder.state.setText(phone.state);
            }else{
                return;
            }
        }catch (Exception ex){
            Toast.makeText(context,ex.getMessage().toString(),Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView id,number,state;
        public  ViewHolder(@Nullable View itemView){
            super(itemView);
            id = itemView.findViewById(R.id.id_text);
            number = itemView.findViewById(R.id.number_text);
            state = itemView.findViewById(R.id.state_text);
        }
    }
}
