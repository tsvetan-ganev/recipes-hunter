package be.vives.recipeshunter.data.local.dao;

import android.database.Cursor;

import java.util.List;

public interface GenericDAO<PK, E> {
    List<E> findAll();
    E findById(PK id);
    void insert(E entity);
    void delete(PK id);
    void open();
    void close();
}
