package com.neo.nbdapi.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.nbdapi.dao.PaginationDAO;
import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.dto.UserGroupDTO;
import com.neo.nbdapi.entity.*;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.services.objsearch.UserGroupSearch;
import com.neo.nbdapi.utils.Constants;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.Logger;

import javax.validation.Valid;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + "/user-group")
public class UserGroupController {
    @Autowired
    private PaginationDAO paginationDAO;

    @Autowired
    private HikariDataSource ds;

    @Autowired
    @Qualifier("objectMapper")
    private ObjectMapper objectMapper;

    @PostMapping("/get-stations")
    public List<ComboBoxStr> getStations() throws SQLException, BusinessException {
        StringBuilder sql = new StringBuilder(" select station_id, station_code, station_name from stations where status=1 order by station_code, station_name");
        try (Connection connection = ds.getConnection();PreparedStatement st = connection.prepareStatement(sql.toString());) {
            List<Object> paramSearch = new ArrayList<>();
//            logger.debug("NUMBER OF SEARCH : {}", paramSearch.size());
            ResultSet rs = st.executeQuery();
            List<ComboBoxStr> list = new ArrayList<>();
            ComboBoxStr stationType = ComboBoxStr.builder()
                    .id("-1")
                    .text("--Lựa chọn--")
                    .build();
            list.add(stationType);
            while (rs.next()) {
                stationType = ComboBoxStr.builder()
                        .id(rs.getString("station_id"))
                        .text(rs.getString("station_code") + " - " + rs.getString("station_name"))
                        .build();
                list.add(stationType);
            }
            rs.close();
            return list;
        }
    }

