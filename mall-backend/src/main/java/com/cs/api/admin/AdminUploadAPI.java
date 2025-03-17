package com.cs.api.admin;

import com.cs.common.Constants;
import com.cs.config.annotation.TokenToAdminUser;
import com.cs.pojo.AdminUserToken;
import com.cs.util.MallUtils;
import com.cs.util.Result;
import com.cs.util.ResultGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@RestController
@Api(value = "v1", tags = "后台管理系统文件上传系统")
public class AdminUploadAPI {

    private static final Logger logger = LoggerFactory.getLogger(AdminUploadAPI.class);

    @RequestMapping(value = "/upload/file", method = RequestMethod.POST)
    @ApiOperation(value = "单图上传", notes = "file name \"file\"")
    public Result uploadFile(HttpServletRequest request, @RequestParam("file") MultipartFile file, @TokenToAdminUser AdminUserToken adminUser) throws URISyntaxException{
        logger.info("adminUSer:{}", adminUser);
        String fileName = file.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        // 生成文件名称
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Random r = new Random();
        StringBuilder tempName = new StringBuilder();
        tempName.append(sdf.format(new Date()))
                .append(r.nextInt(100))
                .append(suffixName);
        String newFileName = tempName.toString();
        File fileDirectory = new File(Constants.FILE_UPLOAD_DIC);
        File destFile = new File(Constants.FILE_UPLOAD_DIC + newFileName);
        try{
            if (!fileDirectory.exists()){
                if (!fileDirectory.mkdir()){
                    throw new IOException("文件夹创建失败， 路径为：" + fileDirectory);
                }
            }
            file.transferTo(destFile);
            Result successResult = ResultGenerator.genSuccessResult();
            System.out.println(request.getRequestURI()+"");
            System.out.println(new URI(request.getRequestURI()+""));
            successResult.setData(MallUtils.getHost(new URI(request.getRequestURL()+"")) + "/upload/" + newFileName);
            return successResult;
        } catch (IOException e){
            e.printStackTrace();
            return ResultGenerator.genFailResult("文件上传失败");
        }
    }
}
