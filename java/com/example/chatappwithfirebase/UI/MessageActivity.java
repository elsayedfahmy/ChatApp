package com.example.chatappwithfirebase.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatappwithfirebase.UI.Adapter.MessageAdapter;
import com.example.chatappwithfirebase.Model.Chat;
import com.example.chatappwithfirebase.Model.User;
import com.example.chatappwithfirebase.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;

public class MessageActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    DatabaseReference refrence;
    String userid;

    CircleImageView profile_imageView;
    TextView txt_username;

    MaterialEditText edt_message;
    ImageButton btn_send;
    RecyclerView message_recyclerView;

    ArrayList<Chat>mchats;
    MessageAdapter messageAdapter;

    ValueEventListener seenlistener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        Intent myintent=getIntent();
          userid=myintent.getStringExtra("userId");


        Toolbar tool_bar=findViewById(R.id.toolbar);
        setSupportActionBar(tool_bar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tool_bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessageActivity.this,StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        profile_imageView=findViewById(R.id.profile_image_user);
        txt_username=findViewById(R.id.txt_username);
        btn_send=findViewById(R.id.btn_sent);
        edt_message=findViewById(R.id.edt_Mesaage);

        message_recyclerView=findViewById(R.id.message_recycle_view);
        message_recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        message_recyclerView.setLayoutManager(linearLayoutManager);



        readuserdata(profile_imageView,txt_username,userid);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message=edt_message.getText().toString();
                if (!message.isEmpty()){
                sendMessage(firebaseUser.getUid(),userid,message);
                edt_message.setText("");
                }else {
                    Toast.makeText(MessageActivity.this, "enter your message", Toast.LENGTH_SHORT).show();
                }
            }
        });

       // seenmessage(userid);


    }
    public void readuserdata(final CircleImageView profileImage, final TextView username, final String userid)
    {

        refrence=FirebaseDatabase.getInstance().getReference("Users").child(userid);
        refrence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if (user.getImageUrl().equals("default")){
                    profileImage.setImageResource(R.mipmap.ic_launcher);
                }else {
                    Glide.with(getApplicationContext()).load(user.getImageUrl()).into(profileImage);
                }
                readMessage(firebaseUser.getUid(),userid);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void sendMessage(String sender,String reciver,String message)
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("reciver",reciver);
        hashMap.put("message",message);
        hashMap.put("if_seen",false);
        reference.child("Chats").push().setValue(hashMap);

    }
    public void seenmessage(final String userid)
    {
        refrence=FirebaseDatabase.getInstance().getReference("Chats");
        seenlistener=refrence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    Chat chat=dataSnapshot.getValue(Chat.class);
                    if (chat.getReciver().equals(firebaseUser.getUid())&&chat.getSender().equals(userid)){
                        HashMap<String,Object>hashMap=new HashMap<>();
                        hashMap.put("if_seen",true);
                        snapshot.getRef().updateChildren(hashMap);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

  void readMessage(final String sender, final String reciver)
    {

        mchats=new ArrayList<>();
        refrence=FirebaseDatabase.getInstance().getReference("Chats");
        refrence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchats.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    Chat chat=snapshot.getValue(Chat.class);
                    if (chat.getReciver().equals(reciver) && chat.getSender().equals(sender)||
                            chat.getReciver().equals(sender) && chat.getSender().equals(reciver)){
                        mchats.add(chat);
                    }
                   // messageAdapter.notifyDataSetChanged();

                    messageAdapter =new MessageAdapter(mchats,MessageActivity.this);
                    message_recyclerView.setAdapter(messageAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    public void status(String status){
        refrence= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("status",status);
        refrence.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
       // seenmessage(userid);
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        refrence.removeEventListener(seenlistener);
        status("offline");
    }
}
