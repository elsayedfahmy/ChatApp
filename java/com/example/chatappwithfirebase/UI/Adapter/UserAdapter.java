package com.example.chatappwithfirebase.UI.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chatappwithfirebase.UI.MessageActivity;
import com.example.chatappwithfirebase.Model.User;
import com.example.chatappwithfirebase.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.Viewholder> {
   private ArrayList<User>musers;
   private Context mcontext;
   boolean ischat;
   public UserAdapter(ArrayList<User>users,Context context,boolean ischat){
       this.mcontext=context;
       this.musers=users;
       this.ischat=ischat;
   }


    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(mcontext).inflate(R.layout.user_item,parent,false);

       return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
       final User user=musers.get(position);
       holder.txt_username.setText(user.getUsername());
       if (user.getImageUrl().equals("default")){
           holder.profile_image.setImageResource(R.mipmap.ic_launcher);
       }else {
           Glide.with(mcontext).load(user.getImageUrl()).into(holder.profile_image);
       }
       holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent=new Intent(mcontext, MessageActivity.class);
               intent.putExtra("userId",user.getId());
               mcontext.startActivity(intent);
           }
       });
       if (ischat){
           if (user.getStatus().equals("online")){
               holder.img_online.setVisibility(View.VISIBLE);
               holder.img_offline.setVisibility(View.GONE);
           }else { holder.img_online.setVisibility(View.GONE);
               holder.img_offline.setVisibility(View.VISIBLE);}

       }else {
           holder.img_online.setVisibility(View.GONE);
           holder.img_offline.setVisibility(View.GONE);
       }

    }

    @Override
    public int getItemCount() {
        return musers.size();
    }

    class Viewholder extends RecyclerView.ViewHolder {
       CircleImageView profile_image,img_online,img_offline;
       TextView txt_username;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            profile_image=itemView.findViewById(R.id.user_profile);
            txt_username=itemView.findViewById(R.id.txt_username);
            img_offline=itemView.findViewById(R.id.status_offline);
            img_online=itemView.findViewById(R.id.status_online);
        }
    }
}
