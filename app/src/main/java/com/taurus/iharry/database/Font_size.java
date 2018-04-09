package com.taurus.iharry.database;

import com.orm.SugarRecord;

/**
 * Created by taurus on 16/10/7.
 */
public class Font_size extends SugarRecord {
    private float size;

    public Font_size() {
    }

    public Font_size(float size) {
        this.size = size;
    }

    public float getSize() {
        return size;
    }
}
