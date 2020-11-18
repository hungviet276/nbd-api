package com.neo.nbdapi.services.impl;

import com.neo.nbdapi.dao.ParameterChartMappingDAO;
import com.neo.nbdapi.entity.ParameterChartMapping;
import com.neo.nbdapi.services.ParameterChartMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

/**
 * @author thanglv on 11/14/2020
 * @project NBD
 */
@Service
public class ParameterChartMappingServiceImpl implements ParameterChartMappingService {

    @Autowired
    private ParameterChartMappingDAO parameterChartMappingDAO;

    public ParameterChartMapping getParameterChartMappingByParameterTypeId(Integer parameterTypeId) throws SQLException {
        return parameterChartMappingDAO.getParameterChartMappingByParameterTypeId(parameterTypeId);
    }
}
