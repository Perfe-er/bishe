package com.example.zwq.assistant.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zwq.assistant.R;
import com.example.zwq.assistant.been.Class;
import com.example.zwq.assistant.been.User;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class ClassmateAdapter extends RecyclerView.Adapter<ClassmateAdapter.ViewHolder> {
    private List<User> mUserList;
    private Context context;
    private View inflater;
    private OnItemClick onItemClick;


    public ClassmateAdapter(Context context,List<User> userList){
        this.mUserList = userList;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClick  onItemClick ){
        this.onItemClick = onItemClick;
    }

    public interface OnItemClick {
        void onAllItemClick(int position,int userID);
        void onChatItemClick(ImageView ivChat);
        void onPhoneItemClick(ImageView ivPhone,String phone);
    }
    @NonNull
    @Override
    public ClassmateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(context).inflate(R.layout.item_classmate,parent,false);
        final ViewHolder holder = new ViewHolder(inflater);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ClassmateAdapter.ViewHolder holder, final int position) {
        final User user = mUserList.get(position);
        holder.tvClassName.setText(user.getName());
        holder.tvPhone.setText(user.getPhone());
        int sex = user.getSex();
        if (sex == 1){
            holder.ivSex.setImageResource(R.drawable.sex_g);
        }else {
            holder.ivSex.setImageResource(R.drawable.sex_m);
        }
        holder.ivChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.onChatItemClick(holder.ivChat);
            }
        });
        holder.ivPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.onPhoneItemClick(holder.ivPhone,user.getPhone());
            }
        });
        holder.conClassmate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.onAllItemClick(position,user.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUserList == null ? 0 : mUserList.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvClassName;
        TextView tvPhone;
        ImageView ivPhone;
        ImageView ivChat;
        ImageView ivSex;
        ConstraintLayout conClassmate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClassName = itemView.findViewById(R.id.tvClassName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            ivPhone = itemView.findViewById(R.id.ivPhone);
            ivChat = itemView.findViewById(R.id.ivChat);
            ivSex = itemView.findViewById(R.id.ivSex);
            conClassmate = itemView.findViewById(R.id.conClassmate);
        }


    }
}
