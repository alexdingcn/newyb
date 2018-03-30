package com.yiban.erp;

import com.alibaba.fastjson.JSON;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import com.yiban.erp.util.UtilTool;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Date;
import java.util.List;

public class OSSClientTest {

    private String endPoint="https://oss-cn-shanghai.aliyuncs.com";
    private String imageEndPoint = "https://img-cn-shanghai.aliyuncs.com";
    private String accessKeyId="LTAIULFYwI6R0YNJ";
    private String accessKeySecret="MjpDvT0AUSG7JuvoW1H7YqgAP3TitO";

    private String bucket = "yibanerp";
    private String keyName = "1/test/test_20180315bw6feP1qahR72UjxXxF9.png";

    private OSSClient getOSSClient() {
        return new OSSClient(endPoint, accessKeyId, accessKeySecret);
    }


    @Test
    public void getListBucket() {
        List<Bucket> buckets = getOSSClient().listBuckets();
        for (Bucket bucket : buckets) {
            System.out.println(JSON.toJSONString(bucket));
        }
        getOSSClient().shutdown();
    }

    @Test
    public void createBucket() {
        CreateBucketRequest createBucketRequest = new CreateBucketRequest("yibanerp");
        createBucketRequest.setCannedACL(CannedAccessControlList.Private); //私有类型
        createBucketRequest.setStorageClass(StorageClass.Standard);
        getOSSClient().createBucket(createBucketRequest);
        getListBucket();
    }

    @Test
    public void getBucketACL() {
        AccessControlList acls = getOSSClient().getBucketAcl("yibanerp");
        System.out.println(acls.toString());
        getOSSClient().shutdown();
    }

    @Test
    public void getBucketInfo() {
        OSSClient client = getOSSClient();
        BucketInfo bucketInfo = client.getBucketInfo("yibanerp");
        System.out.println(JSON.toJSONString(bucketInfo));
        client.shutdown();
    }

    @Test
    public void uploadFile() {
        OSSClient client = getOSSClient();
        StringBuilder key = new StringBuilder();
        key.append("1/test/test_");
        key.append(UtilTool.dateFormat(new Date(), "yyyyMMdd"));
        key.append(RandomStringUtils.randomAlphanumeric(20));
        key.append(".png");
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.addUserMetadata("user-id", "1");

        PutObjectResult result = client.putObject("yibanerp", "1/test/test_20180315docx.docx",
                new File("D:\\home\\yibanerp\\upload\\1\\201803\\2.docx"),
                metadata);
        System.out.println(JSON.toJSONString(result));
        PutObjectResult result1 = client.putObject("yibanerp", "1/test/test_20180315pfd.pdf",
                new File("D:\\home\\yibanerp\\upload\\1\\201803\\2.pdf"),
                metadata);
        System.out.println(JSON.toJSONString(result1));
        PutObjectResult result2 = client.putObject("yibanerp", "1/test/test_20180315xlsx.xlsx",
                new File("D:\\home\\yibanerp\\upload\\1\\201803\\11.xlsx"),
                metadata);
        System.out.println(JSON.toJSONString(result2));

        client.shutdown();
    }

    @Test
    public void listObject() {
        OSSClient client = getOSSClient();
        ObjectListing listing = client.listObjects(bucket);

        List<OSSObjectSummary> summaries = listing.getObjectSummaries();
        for (OSSObjectSummary summary : summaries) {
            System.out.println(summary.getKey());
        }

        client.shutdown();
    }

    @Test
    public void setObjectACL() {
        OSSClient client = getOSSClient();
        client.setObjectAcl(bucket, "1/test/test_20180315docx.docx", CannedAccessControlList.Private);
        client.setObjectAcl(bucket, "1/test/test_20180315pfd.pdf", CannedAccessControlList.Private);
        client.setObjectAcl(bucket, "1/test/test_20180315xlsx.xlsx", CannedAccessControlList.Private);
        client.setObjectAcl(bucket, keyName, CannedAccessControlList.Private);
        client.shutdown();
    }

    @Test
    public void urlSignedTest() throws UnsupportedEncodingException {
        OSSClient client = getOSSClient();
        Date expiration = new Date(new Date().getTime() + 60 * 60 * 1000);
        String imageProcess = "image/resize,w_100,h_100";
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, keyName, HttpMethod.GET);
        request.setExpiration(expiration);
        request.setProcess(imageProcess);
        URL url = client.generatePresignedUrl(request);
        System.out.println(url.toString());
    }

    @Test
    public void deleteObject() {
        OSSClient client = getOSSClient();
        client.deleteObject(bucket, "1/test/test_20180315docx.docx");
        client.deleteObject(bucket, "1/test/test_20180315pfd.pdf");
        listObject();
    }

}
