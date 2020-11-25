package com.neo.nbdapi.rest;

import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.utils.Constants;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.*;

@Controller
@RequestMapping(Constants.APPLICATION_API.API_PREFIX )
public class DownLoadFIleWaterLevelController {

    @Value("${water.level.file.out}")
    private String pathDirectory;

    @GetMapping("/download/water-level")
    public ResponseEntity<InputStreamResource> download2(HttpServletRequest request) throws IOException, BusinessException {
        HttpHeaders responseHeader = new HttpHeaders();
        String filename = request.getParameter("filename");
        try {
            File file =  new File(pathDirectory+filename);
            byte[] data = FileUtils.readFileToByteArray(file);
            // Set mimeType trả về
            responseHeader.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            // Thiết lập thông tin trả về
            responseHeader.set("Content-disposition", "attachment; filename=" + file.getName());
            responseHeader.setContentLength(data.length);
            InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(data));
            InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
            return new ResponseEntity<InputStreamResource>(inputStreamResource, responseHeader, HttpStatus.OK);
        } catch (Exception ex) {
            throw new BusinessException("File dữ liệu không tồn tại");
        }
    }


}
