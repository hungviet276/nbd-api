package com.neo.nbdapi.services;

import com.neo.nbdapi.dto.SelectStationDTO;
import com.neo.nbdapi.rest.vm.SelectVM;

public interface StationService {
    SelectStationDTO getStationSelect(SelectVM selectVM);
}
