package com.taurus.iharry.database;

import com.orm.SugarRecord;

/**
 * Created by taurus on 16/10/13.
 */
public class bookStatus extends SugarRecord {
    private int status;

    public int getStatus() {
        return status;
    }

    public bookStatus(int status) {

        this.status = status;
    }

    public bookStatus() {

    }
}
