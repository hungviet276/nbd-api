package com.neo.nbdapi.dao.impl;

import com.neo.nbdapi.dao.TidalHarmonicConstantsDAO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.entity.TidalHarmonicConstants;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class TidalHarmonicConstantsDAOImpl implements TidalHarmonicConstantsDAO {

    private Logger logger = LogManager.getLogger(UserInfoDAOImpl.class);

    @Autowired
    private HikariDataSource ds;
    @Override
    public DefaultResponseDTO insertTidalHarmonicConstantsDAOs(List<TidalHarmonicConstants> tidalHarmonicConstantes) throws SQLException {
        String sql = "insert into tidal_harmonic_constants (WAVE_NAME, AMPLITUDE, PHASE, CREATE_DATE, STATION_ID) values (?,?,?,sysdate, ?)";
        logger.info("TidalHarmonicConstantsDAOImpl sql : {}", sql);

        Connection connection = null;
        PreparedStatement stm = null;
        try{
            connection = ds.getConnection();
            connection.setAutoCommit(false);
            stm = connection.prepareStatement(sql);
            for(TidalHarmonicConstants tidalHarmonicConstants : tidalHarmonicConstantes){
                stm.setString(1, tidalHarmonicConstants.getWaveName());
                stm.setString(2, tidalHarmonicConstants.getAmplitude());
                stm.setString(3, tidalHarmonicConstants.getPhase());
                stm.setString(4, tidalHarmonicConstants.getStationId());
                stm.addBatch();
            }
            stm.executeBatch();
            connection.commit();

        } catch (Exception e){
            e.printStackTrace();
            return DefaultResponseDTO.builder().status(0).message(e.getMessage()).build();
        } finally {
            if(stm != null){
                stm.close();
            }
            if(connection != null){
                connection.close();
            }
        }
        return DefaultResponseDTO.builder().status(1).message("Thành công").build();
    }
}
