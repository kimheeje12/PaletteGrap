package com.example.palettegrap.view.fragment;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.palettegrap.R;

public class FragmentDialog extends DialogFragment {

    private Fragment fragment;

    public FragmentDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feed_dialog1, container, false);

        Bundle args = getArguments();
        String value = args.getString("add");

        fragment = getActivity().getSupportFragmentManager().findFragmentByTag("tag");

        if (fragment != null) {
            DialogFragment dialogFragment = (DialogFragment) fragment;
            dialogFragment.dismiss();
        }

        return view;
    }
}