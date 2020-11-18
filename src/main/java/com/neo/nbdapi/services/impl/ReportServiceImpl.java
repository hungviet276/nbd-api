package com.neo.nbdapi.services.impl;

import com.neo.nbdapi.dao.ParameterChartMappingDAO;
import com.neo.nbdapi.dao.StationDAO;
import com.neo.nbdapi.dao.StationTimeSeriesDAO;
import com.neo.nbdapi.dto.ParameterChartMappingAndDataDTO;
import com.neo.nbdapi.dto.StationDataReportDTO;
import com.neo.nbdapi.dto.TimeSeriesDataDTO;
import com.neo.nbdapi.entity.*;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.GetParameterChartMappingAndDataVM;
import com.neo.nbdapi.rest.vm.GetStationDataReportVM;
import com.neo.nbdapi.services.ReportService;
import com.neo.nbdapi.utils.Constants;
import com.neo.nbdapi.utils.DateUtils;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author thanglv on 11/14/2020
 * @project NBD
 */

@Service
public class ReportServiceImpl implements ReportService {
    private Logger logger = LogManager.getLogger(ReportServiceImpl.class);

    @Autowired
    private StationDAO stationDAO;

    @Autowired
    private StationTimeSeriesDAO stationTimeSeriesDAO;

    @Autowired
    private ParameterChartMappingDAO parameterChartMappingDAO;

    @Autowired
    @Qualifier("configSyncResource")
    private PropertiesConfiguration config;

    @Override
    public ParameterChartMappingAndDataDTO getParameterChartMappingAndData(GetParameterChartMappingAndDataVM request) throws SQLException, BusinessException {

        // validate request
        validateRequest(request);

        // check xem yếu tố này có phải của trạm không và user có được cấp quyền cho trạm hay không
        // step 1: check trạm có tồn tại hay không
        Station station = stationDAO.findStationByStationCodeAndActiveAndIsdel(request.getStationCode());
        if (station == null || station.getIsDel() == Constants.STATION.IS_DEL_TRUE)
            throw new BusinessException("Trạm không tồn tại trong hệ thống");

        // step 2: check tram co thuoc quyen so huu cua user hay khong
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!stationDAO.isStationOwnedByUser(station.getStationId(), userId))
            throw new BusinessException("User không có quyền điều khiển trạm");

        // step 3: check xem yeu to co phai cua station hay khong
        StationTimeSeries stationTimeSeries = stationTimeSeriesDAO.findByStationIdAndParameterTypeId(station.getStationId(), Long.parseLong(request.getParameterTypeId()));
        if (stationTimeSeries == null)
            throw new BusinessException("Yếu tố không thuộc trạm");

        // step 4: lay ra template dir, loai bieu do cua yeu to dua vao bang parameter_chart_mapping
        ParameterChartMapping parameterChartMapping = parameterChartMappingDAO.getParameterChartMappingByParameterTypeId(Integer.parseInt(request.getParameterTypeId()));
        logger.debug("parameterChartMapping: {}", parameterChartMapping);
        // lấy ra bảng lưu trữ yếu tố
        String storage = stationTimeSeries.getStorage();
        logger.debug("storage: {}", storage);
        // lay du lieu tu bang tong hop ( = storage + type vi du: water_level_1H, salinity...)
        List<ObjectValue> objectValues = null;
        if (storage != null)
            objectValues = stationTimeSeriesDAO.getStorageData(storage, request.getType(), request.getStartDate(), request.getEndDate());
        logger.debug("objectValues: {}", objectValues);
        return ParameterChartMappingAndDataDTO.builder()
                .chartMapping(parameterChartMapping)
                .data(objectValues)
                .build();
    }

    @Override
    public List<TimeSeriesDataDTO> getStationDataReport(GetStationDataReportVM request) throws BusinessException, SQLException {
        // check xem yếu tố này có phải của trạm không và user có được cấp quyền cho trạm hay không
        // step 1: check trạm có tồn tại hay không
        Station station = stationDAO.findStationByStationCodeAndActiveAndIsdel(request.getStationCode());
        if (station == null || station.getIsDel() == Constants.STATION.IS_DEL_TRUE)
            throw new BusinessException("Trạm không tồn tại trong hệ thống");

        // step 2: check tram co thuoc quyen so huu cua user hay khong
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!stationDAO.isStationOwnedByUser(station.getStationId(), userId))
            throw new BusinessException("User không có quyền điều khiển trạm");

        // step 3, lay toan bo du lieu report cua cac yeu to cua tram
        String listParameterTypeId = config.getString(station.getStationCode());
        // get time 7 ngay ve truoc de lay du lieu
        Calendar calendar = Calendar.getInstance();
        String endDate = DateUtils.getStringFromDateFormat(calendar.getTime(), "dd/MM/yyyy HH:mm");
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        String startDate = DateUtils.getStringFromDateFormat(calendar.getTime(), "dd/MM/yyyy HH:mm");

        //danh sách dữ liệu các yếu tố cần hiển thị ở biểu đồ
        List<TimeSeriesDataDTO> dataChart = new ArrayList<>();
        if (listParameterTypeId != null) {
            List<StationTimeSeries> stationTimeSeriesList = stationTimeSeriesDAO.findByStationIdAndListParameterTypeId(station.getStationId(), listParameterTypeId);
            for (StationTimeSeries timeSeries : stationTimeSeriesList) {
                // timeSeries chính là thông tin của yếu tố
                ParameterChartMapping parameterChartMapping = parameterChartMappingDAO.getParameterChartMappingByParameterTypeId(timeSeries.getParameterTypeId());
                TimeSeriesDataDTO timeSeriesDataDTO = TimeSeriesDataDTO.builder()
                        .parameterChartMapping(parameterChartMapping)
                        .stationTimeSeries(timeSeries)
                        .data(stationTimeSeriesDAO.getStorageData(timeSeries.getStorage(), null, startDate, endDate)) // dữ liệu 7 ngày
                        .build();
                dataChart.add(timeSeriesDataDTO);
            }
        }
        return dataChart;
    }

    private void validateRequest(GetParameterChartMappingAndDataVM request) throws BusinessException {
            String requestStartDate = request.getStartDate();
            String requestEndDate = request.getEndDate();
            Date startDate = DateUtils.getDateFromStringFormat(requestStartDate, "dd/MM/yyyy HH:mm");
            if (requestEndDate != null) {
                Date endDate = DateUtils.getDateFromStringFormat(requestEndDate, "dd/MM/yyyy HH:mm");
                if (startDate.after(endDate))
                    throw new BusinessException("Ngày bắt đầu không được vượt quá ngày kết thúc");
            }
    }
}
