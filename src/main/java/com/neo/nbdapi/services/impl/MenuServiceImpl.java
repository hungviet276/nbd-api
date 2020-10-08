package com.neo.nbdapi.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.nbdapi.dao.MenuDAO;
import com.neo.nbdapi.dao.PaginationDAO;
import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.entity.MailConfig;
import com.neo.nbdapi.entity.Menu;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.services.MenuService;
import com.neo.nbdapi.services.objsearch.SearchMailConfig;
import com.neo.nbdapi.services.objsearch.SearchMenu;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MenuServiceImpl implements MenuService {

    private Logger logger = LogManager.getLogger(MenuServiceImpl.class);

    @Autowired
    private MenuDAO menuDAO;

    @Autowired
    private PaginationDAO paginationDAO;

    @Autowired
    @Qualifier("objectMapper")
    private ObjectMapper objectMapper;

    @Autowired
    private HikariDataSource ds;

    @Override
    public DefaultPaginationDTO getListMenuPagination(DefaultRequestPagingVM defaultRequestPagingVM) {
        logger.debug("defaultRequestPagingVM: {}", defaultRequestPagingVM);
        List<Menu> menuList = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            int pageNumber = Integer.parseInt(defaultRequestPagingVM.getStart());
            int recordPerPage = Integer.parseInt(defaultRequestPagingVM.getLength());
            String search = defaultRequestPagingVM.getSearch();

            StringBuilder sql = new StringBuilder("SELECT id, name, display_order, picture_file, detail_file, menu_level, parent_id, publish, created_date, modified_date, created_user, modified_user, sys_id FROM menu WHERE 1 = 1 ");
            List<Object> paramSearch = new ArrayList<>();
            logger.debug("Object search: {}", search);
            if (Strings.isNotEmpty(search)) {
                try {
                    SearchMenu objectSearch = objectMapper.readValue(search, SearchMenu.class);
                    if (Strings.isNotEmpty(objectSearch.getId())) {
                        sql.append(" AND id = ? ");
                        paramSearch.add(objectSearch.getId());
                    }
                    if (Strings.isNotEmpty(objectSearch.getName())) {
                        sql.append(" AND name LIKE ? ");
                        paramSearch.add("%" + objectSearch.getName() + "%");
                    }
                    if (Strings.isNotEmpty(objectSearch.getDisplayOrder())) {
                        sql.append(" AND display_order = ? ");
                        paramSearch.add(objectSearch.getDisplayOrder());
                    }
                    if (Strings.isNotEmpty(objectSearch.getPictureFile())) {
                        sql.append(" AND picture_file LIKE ? ");
                        paramSearch.add("%" + objectSearch.getPictureFile() + "%");
                    }
                    if (Strings.isNotEmpty(objectSearch.getDetailFile())) {
                        sql.append(" AND detail_file LIKE ? ");
                        paramSearch.add("%" + objectSearch.getDetailFile() + "%");
                    }
                    if (Strings.isNotEmpty(objectSearch.getMenuLevel())) {
                        sql.append(" AND menu_level = ? ");
                        paramSearch.add(objectSearch.getMenuLevel());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            logger.debug("NUMBER OF SEARCH : {}", paramSearch.size());
            ResultSet resultSetListData = paginationDAO.getResultPagination(connection, sql.toString(), pageNumber + 1, recordPerPage, paramSearch);

            while (resultSetListData.next()) {
                Menu menu = Menu.builder()
                        .id(resultSetListData.getInt("id"))
                        .name(resultSetListData.getString("name"))
                        .displayOrder(resultSetListData.getInt("display_order"))
                        .pictureFile(resultSetListData.getString("picture_file"))
                        .menuLevel(resultSetListData.getInt("menu_level"))
                        .parentId(resultSetListData.getInt("parent_id"))
                        .publish(resultSetListData.getInt("publish"))
                        .createdDate(resultSetListData.getDate("created_date"))
                        .modifiedDate(resultSetListData.getDate("modified_date"))
                        .createdUser(resultSetListData.getString("created_user"))
                        .modifiedUser(resultSetListData.getString("modified_user"))
                        .sysId(resultSetListData.getInt("sys_id"))
                        .build();
                menuList.add(menu);
            }

            // count result
            long total = paginationDAO.countResultQuery(sql.toString(), paramSearch);
            return DefaultPaginationDTO
                    .builder()
                    .draw(Integer.parseInt(defaultRequestPagingVM.getDraw()))
                    .recordsFiltered(menuList.size())
                    .recordsTotal(total)
                    .content(menuList)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return DefaultPaginationDTO
                    .builder()
                    .draw(Integer.parseInt(defaultRequestPagingVM.getDraw()))
                    .recordsFiltered(0)
                    .recordsTotal(0)
                    .content(menuList)
                    .build();
        }
    }

    @Override
    public List<Menu> getAllMenu() throws SQLException {
        return menuDAO.findAll();
    }
}
