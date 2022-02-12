package com.example.palettegrap.view.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.palettegrap.R;

public class Fragment_Following extends Fragment {



    public Fragment_Following() {
    }


    @Override
    public void onStart() {
        super.onStart();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment__following, container, false);
    }
}