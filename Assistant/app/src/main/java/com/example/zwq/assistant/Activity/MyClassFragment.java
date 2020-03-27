package com.example.zwq.assistant.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zwq.assistant.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyClassFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_class,container,false);
        return view;
    }
}
