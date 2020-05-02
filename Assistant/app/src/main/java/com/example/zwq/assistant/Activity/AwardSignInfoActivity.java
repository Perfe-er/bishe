package com.example.zwq.assistant.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zwq.assistant.Adapter.AwardCommentAdapter;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.AwardsInfo;
import com.example.zwq.assistant.been.AwardSign;
import com.example.zwq.assistant.been.AwardSignComment;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.manager.CosManager;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.manager.UserInfoManager;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;

import java.util.ArrayList;
import java.util.List;

public class AwardSignInfoActivity extends BaseActivity {
    private int userID;
    private int awardSignID;
    private int pass;
    TextView tvPath;
    TextView tvPass;
    TextView tvComment;
    TextView tvDownLoad;
    TextView tvName;
    ImageView ivReturn;
    RecyclerView commentRecycle;
    SwipeRefreshLayout commentRefresh;
    List<AwardSignComment> mComments;
    LinearLayoutManager mLinearLayoutManager;
    AwardCommentAdapter mAwardCommentAdapter;
    TextView etContent;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_award_sign_info);
        initView();
        Intent intent = getIntent();
        userID = Integer.parseInt(intent.getStringExtra("userID"));
        awardSignID = Integer.parseInt(intent.getStringExtra("awardSignID"));
        pass = Integer.parseInt(intent.getStringExtra("pass"));
        if (pass == 1){
            tvPass.setTextColor(R.color.enable);
            tvPass.setEnabled(false);
        }
        int userType = UserInfoManager.getInstance().getLoginUser().getStuType();
        if (userType == 2){
            tvPass.setVisibility(View.VISIBLE);
            tvComment.setVisibility(View.VISIBLE);
        }else {
            tvPass.setVisibility(View.GONE);
            tvComment.setVisibility(View.GONE);
        }
        signInfo();
        commentList();
    }

    public void initView(){
        tvPass = findViewById(R.id.tvPass);
        tvPath = findViewById(R.id.tvPath);
        tvComment = findViewById(R.id.tvComment);
        tvName = findViewById(R.id.tvName);
        tvDownLoad = findViewById(R.id.tvDownLoad);
        ivReturn = findViewById(R.id.ivReturn);
        commentRecycle = findViewById(R.id.commentRecycle);
        commentRefresh = findViewById(R.id.commentRefresh);
        ivReturn.setOnClickListener(this);
        tvPass.setOnClickListener(this);
        tvComment.setOnClickListener(this);
        tvDownLoad.setOnClickListener(this);
        commentRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                commentList();
                Toast.makeText(AwardSignInfoActivity.this,"刷新成功",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivReturn:
                finish();
                break;
            case R.id.tvPass:
                awardPass();
                break;
            case R.id.tvComment:
                editComment();
                break;
            case R.id.tvDownLoad:
                downLoad();
                break;
        }
    }

    public void downLoad(){
        CosXmlProgressListener progressListener = null;
        CosXmlResultListener resultListener =new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Toast.makeText(AwardSignInfoActivity.this,"下载成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Toast.makeText(AwardSignInfoActivity.this,"下载失败",Toast.LENGTH_SHORT).show();
            }
        };
        String url = tvPath.getText().toString();
        CosManager.getInstance().downLoad(url,progressListener,resultListener);
    }

    public void editComment(){
        View view = getLayoutInflater().inflate(R.layout.dialog_comment_content,null);
        etContent = view.findViewById(R.id.etContent);
        new AlertDialog.Builder(this).setTitle("你的修改建议")
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String content = etContent.getText().toString();
                        int uid = UserInfoManager.getInstance().getUid();
                        RetrofitManager.getInstance().createReq(AwardsInfo.class)
                                .awardsComment(uid,awardSignID,content)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<HttpResult<AwardSignComment>>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(HttpResult<AwardSignComment> awardSignCommentHttpResult) {
                                        Toast.makeText(AwardSignInfoActivity.this,awardSignCommentHttpResult.getMsg(),Toast.LENGTH_SHORT).show();
                                        if (awardSignCommentHttpResult.getCode() == 200){
                                            dialog.dismiss();
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
                }).setNegativeButton("取消",null).show();
    }

    public void commentList(){
        mComments = new ArrayList<>();
        mAwardCommentAdapter = new AwardCommentAdapter(R.layout.item_comment,mComments);
        mLinearLayoutManager = new LinearLayoutManager(this);
        commentRecycle.setAdapter(mAwardCommentAdapter);
        commentRecycle.setLayoutManager(mLinearLayoutManager);
        RetrofitManager.getInstance().createReq(AwardsInfo.class)
                .listAwardSignComment(awardSignID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<List<AwardSignComment>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<List<AwardSignComment>> listHttpResult) {
                        if (listHttpResult.getCode() == 200 && listHttpResult.getData() != null){
                            mComments.clear();
                            mComments.addAll(listHttpResult.getData());
                            mAwardCommentAdapter.notifyDataSetChanged();
                            commentRefresh.setRefreshing(false);
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

    public void awardPass(){
        RetrofitManager.getInstance().createReq(AwardsInfo.class)
                .editPass(awardSignID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<AwardSign>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onNext(HttpResult<AwardSign> awardSignHttpResult) {
                        Toast.makeText(AwardSignInfoActivity.this,awardSignHttpResult.getMsg(),Toast.LENGTH_SHORT).show();
                        if (awardSignHttpResult.getCode() == 200){
                            tvPass.setTextColor(R.color.enable);
                            tvPass.setEnabled(false);
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

    public void signInfo(){
        RetrofitManager.getInstance().createReq(AwardsInfo.class)
                .getAwardsSignById(awardSignID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<AwardSign>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<AwardSign> awardSignHttpResult) {
                        if (awardSignHttpResult.getCode() == 200) {
                            tvPath.setText(awardSignHttpResult.getData().getWord());
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
}
