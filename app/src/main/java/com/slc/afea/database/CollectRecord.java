package com.slc.afea.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by achang on 2019/1/14.
 */
@Entity
public class CollectRecord implements Serializable, Cloneable {
    static final long serialVersionUID = 42L;
    @Id
    private long recordId;
    private long time;
    private String name;
    private int collect;
    private int operateType;

    @Generated(hash = 281995908)
    public CollectRecord(long recordId, long time, String name, int collect,
                         int operateType) {
        this.recordId = recordId;
        this.time = time;
        this.name = name;
        this.collect = collect;
        this.operateType = operateType;
    }

    @Generated(hash = 2133593564)
    public CollectRecord() {
    }

    public long getId() {
        return recordId;
    }

    public void setId(long id) {
        this.recordId = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCollect() {
        return collect;
    }

    public void setCollect(int collect) {
        this.collect = collect;
    }

    public int getOperateType() {
        return operateType;
    }

    public void setOperateType(int operateType) {
        this.operateType = operateType;
    }

    public long getRecordId() {
        return this.recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }
}
