package com.example.zwq.assistant.Activity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.zwq.assistant.Adapter.AwardRecordAdapter;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.AwardsInfo;
import com.example.zwq.assistant.been.AwardSign;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.manager.RetrofitManager;

import java.util.ArrayList;
import java.util.List;

public class AwardRecordActivity extends BaseActivity {
    ImageView ivReturn;
    TextView tvNumber;
    AwardRecordAdapter mAwardRecordAdapter;
    LinearLayoutManager mLinearLayoutManager;
    List<AwardSign> mAwardSigns;
    RecyclerView recordRecycle;
    SwipeRefreshLayout recordRefresh;
    private int awardID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_award_record);
        initView();
        Intent intent = getIntent();
        awardID = Integer.parseInt(intent.getStringExtra("awardID"));
        initList();
        onItemClick();
    }

    public void initView() {
        ivReturn = findViewById(R.id.ivReturn);
        tvNumber = findViewById(R.id.tvNumber);
        recordRecycle = findViewById(R.id.recordRecycle);
        recordRefresh = findViewById(R.id.recordRefresh);
        ivReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recordRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initList();
                Toast.makeText(AwardRecordActivity.this,"刷新成功",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void initList(){
        mAwardSigns = new ArrayList<>();
        mLinearLayoutManager = new LinearLayoutManager(AwardRecordActivity.this);
        mAwardRecordAdapter = new AwardRecordAdapter(R.layout.item_award_record,mAwardSigns);
        recordRecycle.setLayoutManager(mLinearLayoutManager);
        recordRecycle.setAdapter(mAwardRecordAdapter);
        RetrofitManager.getInstance().createReq(AwardsInfo.class)
                .getawardsSignOfPub(awardID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<List<AwardSign>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<List<AwardSign>> listHttpResult) {
                        if (listHttpResult.getCode() == 200 && listHttpResult.getData()!=null){
                            mAwardSigns.clear();
                            mAwardSigns.addAll(listHttpResult.getData());
                            String number = String.valueOf(mAwardSigns.size());
                            tvNumber.setText(number);
                            mAwardRecordAdapter.notifyDataSetChanged();
                            recordRefresh.setRefreshing(false);
                        }else {
                            Toast.makeText(AwardRecordActivity.this,"没有参评人",Toast.LENGTH_SHORT).show();
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
        mAwardRecordAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                int awardSignID = mAwardSigns.get(position).getAwardSignID();
                int userID = mAwardSigns.get(position).getUid();
                int pass = mAwardSigns.get(position).getPass();
                Intent intent = new Intent(AwardRecordActivity.this,AwardSignInfoActivity.class);
                intent.putExtra("awardSignID",awardSignID +"");
                intent.putExtra("userID",userID +"");
                intent.putExtra("pass",pass +"");
                startActivity(intent);
            }
        });
    }
}
