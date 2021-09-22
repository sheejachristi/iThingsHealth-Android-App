package com.iorbit_tech.healthcare.caretakerapp.ithingshealthcare;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class Database_Helper extends OrmLiteSqliteOpenHelper {
    private static final String DB_NAME = "healthcare.db";
    private static final int DB_VERSION = 1;
    private Dao<AlertDetail, Integer> informationDao;
    public Database_Helper(Context context) {
        super(context,DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, AlertDetail.class);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {

        try {
            TableUtils.dropTable(connectionSource, AlertDetail.class, true);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        onCreate(database, connectionSource);
    }
    public Dao<AlertDetail, Integer> getInformationDao() throws SQLException,
            java.sql.SQLException {
        if (informationDao == null) {
            informationDao = getDao(AlertDetail.class);
        }
        return informationDao;
    }
}