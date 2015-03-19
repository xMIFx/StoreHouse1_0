package com.gist.github.xMIFx.StoreHouse.SQLPack;

import com.gist.github.xMIFx.StoreHouse.SQLPack.ImplSQL.UnitOfWork;

import java.sql.SQLException;
import java.util.concurrent.Callable;

/**
 * Created by Vlad on 17.02.2015.
 */
public interface TransactionManager {
    public <T,E extends Exception> T doInTransaction(UnitOfWork<T,E> unitOfWork) throws E, SQLException;
}
