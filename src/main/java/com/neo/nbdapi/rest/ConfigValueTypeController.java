package com.neo.nbdapi.rest;

import com.neo.nbdapi.dto.ConfigValueTypeDTO;
import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.dto.StationValueTypeSpatialDTO;
import com.neo.nbdapi.entity.ComboBox;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.rest.vm.SelectStationVM;
import com.neo.nbdapi.rest.vm.SelectVM;
import com.neo.nbdapi.services.ConfigValueTypeService;
import com.neo.nbdapi.services.ValueTypeService;
import com.neo.nbdapi.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + Constants.APPLICATION_API.MODULE.URI_CONFIG_VALUE_TYPES)
public class ConfigValueTypeController {

    @Autowired
    private ConfigValueTypeService configValueTypeService;

    @Autowired
    private ValueTypeService valueTypeService;

    @PostMapping("/get-list-config-value-type")
    public DefaultPaginationDTO getListMailConfigPagination(@RequestBody @Valid DefaultRequestPagingVM defaultRequestPagingVM) throws SQLException, BusinessException {
        return configValueTypeService.getListConfigValueType(defaultRequestPagingVM);
    }

    @PostMapping("/get-list-station")
    public List<ComboBox> getStationComboBox(@RequestBody SelectVM selectVM) throws SQLException {
        return configValueTypeService.getStationComboBox(selectVM.getTerm());
    }

    @PostMapping("/get-list-station-other")
    public List<ComboBox> getStationComboBox(@RequestBody SelectStationVM selectVM) throws SQLException {
        if(selectVM.getStation()==null){
            List<ComboBox> comboBoxes  = new ArrayList<>();
           return  comboBoxes;
        }
        return configValueTypeService.getStationComboBox(selectVM.getTerm(), selectVM.getStation());
    }

    @PostMapping("/get-list-value-type")
    public List<ComboBox> getValueTypeConfig(@RequestParam Long idStation, @RequestParam Long valueTypeId) throws SQLException {
        return configValueTypeService.getValueType(idStation,valueTypeId);
    }

    @GetMapping("/get-station-value-type-spatial")
    public StationValueTypeSpatialDTO getStationValueTypeSpatial(@RequestParam Long idStation, @RequestParam Long idValueType,@RequestParam String code) throws  SQLException{
        return configValueTypeService.getStationValueTypeSpatial(idStation, idValueType,code);
    }

    @GetMapping("/get-value-type-station-select")
    public List<ComboBox> getValueTypesWithStationSelect(@RequestParam Long idStation) throws  SQLException{
        return valueTypeService.getValueTypesWithStationSelect(idStation);
    }

    @PostMapping
    public DefaultResponseDTO  createConfigValuetype(@RequestBody ConfigValueTypeDTO configValueTypeDTO) throws Exception {
        return configValueTypeService.createConfigValuetype(configValueTypeDTO);
    }

    @GetMapping("/get-value-type-station-value-type")
    public ComboBox getValueTypesWithStationAndValueType(@RequestParam Long idStation, @RequestParam Long idValueType) throws  SQLException{
        return valueTypeService.getStationValueType(idStation,idValueType);
    }

    @GetMapping("/get-list-station-value-type-spatial")
    public List<StationValueTypeSpatialDTO> getStationValueTypeSpatialAndParent(@RequestParam Long idConfigValueType) throws  SQLException{
        return configValueTypeService.getStationValueTypeSpatials(idConfigValueType);
    }

    @PutMapping
    public DefaultResponseDTO  editConfigValuetype(@RequestBody ConfigValueTypeDTO configValueTypeDTO) throws Exception {
        return configValueTypeService.editConfigValuetype(configValueTypeDTO);
    }
    @DeleteMapping
    public DefaultResponseDTO deleteConfigValuetype(@RequestParam Long id) throws Exception{
        return configValueTypeService.deleteConfigValuetype(id);
    }
}
