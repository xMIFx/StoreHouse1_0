package com.gist.github.xMIFx.StoreHouse.dao.Impl;

import com.gist.github.xMIFx.StoreHouse.Entity.Directories.InterfaceMenu;
import com.gist.github.xMIFx.StoreHouse.Entity.Directories.MenuButton;
import com.gist.github.xMIFx.StoreHouse.Entity.Directories.UserInterface;
import com.gist.github.xMIFx.StoreHouse.dao.Interfaces.InterfaceMenuDao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Vlad on 09.03.2015.
 */
public class InterfaceMenuDaoJdbc implements InterfaceMenuDao {
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public UserInterface selectById(int iD) throws SQLException {
        Connection con = dataSource.getConnection();
        UserInterface userInterface = null;
        try (Statement st = con.createStatement();
             ResultSet resMenu = st.executeQuery(
                     "select distinct\n" +
                             "binding.idInteface\n" +
                             ",usersinterface.name as Interfacename\n" +
                             ",binding.idMenu\n" +
                             ",userinterfacemenu.name as menuName\n" +
                             "from bindingintefacemenubutton as binding\n" +
                             "left join userinterfacemenu\n" +
                             "on binding.idMenu = userinterfacemenu.ID\n" +
                             "left join usersinterface\n" +
                             "on binding.idInteface = usersinterface.ID\n" +
                             "WHERE binding.idInteface = " + iD);


        ) {

            List<InterfaceMenu> interfaceMenuList = new ArrayList<>();
            String interfaceName = null;
            int interfaceID = 0;
            while (resMenu.next()) {
                if (interfaceName == null) {
                    interfaceName = resMenu.getString("Interfacename");
                    interfaceID = resMenu.getInt("idInteface");
                }
                interfaceMenuList.add(
                        new InterfaceMenu(resMenu.getInt("idMenu")
                                , resMenu.getString("menuName")));
            }

            userInterface = new UserInterface(interfaceID
                    , interfaceName
                    , interfaceMenuList);

        }
        return userInterface;


    }

    @Override
    public UserInterface selectInterfaceByUserUUID(String UUID) throws SQLException {
        Connection con = dataSource.getConnection();
        UserInterface userInterface = null;
        try (Statement st = con.createStatement();
             ResultSet resMenu = st.executeQuery(
                     "Select distinct \n" +
                             "UserInterface \n" +
                             ",binding.idInteface\n" +
                             " ,usersinterface.name as Interfacename\n" +
                             ",binding.idMenu\n" +
                             " ,userinterfacemenu.name as menuName\n" +
                             "from users\n" +
                             "inner join bindingintefacemenubutton as binding\n" +
                             "on users.UserInterface = binding.idInteface \n" +
                             " inner join userinterfacemenu\n" +
                             "   on binding.idMenu = userinterfacemenu.ID\n" +
                             "  inner join usersinterface\n" +
                             "  on binding.idInteface = usersinterface.ID \n" +
                             "where users.UUID =  '" + UUID+"'")
        ) {

            List<InterfaceMenu> interfaceMenuList = new ArrayList<>();
            String interfaceName = null;
            int interfaceID = 0;
            while (resMenu.next()) {
                if (interfaceName == null) {
                    interfaceName = resMenu.getString("Interfacename");
                    interfaceID = resMenu.getInt("idInteface");
                }
                interfaceMenuList.add(
                        new InterfaceMenu(resMenu.getInt("idMenu")
                                , resMenu.getString("menuName")));
            }

            userInterface = new UserInterface(interfaceID
                    , interfaceName
                    , interfaceMenuList);

        }
        return userInterface;
    }

    @Override
    public InterfaceMenu selectButtonsByUserUUID_iDMenu(String UUID, int iD) throws SQLException {
        Connection con = dataSource.getConnection();
        InterfaceMenu interfaceMenu = null;
        try (Statement st = con.createStatement();
             ResultSet res = st.executeQuery(
                     "Select distinct \n" +
                             "UserInterface \n" +
                             ",binding.idInteface\n" +
                             ",binding.idMenu\n" +
                             ",userinterfacemenu.name as menuName\n" +
                             ",binding.idButton\n" +
                             ",buttons.name as buttonName\n" +
                             ",buttons.action \n" +
                             "from users\n" +
                             "inner join bindingintefacemenubutton as binding\n" +
                             "on users.UserInterface = binding.idInteface \n" +
                             " inner join userinterfacemenu\n" +
                             "   on binding.idMenu = userinterfacemenu.ID\n" +
                             "  inner join usersinterface\n" +
                             "  on binding.idInteface = usersinterface.ID \n" +
                             "inner join buttonformenu buttons\n" +
                             "on bind.idButton = buttons.id\n" +
                             "Where users.UUID =  '" + UUID + "'\n" +
                             "& bind.idMenu =" + iD)) {
            List<MenuButton> buttonList = new ArrayList<>();
            String menuName = null;
            while (res.next()) {
                if (menuName == null) {
                    menuName = res.getString("menuName");
                }
                buttonList.add(new MenuButton(res.getInt("idButton"), res.getString("buttonName"), res.getString("action")));
            }
            interfaceMenu = new InterfaceMenu(iD, menuName, buttonList);
        }
        return interfaceMenu;
    }

    @Override
    public InterfaceMenu selectButtonsByiDInterface_iDMenu(int iDInterface, int iDMenu) throws SQLException {
        Connection con = dataSource.getConnection();
        InterfaceMenu interfaceMenu = null;
        try (Statement st = con.createStatement();
             ResultSet res = st.executeQuery("Select distinct \n" +
                     "bind.idMenu\n" +
                     ",menu.name as menuName\n" +
                     ",bind.idButton\n" +
                     ",buttons.name as buttonName\n" +
                     ",buttons.action \n" +
                     "from bindingintefacemenubutton bind\n" +
                     "inner join userinterfacemenu menu\n" +
                     "on bind.idMenu = menu.id\n" +
                     "inner join buttonformenu buttons\n" +
                     "on bind.idButton = buttons.id\n" +
                     "WHERE bind.idInteface = " + iDInterface + "\n" +
                     "& bind.idMenu =" + iDMenu)) {
            List<MenuButton> buttonList = new ArrayList<>();
            String menuName = null;
            while (res.next()) {
                if (menuName == null) {
                    menuName = res.getString("menuName");
                }
                buttonList.add(new MenuButton(res.getInt("idButton"), res.getString("buttonName"), res.getString("action")));
            }
            interfaceMenu = new InterfaceMenu(iDMenu, menuName, buttonList);
        }
        return interfaceMenu;
    }
}
