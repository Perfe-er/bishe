package com.example.zwq.assistant.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zwq.assistant.R;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.Service.UserInfo;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.been.User;
import com.example.zwq.assistant.manager.UserInfoManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MeFragment extends BaseFragment {
    private TextView tvNumber;
    private TextView tvName;
    private TextView tvStuID;
    private TextView tvCollege;
    private TextView tvClass;
    private TextView tvBirthday;
    private TextView tvStuType;
    private TextView tvPhone;
    private TextView tvParentPho;
    private TextView tvIDCard;
    private TextView tvAddress;
    private ImageView ivSex;
    private ConstraintLayout conAddress;
    private ConstraintLayout conInfo;
    private ConstraintLayout conNumber;
    private ConstraintLayout conStuID;
    private ConstraintLayout conIDCard;
    private ConstraintLayout conClass;
    private ConstraintLayout conParentPho;
    //让FragmentManager 知道需要菜单回调
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_me,container,false);
        initView(view);
        getUserInfo();
        return view;
    }

    //实例化菜单
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_info_setting, menu); }

    public void initView(View view){
        tvName = view.findViewById(R.id.tvName);
        tvStuID = view.findViewById(R.id.tvStuID);
        tvNumber = view.findViewById(R.id.tvNumber);
        tvCollege = view.findViewById(R.id.tvCollege);
        tvClass = view.findViewById(R.id.tvClass);
        tvBirthday = view.findViewById(R.id.tvBirthday);
        tvStuType = view.findViewById(R.id.tvStuType);
        tvPhone = view.findViewById(R.id.tvAssistantPhone);
        tvParentPho = view.findViewById(R.id.tvPerentPho);
        tvIDCard = view.findViewById(R.id.tvIDCard);
        tvAddress = view.findViewById(R.id.tvAddress);
        ivSex = view.findViewById(R.id.ivSex);
        conInfo = view.findViewById(R.id.conInfo);
        conNumber = view.findViewById(R.id.conNumber);
        conAddress = view.findViewById(R.id.conAddress);
        conStuID = view.findViewById(R.id.conStuID);
        conClass = view.findViewById(R.id.conClass);
        conParentPho = view.findViewById(R.id.conPerentPho);
        conIDCard = view.findViewById(R.id.conIDCard);
        conInfo.setOnClickListener(this);
        conNumber.setOnClickListener(this);
        conClass.setOnClickListener(this);
    }

    public void onClick(View view){
        super.onClick(view);
        Intent intent;
        switch (view.getId()){
            case R.id.conInfo:
                intent = new Intent(getContext(),InfoEditActivity.class);
                intent.putExtra("name",tvName.getText());
                intent.putExtra("stuID",tvStuID.getText());
                intent.putExtra("college",tvCollege.getText());
                intent.putExtra("birthday",tvBirthday.getText());
                intent.putExtra("parentPho",tvParentPho.getText());
                intent.putExtra("address",tvAddress.getText());
                intent.putExtra("IDCard",tvAddress.getText());
                intent.putExtra("stuType",tvStuType.getText());
                startActivity(intent);
                break;
            case R.id.conClass:
                String className = tvClass.getText().toString();
                intent = new Intent(getContext(),MyClassActivity.class);
                startActivity(intent);
                break;
            case R.id.conNumber:
                break;
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.setting) {
            Intent intent = new Intent(getContext(), SettingActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResume() {
        super.onResume();
            getUserInfo();
    }

    private void getUserInfo() {
        RetrofitManager.getInstance()
                .createReq(UserInfo.class)
                .getUserInfoById(UserInfoManager.getInstance().getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<User>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<User> userHttpResult) {
                        if (userHttpResult.getCode() == 200 && userHttpResult.getData() != null) {
                            tvName.setText(userHttpResult.getData().getName());
                            tvStuID.setText(userHttpResult.getData().getStuID());
                            tvAddress.setText(userHttpResult.getData().getAddress());
                            tvCollege.setText(userHttpResult.getData().getCollege());
                            tvClass.setText(userHttpResult.getData().getClassName());
                            tvIDCard.setText(userHttpResult.getData().getIdentity());
                            tvPhone.setText(userHttpResult.getData().getPhone());
                            tvClass.setText(userHttpResult.getData().getClassName());
                            tvParentPho.setText(userHttpResult.getData().getParentPho());
                            int number = userHttpResult.getData().getNumber();
                            tvNumber.setText(String.valueOf(number));
                            int sex = userHttpResult.getData().getSex();
                            if (sex == 1){
                                ivSex.setImageResource(R.drawable.sex_g);
                            }else {
                                ivSex.setImageResource(R.drawable.sex_m);
                            }
                            tvBirthday.setText(userHttpResult.getData().getBirthday());
                            int stuType = userHttpResult.getData().getStuType();
                            if (stuType == 0 ){
                                tvStuType.setText("普通学生");
                                conAddress.setVisibility(View.VISIBLE);
                                conIDCard.setVisibility(View.VISIBLE);
                                conStuID.setVisibility(View.VISIBLE);
                                conNumber.setVisibility(View.VISIBLE);
                                conClass.setVisibility(View.VISIBLE);
                                conParentPho.setVisibility(View.VISIBLE);
                            }else if (stuType == 1){
                                tvStuType.setText("班委");
                                conAddress.setVisibility(View.VISIBLE);
                                conIDCard.setVisibility(View.VISIBLE);
                                conStuID.setVisibility(View.VISIBLE);
                                conNumber.setVisibility(View.VISIBLE);
                                conClass.setVisibility(View.VISIBLE);
                                conParentPho.setVisibility(View.VISIBLE);
                            }else if (stuType == 2){
                                tvStuType.setText("导员");
                                conAddress.setVisibility(View.GONE);
                                conIDCard.setVisibility(View.GONE);
                                conStuID.setVisibility(View.GONE);
                                conNumber.setVisibility(View.GONE);
                                conClass.setVisibility(View.GONE);
                                conParentPho.setVisibility(View.GONE);
                            }
                        }else {
                            Toast.makeText(getContext(),"获取失败",Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getContext(),"网络异常",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }
}
