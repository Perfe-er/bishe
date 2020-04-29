package com.example.zwq.assistant.Activity;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.manager.CosManager;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.Service.UserInfo;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.been.User;
import com.example.zwq.assistant.manager.UserInfoManager;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.Url;

import static android.app.Activity.RESULT_OK;


public class MeFragment extends BaseFragment {
    private TextView tvNumber;
    private TextView tvName;
    private TextView tvClass;
    private TextView tvStuType;
    private TextView tvPhone;
    private ImageView ivSex;
    private ImageView ivHead;
    private ConstraintLayout conInfo;
    private ConstraintLayout conNumber;
    private ConstraintLayout conClass;
    private ConstraintLayout conEditPassWd;
    private ConstraintLayout conQuit;
    private ConstraintLayout conStuType;
    private LinearLayout llLeave;
    private LinearLayout llSign;
    private LinearLayout llSignList;
    private LinearLayout llLeaveRecord;
    private View layout;
    private AlertDialog.Builder builder;
    private LayoutInflater inflater;
    private AlertDialog dialog;
    private TextView photoGraph;
    private TextView photo;
    private TextView cancel;
    private String path;//图片路径
    private Uri imageUri;
    public static final int NONE = 0;
    public static final int PHOTO_CAMERA = 1;// 相机拍照
    public static final int PHOTO_COMPILE = 2; // 编辑图片
    public static final int PHOTO_RESOULT = 3;// 结果
    private String ImageName;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        initView(view);
        getUserInfo();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
        }
        return view;
    }


    public void initView(View view) {
        tvName = view.findViewById(R.id.tvName);
        tvNumber = view.findViewById(R.id.tvNumber);
        tvClass = view.findViewById(R.id.tvClass);
        tvStuType = view.findViewById(R.id.tvStuType);
        tvPhone = view.findViewById(R.id.tvAssistantPhone);
        ivSex = view.findViewById(R.id.ivSex);
        ivHead = view.findViewById(R.id.ivHead);
        conInfo = view.findViewById(R.id.conInfo);
        conNumber = view.findViewById(R.id.conNumber);
        conStuType = view.findViewById(R.id.conStuType);
        conClass = view.findViewById(R.id.conClass);
        conEditPassWd = view.findViewById(R.id.conEditPassWd);
        conQuit = view.findViewById(R.id.conQuit);
        llLeave = view.findViewById(R.id.llLeave);
        llSign = view.findViewById(R.id.llSign);
        llSignList = view.findViewById(R.id.llSignList);
        llLeaveRecord = view.findViewById(R.id.llLeaveRecord);
        ivHead.setOnClickListener(this);
        conInfo.setOnClickListener(this);
        conEditPassWd.setOnClickListener(this);
        conQuit.setOnClickListener(this);
        conNumber.setOnClickListener(this);
        conClass.setOnClickListener(this);
        conStuType.setOnClickListener(this);
        llLeave.setOnClickListener(this);
        llSignList.setOnClickListener(this);
        llSign.setOnClickListener(this);
        llLeaveRecord.setOnClickListener(this);
    }


    public static String getStringToday() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    public void onClick(View view) {
        super.onClick(view);
        Intent intent;
        switch (view.getId()) {
            case R.id.conInfo:
                intent = new Intent(getContext(), MineActivity.class);
                startActivity(intent);
                break;
            case R.id.conClass:
                intent = new Intent(getContext(), MyClassActivity.class);
                startActivity(intent);
                break;
            case R.id.conNumber:
                intent = new Intent(getContext(), MyMoralRecordActivity.class);
                startActivity(intent);
                break;
            case R.id.conEditPassWd:
                intent = new Intent(getContext(), EditPassActivity.class);
                startActivity(intent);
                break;
            case R.id.conQuit:
                intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.conStuType:
                new AlertDialog.Builder(getContext()).setTitle("切换身份")
                        .setPositiveButton("导员", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                stuType(2);
                            }
                        }).setNegativeButton("普通学生", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stuType(0);
                    }
                }).show();
                break;
            case R.id.ivHead:
                selectHead();
                break;
            case R.id.llLeave:
                intent = new Intent(getContext(), LeaveActivity.class);
                startActivity(intent);
                break;
            case R.id.llLeaveRecord:
                intent = new Intent(getContext(), LeaveRecordActivity.class);
                startActivity(intent);
                break;
            case R.id.llSign:
                intent = new Intent(getContext(),SignActivity.class);
                startActivity(intent);
                break;
            case R.id.llSignList:
                intent = new Intent(getContext(),SignRecordActivity.class);
                startActivity(intent);
                break;
            case R.id.photoGraph:
                if (Build.VERSION.SDK_INT >= 23) {
                    int permission = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.CAMERA);
                    if (permission == PackageManager.PERMISSION_GRANTED) {
                        //如果有了相机的权限就调用相机
//                        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        //设置图片的名称
                        ImageName = "/" + getStringToday() + ".jpg";

                        // 设置调用系统摄像头的意图(隐式意图)
                        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        //设置照片的输出路径和文件名

                        File file= new File(Environment.getExternalStorageDirectory(), ImageName);
                        imageUri = Uri.fromFile(file);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        //开启摄像头
                        startActivityForResult(intent, PHOTO_CAMERA);
                        dialog.dismiss();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("提示");
                        builder.setMessage("是否开启相机权限?");
                        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //去请求相机权限
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 0);
                            }
                        });
                        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity(), "您拒绝了开启相机权限", Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder.show();
                    }
                } else {
                    //不是6.0以上版本直接调用相机
                    //设置图片的名称
                    ImageName = "/" + getStringToday() + ".jpg";

                    // 设置调用系统摄像头的意图(隐式意图)
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    //设置照片的输出路径和文件名

                    File file=   new File(Environment.getExternalStorageDirectory(), ImageName);
                    imageUri = Uri.fromFile(file);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    //开启摄像头
                    startActivityForResult(intent, PHOTO_CAMERA);
                    dialog.dismiss();
                }
                break;
            case R.id.photo:
                // 设置调用系统相册的意图(隐式意图)
                intent = new Intent();
                //设置值活动//android.intent.action.PICK
                intent.setAction(Intent.ACTION_PICK);
                //设置类型和数据
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                // 开启系统的相册
                startActivityForResult(intent, PHOTO_COMPILE);
                dialog.dismiss();
                break;
            case R.id.cancel:
                dialog.dismiss();//关闭对话框
                break;
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        getUserInfo();
    }

    private void stuType(int stuType) {
        RetrofitManager.getInstance().createReq(UserInfo.class)
                .editStuType(UserInfoManager.getInstance().getUid(), stuType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<User>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<User> userHttpResult) {
                        if (userHttpResult.getCode() == 200) {
                            UserInfoManager.getInstance().onLogin(userHttpResult.getData());
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            startActivity(intent);
                            Toast.makeText(getContext(), "切换成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "出现错误", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getContext(), "网络出错", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void selectHead() {
        builder = new AlertDialog.Builder(getContext());//创建对话框
        inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.dialog_select, null);
        builder.setView(layout);//设置对话框的布局
        dialog = builder.create();//生成最终的对话框
        dialog.show();//显示对话框
        photoGraph = layout.findViewById(R.id.photoGraph);
        photo = layout.findViewById(R.id.photo);
        cancel = layout.findViewById(R.id.cancel);
        photoGraph.setOnClickListener(this);
        photo.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    // 调用startActivityResult，返回之后的回调函数
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == NONE)
            return;

        // 通过照相机拍照的图片出理
        if (requestCode == PHOTO_CAMERA) {
            // 设置文件保存路径这里放在跟目录下
            File picture = new File(Environment.getExternalStorageDirectory()
                    + ImageName);
            //裁剪图片
            imageUri = Uri.fromFile(picture);
            startPhotoZoom(imageUri);
        }

        if (data == null)
            return;

        // 读取相册裁剪图片
        if (requestCode == PHOTO_COMPILE) {
            //裁剪图片
            imageUri = data.getData();
            startPhotoZoom(imageUri);
        }

        // 裁剪照片的处理结果
        if (requestCode == PHOTO_RESOULT) {
            if (data != null) {
                setPicToView(data);
                String uid = String.valueOf(UserInfoManager.getInstance().getUid());
                CosXmlProgressListener progressListener = null;
                CosManager.ICosXmlResultListener resultListener = new CosManager.ICosXmlResultListener() {
                    @Override
                    public void onSuccess(String url) {
                        RetrofitManager.getInstance().createReq(UserInfo.class)
                                .editHead(UserInfoManager.getInstance().getUid(), path)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<HttpResult<User>>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(HttpResult<User> userHttpResult) {
                                        Toast.makeText(getContext(), userHttpResult.getMsg(), Toast.LENGTH_SHORT).show();
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
                    public void onFail(int code, String msg) {
                        Toast.makeText(getContext(),"上传失败",Toast.LENGTH_SHORT).show();
                    }
                };
                CosManager.getInstance().uploadFile(path,uid,progressListener,resultListener);

            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    /**调用系统的裁剪图片功能
     *
     * @param
     */
    public void startPhotoZoom(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 64);
        intent.putExtra("outputY", 64);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, PHOTO_RESOULT);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param
     */
    private void setPicToView(Intent data) {
        imageUri = data.getData();
        path = imageUri.getPath();
//        Bundle extras = data.getExtras();
//        if (extras != null) {
//            Bitmap photo = extras.getParcelable("data");
//            //图片路径
//            path = FileUtilcll.saveFile(getContext(),ImageName, photo);
//            System.out.println("----------路径----------" + path);
//            ivHead.setImageBitmap(photo);
//        }

    }

    private String pathFromUri(Uri imageUri) {
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(imageUri, filePathColumn,
                null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        path = cursor.getString(columnIndex);
        return path ;
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
                            tvClass.setText(userHttpResult.getData().getClassName());
                            tvPhone.setText(userHttpResult.getData().getPhone());
                            tvClass.setText(userHttpResult.getData().getClassName());
                            double number = userHttpResult.getData().getNumber();
                            tvNumber.setText(String.valueOf(number));
                            int sex = userHttpResult.getData().getSex();
                            if (sex == 1) {
                                ivSex.setImageResource(R.drawable.icon_girl);
                            } else {
                                ivSex.setImageResource(R.drawable.icon_man);
                            }
                            int stuType = userHttpResult.getData().getStuType();
                            if (stuType == 0) {
                                tvStuType.setText("普通学生");
                                conClass.setVisibility(View.VISIBLE);
                                conNumber.setVisibility(View.VISIBLE);
                            } else if (stuType == 1) {
                                tvStuType.setText("班委");
                                conClass.setVisibility(View.VISIBLE);
                                conNumber.setVisibility(View.VISIBLE);

                            } else if (stuType == 2) {
                                tvStuType.setText("导员");
                                conClass.setVisibility(View.GONE);
                                conNumber.setVisibility(View.GONE);
                            }
                            String head = userHttpResult.getData().getHead();
                            Glide.with(getContext()).load(head).into(ivHead);
                        } else {
                            Toast.makeText(getContext(), "获取失败", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getContext(), "网络异常", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

}
