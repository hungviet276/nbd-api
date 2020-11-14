package com.neo.nbdapi.dao;

import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.rest.vm.MailGroupConFigVM;

public interface MailGroupConfigDAO {
    DefaultResponseDTO createMailGroupConfig(MailGroupConFigVM mailGroupConFigVM);
}
