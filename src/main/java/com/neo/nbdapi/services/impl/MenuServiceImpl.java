package com.neo.nbdapi.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.nbdapi.dao.MenuDAO;
import com.neo.nbdapi.dao.PaginationDAO;
import com.neo.nbdapi.dto.DefaultPaginationDTO;
import com.neo.nbdapi.dto.DefaultResponseDTO;
import com.neo.nbdapi.entity.Menu;
import com.neo.nbdapi.exception.BusinessException;
import com.neo.nbdapi.rest.vm.CreateMenuVM;
import com.neo.nbdapi.rest.vm.DefaultDeleteVM;
import com.neo.nbdapi.rest.vm.DefaultRequestPagingVM;
import com.neo.nbdapi.rest.vm.EditMenuVM;
import com.neo.nbdapi.services.MenuService;
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

/**
 * @project NBD
 * @author thanglv
 */
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

    /**
     * service get list menu pagination
     * @param defaultRequestPagingVM
     * @return DefaultPaginationDTO
     */
    @Override
    public DefaultPaginationDTO getListMenuPagination(DefaultRequestPagingVM defaultRequestPagingVM) {
        logger.debug("defaultRequestPagingVM: {}", defaultRequestPagingVM);
        List<Menu> menuList = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            // pageNumber = start, recordPerPage = length
            int pageNumber = Integer.parseInt(defaultRequestPagingVM.getStart());
            int recordPerPage = Integer.parseInt(defaultRequestPagingVM.getLength());
            String search = defaultRequestPagingVM.getSearch();

            StringBuilder sql = new StringBuilder("SELECT id, name, display_order, picture_file, detail_file, menu_level, parent_id, publish, created_date, modified_date, created_user, modified_user, sys_id FROM menu WHERE 1 = 1 ");
            List<Object> paramSearch = new ArrayList<>();
            logger.debug("Object search: {}", search);
            // set param query to sql
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
            // get result query by paging
            ResultSet resultSetListData = paginationDAO.getResultPagination(connection, sql.toString(), pageNumber + 1, recordPerPage, paramSearch);

            while (resultSetListData.next()) {
                Menu menu = Menu.builder()
                        .id(resultSetListData.getInt("id"))
                        .name(resultSetListData.getString("name"))
                        .displayOrder(resultSetListData.getInt("display_order"))
                        .pictureFile(resultSetListData.getString("picture_file"))
                        .detailFile(resultSetListData.getString("detail_file"))
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

            // count result, totalElements
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

    /**
     * service get all menu
     * @return
     * @throws SQLException
     */
    @Override
    public List<Menu> getAllMenu() throws SQLException {
        return menuDAO.findAll();
    }

    /**
     * service create menu
     * @param createMenuVM
     * @return
     */
    @Override
    public DefaultResponseDTO createMenu(CreateMenuVM createMenuVM) throws SQLException, BusinessException {

        // if parent of menu not null, check parentMenuId exists in system
        Menu parentMenu = null;
        if (Strings.isNotEmpty(createMenuVM.getParentId())) {
            parentMenu = menuDAO.findMenuById(Long.parseLong(createMenuVM.getParentId()));
            // if parent menu not exists throw exception parentMenuId invalid
            if (parentMenu == null)
                throw new BusinessException("Menu cha không tồn tại trong hệ thống");

            if (parentMenu.getMenuLevel() > 0 )
                throw new BusinessException("Menu có tối đa 2 cấp");
        }

        Menu menuCreate = Menu
                .builder()
                .name(createMenuVM.getName())
                .displayOrder(Integer.parseInt(createMenuVM.getDisplayOrder()))
                .pictureFile(createMenuVM.getPictureFile())
                .detailFile(createMenuVM.getDetailFile())
                .publish(Integer.parseInt(createMenuVM.getPublish()))
                .build();
        // if parentMenu not set, default parentId = 0 and level = 0
        if (parentMenu != null) {
            menuCreate.setParentId(parentMenu.getId());
            menuCreate.setMenuLevel(parentMenu.getMenuLevel() + 1);
        } else {
            menuCreate.setMenuLevel(0);
            menuCreate.setParentId(0);
        }
        menuDAO.createMenu(menuCreate);
        return new DefaultResponseDTO(1, "Tạo mới menu thành công");
    }

    /**
     * service edit menu
     * @param editMenuVM
     * @return
     */
    @Override
    public DefaultResponseDTO editMenu(EditMenuVM editMenuVM) throws SQLException, BusinessException {
        // check menuId exists to edit
        Menu menu = menuDAO.findMenuById(Long.parseLong(editMenuVM.getId()));
        if (menu == null)
            throw new BusinessException("Menu không tồn tại trong hệ thống");

        // if not set parentMenuId => default parentId = 0 and level = 0
        Menu parentMenu = null;
        if (Strings.isNotEmpty(editMenuVM.getParentId())) {
            parentMenu = menuDAO.findMenuById(Long.parseLong(editMenuVM.getParentId()));

            if (parentMenu == null)
                throw new BusinessException("Menu cha không tồn tại trong hệ thống");

            // menu max level = 1, min level = 0
            if (parentMenu.getMenuLevel() > 0)
                throw new BusinessException("Menu có tối đa 2 cấp");

            // check parentMenu from edit request is child of current menu, if is child => throw exception
            if (checkMenuFirstIsParentOfSecond(menu.getId(), parentMenu.getId()))
                throw new BusinessException("Menu " + menu.getName() + " đang là cha của menu " + parentMenu.getName());
        }

        menu = menu.toBuilder()
                .name(editMenuVM.getName())
                .displayOrder(Integer.parseInt(editMenuVM.getDisplayOrder()))
                .pictureFile(editMenuVM.getPictureFile())
                .detailFile(editMenuVM.getDetailFile())
                .publish(Integer.parseInt(editMenuVM.getPublish()))
                .build();

        if (parentMenu == null) {
            menu.setMenuLevel(0);
            menu.setParentId(0);
        } else {
            menu.setParentId(menu.getId());
            menu.setMenuLevel(menu.getMenuLevel() + 1);
        }
        menuDAO.editMenu(menu);
        return new DefaultResponseDTO(1, "Sửa menu thành công");
    }

    /**
     * method delete menu
     * @param deleteMenuVM
     * @return
     * @throws SQLException
     * @throws BusinessException
     */
    @Override
    public DefaultResponseDTO deleteMenu(DefaultDeleteVM deleteMenuVM) throws SQLException, BusinessException {
        Menu menu = menuDAO.findMenuById(Long.parseLong(deleteMenuVM.getId()));
        if (menu == null)
            throw new BusinessException("Không tìm thấy menu trong hệ thống");

        menuDAO.deleteMenu(menu);
        return new DefaultResponseDTO(1, "Xoá menu khỏi hệ thống thành công");
    }

    private boolean checkMenuFirstIsParentOfSecond(long menuIdFirst, long menuIdLast) throws SQLException, BusinessException {
        Menu menu = menuDAO.findPathOfMenuByMenuId(menuIdLast);

        String pathParent = "/" + menuIdFirst + "/";
        return menu != null && menu.getPath() != null && menu.getPath().indexOf(pathParent) > 0;
    }
}
