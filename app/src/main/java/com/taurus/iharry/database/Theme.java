package com.taurus.iharry.database;

import com.orm.SugarRecord;

/**
 * Created by taurus on 16/10/8.
 */
public class Theme extends SugarRecord {
    private boolean condition;

    public Theme() {
    }

    public Theme(boolean condition) {

        this.condition = condition;
    }

    public boolean isCondition() {
        return condition;
    }
}
