package com.tgrigorov.postbox.data.entities;

import java.util.Date;

public abstract class Entity {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public Date getUpdated() {
        return updated;
    }

    public boolean isNew() {
        return id == 0;
    }

    @DbColumn("ID")
    @DbPrimary()
    @DbNotNull()
    private int id;
    @DbColumn("CREATED")
    @DbNotNull()
    private Date created;
    @DbColumn("UPDATED")
    @DbNotNull()
    private Date updated;
}
