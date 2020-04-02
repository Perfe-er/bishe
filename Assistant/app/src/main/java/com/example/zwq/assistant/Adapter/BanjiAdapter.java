package com.example.zwq.assistant.Adapter;

import android.content.Context;
import android.icu.text.MessagePattern;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zwq.assistant.R;
import com.example.zwq.assistant.been.Class;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class BanjiAdapter extends RecyclerView.Adapter<BanjiAdapter.ViewHolder> {
    private List<Class> mClasses;
    private Context context;
    private View inflater;

    public BanjiAdapter(Context context,List<Class> classList) {
        this.mClasses = classList;
        this.context = context;
    }


    @NonNull
    @Override
    public BanjiAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(context).inflate(R.layout.item_class,parent,false);
        ViewHolder holder = new ViewHolder(inflater);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BanjiAdapter.ViewHolder holder, int position) {
            Class aClass = mClasses.get(position);
            holder.tvClassName.setText(aClass.getClassName());
            holder.tvFounder.setText(aClass.getFounderID() + "");
    }

    @Override
    public int getItemCount() {
        return mClasses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvClassName;
        TextView tvFounder;
        ConstraintLayout conMyClass;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClassName = itemView.findViewById(R.id.tvClassName);
            tvFounder = itemView.findViewById(R.id.tvFounder);
            conMyClass = itemView.findViewById(R.id.conMyClass);
        }
    }
}
