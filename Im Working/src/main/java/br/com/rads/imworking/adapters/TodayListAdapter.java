package br.com.rads.imworking.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.rads.imworking.fragment.TodayFragment;
import br.com.rads.imworking.model.Check;

/**
 * Created by rafael_2 on 26/11/13.
 */
public class TodayListAdapter extends ArrayAdapter<Check> {

    private final Activity context;
    private final List<Check> checks;

    public TodayListAdapter(Activity context, List<Check> checks) {
        super(context, R.layout.row_today, checks);

        this.context = context;
        this.checks = checks;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;

        if (rowView == null) {

            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.row_today, null, false);

            ViewHolder holder = new ViewHolder();
            holder.checkin = (TextView) rowView.findViewById(R.id.row_checkin);
            holder.checkout = (TextView) rowView.findViewById(R.id.row_checkout);
            holder.checkinHour = (TextView) rowView.findViewById(R.id.row_checkin_time);
            holder.checkoutHour = (TextView) rowView.findViewById(R.id.row_checkout_time);

            rowView.setTag(holder);

        }

        ViewHolder viewHolder = (ViewHolder) rowView.getTag();

        Check c = this.checks.get(position);
        if (c.getCheckIn() != null) {
            viewHolder.checkin.setText("Checked-in");
            viewHolder.checkinHour.setText(c.getCheckIn().format(TodayFragment.TIME_PATTERN));
        }

        if (c.getCheckOut() != null) {
            viewHolder.checkout.setText("Checked-out");
            viewHolder.checkoutHour.setText(c.getCheckOut().format(TodayFragment.TIME_PATTERN));
        } else {
            viewHolder.checkout.setText("");
            viewHolder.checkoutHour.setText("");
        }

        return rowView;

    }

    private static class ViewHolder {
        public TextView checkin;
        public TextView checkout;
        public TextView checkinHour;
        public TextView checkoutHour;
    }
}