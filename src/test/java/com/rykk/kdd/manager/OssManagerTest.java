package com.rykk.kdd.manager;

import javax.annotation.Resource;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Cos 操作测试
 *
 */
@SpringBootTest
class OssManagerTest {

    @Resource
    private OssManager ossManager;

    @Resource
    private OSS ossClient;

    @Test
    void putObject() {
        try {
            String content = "Hello OSS";
            PutObjectResult putObjectResult = ossClient.putObject("kdd-rykk", "kdd/test.txt", new ByteArrayInputStream(content.getBytes()));
            String eTag = putObjectResult.getETag();
            String versionId = putObjectResult.getVersionId();
            InputStream callbackResponseBody = putObjectResult.getCallbackResponseBody();
            String requestId = putObjectResult.getRequestId();
            Long clientCRC = putObjectResult.getClientCRC();
            Long serverCRC = putObjectResult.getServerCRC();
            ResponseMessage response = putObjectResult.getResponse();
            System.out.println("eTag:" + eTag);
            System.out.println("versionId:" + versionId);
            System.out.println("callbackResponseBody:" + callbackResponseBody);
            System.out.println("requestId:" + requestId);
            System.out.println("clientCRC:" + clientCRC);
            System.out.println("serverCRC:" + serverCRC);
            System.out.println("response:" + response);

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
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }


    @Test
    void uploadFile() {
        try {
            // 创建PutObjectRequest对象。
            PutObjectRequest putObjectRequest = new PutObjectRequest("kdd-rykk", "kdd/README.md", new File("D:\\myProject\\kdd\\README.md"));
            // 如果需要上传时设置存储类型和访问权限，请参考以下示例代码。
            // ObjectMetadata metadata = new ObjectMetadata();
            // metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
            // metadata.setObjectAcl(CannedAccessControlList.Private);
            // putObjectRequest.setMetadata(metadata);

            // 上传文件。
            PutObjectResult result = ossClient.putObject(putObjectRequest);
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
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }


    @Test
    void uploadFile1() {
        try {
            // 创建PutObjectRequest对象。
//            PutObjectRequest putObjectRequest = new PutObjectRequest("kdd-rykk", "kdd/README.md", new File("D:\\myProject\\kdd\\README.md"));
            // 打开URL连接并获取输入流
            URL url = new URL("https://sfile.chatglm.cn/testpath/3573eaeb-b39c-5684-a5e9-abe60adbfb0b_0.png");
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpConn.getInputStream();

            // 生成基于日期的文件名
            String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".png";

            // 上传文件到指定的OSS目录中
            String objectName = "kdd/" + fileName;
            PutObjectResult putObjectResult = ossClient.putObject("kdd-rykk", objectName, inputStream);

            System.out.println("File uploaded to OSS: " + objectName);

            // 关闭输入流
            inputStream.close();

            // 上传文件。
//            PutObjectResult result = ossClient.putObject(putObjectRequest);
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
    }

    @Test
    void deleteFile() {

        String filepath = String.format("/%s/%s/%s", "fileUploadBizEnum.getValue()", "loginUser.getId()", "filename");
        System.out.println(filepath);
    }
}