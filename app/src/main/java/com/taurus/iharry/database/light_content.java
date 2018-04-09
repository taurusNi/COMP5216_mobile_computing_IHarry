package com.taurus.iharry.database;

import com.orm.SugarRecord;

/**
 * Created by taurus on 16/10/9.
 */
public class light_content extends SugarRecord {
    private float conditoin;

    public light_content(){
    }

    public light_content(float condition){
        this.conditoin = condition;
    }

    public float getCondition() {
        return conditoin;
    }
}
