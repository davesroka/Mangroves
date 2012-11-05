package com.qfi.mangroves.entities;

/**
 * Interface that must be implemented by persisted objects
 * 
 * @author eyedol
 *
 */
public interface IDbEntity {
    
    public int getDbId();
    
    public void setDbId(int id);
    
}
