package com.example.instagramclone.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagramclone.Login;
import com.example.instagramclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {


    private Button logout;
    private TextView username , name , email;

    FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        logout = view.findViewById(R.id.logout);

        name = view.findViewById(R.id.name);
        username = view.findViewById(R.id.username);
        email = view.findViewById(R.id.email);
        auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");


        if (user == null){

            navigateToLoginActivity();

        }else {

            fetchUserData();

        }


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();

                Toast.makeText(requireContext(), "Logout successful.", Toast.LENGTH_SHORT).show();

//                fra().beginTransaction().replace(R.id.fragment_container , new HomeFragment()).commit();

                navigateToLoginActivity();
            }
        });

        return view;

    }

    private void navigateToLoginActivity() {

        // Create an Intent to start the LoginActivity
        Intent intent = new Intent(getActivity(), Login.class);

        // Clear the back stack to prevent going back to the profile fragment
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        // Start the LoginActivity
        startActivity(intent);

        // Finish the current activity to prevent returning to it when pressing the back button
        getActivity().finish();

    }


    private void fetchUserData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            navigateToLoginActivity();
        } else {
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String userName = dataSnapshot.child("name").getValue(String.class);
                        String userEmail = dataSnapshot.child("email").getValue(String.class);
                        String Datausername = dataSnapshot.child("username").getValue(String.class);

                        // Update UI with the fetched data
                        name.setText(userName);
                        username.setText("@"+Datausername);
                        email.setText(userEmail);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle the error
                }
            });
        }
    }

}