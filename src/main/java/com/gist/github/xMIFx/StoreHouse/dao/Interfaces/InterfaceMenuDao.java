package com.gist.github.xMIFx.StoreHouse.dao.Interfaces;

import com.gist.github.xMIFx.StoreHouse.Entity.Directories.InterfaceMenu;
import com.gist.github.xMIFx.StoreHouse.Entity.Directories.UserInterface;

import java.sql.SQLException;

/**
 * Created by Vlad on 09.03.2015.
 */
public interface InterfaceMenuDao {
    UserInterface selectById(int iD) throws SQLException;
    UserInterface selectInterfaceByUserUUID(String UUID) throws SQLException;
    InterfaceMenu selectButtonsByUserUUID_iDMenu(String UUID, int iD) throws SQLException;
    InterfaceMenu selectButtonsByiDInterface_iDMenu(int iDInterface, int iDMenu) throws SQLException;
}
