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
    public List<User> selectAll() throws SQLException {

        Connection con = dataSource.getConnection();
        List<User> userList = null;
        Map<String, User> userMap = new HashMap<>();
        Map<Integer, UserInterface> userInterfaceMap = new HashMap<>();
        Map<Integer, InterfaceMenu> menuMap = new HashMap<>();
        Map<Integer, MenuButton> buttonMap = new HashMap<>();
        Map<Integer, MessengerGroup> groupMap = new HashMap<>();
        Map<Integer, UserRoles> userRoleMap = new HashMap<>();
        User user = null;
        UserInterface userInterface = null;
        InterfaceMenu interfaceMenu = null;
        MenuButton button = null;
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
                             ",tempForUser.idInterface\n" +
                             ",tempForUser.interfaceName\n" +
                             ",tempForUser.consumeVisible\n" +
                             ",tempForUser.idMessengerGroup\n" +
                             ",tempForUser.messengerGroupName\n" +
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
                             "\t,users.consumeVisible as consumeVisible\n" +
                             "\t,users.idMessengerGroup as idMessengerGroup\n" +
                             "\t,messengergroups.name as messengerGroupName\n" +
                             "\t,userroles.name as roleName\n" +
                             "\t,userroles.full as fullrole\n" +
                             "\t,users.UserInterface as idInterface\n" +
                             "\t,usersinterface.name as interfaceName\n" +
                             "\t\tFROM users\n" +
                             "\t\tinner join userroles\n" +
                             "\t\t\ton users.Role = userroles.id\n" +
                             "\t\tinner join usersinterface\n" +
                             "\t\t\ton users.UserInterface = usersinterface.id\n" +
                             "\t\tinner join messengergroups\n" +
                             "\t\t\ton users.idMessengerGroup = messengergroups.id\n" +
                             "\t\t) as tempForUser\n" +
                             " inner join bindingintefacemenubutton binding\n" +
                             "  on tempForUser.idInterface = binding.idInteface\n" +
                             "\t\tinner join userinterfacemenu\n" +
                             "        on binding.idMenu = userinterfacemenu.id\n" +
                             "        inner join buttonformenu\n" +
                             "        on binding.idButton = buttonformenu.id\n")) {
            while (res.next()) {
                if (userMap.containsKey(res.getString("UUID"))) {
                    user = userMap.get(res.getString("UUID"));
                } else {
                    user = new User(res.getString("UUID"), res.getInt("Id"), res.getBoolean("markForDelete"), res.getString("Login"), res.getString("UserPassword"), res.getString("UserName"), res.getString("Email"), res.getString("telephone"), res.getDate("birthday"), res.getBoolean("consumeVisible"));
                    userMap.put(res.getString("UUID"), user);
                }
                if (userRoleMap.containsKey(res.getInt("idRole"))) {
                    userRole = userRoleMap.get(res.getInt("idRole"));

                } else {
                    userRole = new UserRoles(res.getInt("idRole"), res.getString("roleName"), res.getBoolean("fullrole"));
                    userRoleMap.put(res.getInt("idRole"), userRole);
                }
                if (userInterfaceMap.containsKey(res.getInt("idInterface"))) {
                    userInterface = userInterfaceMap.get(res.getInt("idInterface"));

                } else {
                    userInterface = new UserInterface(res.getInt("idInterface"), res.getString("interfaceName"));
                    userInterfaceMap.put(res.getInt("idInterface"), userInterface);
                }
                if (menuMap.containsKey(res.getInt("idMenu"))) {
                    interfaceMenu = menuMap.get(res.getInt("idMenu"));

                } else {
                    interfaceMenu = new InterfaceMenu(res.getInt("idMenu"), res.getString("menuName"));
                    menuMap.put(res.getInt("idMenu"), interfaceMenu);
                }
                if (buttonMap.containsKey(res.getInt("idButton"))) {
                    button = buttonMap.get(res.getInt("idButton"));
                } else {
                    button = new MenuButton(res.getInt("idButton"), res.getString("buttonName"), res.getString("buttonAction"));
                    buttonMap.put(res.getInt("idButton"), button);
                }
                if (!interfaceMenu.getButtonList().contains(button)) {
                    interfaceMenu.getButtonList().add(button);
                }
                if (!userInterface.getMenuList().contains(interfaceMenu)) {
                    userInterface.getMenuList().add(interfaceMenu);
                }
                if (groupMap.containsKey(res.getInt("idMessengerGroup"))) {
                    user.setMessengerGroup(groupMap.get(res.getInt("idMessengerGroup")));
                } else {
                    MessengerGroup mesGr = new MessengerGroup(res.getInt("idMessengerGroup"), res.getString("messengerGroupName"));
                    groupMap.put(mesGr.getID(), mesGr);
                    user.setMessengerGroup(mesGr);
                }
                if (user.getRole() == null) {
                    user.setRole(userRole);
                }
                if (user.getUserInterface() == null) {
                    user.setUserInterface(userInterface);
                }
            }
            userList = new ArrayList<>();
            userList.addAll(userMap.values());
            allUser.putAll(userMap);
        }


        return userList;
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
        Map<Integer, MessengerGroup> groupMap = new HashMap<>();
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
                             ",tempForUser.idInterface\n" +
                             ",tempForUser.interfaceName\n" +
                             ",tempForUser.consumeVisible\n" +
                             ",tempForUser.idMessengerGroup\n" +
                             ",tempForUser.messengerGroupName\n" +
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
                             "\t,users.consumeVisible as consumeVisible\n" +
                             "\t,users.idMessengerGroup as idMessengerGroup\n" +
                             "\t,messengergroups.name as messengerGroupName\n" +
                             "\t,userroles.name as roleName\n" +
                             "\t,userroles.full as fullrole\n" +
                             "\t,users.UserInterface as idInterface\n" +
                             "\t,usersinterface.name as interfaceName\n" +
                             "\t\tFROM users\n" +
                             "\t\tinner join userroles\n" +
                             "\t\t\ton users.Role = userroles.id\n" +
                             "\t\tinner join usersinterface\n" +
                             "\t\t\ton users.UserInterface = usersinterface.id\n" +
                             "\t\tinner join messengergroups\n" +
                             "\t\t\ton users.idMessengerGroup = messengergroups.id\n" +
                             "\t\twhere users.markForDelete <> 1 and users.UUID = '" + uuid + "') as tempForUser\n" +
                             " inner join bindingintefacemenubutton binding\n" +
                             "  on tempForUser.idInterface = binding.idInteface\n" +
                             "\t\tinner join userinterfacemenu\n" +
                             "        on binding.idMenu = userinterfacemenu.id\n" +
                             "        inner join buttonformenu\n" +
                             "        on binding.idButton = buttonformenu.id\n")) {
            while (res.next()) {
                if (user == null) {
                    user = new User(res.getString("UUID"), res.getInt("Id"), res.getBoolean("markForDelete"), res.getString("Login"), res.getString("UserPassword"), res.getString("UserName"), res.getString("Email"), res.getString("telephone"), res.getDate("birthday"), res.getBoolean("consumeVisible"));
                }
                if (userRole == null) {
                    userRole = new UserRoles(res.getInt("idRole"), res.getString("roleName"), res.getBoolean("fullrole"));

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
                if (groupMap.containsKey(res.getInt("idMessengerGroup"))) {
                    user.setMessengerGroup(groupMap.get(res.getInt("idMessengerGroup")));
                } else {
                    MessengerGroup mesGr = new MessengerGroup(res.getInt("idMessengerGroup"), res.getString("messengerGroupName"));
                    groupMap.put(mesGr.getID(), mesGr);
                    user.setMessengerGroup(mesGr);
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
        Map<Integer, MessengerGroup> groupMap = new HashMap<>();
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
                        ",tempForUser.consumeVisible\n" +
                        ",tempForUser.idMessengerGroup\n" +
                        ",tempForUser.messengerGroupName\n" +
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
                        "\t,users.consumeVisible as consumeVisible\n" +
                        "\t,users.idMessengerGroup as idMessengerGroup\n" +
                        "\t,messengergroups.name as messengerGroupName\n" +
                        "\t,userroles.name as roleName\n" +
                        "\t,userroles.full as fullrole\n" +
                        "\t,users.UserInterface as idInterface\n" +
                        "\t,usersinterface.name as interfaceName\n" +
                        "\t\tFROM users\n" +
                        "\t\tinner join userroles\n" +
                        "\t\t\ton users.Role = userroles.id\n" +
                        "\t\tinner join usersinterface\n" +
                        "\t\t\ton users.UserInterface = usersinterface.id\n" +
                        "\t\tinner join messengergroups\n" +
                        "\t\t\ton users.idMessengerGroup = messengergroups.id\n" +
                        "\t\twhere users.markForDelete <> 1 and users.Login = '" + log + "'\n" +
                        "\t\t\t\tand users.UserPassword = '" + pas + "') as tempForUser\n" +
                        " inner join bindingintefacemenubutton binding\n" +
                        "  on tempForUser.idInterface = binding.idInteface\n" +
                        "\t\tinner join userinterfacemenu\n" +
                        "        on binding.idMenu = userinterfacemenu.id\n" +
                        "        inner join buttonformenu\n" +
                        "        on binding.idButton = buttonformenu.id\n")) {
            while (res.next()) {
                if (user == null) {
                    user = new User(res.getString("UUID"), res.getInt("Id"), res.getBoolean("markForDelete"), res.getString("Login"), res.getString("UserPassword"), res.getString("UserName"), res.getString("Email"), res.getString("telephone"), res.getDate("birthday"), res.getBoolean("consumeVisible"));
                }
                if (userRole == null) {
                    userRole = new UserRoles(res.getInt("idRole"), res.getString("roleName"), res.getBoolean("fullrole"));

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
                if (groupMap.containsKey(res.getInt("idMessengerGroup"))) {
                    user.setMessengerGroup(groupMap.get(res.getInt("idMessengerGroup")));
                } else {
                    MessengerGroup mesGr = new MessengerGroup(res.getInt("idMessengerGroup"), res.getString("messengerGroupName"));
                    groupMap.put(mesGr.getID(), mesGr);
                    user.setMessengerGroup(mesGr);
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

    @Override
    public void setAllUser() throws SQLException {
        Connection con = dataSource.getConnection();
        Map<String, User> userMap = new HashMap<>();
        Map<Integer, UserInterface> userInterfaceMap = new HashMap<>();
        Map<Integer, InterfaceMenu> menuMap = new HashMap<>();
        Map<Integer, MenuButton> buttonMap = new HashMap<>();
        Map<Integer, MessengerGroup> groupMap = new HashMap<>();
        Map<Integer, UserRoles> userRoleMap = new HashMap<>();
        User user = null;
        UserInterface userInterface = null;
        InterfaceMenu interfaceMenu = null;
        MenuButton button = null;
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
                             ",tempForUser.idInterface\n" +
                             ",tempForUser.interfaceName\n" +
                             ",tempForUser.consumeVisible\n" +
                             ",tempForUser.idMessengerGroup\n" +
                             ",tempForUser.messengerGroupName\n" +
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
                             "\t,users.consumeVisible as consumeVisible\n" +
                             "\t,users.idMessengerGroup as idMessengerGroup\n" +
                             "\t,messengergroups.name as messengerGroupName\n" +
                             "\t,userroles.name as roleName\n" +
                             "\t,userroles.full as fullrole\n" +
                             "\t,users.UserInterface as idInterface\n" +
                             "\t,usersinterface.name as interfaceName\n" +
                             "\t\tFROM users\n" +
                             "\t\tinner join userroles\n" +
                             "\t\t\ton users.Role = userroles.id\n" +
                             "\t\tinner join usersinterface\n" +
                             "\t\t\ton users.UserInterface = usersinterface.id\n" +
                             "\t\tinner join messengergroups\n" +
                             "\t\t\ton users.idMessengerGroup = messengergroups.id\n" +
                             "\t\t) as tempForUser\n" +
                             " inner join bindingintefacemenubutton binding\n" +
                             "  on tempForUser.idInterface = binding.idInteface\n" +
                             "\t\tinner join userinterfacemenu\n" +
                             "        on binding.idMenu = userinterfacemenu.id\n" +
                             "        inner join buttonformenu\n" +
                             "        on binding.idButton = buttonformenu.id\n")) {
            while (res.next()) {
                if (userMap.containsKey(res.getString("UUID"))) {
                    user = userMap.get(res.getString("UUID"));
                } else {
                    user = new User(res.getString("UUID"), res.getInt("Id"), res.getBoolean("markForDelete"), res.getString("Login"), res.getString("UserPassword"), res.getString("UserName"), res.getString("Email"), res.getString("telephone"), res.getDate("birthday"), res.getBoolean("consumeVisible"));
                    userMap.put(res.getString("UUID"), user);
                }
                if (userRoleMap.containsKey(res.getInt("idRole"))) {
                    userRole = userRoleMap.get(res.getInt("idRole"));

                } else {
                    userRole = new UserRoles(res.getInt("idRole"), res.getString("roleName"), res.getBoolean("fullrole"));
                    userRoleMap.put(res.getInt("idRole"), userRole);
                }
                if (userInterfaceMap.containsKey(res.getInt("idInterface"))) {
                    userInterface = userInterfaceMap.get(res.getInt("idInterface"));

                } else {
                    userInterface = new UserInterface(res.getInt("idInterface"), res.getString("interfaceName"));
                    userInterfaceMap.put(res.getInt("idInterface"), userInterface);
                }
                if (menuMap.containsKey(res.getInt("idMenu"))) {
                    interfaceMenu = menuMap.get(res.getInt("idMenu"));

                } else {
                    interfaceMenu = new InterfaceMenu(res.getInt("idMenu"), res.getString("menuName"));
                    menuMap.put(res.getInt("idMenu"), interfaceMenu);
                }
                if (buttonMap.containsKey(res.getInt("idButton"))) {
                    button = buttonMap.get(res.getInt("idButton"));
                } else {
                    button = new MenuButton(res.getInt("idButton"), res.getString("buttonName"), res.getString("buttonAction"));
                    buttonMap.put(res.getInt("idButton"), button);
                }
                if (!interfaceMenu.getButtonList().contains(button)) {
                    interfaceMenu.getButtonList().add(button);
                }
                if (!userInterface.getMenuList().contains(interfaceMenu)) {
                    userInterface.getMenuList().add(interfaceMenu);
                }
                if (groupMap.containsKey(res.getInt("idMessengerGroup"))) {
                    user.setMessengerGroup(groupMap.get(res.getInt("idMessengerGroup")));
                } else {
                    MessengerGroup mesGr = new MessengerGroup(res.getInt("idMessengerGroup"), res.getString("messengerGroupName"));
                    groupMap.put(mesGr.getID(), mesGr);
                    user.setMessengerGroup(mesGr);
                }
                if (user.getRole() == null) {
                    user.setRole(userRole);
                }
                if (user.getUserInterface() == null) {
                    user.setUserInterface(userInterface);
                }
            }

            allUser.putAll(userMap);
        }
    }
}
