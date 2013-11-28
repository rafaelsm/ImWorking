package br.com.rads.imworking.model;

import android.text.format.Time;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rafael_2 on 26/11/13.
 */
public class DummyChecks {

    public static List<Check> getDummyChecks() {
        List<Check> list = new ArrayList<Check>();

        for (int i = 0; i <= 4; i++) {

            Check c = new Check(new Time());
            c.setCheckOut(new Time());
            list.add(c);
        }

        return list;
    }

}
