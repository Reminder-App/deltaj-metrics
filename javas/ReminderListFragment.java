package br.unb.cic.reminders.view;

import java.util.ArrayList;
//#if fixedDate || dateRange
import java.util.Calendar;
import java.util.GregorianCalendar;
//#endif
import java.util.List;
import util.Utility;
import android.app.Fragment;
//#if staticCategory || manageCategory || priority
import android.app.FragmentTransaction;
//#endif
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import br.unb.cic.framework.persistence.DBException;
import br.unb.cic.reminders.controller.AllRemindersFilter;
import br.unb.cic.reminders.controller.Controller;
import br.unb.cic.reminders.controller.ReminderFilter;
import br.unb.cic.reminders.model.Reminder;
import br.unb.cic.reminders2.R;

public class ReminderListFragment extends Fragment implements FiltersListChangeListener {
	private static String TAG = "reminder fragment list";
	// #if fixedDate || dateRange
	private ListView lvReminderLate, lvReminderToday, lvReminderNextDays;
	// #endif
	// #ifdef fixedDate
	private ListView lvReminderNoDate;
	// #endif
	// #ifdef dateRepeat
	private ListView lvMonday, lvTuesday, lvWednesday, lvThursday, lvFriday, lvSaturday, lvSunday;
	// #endif
	private ReminderArrayAdapter adapter;
	private ReminderArrayAdapter contextMenuAdapter;
	private View view;

