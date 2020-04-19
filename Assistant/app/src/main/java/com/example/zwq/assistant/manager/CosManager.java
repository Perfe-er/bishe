package com.example.zwq.assistant.manager;

import android.content.Context;

import com.hapi.ut.AppCache;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.transfer.COSXMLDownloadTask;
import com.tencent.cos.xml.transfer.COSXMLUploadTask;
import com.tencent.cos.xml.transfer.TransferConfig;
import com.tencent.cos.xml.transfer.TransferManager;
import com.tencent.qcloud.core.auth.QCloudCredentialProvider;
import com.tencent.qcloud.core.auth.ShortTimeCredentialProvider;

/**
 * 上传文档　用于奖学金文档等
 * secretId 是我的秘钥　可泄露不好
 */
public class CosManager {


    String bucket = "13758648443-1301628623"; //存储桶，格式：BucketName-APPID
    private String cosPathSplit = "assistant"; //对象在存储桶中的位置标识符，即称对象键
    private String region = "ap-shanghai";
    // 创建 CosXmlServiceConfig 对象，根据需要修改默认的配置参数
    private CosXmlServiceConfig serviceConfig = new CosXmlServiceConfig.Builder()
            .setRegion(region)
            .isHttps(true) // 使用 HTTPS 请求, 默认为 HTTP 请求
            .builder();


    private QCloudCredentialProvider credentialProvider;//
    private CosXmlService cosXmlService;

    private static class Holder {
        private static CosManager CosManager = new CosManager();
    }

    private CosManager() {
    }

    public void init(String secretId, String secretKey) {
        credentialProvider = new ShortTimeCredentialProvider(secretId, secretKey, 300);
        cosXmlService = new CosXmlService(AppCache.getContext(), serviceConfig, credentialProvider);
    }

    public static CosManager getInstance() {
        return Holder.CosManager;
    }


    /**
     *
     */
    public static interface ICosXmlResultListener {
        void onSuccess(String url);

        void onFail(int code, String msg);
    }

    public void uploadFile(String srcPath, String uid, CosXmlProgressListener progressListener, ICosXmlResultListener resultListener) {
        // 初始化 TransferConfig
        TransferConfig transferConfig = new TransferConfig.Builder().build();

        /*若有特殊要求，则可以如下进行初始化定制。例如限定当对象 >= 2M 时，启用分块上传，且分块上传的分块大小为1M，当源对象大于5M时启用分块复制，且分块复制的大小为5M。*/
        transferConfig = new TransferConfig.Builder()
                .setDividsionForCopy(5 * 1024 * 1024) // 是否启用分块复制的最小对象大小
                .setSliceSizeForCopy(5 * 1024 * 1024) // 分块复制时的分块大小
                .setDivisionForUpload(2 * 1024 * 1024) // 是否启用分块上传的最小对象大小
                .setSliceSizeForUpload(1024 * 1024) // 分块上传时的分块大小
                .build();

// 初始化 TransferManager
        TransferManager transferManager = new TransferManager(cosXmlService, transferConfig);

        String uploadId = null; //若存在初始化分块上传的 UploadId，则赋值对应的 uploadId 值用于续传；否则，赋值 null
// 上传对象
        String key = uid + String.valueOf(System.currentTimeMillis()) + cosPathSplit;
        COSXMLUploadTask cosxmlUploadTask = transferManager.upload(bucket, key, srcPath, uploadId);
        cosxmlUploadTask.setCosXmlProgressListener(progressListener);

        //设置返回结果回调
        cosxmlUploadTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                COSXMLUploadTask.COSXMLUploadTaskResult cOSXMLUploadTaskResult = (COSXMLUploadTask.COSXMLUploadTaskResult) result;
                String url = key + result.accessUrl;
                resultListener.onSuccess(url);
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                resultListener.onFail(exception.errorCode, exception.getMessage());
            }
        });
        cosxmlUploadTask.resume();
    }


    public void downLoad(String url, CosXmlProgressListener progressListener, CosXmlResultListener resultListener) {
        Context applicationContext = AppCache.getContext(); // application context
        String savePathDir = applicationContext.getExternalCacheDir().toString(); //本地目录路径
        String savedFileName = "exampleobject";//本地保存的文件名，若不填（null），则与 COS 上的文件名一样
//下载对象
        TransferConfig transferConfig = new TransferConfig.Builder().build();
//初始化 TransferManager
        TransferManager transferManager = new TransferManager(cosXmlService, transferConfig);
        String key = url.split(cosPathSplit)[0] + cosPathSplit;

        COSXMLDownloadTask cosxmlDownloadTask = transferManager.download(applicationContext, bucket, key, savePathDir, savedFileName);
//设置下载进度回调
        cosxmlDownloadTask.setCosXmlProgressListener(progressListener);
//设置返回结果回调
        cosxmlDownloadTask.setCosXmlResultListener(resultListener);
//恢复下载
        cosxmlDownloadTask.resume();
    }

}
