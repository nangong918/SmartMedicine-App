package com.czy.baseUtilsLib.database;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;

import java.util.List;

public abstract class BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insert(T entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertAll(T... entities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertAll(List<T> entities);

    @Update
    public abstract void update(T entity);

    @Delete
    public abstract int deleteEntity(T entity);

}
