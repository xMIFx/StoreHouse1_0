package com.gist.github.xMIFx.StoreHouse.dao.Impl;

import com.gist.github.xMIFx.StoreHouse.Entity.Directories.*;
import com.gist.github.xMIFx.StoreHouse.dao.Interfaces.UserDao;


import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * Created by Vlad on 17.02.2015.
 */
public class UserDaoJdbc implements UserDao {
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean delete() {
        return false;
    }

    @Override
    public int create() {

        return 0;
    }

    @Override
    public boolean save() {
        return false;
    }

    @Override
    public List<User> selectAll() {
        return null;
    }

    @Override
    public User selectById(int id) {
        return null;
    }

    @Override
    public User selectByLogin(String login) {
        return null;
    }

    @Override
    public User selectByUUID(String uuid) throws SQLException {
        Connection con = dataSource.getConnection();
        User user = null;
        UserInterface userInterface = null;
        Map<Integer, InterfaceMenu> menuMap = new HashMap<>();
        UserRoles userRole = null;

        try (Statement st = con.createStatement();
             ResultSet res = st.executeQuery(
                     "Select \n" +
                             "tempForUser.UUID\n" +
                             ",tempForUser.Id\n" +
                             ",tempForUser.Login\n" +
                             ",tempForUser.UserPassword\n" +
                             ",tempForUser.UserName\n" +
                             ",tempForUser.Email\n" +
                             ",tempForUser.markForDelete\n" +
                             ",tempForUser.telephone\n" +
                             ",tempForUser.birthday\n" +
                             ",tempForUser.idRole\n" +
                             ",tempForUser.roleName\n" +
                             ",tempForUser.fullrole\n" +
                             ",tempForUser.consumevisible \n" +
                             ",tempForUser.idInterface\n" +
                             ",tempForUser.interfaceName\n" +
                             ",binding.idMenu\n" +
                             ",userinterfacemenu.name as menuName\n" +
                             ",binding.idButton\n" +
                             ",buttonformenu.name as buttonName\n" +
                             ",buttonformenu.action as buttonAction\n" +
                             " from (\n" +
                             "\tSELECT \n" +
                             "\tusers.UUID\n" +
                             "\t,users.Id\n" +
                             "\t,users.Login\n" +
                             "\t,users.UserPassword\t\n" +
                             "\t,users.UserName\n" +
                             "\t,users.Email\n" +
                             "\t,users.markForDelete\n" +
                             "\t,users.telephone\n" +
                             "\t,users.birthday\n" +
                             "\t,users.Role as idRole\n" +
                             "\t,userroles.name as roleName\n" +
                             "\t,userroles.full as fullrole\n" +
                             "\t,userroles.consumevisible \n" +
                             "\t,users.UserInterface as idInterface\n" +
                             "\t,usersinterface.name as interfaceName\n" +
                             "\t\tFROM users\n" +
                             "\t\tinner join userroles\n" +
                             "\t\t\ton users.Role = userroles.id\n" +
                             "\t\tinner join usersinterface\n" +
                             "\t\t\ton users.UserInterface = usersinterface.id\n" +
                             "\t\twhere users.UUID = '"+uuid+"') as tempForUser\n" +
                             " inner join bindingintefacemenubutton binding\n" +
                             "  on tempForUser.idInterface = binding.idInteface\n" +
                             "\t\tinner join userinterfacemenu\n" +
                             "        on binding.idMenu = userinterfacemenu.id\n" +
                             "        inner join buttonformenu\n" +
                             "        on binding.idButton = buttonformenu.id\n")) {
            while (res.next()) {
                if (user == null) {
                    user = new User(res.getString("UUID"), res.getInt("Id"), res.getBoolean("markForDelete"), res.getString("Login"), res.getString("UserPassword"), res.getString("UserName"), res.getString("Email"), res.getString("telephone"), res.getDate("birthday"));
                }
                if (userRole == null) {
                    userRole = new UserRoles(res.getInt("idRole"), res.getString("roleName"), res.getBoolean("fullrole"), res.getBoolean("consumevisible"));

                }
                if (userInterface == null) {
                    userInterface = new UserInterface(res.getInt("idInterface"), res.getString("interfaceName"));
                }
                if (menuMap.containsKey(res.getInt("idMenu"))) {
                    menuMap.get(res.getInt("idMenu")).getButtonList().add(new MenuButton(res.getInt("idButton"), res.getString("buttonName"), res.getString("buttonAction")));
                } else {
                    InterfaceMenu intMenu = new InterfaceMenu(res.getInt("idMenu"), res.getString("menuName"));
                    intMenu.getButtonList().add(new MenuButton(res.getInt("idButton"), res.getString("buttonName"), res.getString("buttonAction")));
                    menuMap.put(res.getInt("idMenu"), intMenu);
                }
            }
            List<InterfaceMenu> menuList = new ArrayList<>();
            menuList.addAll(menuMap.values());
            userInterface.setMenuList(menuList);
            user.setUserInterface(userInterface);
            user.setRole(userRole);
        }

        return user;
    }

