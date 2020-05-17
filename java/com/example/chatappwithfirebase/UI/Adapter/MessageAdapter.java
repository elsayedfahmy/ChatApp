package com.example.chatappwithfirebase.UI.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chatappwithfirebase.Model.Chat;
import com.example.chatappwithfirebase.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.Viewholder> {

    final static int left_type=0;
    final static int right_type=1;
    private ArrayList<Chat> mChat;
    private Context mcontext;
    FirebaseUser firebaseUser;

    public MessageAdapter(ArrayList<Chat>mChat,Context context){
        this.mcontext=context;
        this.mChat=mChat;
    }


    @NonNull
    @Override
    public MessageAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType==right_type)
        {
             view= LayoutInflater.from(mcontext).inflate(R.layout.send_item,parent,false);
            return new MessageAdapter.Viewholder(view);
        }else
        {  view= LayoutInflater.from(mcontext).inflate(R.layout.recive_item,parent,false);
        return new MessageAdapter.Viewholder(view);}



    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.Viewholder holder, int position) {
        Chat chat=mChat.get(position);
        holder.txt_show_message.setText(chat.getMessage());
        if (position==mChat.size()-1){
            if (chat.isIf_seen()){holder.txt_seen.setText("seen");}else {holder.txt_seen.setText("delivered");}

        }else holder.txt_seen.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    class Viewholder extends RecyclerView.ViewHolder {

        TextView txt_show_message,txt_seen;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            txt_show_message=itemView.findViewById(R.id.txt_show_message);
            txt_seen =itemView.findViewById(R.id.txt_seen);

        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(firebaseUser.getUid())){
            Log.d(TAG,"sender >>>>>>>>>>>>>>>>>>>>>>");
            return right_type;
        }else {
            Log.d(TAG,"reciver >>>>>>>>>>>>>>>>>>>>>>");
            return  left_type;}
    }
}

