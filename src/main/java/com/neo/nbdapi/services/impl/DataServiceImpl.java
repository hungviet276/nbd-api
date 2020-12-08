package com.neo.nbdapi.services.impl;

import com.neo.nbdapi.dao.StationDAO;
import com.neo.nbdapi.dao.StationTimeSeriesDAO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.dto.StationTimeSeriesDTO;
import com.neo.nbdapi.entity.Station;
import com.neo.nbdapi.entity.StationTimeSeries;
import com.neo.nbdapi.services.DataService;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;


@Service
public class DataServiceImpl implements DataService {
    private Logger logger = LogManager.getLogger(DataServiceImpl.class);

    @Autowired
    private StationTimeSeriesDAO stationTimeSeriesDAO;

    @Autowired
    private StationDAO stationDAO;

    @Autowired
    @Qualifier("configCdh")
    private PropertiesConfiguration cdhConfig;

    @Override
    public List<StationTimeSeries> findByStationId(String stationId) {
        return stationTimeSeriesDAO.findByStationId(stationId);
    }

    @Override
    public List<StationTimeSeriesDTO> getValueOfStationTimeSeries(StationTimeSeriesDTO seriesDTO) {
        return stationTimeSeriesDAO.getValueOfStationTimeSeries(seriesDTO);
    }

    @Override
    public DefaultResponseDTO sendDataToCDH(String stationId, List<StationTimeSeriesDTO> seriesDTOs) throws IOException {
        Station station = stationDAO.getStationById(stationId);
        if (station == null)
            return DefaultResponseDTO.builder().status(-2).message("Không tìm thấy trạm").build();
        if (seriesDTOs.size() <= 0)
            return DefaultResponseDTO.builder().status(-3).message("Không tìm thấy giá trị phù hợp").build();

        String fileContent1 = createFileContent(station, seriesDTOs, 1);
        String fileContent2 = createFileContent(station, seriesDTOs, 2);
        String fileContent3 = createFileContent(station, seriesDTOs, 3);
        File file1 = writeFile(fileContent1, station, "type1");
        File file2 = writeFile(fileContent2, station, "type2");
        File file3 = writeFile(fileContent3, station, "type3");
        if (file1.exists()) {
            boolean resultSendFile = sendFile(file1);
            boolean resultSendFile2 = sendFile(file2);
            boolean resultSendFile3 = sendFile(file3);
            logger.info("resultSendFile " + resultSendFile);
            logger.info("resultSendFile2 " + resultSendFile2);
            logger.info("resultSendFile3 " + resultSendFile3);
            if (!resultSendFile)
                return DefaultResponseDTO.builder().status(-4).message("Không connect được server").build();
            file1.delete();
            if (resultSendFile) return DefaultResponseDTO.builder().status(1).message("Thành công").build();
        }
        return DefaultResponseDTO.builder().status(-1).message("failed").build();
    }

    private String createFileContent(Station station, List<StationTimeSeriesDTO> seriesDTOS, int type) {
        StringBuilder content = new StringBuilder();
        switch (type) {
            case 1:
                content.append("#" + station.getStationCode() + ".Manual|*|\n");
                content.append("#REXCHANGE0001");
                content.append("#|*|TZUTC+7|*|CUNITmm\n");
                content.append("#LAYOUT(timestamp, value, primary_status)|*|\n");
                break;
            case 2:
                content.append("#" + station.getStationCode() + ".Manual|*|\n");
                content.append("#TZAsia/Ho_Chi_Minh|*|");
                content.append("#REXCHANGE0001" + station.getStationCode() + "|*|TZUTC+7|*|CUNITmm \n");
                content.append("#LAYOUT(timestamp, value, primary_status, remark)|*|\n");
                break;
        }

        for (int i = 0; i < seriesDTOS.size(); i++) {
            StationTimeSeriesDTO seriesDTO = seriesDTOS.get(i);
            String timeStamps = seriesDTO.getTimeStamp()
                    .replace("/", "")
                    .replace(" ", "")
                    .replace(":", "");
            switch (type) {
                case 1:
                    content.append(timeStamps + " " + seriesDTO.getValue() + " 0 \n");
                    break;
                case 2:
                    if (i % 2 == 0)
                        content.append(timeStamps + " " + seriesDTO.getValue() + " 0 \"weather::" + "good\n");
                    if (i % 2 == 1)
                        content.append(timeStamps + " " + seriesDTO.getValue() + " 0 \"weather::" + "bad\n");

                    break;
                case 3:
                    content.append("#REXCHANGE" + station.getStationCode() + "." + i + "|*| RINVAL-777|*|\n");
                    content.append("#LAYOUT(timestamp, value)|*|\n");
                    content.append(timeStamps + " " + seriesDTO.getValue() + " 0 \n");
                    content.append("\n");
            }
        }
        return content.toString();
    }

    private File writeFile(String contentFile, Station station, String type) throws IOException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        String SYSTEM_PATH = System.getProperty("user.dir");
        String fileName = station.getStationCode() + "_" + sf.format(new Date()) + "_" + type;
        String FILE_PATH = SYSTEM_PATH + "/src/main/resources/mail/" + fileName + ".ZRXP";
        FileWriter myWriter = new FileWriter(FILE_PATH);
        myWriter.write(contentFile);
        myWriter.close();
        return new File(FILE_PATH);
    }

    private boolean sendFile(File file) {
        FTPClient client = new FTPClient();
        FileInputStream fis = null;
        boolean completed = false;
        try {
            String serverIp = cdhConfig.getString("ip");
            String username = cdhConfig.getString("username");
            String password = cdhConfig.getString("password");
            client.connect(serverIp);
            client.login(username, password);
            boolean isConnect = client.isConnected();
            if (!isConnect) return false;
            fis = new FileInputStream(file);
            completed = client.storeFile(file.getName(), fis);
            System.out.println("The  file is uploaded : " + completed);
            client.logout();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                client.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return completed;
    }

    private String readLineByLineJava8(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("contentBuilder.toString() : " + contentBuilder.toString());
        return contentBuilder.toString();
    }

}
