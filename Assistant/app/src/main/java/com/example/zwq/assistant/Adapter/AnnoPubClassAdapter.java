package com.example.zwq.assistant.Adapter;


import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.been.Class;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class AnnoPubClassAdapter extends BaseQuickAdapter<Class, BaseViewHolder> {
    private List<String> pubClass = new ArrayList<>();

    public AnnoPubClassAdapter(int layoutResId, @Nullable List<Class> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Class item) {
        int position = helper.getLayoutPosition();
        helper.setText(R.id.tvPubClass,item.getClassName());
        CheckBox cbPubClass = helper.getView(R.id.cbPubClass);
        final String classID = String.valueOf(item.getClassID());

        helper.setOnCheckedChangeListener(R.id.cbPubClass, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    pubClass.add(classID);
                }else {
                    pubClass.remove(classID);
                }
            }
        });
    }
    public List<String> getPubClass() {
        return pubClass;
    }
}
