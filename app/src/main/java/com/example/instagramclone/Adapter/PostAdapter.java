package com.example.instagramclone.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import com.example.instagramclone.Model.Post;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context mContext;
    private List<Post> posts;
    private boolean isFragment;
    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> posts, boolean isFragment) {
        this.mContext = mContext;
        this.posts = posts;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Post post = posts.get(position);
        holder.description.setText(post.getDescription());
        holder.price.setText(post.getPrice());

        Log.d("PostAdapter", "Image URL: " + post.getImageUrl());

        // Assuming you have an attribute called "imageUrl" in your Post class
        Picasso.get().load(post.getImageUrl()).placeholder(R.mipmap.ic_launcher).into(holder.imagePost);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imagePost;
        public TextView description;
        public TextView price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imagePost = itemView.findViewById(R.id.post_image);
            description = itemView.findViewById(R.id.description);
            price = itemView.findViewById(R.id.price);
        }
    }

}
