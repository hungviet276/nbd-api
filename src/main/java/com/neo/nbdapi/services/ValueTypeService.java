package com.neo.nbdapi.services;

import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.rest.vm.SelectVM;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.SQLException;
import java.util.List;

public interface ValueTypeService {
    List<ComboBox> getValueTypesSelect(@RequestBody SelectVM selectVM) throws SQLException;
}
