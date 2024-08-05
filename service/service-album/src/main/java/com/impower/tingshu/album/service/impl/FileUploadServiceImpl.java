package com.impower.tingshu.album.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import com.impower.tingshu.album.config.MinioConstantProperties;
import com.impower.tingshu.album.service.FileUploadService;
import com.impower.tingshu.common.execption.GuiguException;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;


/**
 * @classname tingshu-parent
 * @Auther d3Lap1ace
 * @Time 2/8/2024 20:55 周五
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Resource
    private MinioClient minioClient;
    @Autowired
    private MinioConstantProperties minioConstantProperties;
    @Override
    public String fileUpload(MultipartFile multipartFile) {
        //1.业务校验验证图片内容格式是否合法
        try {
            BufferedImage bufferedImage = ImageIO.read(multipartFile.getInputStream());
            if (bufferedImage == null) {
                throw new GuiguException(400,"The file format is incorrect ");
            }
            //2.业务校验-验证图片大小合法
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();
            if(width > 9000 || height > 9000){
                throw new GuiguException(400,"the file size if incorrent ");
            }
            // TODO 校验图片是否合法

        } catch (IOException e) {
            e.printStackTrace();
        }
        //3.将文件上传到MInIO
        //3.1 生成带有文件夹目录文件名称 形式：/日期/随机文件名称.后缀
        String folder = "/"+ DateUtil.today();
        String fileName = IdUtil.randomUUID();
        String extName = FileNameUtil.extName(multipartFile.getOriginalFilename());
        String objName = folder + "/" + fileName + "." + extName;
        //3.2 调用minio客户对象上传文件方法
        String bucketName = minioConstantProperties.getBucketName();
        try {
            minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objName).stream(
                    multipartFile.getInputStream(),
                    multipartFile.getSize(),-1)
                    .contentType(multipartFile.getContentType())
                    .build());
            //3.3 拼接上传文件在线路径地址
            return minioConstantProperties.getEndpointUrl()+"/"+bucketName+"/"+objName;
        } catch (Exception e) {
            throw new GuiguException(500, "文件上传失败");
        }

    }
}
