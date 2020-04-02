package com.example.zwq.assistant.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zwq.assistant.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    private Context mContext;
    private List<String> mList = new ArrayList<String>();
    private RecyclerViewAdapter.OnItemChatClickLitener mOnItemClickLitener;
    public RecyclerViewAdapter(Context context, RecyclerViewAdapter.OnItemChatClickLitener mLitener) {
        this.mContext = context;
        this.mOnItemClickLitener = mLitener;
    }

    public List<String> getData() {
        return mList;
    }

    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.item_class, null);
        return new RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewAdapter.MyViewHolder holder, final int position) {
        final String str = mList.get(position);
        //判断String传值是否为空
        if (!TextUtils.isEmpty(str)) {
            //给姓名赋值
            holder.tvClassName.setText(str);
            holder.tvFounder.setText(str);
            //头像设置点击事件
            holder.conMyClass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickLitener != null) {
                        mOnItemClickLitener.onItemClick(position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface OnItemChatClickLitener {
        void onItemClick(int position);
    }

    /**
     * 新建一个适配器类，同时内部新建一个ViewHolder类并继承相相应的类
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvClassName;
        TextView tvFounder;
        ConstraintLayout conMyClass;

        public MyViewHolder(View itemView) {
            // 这里很重要，参数中的View对象也很重要
            super(itemView);
            tvClassName = itemView.findViewById(R.id.tvClassName);
            tvFounder = itemView.findViewById(R.id.tvFounder);
            conMyClass = itemView.findViewById(R.id.conMyClass);
        }
    }
}
