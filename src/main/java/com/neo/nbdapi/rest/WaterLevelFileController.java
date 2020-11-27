package com.neo.nbdapi.rest;

import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.services.WaterLevelService;
import com.neo.nbdapi.utils.Constants;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;

@Controller
@RequestMapping(Constants.APPLICATION_API.API_PREFIX )
public class WaterLevelFileController {

    @Value("${water.level.file.out}")
    private String pathDirectory;

    @Autowired
    private WaterLevelService waterLevelService;

    @GetMapping("/download/water-level")
    public ResponseEntity<InputStreamResource> downloadFileHG(HttpServletRequest request) throws IOException, BusinessException {
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

    @GetMapping("/download/template")
    public ResponseEntity<InputStreamResource> downloadTemplate(HttpServletRequest request) throws IOException, BusinessException {
        return waterLevelService.downloadTemplate(request);
    }

    @PostMapping("/upload-file-execute")
    @ResponseBody
    public DefaultResponseDTO uploadOneFileHandlerPOST(@RequestParam("stationId") String stationId , @RequestParam("end") Integer end, @RequestParam("start") Integer start,
                                                       @RequestParam(value = "file") MultipartFile file, @RequestParam String type) throws IOException {
        return waterLevelService.executeGuess(stationId, end, start, file, type);
    }

}
