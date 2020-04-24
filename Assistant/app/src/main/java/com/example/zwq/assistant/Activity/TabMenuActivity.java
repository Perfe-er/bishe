package com.example.zwq.assistant.Activity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zwq.assistant.R;
import com.example.zwq.assistant.manager.UserInfoManager;

public class TabMenuActivity extends BaseActivity {
    ImageView ivHome;
    ImageView ivClass;
    ImageView ivCommunicate;
    ImageView ivMine;
    TextView tvHome;
    TextView tvClass;
    TextView tvCommunicate;
    TextView tvMine;
    LinearLayout myHome;
    LinearLayout lClass;
    LinearLayout myCommunicate;
    LinearLayout myInfo;
    FrameLayout contentFrame;
    FragmentManager fragmentManager;
    HomeFragment mHomeFragment;
    MyClassFragment mMyClassFragment;
    CommunicateFragment mCommunicateFragment;
    MeFragment mMeFragment;
    private int hindColor = 0xFF8C8B8B;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_tab_menu);
        initView();
        changeCheck(myHome);
    }
    public void initView(){
        ivHome = findViewById(R.id.ivHome);
        ivClass = findViewById(R.id.ivClass);
        ivCommunicate = findViewById(R.id.ivCommunicate);
        ivMine = findViewById(R.id.ivMine);
        tvHome = findViewById(R.id.tvHome);
        tvClass = findViewById(R.id.tvClass);
        tvCommunicate = findViewById(R.id.tvCommunicate);
        tvMine = findViewById(R.id.tvMine);
        myHome = findViewById(R.id.myHome);
        lClass = findViewById(R.id.lClass);
        myCommunicate = findViewById(R.id.myCommunicate);
        myInfo = findViewById(R.id.myInfo);
        contentFrame = findViewById(R.id.contentFrame);
        myHome.setOnClickListener(this);
        lClass.setOnClickListener(this);
        myCommunicate.setOnClickListener(this);
        myInfo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.myHome:
                changeCheck(myHome);
                break;
            case R.id.lClass:
                if (UserInfoManager.getInstance().getLoginUser().getStuType() == 2){
                changeCheck(lClass);
                }else {
                    Toast.makeText(this,"你还不是导员，无法操作",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.myCommunicate:
                changeCheck(myCommunicate);
                break;
            case R.id.myInfo:
                changeCheck(myInfo);
                break;
        }
    }

    public void changeCheck(View view){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        switch (view.getId()){
            case R.id.myHome:
                select();
                ivHome.setImageResource(R.drawable.assistant_home_s);
                tvHome.setTextColor(Color.RED);
                if(mHomeFragment==null){
                    mHomeFragment = new HomeFragment();
                    transaction.add(R.id.contentFrame,mHomeFragment);
                }else{
                    transaction.show(mHomeFragment);
                }
                break;
            case R.id.lClass:
                select();
                ivClass.setImageResource(R.drawable.myclass_s);
                tvClass.setTextColor(Color.RED);
                if(mMyClassFragment==null){
                    mMyClassFragment = new MyClassFragment();
                    transaction.add(R.id.contentFrame,mMyClassFragment);
                }else{
                    transaction.show(mMyClassFragment);
                }
                break;
            case R.id.myCommunicate:
                select();
                ivCommunicate.setImageResource(R.drawable.icon_use_message_s);
                tvCommunicate.setTextColor(Color.RED);
                if(mCommunicateFragment==null){
                    mCommunicateFragment = new CommunicateFragment();
                    transaction.add(R.id.contentFrame,mCommunicateFragment);
                }else{
                    transaction.show(mCommunicateFragment);
                }
                break;
            case R.id.myInfo:
                select();
                ivMine.setImageResource(R.drawable.icon_user_mine_s);
                tvMine.setTextColor(Color.RED);
                if(mMeFragment==null){
                    mMeFragment = new MeFragment();
                    transaction.add(R.id.contentFrame,mMeFragment);
                }else{
                    transaction.show(mMeFragment);
                }
                break;
        }
        transaction.commit();
    }

    public void select(){
        ivHome.setImageResource(R.drawable.assistant_home);
        tvHome.setTextColor(hindColor);
        ivClass.setImageResource(R.drawable.myclass);
        tvClass.setTextColor(hindColor);
        ivCommunicate.setImageResource(R.drawable.icon_use_message);
        tvCommunicate.setTextColor(hindColor);
        ivMine.setImageResource(R.drawable.icon_user_mine);
        tvMine.setTextColor(hindColor);
    }

    public void hideAllFragment(FragmentTransaction transaction){
        if(mHomeFragment!=null){
            transaction.hide(mHomeFragment);
        }
        if(mMyClassFragment!=null){
            transaction.hide(mMyClassFragment);
        }
        if(mCommunicateFragment!=null){
            transaction.hide(mCommunicateFragment);
        }
        if(mMeFragment!=null){
            transaction.hide(mMeFragment);
        }
    }



}
