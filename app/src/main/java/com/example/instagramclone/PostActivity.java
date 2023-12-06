package com.example.instagramclone;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instagramclone.Adapter.PostAdapter;
import com.example.instagramclone.Fragments.HomeFragment;
import com.example.instagramclone.Model.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.UploadTask;
import com.hendraanggrian.appcompat.socialview.widget.SocialAutoCompleteTextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class PostActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> launcher;

    private ProgressBar progress;

    private List<String> imgUrls;
    private StorageReference storageReference;

    private ImageView close , image;
    private TextView post;
    private EditText price;
    SocialAutoCompleteTextView description;

    private PostAdapter postAdapter;
    private boolean dataUpload = false;

    public String fileName;

    public Uri downloadUri;
    private Post postItem;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        close = findViewById(R.id.close);
        image = findViewById(R.id.image);
        post = findViewById(R.id.post);
        description = findViewById(R.id.description);
        price = findViewById(R.id.price);
        progress = findViewById(R.id.progress_bar);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PostActivity.this , Home.class));
                finish();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });


        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult() , result -> {
            if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                Intent data = result.getData();

                if (data != null) {
                    Uri imgUri = data.getData();

                    postImage(imgUri);

                }
            }
        });

    }

    private void postImage(Uri uri) {

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress.setVisibility(View.VISIBLE);
                if ( description != null){
                    uploadImg(uri);
                }else {
                    Toast.makeText(PostActivity.this, "Please write a description.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        launcher.launch(intent);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    private void uploadImg(Uri imgUri) {
        if (imgUri != null) {


            storageReference = FirebaseStorage.getInstance().getReference().child("uploads");

            fileName = System.currentTimeMillis() + "." + getFileExtension(imgUri);

            // Create a reference to the file to be uploaded
            StorageReference fileRef = storageReference.child(fileName);

            // Upload the file to Firebase Storage


            fileRef.putFile(imgUri).addOnSuccessListener(taskSnapshot -> {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {

                    progress.setVisibility(View.GONE);

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("posts");


                    String downloadUrl = uri.toString();
                    String postId = ref.push().getKey();

                    postItem = new Post();
                    postItem.setImageUrl(downloadUrl);

                    storeImageDetails(fileName , postId , downloadUrl , description.getText().toString() ,
                            FirebaseAuth.getInstance().getCurrentUser().getUid() , price.getText().toString());

                });
            }).addOnFailureListener(e -> {
                progress.setVisibility(View.GONE);
                Toast.makeText(this, "Error"+e.getMessage(), Toast.LENGTH_SHORT).show();
            });


//            fileRef.putFile(imgUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                                // Check if the activity is still valid before updating the UI
//                        if (isActivityValid(PostActivity.this)) {
//
//                                if (task.isSuccessful()) {
//
//
//                                    String imageUrl = task.getResult().toString();
//
////                                    Toast.makeText(PostActivity.this, imageUrl, Toast.LENGTH_LONG).show();
//
//                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("posts");
//                                    String postId = ref.push().getKey();
//
//
//                                    HashMap<String , Object> map = new HashMap<>();
//                                    map.put("postId" , postId);
//                                    map.put("imageUrl" , imageUrl);
//                                    map.put("description" , description.getText().toString());
//                                    map.put("publisher" , FirebaseAuth.getInstance().getCurrentUser().getUid());
//                                    map.put("price" , price.getText().toString());
//                                    map.put("imageName" , fileName);
//
//                                    ref.child(postId).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            if (task.isSuccessful()){
//
//                                                progress.setVisibility(View.GONE);
//
//                                                image.setImageURI(null);
//                                                Toast.makeText(PostActivity.this, "Image uploaded successfully.", Toast.LENGTH_SHORT).show();
//
//                                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container , new HomeFragment()).commit();
//
//
//                                                DatabaseReference hashtagRef = FirebaseDatabase.getInstance().getReference().child("HashTags");
//                                                List<String> hashTags = description.getHashtags();
//
//                                                if (!hashTags.isEmpty()){
//                                                    for (String tag: hashTags ) {
//                                                        map.clear();
//                                                        map.put("tag" , tag.toLowerCase());
//                                                        map.put("postId" , postId);
//
//                                                        hashtagRef.child(tag.toLowerCase()).child(postId).setValue(map);
//
//                                                    }
//                                                }
//                                            }else {
//                                                return;
//                                            }
//                                        }
//                                    });
//
//
//                                } else {
//                                    progress.setVisibility(View.GONE);
//                                    Toast.makeText(PostActivity.this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//
//                    }
//                });


        }
    }


    // Helper method to check if the activity is still valid
    private boolean isActivityValid(Activity activity) {
        return activity != null && !activity.isFinishing() && !activity.isDestroyed();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == AppCompatActivity.RESULT_OK) {

            if (data != null) {
                Uri imgUri = data.getData();
                image.setImageURI(imgUri);
            }
        }
    }


    private void storeImageDetails(String imageName , String postId , String downloadUri ,
                                   String description , String publisher , String price)
    {
        HashMap<String , Object> map = new HashMap<>();

        map.put("name" , imageName);
        map.put("postId" , postId);
        map.put("imageUrl" , downloadUri);
        map.put("description" , description);
        map.put("publisher" , publisher);
        map.put("price" , price);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");

        reference.child(postId).setValue(map).addOnSuccessListener(task -> {
            Toast.makeText(this, "Post added.", Toast.LENGTH_SHORT).show();

            progress.setVisibility(View.GONE);

            image.setImageURI(null);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container , new HomeFragment()).commit();

        }).addOnFailureListener(e ->{
            progress.setVisibility(View.GONE);
            Toast.makeText(this, "Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
        });

    }


}