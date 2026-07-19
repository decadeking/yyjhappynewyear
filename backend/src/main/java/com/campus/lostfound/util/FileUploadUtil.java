package com.campus.lostfound.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;

@Component
public class FileUploadUtil {

    @Value("${file.upload-path}")
    private String uploadPath;

    public String upload(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        String dateDir = DateUtil.format(new Date(), "yyyyMMdd");
        String dir = uploadPath + dateDir + "/";
        FileUtil.mkdir(dir);

        String originalName = file.getOriginalFilename();
        String ext = originalName != null ? originalName.substring(originalName.lastIndexOf(".")) : ".jpg";
        String fileName = IdUtil.fastSimpleUUID() + ext;

        File dest = new File(dir + fileName);
        file.transferTo(dest);

        return "/uploads/" + dateDir + "/" + fileName;
    }
}
