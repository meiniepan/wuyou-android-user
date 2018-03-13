package com.wuyou.user.bean;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.wuyou.user.bean.UserInfo.AddressConverter;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "USER_INFO".
*/
public class UserInfoDao extends AbstractDao<UserInfo, Long> {

    public static final String TABLENAME = "USER_INFO";

    /**
     * Properties of entity UserInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Mid = new Property(0, Long.class, "mid", true, "_id");
        public final static Property Name = new Property(1, String.class, "name", false, "USERNAME");
        public final static Property Mobile = new Property(2, String.class, "mobile", false, "PHONE");
        public final static Property Uid = new Property(3, String.class, "uid", false, "UID");
        public final static Property Head_image = new Property(4, String.class, "head_image", false, "HEAD");
        public final static Property Token = new Property(5, String.class, "token", false, "TOKEN");
        public final static Property Password = new Property(6, String.class, "password", false, "PWD");
        public final static Property Gender = new Property(7, String.class, "gender", false, "GENDER");
        public final static Property Address = new Property(8, String.class, "address", false, "ADDRESS");
    }

    private final AddressConverter addressConverter = new AddressConverter();

    public UserInfoDao(DaoConfig config) {
        super(config);
    }
    
    public UserInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"USER_INFO\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: mid
                "\"USERNAME\" TEXT," + // 1: name
                "\"PHONE\" TEXT," + // 2: mobile
                "\"UID\" TEXT," + // 3: uid
                "\"HEAD\" TEXT," + // 4: head_image
                "\"TOKEN\" TEXT," + // 5: token
                "\"PWD\" TEXT," + // 6: password
                "\"GENDER\" TEXT," + // 7: gender
                "\"ADDRESS\" TEXT);"); // 8: address
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"USER_INFO\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, UserInfo entity) {
        stmt.clearBindings();
 
        Long mid = entity.getMid();
        if (mid != null) {
            stmt.bindLong(1, mid);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
 
        String mobile = entity.getMobile();
        if (mobile != null) {
            stmt.bindString(3, mobile);
        }
 
        String uid = entity.getUid();
        if (uid != null) {
            stmt.bindString(4, uid);
        }
 
        String head_image = entity.getHead_image();
        if (head_image != null) {
            stmt.bindString(5, head_image);
        }
 
        String token = entity.getToken();
        if (token != null) {
            stmt.bindString(6, token);
        }
 
        String password = entity.getPassword();
        if (password != null) {
            stmt.bindString(7, password);
        }
 
        String gender = entity.getGender();
        if (gender != null) {
            stmt.bindString(8, gender);
        }
 
        AddressBean address = entity.getAddress();
        if (address != null) {
            stmt.bindString(9, addressConverter.convertToDatabaseValue(address));
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, UserInfo entity) {
        stmt.clearBindings();
 
        Long mid = entity.getMid();
        if (mid != null) {
            stmt.bindLong(1, mid);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
 
        String mobile = entity.getMobile();
        if (mobile != null) {
            stmt.bindString(3, mobile);
        }
 
        String uid = entity.getUid();
        if (uid != null) {
            stmt.bindString(4, uid);
        }
 
        String head_image = entity.getHead_image();
        if (head_image != null) {
            stmt.bindString(5, head_image);
        }
 
        String token = entity.getToken();
        if (token != null) {
            stmt.bindString(6, token);
        }
 
        String password = entity.getPassword();
        if (password != null) {
            stmt.bindString(7, password);
        }
 
        String gender = entity.getGender();
        if (gender != null) {
            stmt.bindString(8, gender);
        }
 
        AddressBean address = entity.getAddress();
        if (address != null) {
            stmt.bindString(9, addressConverter.convertToDatabaseValue(address));
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public UserInfo readEntity(Cursor cursor, int offset) {
        UserInfo entity = new UserInfo( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // mid
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // name
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // mobile
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // uid
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // head_image
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // token
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // password
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // gender
            cursor.isNull(offset + 8) ? null : addressConverter.convertToEntityProperty(cursor.getString(offset + 8)) // address
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, UserInfo entity, int offset) {
        entity.setMid(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setMobile(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setUid(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setHead_image(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setToken(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setPassword(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setGender(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setAddress(cursor.isNull(offset + 8) ? null : addressConverter.convertToEntityProperty(cursor.getString(offset + 8)));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(UserInfo entity, long rowId) {
        entity.setMid(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(UserInfo entity) {
        if(entity != null) {
            return entity.getMid();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(UserInfo entity) {
        return entity.getMid() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
