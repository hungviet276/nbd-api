package com.neo.nbdapi.rest;

import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.rest.vm.SelectVM;
import com.neo.nbdapi.services.ValueTypeService;
import com.neo.nbdapi.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + Constants.APPLICATION_API.MODULE.URI_VALUE_TYPES)
public class ValueTypeController {

    @Autowired
    private ValueTypeService valueTypeService;

    @PostMapping("/get-value-type-select")
    public List<ComboBox> getValueTypesSelect(@RequestBody SelectVM selectVM) throws SQLException {
        return valueTypeService.getValueTypesSelect(selectVM);
    }
}
