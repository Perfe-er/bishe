package com.example.zwq.assistant.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.TestLooperManager;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.zwq.assistant.Adapter.AnnoListAdapter;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.AnnoInfo;
import com.example.zwq.assistant.been.Anno;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.manager.UserInfoManager;

import java.util.ArrayList;
import java.util.List;

public class AnnoSearchActivity extends BaseActivity {
    ImageView ivReturn;
    EditText etKeyWords;
    TextView tvCancel;
    RecyclerView searchRecycle;
    ImageView ivSearch;
    AnnoListAdapter mAnnoListAdapter;
    LinearLayoutManager mLinearLayoutManager;
    List<Anno> mAnnos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_anno_search);
        initView();
    }
    public void initView(){
        ivReturn = findViewById(R.id.ivReturn);
        etKeyWords = findViewById(R.id.etKeyWords);
        tvCancel = findViewById(R.id.tvCancel);
        ivSearch = findViewById(R.id.ivSearch);
        searchRecycle = findViewById(R.id.searchRecycle);
        ivReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etKeyWords.getText().clear();
            }
        });
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchList();
                onItemClick();
                onItemLongClick();
            }
        });
    }


    public void searchList(){
        mAnnos = new ArrayList<>();
        mAnnoListAdapter = new AnnoListAdapter(R.layout.item_anno_list,mAnnos);
        mLinearLayoutManager = new LinearLayoutManager(this);
        searchRecycle.setLayoutManager(mLinearLayoutManager);
        searchRecycle.setAdapter(mAnnoListAdapter);
        String keyWords = etKeyWords.getText().toString();
        RetrofitManager.getInstance().createReq(AnnoInfo.class)
                .searchAnno(keyWords)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<List<Anno>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<List<Anno>> listHttpResult) {
                        if (listHttpResult.getCode() == 200 && listHttpResult.getData() != null) {
                            mAnnos.clear();
                            mAnnos.addAll(listHttpResult.getData());
                            mAnnoListAdapter.notifyDataSetChanged();
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

    public void onItemClick(){
        mAnnoListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                int annoID = mAnnos.get(position).getAnnoID();
                TextView tvTitle = view.findViewById(R.id.tvTitle);
                TextView tvContent = view.findViewById(R.id.tvContent);
                TextView tvRelease = view.findViewById(R.id.tvRelease);
                TextView tvDateTime = view.findViewById(R.id.tvDateTime);
                String title = tvTitle.getText().toString();
                String content = tvContent.getText().toString();
                String release = tvRelease.getText().toString();
                String dateTime = tvDateTime.getText().toString();
                Intent intent = new Intent(AnnoSearchActivity.this,AnnoInfoActivity.class);
                intent.putExtra("title",title);
                intent.putExtra("content",content);
                intent.putExtra("release",release);
                intent.putExtra("dateTime",dateTime);
                intent.putExtra("annoID",annoID + "");
                startActivity(intent);
            }
        });

    }

    public void onItemLongClick(){
        mAnnoListAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, final int position) {
                final int annoID = mAnnos.get(position).getAnnoID();
                TextView tvTitle = view.findViewById(R.id.tvTitle);
                new AlertDialog.Builder(AnnoSearchActivity.this).setTitle("是否删除")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                RetrofitManager.getInstance().createReq(AnnoInfo.class)
                                        .deleteAnno(annoID)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Observer<HttpResult<Anno>>() {
                                            @Override
                                            public void onSubscribe(Disposable d) {

                                            }

                                            @Override
                                            public void onNext(HttpResult<Anno> annoHttpResult) {
                                                if (annoHttpResult.getCode() == 200 ){
                                                    mAnnos.remove(position);
                                                    mAnnoListAdapter.notifyDataSetChanged();
                                                    Toast.makeText(AnnoSearchActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Toast.makeText(AnnoSearchActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
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
                        }).setNegativeButton("取消", null).show();
                return false;
            }
        });
    }
}
