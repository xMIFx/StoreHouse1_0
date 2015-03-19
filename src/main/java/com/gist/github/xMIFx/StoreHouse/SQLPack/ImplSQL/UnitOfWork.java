package com.gist.github.xMIFx.StoreHouse.SQLPack.ImplSQL;

/**
 * Created by Vlad on 19.02.2015.
 */
public interface UnitOfWork<T,E extends Exception>  {
   public T doInTx() throws E;
}
