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
//        List<StationTimeSeriesDTO> seriesDTOS = stationTimeSeriesDAO.getValueOfStationTimeSeries(seriesDTOs);
        if (seriesDTOs.size() <= 0)
            return DefaultResponseDTO.builder().status(-3).message("Không tìm thấy giá trị phù hợp").build();

        String fileContent = createFileContent(station, seriesDTOs);
        File file = writeFile(fileContent, station);
        if (file.exists()) {
            boolean resultSendFile = sendFile(file);
            if (!resultSendFile) return DefaultResponseDTO.builder().status(-4).message("Không connect được server").build();
            file.delete();
            if (resultSendFile) return DefaultResponseDTO.builder().status(1).message("Thành công").build();
        }
        return DefaultResponseDTO.builder().status(-1).message("failed").build();
    }

    private String createFileContent(Station station, List<StationTimeSeriesDTO> seriesDTOS) {

        StringBuilder s = new StringBuilder();
        s.append("##station: " + seriesDTOS.get(0).getStationCode() + " \n");
        s.append("#ZRXPVERSION3014.03|*|ZRXPCREATORKiIOSystem.Manual|*| \n");
        s.append("#REXCHANGE" + station.getStationCode() + "|*|CUNITmm|*|RIVAL-777|*| \n");
        s.append("#LAYOUT(timestamp,value,primary_status)|*| \n\"");
        seriesDTOS.forEach(seriesDTO1 -> s.append(seriesDTO1.getTimeStamp() + " " + seriesDTO1.getValue() + " 0 \n"));
        return s.toString();
    }

    private File writeFile(String contentFile, Station station) throws IOException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        String SYSTEM_PATH = System.getProperty("user.dir");
        String fileName = station.getStationCode() + "_" + sf.format(new Date());
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
