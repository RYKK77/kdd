package com.rykk.kdd.manager;


import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.rykk.kdd.config.OssClientConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Resource;

import com.rykk.kdd.constant.FileConstant;
import com.rykk.kdd.model.entity.App;
import org.springframework.stereotype.Component;

/**
 * Cos 对象存储操作
 */
@Component
public class OssManager {


    @Resource
    private OSS ossClient;

    /**
     * 上传对象
     *
     * @param key  唯一键
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

    /**
     * 将URL的图片上传到OSS中
     *
     * @param app 应用信息
     * @param URL URL地址
     * @return 返回远程地址结果
     */
    public String putObjectFromURL(App app, String URL) {
        try {
            // 打开URL连接并获取输入流
            URL url = new URL(URL);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpConn.getInputStream();

            // 生成基于APPID和日期的文件名
            Long id = app.getId();
            String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".png";
            String objectName = "kdd/app_icon/" + id + "-" + fileName;

            // 上传文件到指定的OSS目录中
            PutObjectResult putObjectResult = ossClient.putObject("kdd-rykk", objectName, inputStream);

            // 关闭输入流
            inputStream.close();
            // 获取文件访问的URL
            String realUrl = FileConstant.OSS_HOST + '/' + objectName;

            return realUrl;
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
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return null;
    }
}
