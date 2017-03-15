package com.tgrigorov.postbox.data.filters;

public abstract class Filter {
    public Integer getIdEqual() {
        return idEqual;
    }

    public void setIdEqual(Integer idEqual) {
        this.idEqual = idEqual;
    }

    public Integer getIdNotEqual() {
        return idNotEqual;
    }

    public void setIdNotEqual(Integer idNotEqual) {
        this.idNotEqual = idNotEqual;
    }

    @Equal()
    @MapField("ID")
    private Integer idEqual;

    @Equal(false)
    @MapField("ID")
    private Integer idNotEqual;
}