	@Override
	public View onCreateView(android.view.LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		view = inflater.inflate(R.layout.reminders_list_fragment, container, false);
		createUI();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		updateListView(null);
	}

	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		ListView view = (ListView) v;
		contextMenuAdapter = (ReminderArrayAdapter) view.getAdapter();
		menu.setHeaderTitle(R.string.context_menu_reminder_title);
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.reminder_list_fragment_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getGroupId() == R.id.context_menu_reminder) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			Reminder reminder = (Reminder) contextMenuAdapter.getItem(info.position);
			switch (item.getItemId()) {
			case R.id.edit:
				Intent editIntent = editIntent(reminder);
				editIntent.putExtra("id", reminder.getId());
				editIntent.putExtra("text", reminder.getText());
				editIntent.putExtra("details", reminder.getDetails());
				startActivity(editIntent);
				updateListView(null);
				return true;
			case R.id.delete:
				try {
					Controller.instance(getActivity().getApplicationContext()).deleteReminder(reminder);
				} catch (DBException e) {
					Log.e(TAG, e.getMessage());
				}
				//#if staticCategory || manageCategory || priority
				reloadFilterListFragment();
				//#endif
				updateListView(null);
				return true;
			default:
				return super.onContextItemSelected(item);
			}
		}
		return super.onContextItemSelected(item);
	}

	//#if staticCategory || manageCategory || priority
	public void reloadFilterListFragment() {
		Fragment currentFragment = getActivity().getFragmentManager().findFragmentById(R.id.listCategories);
		if (currentFragment instanceof FilterListFragment) {
			FragmentTransaction fragTransaction = (getActivity()).getFragmentManager().beginTransaction();
			fragTransaction.detach(currentFragment);
			fragTransaction.attach(currentFragment);
			fragTransaction.commit();
		}
	}
	//#endif

	private Intent editIntent(Reminder reminder) {
		Intent editIntent = new Intent(getActivity().getApplicationContext(), EditReminderActivity.class);
		// #ifdef fixedDate
		editIntent.putExtra("date", reminder.getDate());
		// #endif
		// #if fixedDate || dateRepeat
		editIntent.putExtra("hour", reminder.getHour());
		// #endif
		// #ifdef dateRange
		editIntent.putExtra("dateStart", reminder.getDateStart());
		editIntent.putExtra("hourStart", reminder.getHourStart());
		editIntent.putExtra("dateFinal", reminder.getDateFinal());
		editIntent.putExtra("hourFinal", reminder.getHourFinal());
		// #endif
		// #ifdef dateRepeat
		editIntent.putExtra("monday", reminder.isMonday());
		editIntent.putExtra("tuesday", reminder.isTuesday());
		editIntent.putExtra("wednesday", reminder.isWednesday());
		editIntent.putExtra("thursday", reminder.isThursday());
		editIntent.putExtra("friday", reminder.isFriday());
		editIntent.putExtra("saturday", reminder.isSaturday());
		editIntent.putExtra("sunday", reminder.isSunday());
		// #endif
		// #if staticCategory || manageCategory
		editIntent.putExtra("category_name", reminder.getCategory().getName());
		editIntent.putExtra("category_id", Long.toString(reminder.getCategory().getId()));
		// #endif
		// #ifdef priority
		editIntent.putExtra("priority", Integer.toString(reminder.getPriority()));
		// #endif
		return editIntent;
	}

	public void createUI() {
		// #if fixedDate || dateRange
		lvReminderLate = (ListView) view.findViewById(R.id.lvRemindersLate);
		lvReminderToday = (ListView) view.findViewById(R.id.lvRemindersToday);
		lvReminderNextDays = (ListView) view.findViewById(R.id.lvRemindersNextDays);
		// #endif
		// #ifdef fixedDate
		lvReminderNoDate = (ListView) view.findViewById(R.id.lvRemindersNoDate);
		// #endif
		// #ifdef dateRepeat
		lvMonday = (ListView) view.findViewById(R.id.lvMonday);
		lvTuesday = (ListView) view.findViewById(R.id.lvTuesday);
		lvWednesday = (ListView) view.findViewById(R.id.lvWednesday);
		lvThursday = (ListView) view.findViewById(R.id.lvThursday);
		lvFriday = (ListView) view.findViewById(R.id.lvFriday);
		lvSaturday = (ListView) view.findViewById(R.id.lvSaturday);
		lvSunday = (ListView) view.findViewById(R.id.lvSunday);
		// #endif
		updateListView(null);
		// #if fixedDate || dateRange
		registerForContextMenu(lvReminderLate);
		registerForContextMenu(lvReminderToday);
		registerForContextMenu(lvReminderNextDays);
		// #endif
		// #ifdef fixedDate
		registerForContextMenu(lvReminderNoDate);
		// #endif
		// #ifdef dateRepeat
		registerForContextMenu(lvMonday);
		registerForContextMenu(lvTuesday);
		registerForContextMenu(lvWednesday);
		registerForContextMenu(lvThursday);
		registerForContextMenu(lvFriday);
		registerForContextMenu(lvSaturday);
		registerForContextMenu(lvSunday);
		// #endif
	}

	public void updateListView(ReminderFilter filter) {
		if (filter == null)
			filter = new AllRemindersFilter(getActivity());
		adapter = new ReminderArrayAdapter(getActivity().getApplicationContext(), filter.getReminderList());
		// #if fixedDate || dateRange
		ReminderArrayAdapter adapterLate, adapterToday, adapterNextDays;
		// #endif
		// #ifdef fixedDate
		ReminderArrayAdapter adapterNoDate;
		// #endif
		// #ifdef dateRepeat
		ReminderArrayAdapter adapterMonday, adapterTuesday, adapterWednesday, adapterThursday, adapterFriday,
				adapterSaturday, adapterSunday;
		// #endif
		Reminder r = new Reminder();
		// #if fixedDate || dateRange
		List<Reminder> remindersLate = new ArrayList<Reminder>();
		List<Reminder> remindersToday = new ArrayList<Reminder>();
		List<Reminder> remindersNextDays = new ArrayList<Reminder>();
		// #endif
		// #ifdef fixedDate
		List<Reminder> remindersNoDate = new ArrayList<Reminder>();
		// #endif
		// #ifdef dateRepeat
		List<Reminder> remindersModay = new ArrayList<Reminder>();
		List<Reminder> remindersTuesday = new ArrayList<Reminder>();
		List<Reminder> remindersWednesday = new ArrayList<Reminder>();
		List<Reminder> remindersThursday = new ArrayList<Reminder>();
		List<Reminder> remindersFriday = new ArrayList<Reminder>();
		List<Reminder> remindersSaturday = new ArrayList<Reminder>();
		List<Reminder> remindersSunday = new ArrayList<Reminder>();
		// #endif

		for (int i = 0; i < adapter.getCount(); ++i) {
			r = adapter.getItem(i);
			// #ifdef fixedDate
			if (r.getDate() != null) {
				String day = r.getDate().substring(0, 2);
				String month = r.getDate().substring(3, 5);
				String year = r.getDate().substring(6, 10);

				Calendar cal = Calendar.getInstance();
				GregorianCalendar gc = new GregorianCalendar();
				if (r.getHour() != null) {
					String hour = r.getHour().substring(0, 2);
					String min = r.getHour().substring(3, 5);
					gc.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day),
							Integer.parseInt(hour), Integer.parseInt(min));
				} else {
					gc.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
				}
				if (gc.before(cal))
					remindersLate.add(r);
				else if ((cal.get(Calendar.YEAR) == gc.get(Calendar.YEAR))
						&& (cal.get(Calendar.MONTH) == gc.get(Calendar.MONTH))
						&& (cal.get(Calendar.DAY_OF_MONTH) == gc.get(Calendar.DAY_OF_MONTH)))
					remindersToday.add(r);
				else
					remindersNextDays.add(r);
			} else {
				remindersNoDate.add(r);
			}
			// #endif
			// #ifdef dateRange
			if (r.getDateFinal() != null && r.getDateStart() != null) {
				String dayStart = r.getDateStart().substring(0, 2);
				String monthStart = r.getDateStart().substring(3, 5);
				String yearStart = r.getDateStart().substring(6, 10);
				String dayFinal = r.getDateFinal().substring(0, 2);
				String monthFinal = r.getDateFinal().substring(3, 5);
				String yearFinal = r.getDateFinal().substring(6, 10);

				Calendar cal = Calendar.getInstance();
				GregorianCalendar gc = new GregorianCalendar();
				GregorianCalendar gc2 = new GregorianCalendar();
				if (r.getHourFinal() != null) {
					String hour = r.getHourFinal().substring(0, 2);
					String min = r.getHourFinal().substring(3, 5);
					gc.set(Integer.parseInt(yearFinal), Integer.parseInt(monthFinal) - 1, Integer.parseInt(dayFinal),
							Integer.parseInt(hour), Integer.parseInt(min));
				} else {
					gc.set(Integer.parseInt(yearFinal), Integer.parseInt(monthFinal) - 1, Integer.parseInt(dayFinal));
				}
				if (r.getHourStart() != null) {
					String hour = r.getHourStart().substring(0, 2);
					String min = r.getHourStart().substring(3, 5);
					gc2.set(Integer.parseInt(yearStart), Integer.parseInt(monthStart) - 1, Integer.parseInt(dayStart),
							Integer.parseInt(hour), Integer.parseInt(min));
				} else {
					gc2.set(Integer.parseInt(yearStart), Integer.parseInt(monthStart) - 1, Integer.parseInt(dayStart));
				}
				if (gc.before(cal))
					remindersLate.add(r);
				else if (gc.after(cal) && gc2.before(cal))
					remindersToday.add(r);
				else
					remindersNextDays.add(r);
			}
			// #endif
			// #ifdef dateRepeat
			if (r.isMonday())
				remindersModay.add(r);
			if (r.isTuesday())
				remindersTuesday.add(r);
			if (r.isWednesday())
				remindersWednesday.add(r);
			if (r.isThursday())
				remindersThursday.add(r);
			if (r.isFriday())
				remindersFriday.add(r);
			if (r.isSaturday())
				remindersSaturday.add(r);
			if (r.isSunday())
				remindersSunday.add(r);
			// #endif
		}

		// #if fixedDate || dateRange
		adapterLate = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersLate,
				Color.rgb(0xED, 0x1C, 0x24), ReminderArrayAdapter.LATE);
		adapterToday = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersToday,
				Color.rgb(0x33, 0xB5, 0xE5), ReminderArrayAdapter.TODAY);
		adapterNextDays = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersNextDays,
				Color.rgb(0x99, 0x99, 0x99), ReminderArrayAdapter.NEXT_DAYS);
		lvReminderLate.setAdapter(adapterLate);
		Utility.setListViewHeightBasedOnChildren(lvReminderLate);
		lvReminderToday.setAdapter(adapterToday);
		Utility.setListViewHeightBasedOnChildren(lvReminderToday);
		lvReminderNextDays.setAdapter(adapterNextDays);
		Utility.setListViewHeightBasedOnChildren(lvReminderNextDays);
		// #endif
		// #ifdef fixedDate
		adapterNoDate = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersNoDate,
				Color.rgb(0x00, 0x00, 0x00), ReminderArrayAdapter.NO_DATE);
		lvReminderNoDate.setAdapter(adapterNoDate);
		Utility.setListViewHeightBasedOnChildren(lvReminderNoDate);
		// #endif
		// #ifdef dateRepeat
		adapterMonday = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersModay,
				Color.rgb(0, 0, 0), ReminderArrayAdapter.MONDAY);
		adapterTuesday = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersTuesday,
				Color.rgb(0, 0, 255), ReminderArrayAdapter.TUESDAY);
		adapterWednesday = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersWednesday,
				Color.rgb(210, 105, 30), ReminderArrayAdapter.WEDNESDAY);
		adapterThursday = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersThursday,
				Color.rgb(0, 128, 0), ReminderArrayAdapter.THURSDAY);
		adapterFriday = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersFriday,
				Color.rgb(255, 20, 147), ReminderArrayAdapter.FRIDAY);
		adapterSaturday = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersSaturday,
				Color.rgb(255, 0, 0), ReminderArrayAdapter.SATURDAY);
		adapterSunday = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersSunday,
				Color.rgb(128, 0, 128), ReminderArrayAdapter.SUNDAY);

		lvMonday.setAdapter(adapterMonday);
		Utility.setListViewHeightBasedOnChildren(lvMonday);
		lvTuesday.setAdapter(adapterTuesday);
		Utility.setListViewHeightBasedOnChildren(lvTuesday);
		lvWednesday.setAdapter(adapterWednesday);
		Utility.setListViewHeightBasedOnChildren(lvWednesday);
		lvThursday.setAdapter(adapterThursday);
		Utility.setListViewHeightBasedOnChildren(lvThursday);
		lvFriday.setAdapter(adapterFriday);
		Utility.setListViewHeightBasedOnChildren(lvFriday);
		lvSaturday.setAdapter(adapterSaturday);
		Utility.setListViewHeightBasedOnChildren(lvSaturday);
		lvSunday.setAdapter(adapterSunday);
		Utility.setListViewHeightBasedOnChildren(lvSunday);
		// #endif
	}

	public void onSelectedFilterChanged(ReminderFilter filter) {
		updateListView(filter);
	}

	// #ifdef search
	public void updateListViewFilter(String filterRemider) {

		ReminderFilter filter = new AllRemindersFilter(getActivity());
		ArrayList<Reminder> reminders = new ArrayList<Reminder>();
		for (Reminder item : filter.getReminderList()) {

			if (item.getText().contains(filterRemider)) {
				reminders.add(item);
			}
		}

		adapter = new ReminderArrayAdapter(getActivity().getApplicationContext(), reminders);
		// #if fixedDate || dateRange
		ReminderArrayAdapter adapterLate, adapterToday, adapterNextDays;
		// #endif
		// #ifdef fixedDate
		ReminderArrayAdapter adapterNoDate;
		// #endif
		// #ifdef dateRepeat
		ReminderArrayAdapter adapterMonday, adapterTuesday, adapterWednesday, adapterThursday, adapterFriday,
				adapterSaturday, adapterSunday;
		// #endif
		Reminder r = new Reminder();
		// #if fixedDate || dateRange
		List<Reminder> remindersLate = new ArrayList<Reminder>();
		List<Reminder> remindersToday = new ArrayList<Reminder>();
		List<Reminder> remindersNextDays = new ArrayList<Reminder>();
		// #endif
		// #ifdef fixedDate
		List<Reminder> remindersNoDate = new ArrayList<Reminder>();
		// #endif
		// #ifdef dateRepeat
		List<Reminder> remindersModay = new ArrayList<Reminder>();
		List<Reminder> remindersTuesday = new ArrayList<Reminder>();
		List<Reminder> remindersWednesday = new ArrayList<Reminder>();
		List<Reminder> remindersThursday = new ArrayList<Reminder>();
		List<Reminder> remindersFriday = new ArrayList<Reminder>();
		List<Reminder> remindersSaturday = new ArrayList<Reminder>();
		List<Reminder> remindersSunday = new ArrayList<Reminder>();
		// #endif

		for (int i = 0; i < adapter.getCount(); ++i) {
			r = adapter.getItem(i);
			// #ifdef fixedDate
			if (r.getDate() != null) {
				String day = r.getDate().substring(0, 2);
				String month = r.getDate().substring(3, 5);
				String year = r.getDate().substring(6, 10);

				Calendar cal = Calendar.getInstance();
				GregorianCalendar gc = new GregorianCalendar();
				if (r.getHour() != null) {
					String hour = r.getHour().substring(0, 2);
					String min = r.getHour().substring(3, 5);
					gc.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day),
							Integer.parseInt(hour), Integer.parseInt(min));
				} else {
					gc.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
				}
				if (gc.before(cal))
					remindersLate.add(r);
				else if ((cal.get(Calendar.YEAR) == gc.get(Calendar.YEAR))
						&& (cal.get(Calendar.MONTH) == gc.get(Calendar.MONTH))
						&& (cal.get(Calendar.DAY_OF_MONTH) == gc.get(Calendar.DAY_OF_MONTH)))
					remindersToday.add(r);
				else
					remindersNextDays.add(r);
			} else {
				remindersNoDate.add(r);
			}
			// #endif
			// #ifdef dateRange
			if (r.getDateFinal() != null && r.getDateStart() != null) {
				String dayStart = r.getDateStart().substring(0, 2);
				String monthStart = r.getDateStart().substring(3, 5);
				String yearStart = r.getDateStart().substring(6, 10);
				String dayFinal = r.getDateFinal().substring(0, 2);
				String monthFinal = r.getDateFinal().substring(3, 5);
				String yearFinal = r.getDateFinal().substring(6, 10);

				Calendar cal = Calendar.getInstance();
				GregorianCalendar gc = new GregorianCalendar();
				GregorianCalendar gc2 = new GregorianCalendar();
				if (r.getHourFinal() != null) {
					String hour = r.getHourFinal().substring(0, 2);
					String min = r.getHourFinal().substring(3, 5);
					gc.set(Integer.parseInt(yearFinal), Integer.parseInt(monthFinal) - 1, Integer.parseInt(dayFinal),
							Integer.parseInt(hour), Integer.parseInt(min));
				} else {
					gc.set(Integer.parseInt(yearFinal), Integer.parseInt(monthFinal) - 1, Integer.parseInt(dayFinal));
				}
				if (r.getHourStart() != null) {
					String hour = r.getHourStart().substring(0, 2);
					String min = r.getHourStart().substring(3, 5);
					gc2.set(Integer.parseInt(yearStart), Integer.parseInt(monthStart) - 1, Integer.parseInt(dayStart),
							Integer.parseInt(hour), Integer.parseInt(min));
				} else {
					gc2.set(Integer.parseInt(yearStart), Integer.parseInt(monthStart) - 1, Integer.parseInt(dayStart));
				}
				if (gc.before(cal))
					remindersLate.add(r);
				else if (gc.after(cal) && gc2.before(cal))
					remindersToday.add(r);
				else
					remindersNextDays.add(r);
			}
			// #endif
			// #ifdef dateRepeat
			if (r.isMonday())
				remindersModay.add(r);
			if (r.isTuesday())
				remindersTuesday.add(r);
			if (r.isWednesday())
				remindersWednesday.add(r);
			if (r.isThursday())
				remindersThursday.add(r);
			if (r.isFriday())
				remindersFriday.add(r);
			if (r.isSaturday())
				remindersSaturday.add(r);
			if (r.isSunday())
				remindersSunday.add(r);
			// #endif
		}

		// #if fixedDate || dateRange
		adapterLate = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersLate,
				Color.rgb(0xED, 0x1C, 0x24), ReminderArrayAdapter.LATE);
		adapterToday = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersToday,
				Color.rgb(0x33, 0xB5, 0xE5), ReminderArrayAdapter.TODAY);
		adapterNextDays = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersNextDays,
				Color.rgb(0x99, 0x99, 0x99), ReminderArrayAdapter.NEXT_DAYS);
		lvReminderLate.setAdapter(adapterLate);
		Utility.setListViewHeightBasedOnChildren(lvReminderLate);
		lvReminderToday.setAdapter(adapterToday);
		Utility.setListViewHeightBasedOnChildren(lvReminderToday);
		lvReminderNextDays.setAdapter(adapterNextDays);
		Utility.setListViewHeightBasedOnChildren(lvReminderNextDays);
		// #endif
		// #ifdef fixedDate
		adapterNoDate = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersNoDate,
				Color.rgb(0x00, 0x00, 0x00), ReminderArrayAdapter.NO_DATE);
		lvReminderNoDate.setAdapter(adapterNoDate);
		Utility.setListViewHeightBasedOnChildren(lvReminderNoDate);
		// #endif
		// #ifdef dateRepeat
		adapterMonday = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersModay,
				Color.rgb(0, 0, 0), ReminderArrayAdapter.MONDAY);
		adapterTuesday = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersTuesday,
				Color.rgb(0, 0, 255), ReminderArrayAdapter.TUESDAY);
		adapterWednesday = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersWednesday,
				Color.rgb(210, 105, 30), ReminderArrayAdapter.WEDNESDAY);
		adapterThursday = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersThursday,
				Color.rgb(0, 128, 0), ReminderArrayAdapter.THURSDAY);
		adapterFriday = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersFriday,
				Color.rgb(255, 20, 147), ReminderArrayAdapter.FRIDAY);
		adapterSaturday = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersSaturday,
				Color.rgb(255, 0, 0), ReminderArrayAdapter.SATURDAY);
		adapterSunday = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersSunday,
				Color.rgb(128, 0, 128), ReminderArrayAdapter.SUNDAY);

		lvMonday.setAdapter(adapterMonday);
		Utility.setListViewHeightBasedOnChildren(lvMonday);
		lvTuesday.setAdapter(adapterTuesday);
		Utility.setListViewHeightBasedOnChildren(lvTuesday);
		lvWednesday.setAdapter(adapterWednesday);
		Utility.setListViewHeightBasedOnChildren(lvWednesday);
		lvThursday.setAdapter(adapterThursday);
		Utility.setListViewHeightBasedOnChildren(lvThursday);
		lvFriday.setAdapter(adapterFriday);
		Utility.setListViewHeightBasedOnChildren(lvFriday);
		lvSaturday.setAdapter(adapterSaturday);
		Utility.setListViewHeightBasedOnChildren(lvSaturday);
		lvSunday.setAdapter(adapterSunday);
		Utility.setListViewHeightBasedOnChildren(lvSunday);
		// #endif
	}
	// #endif
}
