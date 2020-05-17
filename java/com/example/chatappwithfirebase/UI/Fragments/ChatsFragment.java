package com.example.chatappwithfirebase.UI.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatappwithfirebase.UI.Adapter.UserAdapter;
import com.example.chatappwithfirebase.Model.Chat;
import com.example.chatappwithfirebase.Model.User;
import com.example.chatappwithfirebase.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ChatsFragment extends Fragment {


    DatabaseReference reference;

    UserAdapter userAdapter;
    ArrayList<User> musers;

    List<String>mUsersList_WithCats;
    RecyclerView users_RecycleView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_chat, container, false);

        users_RecycleView=view.findViewById(R.id.chat_recycle_view);
        users_RecycleView.setHasFixedSize(true);
        users_RecycleView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsersList_WithCats =new ArrayList<>();
        musers =new ArrayList<>();
        readUsersChats();
        userAdapter=new UserAdapter(musers,getContext(),true);
       users_RecycleView.setAdapter(userAdapter);

        return view;
    }
 public void  readUsersChats()
    {final  String currentUser= FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference=FirebaseDatabase.getInstance().getReference("Chats");
         reference.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 mUsersList_WithCats.clear();
                 for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                     Chat chat=snapshot.getValue(Chat.class);
                     if (chat.getSender().equals(currentUser)){
                         mUsersList_WithCats.add(chat.getReciver());
                     } if (chat.getReciver().equals(currentUser)){
                         mUsersList_WithCats.add(chat.getSender());
                     }

                 }
                 Log.d(TAG,"mUsersList_WithCats"+mUsersList_WithCats.size()+" >>>>>>>>>>>>");
                 readUsers();
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });

    }
  public void   readUsers(){
        reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                musers.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    User user=snapshot.getValue(User.class);
                    for (String id:mUsersList_WithCats){
                        if (user.getId().equals(id)){
                            if (musers.size()!=0){
                                for (User user1: musers){
                                    if (!user.getId().equals(user1.getId())){
                                        musers.add(user);
                                    }
                                }
                            }else{ musers.add(user);}

                        }
                    }


                }
               // Log.d(TAG,"muers"+ musers.size()+ ">>>>>>>>>>>>");
                userAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

  }
}
