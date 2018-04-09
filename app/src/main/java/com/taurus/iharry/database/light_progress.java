package com.taurus.iharry.database;

import com.orm.SugarRecord;

/**
 * Created by taurus on 16/10/8.
 */
public class light_progress extends SugarRecord {
    private int progress;

    public int getProgress() {
        return progress;
    }

    public light_progress() {

    }

    public light_progress(int progress) {

        this.progress = progress;
    }
}
