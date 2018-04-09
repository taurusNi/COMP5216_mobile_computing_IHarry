package com.taurus.iharry.database;

import com.orm.SugarRecord;

/**
 * Created by taurus on 16/10/12.
 */
public class Speed1 extends SugarRecord {
        private int progress;

        public int getProgress() {
            return progress;
        }

        public Speed1() {

        }

        public Speed1(int progress) {

            this.progress = progress;
        }
    }


