package br.unb.cic.reminders.view;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import br.unb.cic.reminders.controller.ReminderFilter;
import br.unb.cic.reminders2.R;

public class ReminderFilterArrayAdapter extends ArrayAdapter<ReminderFilter> {
	public ReminderFilterArrayAdapter(Context context, List<ReminderFilter> objects) {
		super(context, 
				//#if staticCategory || manageCategory || priority
				R.layout.category_row,
				//#else
				R.layout.reminder_row,
				//#endif
				objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout filterRow;
		if (convertView == null) {
			filterRow = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi;
			vi = (LayoutInflater) getContext().getSystemService(inflater);
			vi.inflate(
					//#if staticCategory || manageCategory || priority
					R.layout.category_row,
					//#else
					R.layout.reminder_row,
					//#endif
					filterRow, true);
		} else {
			filterRow = (LinearLayout) convertView;
		}
		
		//#if staticCategory || manageCategory || priority
	    TextView tvFilter = (TextView) filterRow.findViewById(R.id.row_categoryName); 
	    tvFilter.setText(getItem(position).getName()); 
	 
	    TextView tvNumReminders = (TextView) filterRow.findViewById(R.id.row_categoryCounter); 
	    tvNumReminders.setText(Integer.toString(getItem(position).getNumReminders())); 
	    //#else
	    TextView tvReminder = (TextView) filterRow.findViewById(R.id.txtReminder);
		tvReminder.setText(getItem(position).getName());
		//#endif
		
		return filterRow;
	}
}