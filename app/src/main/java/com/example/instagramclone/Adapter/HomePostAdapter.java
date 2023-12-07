package com.example.instagramclone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramclone.Model.HomePost;
import com.example.instagramclone.Model.Post;
import com.example.instagramclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HomePostAdapter extends RecyclerView.Adapter<HomePostAdapter.Viewholder> {

    private Context mContext;
    private List<HomePost> homePost;
    private FirebaseUser firebaseUser;

    public HomePostAdapter(Context mContext, List<HomePost> homePost) {
        this.mContext = mContext;
        this.homePost = homePost;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.home_post_item , parent , false);

        return new HomePostAdapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        HomePost homepagePost = homePost.get(position);

        if ("default".equals(homepagePost.getImageUrl()) || homepagePost.getImageUrl() == null){
            holder.postImage.setImageResource(R.mipmap.ic_launcher);
        } else {
            Picasso.get().load(homepagePost.getImageUrl()).into(holder.postImage);
        }



        holder.description.setText(homepagePost.getDescription());

        FirebaseDatabase.getInstance().getReference().child("posts").child(homepagePost.getPublisher())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Post post = snapshot.getValue(Post.class);
                            if (post != null) {
                                holder.author.setText(post.getPublisher());
                            }
                        }
                    }


                    @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return homePost.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{

        public ImageView postImage;
        public TextView description , author , buy;


        public Viewholder(@NonNull View itemView) {
            super(itemView);

            postImage = itemView.findViewById(R.id.post_image);
            description = itemView.findViewById(R.id.description);
            author = itemView.findViewById(R.id.publisher);
            buy = itemView.findViewById(R.id.buy);

        }
    }

}
