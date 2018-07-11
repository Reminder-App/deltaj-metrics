package br.unb.cic.reminders;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import br.unb.cic.reminders.view.AddReminderActivity;
import br.unb.cic.reminders.view.ReminderListFragment;
import br.unb.cic.reminders2.R;
//#if staticCategory || manageCategory || priority
import br.unb.cic.reminders.view.FilterListFragment;
//#endif
//#ifdef search 
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.widget.EditText;
//#endif 

public class ReminderMainActivity extends Activity {
	private static String TAG = "Reminder";
	private FragmentTransaction ft;
	private ReminderListFragment listReminderFragment;
	//#ifdef search
	public static String search = "";
	//#endif

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reminders_main_activity);
		createUI();
	}

	private void createUI() {
		ft = getFragmentManager().beginTransaction();
		listReminderFragment = new ReminderListFragment();
		ft.add(R.id.listReminders, listReminderFragment);
		//#if staticCategory || manageCategory || priority
		FilterListFragment listCategoryFragment = new FilterListFragment();
		listCategoryFragment.addListener(listReminderFragment);
		ft.add(R.id.listCategories, listCategoryFragment);
		//#endif
		ft.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_bar_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_addReminder:
			Intent reminderIntent = new Intent(getApplicationContext(), AddReminderActivity.class);
			startActivity(reminderIntent);
			return true;
		//#ifdef search
		case R.id.menu_searchReminder:
			searchReminderDialog(this);
	    //#endif
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	//#ifdef search
	public void searchReminderDialog(final Context context) {
		final EditText etBusca = new EditText(this);

		new AlertDialog.Builder(this).setTitle("Search for a reminder")
				.setView(etBusca).setCancelable(true)
				.setPositiveButton("Search", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String text = etBusca.getText().toString();
						ReminderMainActivity.search = text;
						listReminderFragment.updateListViewFilter(text);
					}

				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						ReminderMainActivity.search = null;
						listReminderFragment.updateListView(null);
					}
				}).show();

	}
	//#endif
}