package com.example.zwq.assistant.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.example.zwq.assistant.Activity.ClassManageActivity;
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

public class BanjiAdapter extends RecyclerView.Adapter<BanjiAdapter.ViewHolder> implements View.OnLongClickListener {
    private List<Class> mClasses;
    private Context context;
    private onItemLongClickListener itemLongClickListener;


    public BanjiAdapter(Context context,List<Class> classList) {
        this.mClasses = classList;
        this.context = context;
    }



    //条目长按接口
    public interface onItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(onItemLongClickListener listener) {
        this.itemLongClickListener = listener;
    }

    @Override
    public boolean onLongClick(View v) {
        Log.d("TAG", "onLongClick: 长按");
        if (itemLongClickListener != null) {
            Log.d("TAG", "onLongClick: 长按事件");
            itemLongClickListener.onItemLongClick(v, (int) v.getTag());//注意这里使用getTag方法获取position
        }
        return true;
    }


    @NonNull
    @Override
    public BanjiAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_class,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        view.setOnLongClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final BanjiAdapter.ViewHolder holder, final int position) {
            final Class aClass = mClasses.get(position);
            holder.classID = aClass.getClassID();
            holder.itemView.setTag(position);
            holder.itemView.setOnLongClickListener(this);
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
