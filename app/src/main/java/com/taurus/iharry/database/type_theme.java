package com.taurus.iharry.database;

import com.orm.SugarRecord;

/**
 * Created by taurus on 16/10/12.
 */
public class type_theme extends SugarRecord {
    private boolean condition;

    public type_theme() {
    }

    public type_theme(boolean condition) {

        this.condition = condition;
    }

    public boolean isCondition() {
        return condition;
    }
}
