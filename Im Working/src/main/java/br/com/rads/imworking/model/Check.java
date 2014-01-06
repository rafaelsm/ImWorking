package br.com.rads.imworking.model;

import android.text.format.Time;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rafael_2 on 26/11/13.
 */
public class Check {

    private Time checkIn;
    private Time checkOut;

    public Check(Time checkIn) {
        this.checkIn = checkIn;
    }

    public Time getCheckIn() {
        return checkIn;
    }

    public Time getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(Time checkOut) {
        this.checkOut = checkOut;
    }

    public long differenceBetweenInAndOut() {

        if (this.checkOut != null) {
            long in = this.checkIn.toMillis(false);
            long out = this.checkOut.toMillis(false);
            long difference = out - in;

            return difference;
        }

        return 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (checkIn != null) {
            sb.append("CheckIn: " + this.checkIn.format("%H:%M:%S"));
        }

        if (checkOut != null) {
            sb.append(" - CheckOut:" + this.checkOut.format("%H:%M:%S"));
        }

        return sb.toString();
    }

    public JSONObject jsonValue() throws JSONException {

        JSONObject o = new JSONObject();
        o.put("checkin", this.checkIn.toMillis(false));

        if (checkOut != null)
            o.put("checkout", this.checkOut.toMillis(false));

        return o;

    }

    public static Check valueOf(JSONObject o) throws JSONException {

        Time checkIn = new Time();
        checkIn.set(o.getLong("checkin"));

        Check check = new Check(checkIn);

        if (o.has("checkout")) {
            Time checkout = new Time();
            checkout.set(o.getLong("checkout"));

            check.setCheckOut(checkout);
        }

        return check;
    }

    public String getFileName(){
        return this.getCheckIn().year + "-" + this.getCheckIn().month;
    }

    public Day getDay(){
        return new Day(this.checkOut);
    }
}
