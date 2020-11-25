package com.neo.nbdapi.dao;

import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.entity.TidalHarmonicConstants;

import java.sql.SQLException;
import java.util.List;

public interface TidalHarmonicConstantsDAO {
    DefaultResponseDTO insertTidalHarmonicConstantsDAOs(List<TidalHarmonicConstants> tidalHarmonicConstantes) throws SQLException;
}
