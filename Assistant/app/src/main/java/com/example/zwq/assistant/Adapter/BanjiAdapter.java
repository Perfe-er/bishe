package com.example.zwq.assistant.Adapter;

import android.content.Context;
import android.content.Intent;
import android.icu.text.MessagePattern;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zwq.assistant.Activity.ClassManageActivity;
import com.example.zwq.assistant.Activity.MyClassFragment;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.UserInfo;
import com.example.zwq.assistant.been.Class;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.been.User;
import com.example.zwq.assistant.manager.RetrofitManager;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BanjiAdapter extends RecyclerView.Adapter<BanjiAdapter.ViewHolder> {
    private List<Class> mClasses;
    private Context context;
    private View inflater;
    private onItemLongClickListener itemLongClickListener;


    public BanjiAdapter(Context context,List<Class> classList) {
        this.mClasses = classList;
        this.context = context;
    }

    //条目长按接口
    public interface onItemLongClickListener {
        void onItemLongClick(View view, int position,int classID);
    }

    //设置提供监听方法
    public void setOnItemLongClickListener(BanjiAdapter.onItemLongClickListener onItemLongClickListener) {
        this.itemLongClickListener = onItemLongClickListener;
    }

    @NonNull
    @Override
    public BanjiAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(context).inflate(R.layout.item_class,parent,false);
        final ViewHolder holder = new ViewHolder(inflater);
        inflater.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //获取view对应的位置
                int position=holder.getLayoutPosition();
                int classID = mClasses.get(position).getClassID();
                if (itemLongClickListener!=null){
                    //回调监听
                    itemLongClickListener.onItemLongClick(v,position,classID);
                }
                return true;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final BanjiAdapter.ViewHolder holder, final int position) {
            final Class aClass = mClasses.get(position);
            holder.classID = aClass.getClassID();
            holder.tvClassName.setText(aClass.getClassName());
            RetrofitManager.getInstance().createReq(UserInfo.class)
                .getUserInfoById(aClass.getFounderID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<User>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<User> userHttpResult) {
                        if (userHttpResult.getCode() == 200) {
                            holder.tvFounder.setText(userHttpResult.getData().getName());
                        }else {
                            Toast.makeText(context,"创建人出错",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return mClasses == null ? 0 : mClasses.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvClassName;
        TextView tvFounder;
        int classID;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClassName = itemView.findViewById(R.id.tvClassName);
            tvFounder = itemView.findViewById(R.id.tvFounder);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String className = tvClassName.getText().toString();
            Intent intent = new Intent(context, ClassManageActivity.class);
            intent.putExtra("classID",classID + "");
            intent.putExtra("className",className);
            context.startActivity(intent);
        }
    }


}
