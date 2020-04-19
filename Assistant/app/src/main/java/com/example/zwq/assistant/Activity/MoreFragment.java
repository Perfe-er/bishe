package com.example.zwq.assistant.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zwq.assistant.R;

public class MoreFragment extends BaseFragment {
    ConstraintLayout conLeave;
    ConstraintLayout conLeaveRecord;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more,container,false);
        initView(view);
        return view;
    }

    public void initView(View view){
        conLeave = view.findViewById(R.id.conLeave);
        conLeaveRecord = view.findViewById(R.id.conLeaveRecord);

        conLeave.setOnClickListener(this);
        conLeaveRecord.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.conLeave:
                intent = new Intent(getContext(), LeaveActivity.class);
                startActivity(intent);
                break;
            case R.id.conLeaveRecord:
                intent = new Intent(getContext(), LeaveRecordActivity.class);
                startActivity(intent);
                break;
        }
    }
}
