package com.taurus.iharry.database;

import com.orm.SugarRecord;

/**
 * Created by taurus on 16/9/2.
 */
public class Book_Condition extends SugarRecord {
    private float conditoin;

    public Book_Condition(){
    }

    public Book_Condition(float condition){
        this.conditoin = condition;
    }

    public float getCondition() {
        return conditoin;
    }
}