    @Override
    public User selectByLoginPassword(String log, String pas) throws SQLException {
        Connection con = dataSource.getConnection();
        User user = null;

        UserInterface userInterface = null;
        Map<Integer, InterfaceMenu> menuMap = new HashMap<>();
        UserRoles userRole = null;
        try (
                Statement st = con.createStatement();
                ResultSet res = st.executeQuery("Select \n" +
                        "tempForUser.UUID\n" +
                        ",tempForUser.Id\n" +
                        ",tempForUser.Login\n" +
                        ",tempForUser.UserPassword\n" +
                        ",tempForUser.UserName\n" +
                        ",tempForUser.Email\n" +
                        ",tempForUser.markForDelete\n" +
                        ",tempForUser.telephone\n" +
                        ",tempForUser.birthday\n" +
                        ",tempForUser.idRole\n" +
                        ",tempForUser.roleName\n" +
                        ",tempForUser.fullrole\n" +
                        ",tempForUser.consumevisible \n" +
                        ",tempForUser.idInterface\n" +
                        ",tempForUser.interfaceName\n" +
                        ",binding.idMenu\n" +
                        ",userinterfacemenu.name as menuName\n" +
                        ",binding.idButton\n" +
                        ",buttonformenu.name as buttonName\n" +
                        ",buttonformenu.action as buttonAction\n" +
                        " from (\n" +
                        "\tSELECT \n" +
                        "\tusers.UUID\n" +
                        "\t,users.Id\n" +
                        "\t,users.Login\n" +
                        "\t,users.UserPassword\t\n" +
                        "\t,users.UserName\n" +
                        "\t,users.Email\n" +
                        "\t,users.markForDelete\n" +
                        "\t,users.telephone\n" +
                        "\t,users.birthday\n" +
                        "\t,users.Role as idRole\n" +
                        "\t,userroles.name as roleName\n" +
                        "\t,userroles.full as fullrole\n" +
                        "\t,userroles.consumevisible \n" +
                        "\t,users.UserInterface as idInterface\n" +
                        "\t,usersinterface.name as interfaceName\n" +
                        "\t\tFROM users\n" +
                        "\t\tinner join userroles\n" +
                        "\t\t\ton users.Role = userroles.id\n" +
                        "\t\tinner join usersinterface\n" +
                        "\t\t\ton users.UserInterface = usersinterface.id\n" +
                        "\t\twhere users.Login = '"+log+"'\n" +
                        "\t\t\t\tand users.UserPassword = '"+pas+"') as tempForUser\n" +
                        " inner join bindingintefacemenubutton binding\n" +
                        "  on tempForUser.idInterface = binding.idInteface\n" +
                        "\t\tinner join userinterfacemenu\n" +
                        "        on binding.idMenu = userinterfacemenu.id\n" +
                        "        inner join buttonformenu\n" +
                        "        on binding.idButton = buttonformenu.id\n")) {
            while (res.next()) {
                if (user == null) {
                    user = new User(res.getString("UUID"), res.getInt("Id"), res.getBoolean("markForDelete"), res.getString("Login"), res.getString("UserPassword"), res.getString("UserName"), res.getString("Email"), res.getString("telephone"), res.getDate("birthday"));
                }
                if (userRole == null) {
                    userRole = new UserRoles(res.getInt("idRole"), res.getString("roleName"), res.getBoolean("fullrole"), res.getBoolean("consumevisible"));

                }
                if (userInterface == null) {
                    userInterface = new UserInterface(res.getInt("idInterface"), res.getString("interfaceName"));
                }
                if (menuMap.containsKey(res.getInt("idMenu"))) {
                    menuMap.get(res.getInt("idMenu")).getButtonList().add(new MenuButton(res.getInt("idButton"), res.getString("buttonName"), res.getString("buttonAction")));
                } else {
                    InterfaceMenu intMenu = new InterfaceMenu(res.getInt("idMenu"), res.getString("menuName"));
                    intMenu.getButtonList().add(new MenuButton(res.getInt("idButton"), res.getString("buttonName"), res.getString("buttonAction")));
                    menuMap.put(res.getInt("idMenu"), intMenu);
                }
            }
            List<InterfaceMenu> menuList = new ArrayList<>();
            menuList.addAll(menuMap.values());
            userInterface.setMenuList(menuList);
            user.setUserInterface(userInterface);
            user.setRole(userRole);
        }
        return user;
    }

    @Override
    public User createSimpleUser() throws SQLException {
        User user = selectByUUID(User.getEmptyUUID());
        return user;

    }

    @Override
    public List<User> selectByName(String name) {
        return null;
    }
}