    @GetMapping("/get-user-group-by-id")
    public UserGroupDTO getUserGroupById(@RequestParam(name = "groupId") String groupId) {
        List<UserGroupDetail> users = new ArrayList<>();
        if (StringUtils.isEmpty(groupId)) return UserGroupDTO.builder().build();
        StringBuilder sql1 = new StringBuilder("select id,name,description,create_by,create_at,modify_at,modify_by,group_parent,group_level,station_id,status from group_user_info where status=1 and id=?");
        StringBuilder sql2 = new StringBuilder("select id,group_id,user_info_id,is_group_leader from group_detail where group_id=?");
        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql1.toString());
             PreparedStatement ps2 = connection.prepareStatement(sql2.toString())) {
            ps2.setLong(1, Long.parseLong(groupId));
            ResultSet rs = ps2.executeQuery();
            while (rs.next()) {
                UserGroupDetail user = UserGroupDetail.builder()
                        .userId(rs.getString("user_info_id"))
                        .isLeader(rs.getInt("is_group_leader"))
                        .build();
                users.add(user);
            }
            rs.close();
            ps.setLong(1, Long.parseLong(groupId));
            rs = ps.executeQuery();
            while (rs.next()) {
                UserGroupDTO uGroup = UserGroupDTO.builder().id(rs.getLong("id"))
                        .name(rs.getString("name"))
                        .groupLevel(rs.getInt("group_level"))
                        .description(rs.getString("description"))
                        .groupParent(rs.getLong("group_parent"))
                        .stationId(rs.getLong("station_id"))
                        .groupLevel(rs.getInt("group_level"))
                        .status(rs.getInt("status"))
                        .users(users)
                        .build();
                return uGroup;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return UserGroupDTO.builder().build();
    }

    @GetMapping("/get-user-group")
    public List<UserGroupDTO> getUserGroup(@RequestParam(name = "stationId") String stationId) {
        List<UserGroupDTO> uGroups = new ArrayList<>();
        if (StringUtils.isEmpty(stationId)) return uGroups;
        try (Connection connection = ds.getConnection()) {
            StringBuilder sql = new StringBuilder("select id, name, group_level from group_user_info where status=1 and station_id=? order by name");
            PreparedStatement ps = connection.prepareStatement(sql.toString());
            ps.setLong(1, Long.parseLong(stationId));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UserGroupDTO uGroup = UserGroupDTO.builder().id(rs.getLong("id"))
                        .name(rs.getString("name"))
                        .groupLevel(rs.getInt("group_level"))
                        .build();
                uGroups.add(uGroup);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uGroups;
    }

    @PostMapping("/save")
    public DefaultResponseDTO save(@RequestBody @Valid UserGroupDTO userGroup) throws SQLException, JsonProcessingException {
        DefaultResponseDTO defaultResponseDTO = DefaultResponseDTO.builder().build();
        Connection connection = ds.getConnection();
        CallableStatement ps = null;
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User userLogin = (User) auth.getPrincipal();
            connection.setAutoCommit(false);
            StringBuilder sql = new StringBuilder("begin ?:= group_user_info_seq.nextval; insert into group_user_info(id,name,description,create_by,create_at,group_parent,group_level,station_id,status) " +
                    "values (group_user_info_seq.currval,?,?,?,sysdate,?,?,?,?); end;");
            ps = connection.prepareCall(sql.toString());
            ps.registerOutParameter(1, Types.INTEGER);
            ps.setString(2, userGroup.getName());
            ps.setString(3, userGroup.getDescription());
            ps.setString(4, userLogin.getUsername());
            ps.setLong(5, userGroup.getGroupParent());
            ps.setInt(6, userGroup.getGroupLevel());
            ps.setLong(7, userGroup.getStationId());
            ps.setInt(8, userGroup.getStatus());

            ps.executeUpdate();
            Long id = ps.getLong(1);
            ps.close();

            // them tung user vao group
            if (userGroup.getUsers().size() > 0) {
                sql = new StringBuilder("insert into group_detail(id,group_id,user_info_id,is_group_leader) values (group_detail_seq.nextval,?,?,?)");
                ps = connection.prepareCall(sql.toString());
                for (UserGroupDetail user : userGroup.getUsers()) {
                    ps.setLong(1, id);
                    ps.setString(2, user.getUserId());
                    ps.setInt(3, user.getIsLeader());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            connection.commit();
            defaultResponseDTO.setStatus(1);
            defaultResponseDTO.setMessage("Thêm mới thành công");
        } catch (Exception e) {
            defaultResponseDTO.setStatus(0);
            defaultResponseDTO.setMessage("Thêm mới thất bại: " + e.getMessage());
        } finally {
            if (ps != null) ps.close();
            connection.rollback();
            connection.close();
        }

        return defaultResponseDTO;
    }

    @PostMapping("/update")
    public DefaultResponseDTO update(@RequestBody @Valid UserGroupDTO userGroup) throws SQLException, JsonProcessingException {
        DefaultResponseDTO defaultResponseDTO = DefaultResponseDTO.builder().build();
        Connection connection = ds.getConnection();
        CallableStatement ps = null;
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User userLogin = (User) auth.getPrincipal();
            connection.setAutoCommit(false);
            StringBuilder sql = new StringBuilder("begin update group_user_info set name=?,description=?,modify_by=?,modify_at=sysdate,group_parent=?,group_level=?,station_id=?,status=?" +
                    " where id=?; delete from group_detail where group_id=?; end;");
            ps = connection.prepareCall(sql.toString());
            ps.setString(1, userGroup.getName());
            ps.setString(2, userGroup.getDescription());
            ps.setString(3, userLogin.getUsername());
            ps.setLong(4, userGroup.getGroupParent());
            ps.setInt(5, userGroup.getGroupLevel());
            ps.setLong(6, userGroup.getStationId());
            ps.setInt(7, userGroup.getStatus());
            ps.setLong(8, userGroup.getId());
            ps.setLong(9, userGroup.getId());

            ps.executeUpdate();
            ps.close();

            // them tung user vao group
            if (userGroup.getUsers().size() > 0) {
                sql = new StringBuilder("insert into group_detail(id,group_id,user_info_id,is_group_leader) values (group_detail_seq.nextval,?,?,?)");
                ps = connection.prepareCall(sql.toString());
                for (UserGroupDetail user : userGroup.getUsers()) {
                    ps.setLong(1, userGroup.getId());
                    ps.setString(2, user.getUserId());
                    ps.setInt(3, user.getIsLeader());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            connection.commit();
            defaultResponseDTO.setStatus(1);
            defaultResponseDTO.setMessage("Cập nhật thành công");
        } catch (Exception e) {
            e.printStackTrace();
            defaultResponseDTO.setStatus(0);
            defaultResponseDTO.setMessage("Cập nhật thất bại: " + e.getMessage());
        } finally {
            if (ps != null) ps.close();
            connection.rollback();
            connection.close();
        }

        return defaultResponseDTO;
    }

    @PostMapping("/delete")
    public DefaultResponseDTO delete(@RequestBody @Valid UserGroupDTO userGroup) throws SQLException, JsonProcessingException {
        DefaultResponseDTO defaultResponseDTO = DefaultResponseDTO.builder().build();
        Connection connection = ds.getConnection();
        CallableStatement ps = null;
        try {
            connection.setAutoCommit(false);
            StringBuilder sql = new StringBuilder("begin delete from group_detail where group_id=?; delete from group_user_info where id=?; end;");
            ps = connection.prepareCall(sql.toString());
            ps.setLong(1, userGroup.getId());
            ps.setLong(2, userGroup.getId());

            ps.executeUpdate();
            ps.close();
            connection.commit();
            defaultResponseDTO.setStatus(1);
            defaultResponseDTO.setMessage("Xóa thành công");
        } catch (Exception e) {
            e.printStackTrace();
            defaultResponseDTO.setStatus(0);
            defaultResponseDTO.setMessage("Xóa thất bại: " + e.getMessage());
        } finally {
            if (ps != null) ps.close();
            connection.rollback();
            connection.close();
        }

        return defaultResponseDTO;
    }

    @GetMapping("/get-user")
    public List<UserInfo> getUserGroup() throws SQLException, BusinessException{
        StringBuilder sql = new StringBuilder(" select id, name from user_info where status_id=1 and rownum < 100 order by id, name");
        try (Connection connection = ds.getConnection();PreparedStatement st = connection.prepareStatement(sql.toString());) {
            List<Object> paramSearch = new ArrayList<>();
//            logger.debug("NUMBER OF SEARCH : {}", paramSearch.size());
            ResultSet rs = st.executeQuery();
            List<UserInfo> list = new ArrayList<>();
            UserInfo userInfo = UserInfo.builder()
                    .id("")
                    .text("--Không chọn--")
                    .build();;
            list.add(userInfo);
            while (rs.next()) {
                userInfo = UserInfo.builder()
                        .id(rs.getString("id"))
                        .text(rs.getString("id") + " - " + rs.getString("name"))
                        .build();
                list.add(userInfo);
            }
            rs.close();
            return list;
        }
    }

    @PostMapping("/get-data")
    public DefaultPaginationDTO getUserGroup(@RequestBody @Valid DefaultRequestPagingVM defaultRequestPagingVM) {
        List<UserGroupDTO> userGroupDTOS = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            int pageNumber = Integer.parseInt(defaultRequestPagingVM.getStart());
            int recordPerPage = Integer.parseInt(defaultRequestPagingVM.getLength());
            StringBuilder sql = new StringBuilder("select a.id, a.name, a.description,a.create_by, a.create_at, a.modify_by, " +
                    "a.modify_at, a.group_parent, c.name parent, a.group_level,a.station_id,a.status,b.station_name " +
                    "from group_user_info a inner join stations b on a.station_id = b.station_id " +
                    "left join group_user_info c on a.group_parent=c.id where 1 = 1");
            String search = defaultRequestPagingVM.getSearch();
            List<Object> paramSearch = new ArrayList<>();
            if (Strings.isNotEmpty(search)) {
                try {
                    UserGroupSearch objectSearch = objectMapper.readValue(search, UserGroupSearch.class);
                    if (Strings.isNotEmpty(objectSearch.getGroupName())) {
                        sql.append(" AND a.name like ? ");
                        paramSearch.add("%" + objectSearch.getGroupName() + "%");
                    }
                    if (Strings.isNotEmpty(objectSearch.getStationName())) {
                        sql.append(" AND c.name like ? ");
                        paramSearch.add("%" + objectSearch.getGroupParentName() + "%");
                    }
                    if (Strings.isNotEmpty(objectSearch.getStatus())) {
                        sql.append(" AND a.status =  ? ");
                        paramSearch.add(objectSearch.getStatus());
                    }
                    if (Strings.isNotEmpty(objectSearch.getStationName())) {
                        sql.append(" AND b.station_name like ? ");
                        paramSearch.add("%" + objectSearch.getStationName() + "%");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ResultSet resultSetListData = paginationDAO.getResultPagination(connection, sql.toString(), pageNumber + 1, recordPerPage, paramSearch);

            while (resultSetListData.next()) {
                UserGroupDTO userGroupDTO = UserGroupDTO.builder()
                        .id(resultSetListData.getLong("id"))
                        .name(resultSetListData.getString("name"))
                        .description(resultSetListData.getString("description"))
                        .createBy(resultSetListData.getString("create_by"))
                        .createAt(resultSetListData.getDate("create_at"))
                        .modifyBy(resultSetListData.getString("modify_by"))
                        .modifyAt(resultSetListData.getDate("modify_at"))
                        .groupParent(resultSetListData.getInt("group_parent"))
                        .groupParentName(resultSetListData.getString("parent"))
                        .groupLevel(resultSetListData.getInt("group_level"))
                        .stationId(resultSetListData.getLong("station_id"))
                        .status(resultSetListData.getInt("status"))
                        .stationsName(resultSetListData.getString("station_name"))
                        .build();
                userGroupDTOS.add(userGroupDTO);
            }
            long total = paginationDAO.countResultQuery(sql.toString(), paramSearch);
            return DefaultPaginationDTO
                    .builder()
                    .draw(Integer.parseInt(defaultRequestPagingVM.getDraw()))
                    .recordsFiltered(userGroupDTOS.size())
                    .recordsTotal(total)
                    .content(userGroupDTOS)
                    .build();

        } catch (Exception e) {

            return DefaultPaginationDTO
                    .builder()
                    .draw(Integer.parseInt(defaultRequestPagingVM.getDraw()))
                    .recordsFiltered(0)
                    .recordsTotal(0)
                    .content(userGroupDTOS)
                    .build();
        }
    }
}
