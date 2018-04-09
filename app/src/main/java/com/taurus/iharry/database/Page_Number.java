package com.taurus.iharry.database;

import com.orm.SugarRecord;

/**
 * Created by taurus on 16/9/13.
 */
public class Page_Number extends SugarRecord {
    private int pageNumber;
    public Page_Number(){

    }

    public Page_Number(int index) {
        this.pageNumber = index;
    }

    public int getIndex() {
        return pageNumber;
    }
}
