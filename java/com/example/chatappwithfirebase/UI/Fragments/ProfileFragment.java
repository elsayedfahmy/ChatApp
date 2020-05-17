package com.example.chatappwithfirebase.UI.Fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatappwithfirebase.Model.User;
import com.example.chatappwithfirebase.R;
import com.example.chatappwithfirebase.UI.LoginActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import javax.xml.transform.Result;


public class ProfileFragment extends Fragment {

   // private static final Object RESULT_OK =-1 ;
    CircleImageView img_profile,img_add;
    TextView txt_username;
    DatabaseReference reference;
    StorageReference storageReference;
    Uri img_uri;
    StorageTask uploadTask;
    private static final int image_Request=1;
    String currentUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
         currentUser= FirebaseAuth.getInstance().getCurrentUser().getUid();
       storageReference= FirebaseStorage.getInstance().getReference("Uploads");

        img_profile =view.findViewById(R.id.img_profile);
        txt_username =view.findViewById(R.id.txt_username);
        img_add =view.findViewById(R.id.img_add);
        // to get and upload user infromation
        getUserInfo();

        // to add image of iser
        img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

        return view;
    }


    public void openImage()
    {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,image_Request);

    }
    private void upload_ImageProfile() {
       // CropImage.activity().setAspectRatio(1,1).start(ProfileFragmentnt.this);
        final ProgressDialog pd=new ProgressDialog(getContext());
        pd.setMessage("Uploading ..");
        pd.show();
        if (img_uri!=null){
      final  StorageReference fileReference=storageReference.child(System.currentTimeMillis()+"."+getfileExtreation(img_uri));
            uploadTask=fileReference.putFile(img_uri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
               ////-------------------- وقفت هنا ياسيد  ========================================================
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloaduri=task.getResult();
                        String myuri=downloaduri.toString();
                         reference=FirebaseDatabase.getInstance().getReference("Users").child(currentUser);
                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("ImageUrl",myuri);
                        reference.updateChildren(hashMap);
                        pd.dismiss();
                    }
                    else Toast.makeText(getContext(), "uploading failed", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                     Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });

        }else{ Toast.makeText(getContext(), "no image selected", Toast.LENGTH_SHORT).show();
        }
    }

    //---------------------------------------
   public String getfileExtreation(Uri uri)
   {
       ContentResolver contentResolver=getContext().getContentResolver();
       MimeTypeMap mime=MimeTypeMap.getSingleton();
     return   mime.getExtensionFromMimeType( contentResolver.getType(uri));

   }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==image_Request&&resultCode== LoginActivity.RESULT_OK
                &&data!=null&&data.getData()!=null)
            img_uri=data.getData();
        if (uploadTask !=null && uploadTask.isInProgress()){
            Toast.makeText(getContext(), "upload inprogress", Toast.LENGTH_SHORT).show();
        }else upload_ImageProfile();

    }
//-----------------------------------------------------------------------------
    public void getUserInfo()
    {

        reference= FirebaseDatabase.getInstance().getReference("Users").child(currentUser);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                       txt_username.setText(user.getUsername());
                       if (user.getImageUrl().equals("default")){
                           img_profile.setImageResource(R.mipmap.ic_launcher);
                       }
                       else {
                       Glide.with(getContext()).load(user.getImageUrl()).into(img_profile);}



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }


}
