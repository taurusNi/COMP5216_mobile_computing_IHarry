package com.taurus.iharry.database;

import com.orm.SugarRecord;

/**
 * Created by taurus on 16/10/11.
 */
public class Page_Number_2 extends SugarRecord {
    private int pageNumber;
    public Page_Number_2(){

    }

    public Page_Number_2(int index) {
        this.pageNumber = index;
    }

    public int getIndex() {
        return pageNumber;
    }
}
