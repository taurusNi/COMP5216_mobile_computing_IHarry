package com.taurus.iharry.database;

import com.orm.SugarRecord;

/**
 * Created by taurus on 16/10/7.
 */
public class Background_color extends SugarRecord {
    private int color;

    public Background_color() {
    }

    public Background_color(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
