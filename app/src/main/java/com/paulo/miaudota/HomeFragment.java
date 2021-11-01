package com.paulo.miaudota;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("Warning_Activity","onCreateView -> HomeFragment");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user == null) {
            Log.e("Warning_Activity","onCreateView HomeFragment userNull-> ");
            startActivity(new Intent(getActivity(), WelcomeScreen.class));
        }

        return inflater.inflate(R.layout.fragment_home,container,false);
    }

}
