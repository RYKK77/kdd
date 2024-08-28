package com.rykk.kdd.manager;


import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.rykk.kdd.config.OssClientConfig;
import java.io.File;
import javax.annotation.Resource;

import com.rykk.kdd.constant.FileConstant;
import org.springframework.stereotype.Component;

/**
 * Cos 对象存储操作
 *
 */
@Component
public class OssManager {

    @Resource
    private OssClientConfig cosClientConfig;

    @Resource
    private OSS ossClient;

    /**
     * 上传对象
     *
     * @param key 唯一键
     * @param file 文件
     * @return
     */
    public PutObjectResult putObject(String key, File file) {
        try {
            // 创建PutObjectRequest对象。
            PutObjectRequest putObjectRequest = new PutObjectRequest(FileConstant.BUCKET_NAME, key, file);
            // 上传文件。
            PutObjectResult result = ossClient.putObject(putObjectRequest);
            return result;
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        }
        return null;
    }
}
