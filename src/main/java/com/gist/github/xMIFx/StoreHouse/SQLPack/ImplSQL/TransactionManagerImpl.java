package com.gist.github.xMIFx.StoreHouse.SQLPack.ImplSQL;

import com.gist.github.xMIFx.StoreHouse.SQLPack.TransactionManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Callable;

/**
 * Created by Vlad on 18.02.2015.
 */
public class TransactionManagerImpl extends BaseDataSource implements TransactionManager {
    public static final String JDBC_URL = "jdbc:mysql://127.0.0.1:3306/storehouse?user=root&password=Lytghj12";
    private static DataSource dataSourcePool;

    public void setDataSourcePool(DataSource dataSourcePool) {
        TransactionManagerImpl.dataSourcePool = dataSourcePool;
    }


    @Override
    public Connection getConnection() throws SQLException {
        return dataSourcePool.getConnection();
    }


    @Override
    public <T, E extends Exception> T doInTransaction(UnitOfWork<T, E> unitOfWork) throws E, SQLException {
        //Class.forName("com.mysql.jdbc.Driver");
        Connection con = dataSourcePool.getConnection();
        con.setAutoCommit(false);
        try {
            T result = unitOfWork.doInTx();
            con.commit();
            return result;
        } catch (Exception e) {
            con.rollback();
            throw e;
        } finally {
            con.close();
        }
    }
}
