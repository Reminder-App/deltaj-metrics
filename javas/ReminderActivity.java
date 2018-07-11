package br.unb.cic.reminders.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import br.unb.cic.reminders.model.InvalidDateException;
import br.unb.cic.reminders.model.InvalidTextException;
import br.unb.cic.reminders.model.Reminder;
import br.unb.cic.reminders2.R;
//#if staticCategory || manageCategory
import br.unb.cic.reminders.model.Category;
import br.unb.cic.reminders.controller.Controller;
//#endif
//#ifdef priority
import br.unb.cic.reminders.model.Priority;
import java.util.Arrays;
//#endif
//#ifdef googleCalendar
import br.unb.cic.reminders.calendar.CalendarEventCreator;
import br.unb.cic.reminders.model.CalendarNotFoundException;
//#endif
//#if googleCalendar || dateRepeat
import android.widget.CheckBox;
//#endif
//#ifdef dateRepeat
import br.unb.cic.reminders.model.InvalidDaysException;
//#endif 

public abstract class ReminderActivity extends Activity {
	protected Reminder reminder;
	//#ifdef fixedDate
	protected Calendar date;
	protected Spinner spinnerDate;
	protected EditText edtDate;
	//#endif
	//#if fixedDate || dateRepeat
	protected Calendar time;
	protected EditText edtTime;
	protected Spinner spinnerTime;
	//#endif
	//#ifdef dateRange
	protected Calendar dateStart, timeStart, dateFinal, timeFinal;
	protected Spinner spinnerDateStart, spinnerTimeStart, spinnerDateFinal, spinnerTimeFinal;
	//#endif
	protected EditText edtReminder, edtDetails;
	private Button btnSave, btnCancel;
	//#ifdef googleCalendar
	private CalendarEventCreator creator;
	private CheckBox cbCalendar;
	//#endif
	//#ifdef dateRepeat
	protected CheckBox cbMonday, cbTuesday, cbWednesday, cbThursday, cbFriday, cbSaturday, cbSunday;
	//#endif

	//#if staticCategory || manageCategory
	protected Spinner spinnerCategory;

	private void addListenerToSpinnerCategory() {
		spinnerCategory.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<? extends Object> parent, View view, int pos, long id) {
				//#ifdef manageCategory
				if (pos == (spinnerCategory.getCount() - 1)) {
					DialogFragment newFragment = AddCategoryDialogFragment.newInstance(spinnerCategory);
					newFragment.show(getFragmentManager(), "" + R.string.dialog_addcategory_title);
				}
				//#endif
			}

			public void onNothingSelected(AdapterView<? extends Object> parent) {

			}
		});
	}

	private Spinner getSpinnerCategory() throws Exception {
		Spinner spinner = (Spinner) findViewById(R.id.spinnerCategories);

		SpinnerAdapterGenerator<Category> adapterCategoryGenerator = new SpinnerAdapterGenerator<Category>();

		List<Category> categories = getCategories();

		ArrayAdapter<Category> adapter = adapterCategoryGenerator.getSpinnerAdapter(categories, this);

		spinner.setAdapter(adapter);

		//#ifdef manageCategory
		Category temp = new Category();
		temp.setName("+ Category");
		adapter.add(temp);
		//#endif

		return spinner;
	}

	protected List<Category> getCategories() throws Exception {
		return Controller.instance(getApplicationContext()).listCategories();
	}

	protected Spinner getSpinnerCategory(List<Category> categories) throws Exception {
		Spinner spinner = (Spinner) findViewById(R.id.spinnerCategories);

		SpinnerAdapterGenerator<Category> adapterCategoryGenerator = new SpinnerAdapterGenerator<Category>();

		spinner.setAdapter(adapterCategoryGenerator.getSpinnerAdapter(categories, this));

		return spinner;
	}
	//#endif

	//#ifdef priority
	private Priority selectedPriority;
	protected Spinner spinnerPriority;

	private void addListenerToSpinnerPriority() {
		spinnerPriority.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<? extends Object> parent, View view, int pos, long id) {
				selectedPriority = (Priority) parent.getItemAtPosition(pos);
			}

			public void onNothingSelected(AdapterView<? extends Object> parent) {

			}
		});
	}

	private Spinner getSpinnerPriority() {
		Spinner spinner = (Spinner) findViewById(R.id.spinnerPriorities);

		SpinnerAdapterGenerator<Priority> adapterPriorityGenerator = new SpinnerAdapterGenerator<Priority>();

		List<Priority> priorityValues = Arrays.asList(Priority.values());

		ArrayAdapter<Priority> priorityArrayAdapter = adapterPriorityGenerator.getSpinnerAdapter(priorityValues, this);

		spinner.setAdapter(priorityArrayAdapter);

		spinner.setSelection(Priority.NORMAL.getCode());

		return spinner;
	}
	//#endif

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reminder_add);
		if (reminder == null)
			reminder = new Reminder();
		initializeFields();
		initializeListeners();
		initializeValues();
	}

	private void initializeFields() {
		btnSave = (Button) findViewById(R.id.btnSave);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		edtReminder = (EditText) findViewById(R.id.edtReminder);
		edtDetails = (EditText) findViewById(R.id.edtDetails);
		//#ifdef fixedDate
		spinnerDate = getSpinnerDate();
		//#endif
		//#if fixedDate || dateRepeat
		spinnerTime = getSpinnerTime();
		//#endif
		//#ifdef dateRange
		spinnerDateStart = getSpinnerDateStart();
		spinnerTimeStart = getSpinnerTimeStart();
		spinnerDateFinal = getSpinnerDateFinal();
		spinnerTimeFinal = getSpinnerTimeFinal();
		//#endif
		//#ifdef dateRepeat
		cbMonday = (CheckBox) findViewById(R.id.cbMonday);
		cbTuesday = (CheckBox) findViewById(R.id.cbTuesday);
		cbWednesday = (CheckBox) findViewById(R.id.cbWednesday);
		cbThursday = (CheckBox) findViewById(R.id.cbThursday);
		cbFriday = (CheckBox) findViewById(R.id.cbFriday);
		cbSaturday = (CheckBox) findViewById(R.id.cbSaturday);
		cbSunday = (CheckBox) findViewById(R.id.cbSunday);
		//#endif
		//#if staticCategory || manageCategory
		try {
			spinnerCategory = getSpinnerCategory();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//#endif
		//#ifdef priority
		spinnerPriority = getSpinnerPriority();
		//#endif
		//#ifdef googleCalendar
		cbCalendar = (CheckBox) findViewById(R.id.cbCalendar);
		//#endif
	}

	private void initializeListeners() {
		addListenerToBtnSave();
		addListenerToBtnCancel();
		//#if fixedDate || dateRange
		addListenerToSpinnerDate();
		//#endif
		addListenerToSpinnerTime();
		//#if staticCategory || manageCategory
		addListenerToSpinnerCategory();
		//#endif
		//#ifdef priority
		addListenerToSpinnerPriority();
		//#endif
	}

	protected abstract void initializeValues();

	private void addListenerToBtnSave() {
		btnSave.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				try {
					createReminder();
					persist(reminder);
					finish();
				} catch (Exception e) {
					Log.e("ReminderActivity", e.getMessage());
					e.printStackTrace();
				}
			}
		});
	}

	private void addListenerToBtnCancel() {
		btnCancel.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}

	//#if fixedDate || dateRange
	private void addListenerToSpinnerDate() {
		//#ifdef fixedDate
		spinnerDate.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				spinnerDate = getSpinnerDate();
				return false;
			}
		});
		spinnerDate.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				spinnerDate = getSpinnerDate();
				return false;
			}
		});
		spinnerDate.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<? extends Object> parent, View view, int pos, long id) {
				switch (pos) {
				case 0:
					date = null;
					break;
				case 1:
					if (date == null)
						date = Calendar.getInstance();
					DialogFragment newFragment = new DatePickerDialogFragment(date, spinnerDate);
					newFragment.show(getFragmentManager(), "datePicker");
					break;
				default:
				}
			}

			public void onNothingSelected(AdapterView<? extends Object> arg0) {
			}
		});
		//#endif

		//#ifdef dateRange
		spinnerDateStart.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				spinnerDateStart = getSpinnerDateStart();
				return false;
			}
		});

		spinnerDateStart.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				spinnerDateStart = getSpinnerDateStart();
				return false;
			}
		});

		spinnerDateStart.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<? extends Object> parent, View view, int pos, long id) {

				switch (pos) {
				case 0:
					dateStart = null;
					break;
				case 1:
					if (dateStart == null)
						dateStart = Calendar.getInstance();
					DialogFragment newFragment = new DatePickerDialogFragment(dateStart, spinnerDateStart);
					newFragment.show(getFragmentManager(), "datePicker");
					break;
				default:
				}
			}

			public void onNothingSelected(AdapterView<? extends Object> arg0) {

			}

		});

		spinnerDateFinal.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				spinnerDateFinal = getSpinnerDateFinal();
				return false;
			}
		});

		spinnerDateFinal.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				spinnerDateFinal = getSpinnerDateFinal();
				return false;
			}
		});

		spinnerDateFinal.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<? extends Object> parent, View view, int pos, long id) {

				switch (pos) {
				case 0:
					dateFinal = null;
					break;
				case 1:
					if (dateFinal == null)
						dateFinal = Calendar.getInstance();
					DialogFragment newFragment = new DatePickerDialogFragment(dateFinal, spinnerDateFinal);
					newFragment.show(getFragmentManager(), "datePicker");
					break;
				default:
				}
			}

			public void onNothingSelected(AdapterView<? extends Object> arg0) {

			}

		});
		//#endif
	}
	//#endif

	private void addListenerToSpinnerTime() {
		//#if fixedDate || dateRepeat
		spinnerTime.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				spinnerTime = getSpinnerTime();
				return false;
			}
		});
		spinnerTime.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				spinnerTime = getSpinnerTime();
				return false;
			}
		});
		spinnerTime.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<? extends Object> parent, View view, int pos, long id) {
				switch (pos) {
				case 0:
					time = null;
					break;
				case 1:
					if (time == null)
						time = Calendar.getInstance();
					DialogFragment newFragment = new TimePickerDialogFragment(time, spinnerTime);
					newFragment.show(getFragmentManager(), "timePicker");
					break;
				default:
				}
			}

			public void onNothingSelected(AdapterView<? extends Object> arg0) {
			}
		});
		//#endif

		//#ifdef dateRange
		spinnerTimeStart.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				spinnerTimeStart = getSpinnerTimeStart();
				return false;
			}
		});

		spinnerTimeStart.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				spinnerTimeStart = getSpinnerTimeStart();
				return false;
			}
		});

		spinnerTimeStart.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<? extends Object> parent, View view, int pos, long id) {

				switch (pos) {
				case 0:
					timeStart = null;
					break;
				case 1:
					if (timeStart == null)
						timeStart = Calendar.getInstance();
					DialogFragment newFragment = new TimePickerDialogFragment(timeStart, spinnerTimeStart);
					newFragment.show(getFragmentManager(), "timePicker");
					break;
				default:
				}
			}

			public void onNothingSelected(AdapterView<? extends Object> arg0) {

			}
		});

		spinnerTimeFinal.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				spinnerTimeFinal = getSpinnerTimeFinal();
				return false;
			}
		});

		spinnerTimeFinal.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				spinnerTimeFinal = getSpinnerTimeFinal();
				return false;
			}
		});

		spinnerTimeFinal.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<? extends Object> parent, View view, int pos, long id) {

				switch (pos) {
				case 0:
					timeFinal = null;
					break;
				case 1:
					if (timeFinal == null)
						timeFinal = Calendar.getInstance();
					DialogFragment newFragment = new TimePickerDialogFragment(timeFinal, spinnerTimeFinal);
					newFragment.show(getFragmentManager(), "timePicker");
					break;
				default:
				}
			}

			public void onNothingSelected(AdapterView<? extends Object> arg0) {

			}
		});
		//#endif
	}

	private void createReminder() {
		try {
			reminder.setText(edtReminder.getText().toString());
			reminder.setDetails(edtDetails.getText().toString());
			setValuesOnReminder();
		} catch (InvalidTextException e) {
			Toast.makeText(getApplicationContext(), "Invalid text.", Toast.LENGTH_SHORT).show();
		} catch (InvalidDateException e) {
			Toast.makeText(getApplicationContext(), "Invalid date.", Toast.LENGTH_SHORT).show();
		} catch (InvalidHourException e) {
			Toast.makeText(getApplicationContext(), "Invalid time.", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "Serious error.", Toast.LENGTH_SHORT).show();
		}
	}

	private void setValuesOnReminder() throws Exception {
		//#ifdef fixedDate
		reminder.setDate(dateToString());
		//#endif
		//#if fixedDate || dateRepeat
		reminder.setHour(timeToString());
		//#endif
		//#ifdef dateRange
		reminder.setDateStart(dateToString(dateStart));
		reminder.setHourStart(timeToString(timeStart));
		reminder.setDateFinal(dateToString(dateFinal));
		reminder.setHourFinal(timeToString(timeFinal));
		//#endif
		//#ifdef dateRepeat
		reminder.setMonday(cbMonday.isChecked());
		reminder.setTuesday(cbTuesday.isChecked());
		reminder.setWednesday(cbWednesday.isChecked());
		reminder.setThursday(cbThursday.isChecked());
		reminder.setFriday(cbFriday.isChecked());
		reminder.setSaturday(cbSaturday.isChecked());
		try {
			reminder.setSunday(cbSunday.isChecked());
		} catch (InvalidDaysException e) {
			Toast.makeText(getApplicationContext(), "At least one day should be checked.", Toast.LENGTH_SHORT).show();
		}
		//#endif
		//#if staticCategory || manageCategory
		reminder.setCategory((Category) spinnerCategory.getSelectedItem());
		//#endif
		//#ifdef priority
		reminder.setPriority(selectedPriority);
		//#endif
		//#ifdef googleCalendar
		if (cbCalendar.isChecked()) {
			creator = new CalendarEventCreator();
			creator.addEventCalendar(reminder, getApplicationContext());
		}
		//#endif
	}

	//#if fixedDate || dateRange
	private String dateToString(
			//#ifdef dateRange
			Calendar date
	//#endif
	) {
		if (date == null)
			return null;
		String sDate;
		sDate = Integer.toString(date.get(Calendar.MONTH) + 1);
		if (date.get(Calendar.MONTH) + 1 < 10)
			sDate = "0" + sDate;
		sDate = Integer.toString(date.get(Calendar.DAY_OF_MONTH)) + "-" + sDate;
		if (date.get(Calendar.DAY_OF_MONTH) < 10)
			sDate = "0" + sDate;
		sDate += "-" + Integer.toString(date.get(Calendar.YEAR));
		return sDate;
	}
	//#endif

	private String timeToString(
			//#ifdef dateRange
			Calendar time
	//#endif
	) {
		if (time == null)
			return null;
		String sTime;
		sTime = Integer.toString(time.get(Calendar.MINUTE));
		if (time.get(Calendar.MINUTE) < 10)
			sTime = "0" + sTime;
		sTime = Integer.toString(time.get(Calendar.HOUR_OF_DAY)) + ":" + sTime;
		if (time.get(Calendar.HOUR_OF_DAY) < 10)
			sTime = "0" + sTime;
		return sTime;
	}

	//#if fixedDate || dateRange
	protected void updateDateFromString(String sDate
	//#ifdef dateRange
			, boolean isFinal
	//#endif
	) {
		if (sDate == null || sDate.equals("")) {
			//#ifdef fixedDate
			date = null;
			//#endif
			//#ifdef dateRange
			if (isFinal)
				dateFinal = null;
			else
				dateStart = null;
			//#endif
			return;
		}
		char sDay[] = { sDate.charAt(0), sDate.charAt(1) };
		int day = Integer.parseInt(new String(sDay), 10);
		char sMonth[] = { sDate.charAt(3), sDate.charAt(4) };
		int month = Integer.parseInt(new String(sMonth), 10);
		char sYear[] = { sDate.charAt(6), sDate.charAt(7), sDate.charAt(8), sDate.charAt(9) };
		int year = Integer.parseInt(new String(sYear), 10);

		//#ifdef fixedDate
		if (date == null)
			date = Calendar.getInstance();
		date.set(year, month - 1, day);
		//#endif

		//#ifdef dateRange
		if (dateFinal == null)
			dateFinal = Calendar.getInstance();
		if (dateStart == null)
			dateStart = Calendar.getInstance();
		if (isFinal)
			dateFinal.set(year, month - 1, day);
		else
			dateStart.set(year, month - 1, day);
		//#endif
	}
	//#endif

	protected void updateTimeFromString(String sTime
	//#ifdef dateRange
			, boolean isFinal
	//#endif
	) {
		if (sTime == null || sTime.equals("")) {
			//#if fixedDate || dateRepeat
			time = null;
			//#endif
			//#ifdef dateRange
			if (isFinal)
				timeFinal = null;
			else
				timeStart = null;
			//#endif
			return;
		}
		char sHour[] = { sTime.charAt(0), sTime.charAt(1) };
		int hour = Integer.parseInt(new String(sHour), 10);
		char sMinute[] = { sTime.charAt(3), sTime.charAt(4) };
		int minute = Integer.parseInt(new String(sMinute), 10);

		//#if fixedDate || dateRepeat
		if (time == null)
			time = Calendar.getInstance();
		time.set(Calendar.MINUTE, minute);
		time.set(Calendar.HOUR_OF_DAY, hour);
		//#endif

		//#ifdef dateRange
		if (timeStart == null)
			timeStart = Calendar.getInstance();
		if (timeFinal == null)
			timeFinal = Calendar.getInstance();
		if (isFinal) {
			timeFinal.set(Calendar.MINUTE, minute);
			timeFinal.set(Calendar.HOUR_OF_DAY, hour);
		} else {
			timeStart.set(Calendar.MINUTE, minute);
			timeStart.set(Calendar.HOUR_OF_DAY, hour);
		}
		//#endif
	}

	@SuppressWarnings("unchecked")
	protected void updateSpinnerDateHour(Spinner spinner, String dateOrHour) {
		if (dateOrHour == null)
			return;
		ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
		int count = adapter.getCount();
		if (count > 2) {
			for (int i = 2; i < count; ++i)
				adapter.remove(adapter.getItem(i));
		}
		adapter.add(dateOrHour);
		spinner.setSelection(2);
	}

	//#ifdef fixedDate
	private Spinner getSpinnerDate() {
		Spinner spinner = (Spinner) findViewById(R.id.spinnerDate);
		SpinnerAdapterGenerator<String> adapterDateGenerator = new SpinnerAdapterGenerator<String>();
		List<String> items = new ArrayList<String>();
		items.add("No date");
		items.add("+ Select");
		spinner.setAdapter(adapterDateGenerator.getSpinnerAdapter(items, this));
		return spinner;
	}
	//#endif

	//#if fixedDate || dateRepeat
	private Spinner getSpinnerTime() {
		Spinner spinner = (Spinner) findViewById(R.id.spinnerTime);
		SpinnerAdapterGenerator<String> adapterTimeGenerator = new SpinnerAdapterGenerator<String>();
		List<String> items = new ArrayList<String>();
		items.add("No time");
		items.add("+ Select");
		spinner.setAdapter(adapterTimeGenerator.getSpinnerAdapter(items, this));
		return spinner;
	}
	//#endif

	//#ifdef dateRange
	private Spinner getSpinnerDateStart() {
		Spinner spinner = (Spinner) findViewById(R.id.spinnerDateStart);

		SpinnerAdapterGenerator<String> adapterDateGenerator = new SpinnerAdapterGenerator<String>();

		List<String> items = new ArrayList<String>();

		items.add("No date");
		items.add("+ Select");

		spinner.setAdapter(adapterDateGenerator.getSpinnerAdapter(items, this));

		return spinner;
	}

	private Spinner getSpinnerTimeStart() {
		Spinner spinner = (Spinner) findViewById(R.id.spinnerTimeStart);

		SpinnerAdapterGenerator<String> adapterTimeGenerator = new SpinnerAdapterGenerator<String>();

		List<String> items = new ArrayList<String>();

		items.add("No time");
		items.add("+ Select");

		spinner.setAdapter(adapterTimeGenerator.getSpinnerAdapter(items, this));

		return spinner;
	}

	private Spinner getSpinnerDateFinal() {
		Spinner spinner = (Spinner) findViewById(R.id.spinnerDateFinal);

		SpinnerAdapterGenerator<String> adapterDateGenerator = new SpinnerAdapterGenerator<String>();

		List<String> items = new ArrayList<String>();

		items.add("No date");
		items.add("+ Select");

		spinner.setAdapter(adapterDateGenerator.getSpinnerAdapter(items, this));

		return spinner;
	}

	private Spinner getSpinnerTimeFinal() {
		Spinner spinner = (Spinner) findViewById(R.id.spinnerTimeFinal);

		SpinnerAdapterGenerator<String> adapterTimeGenerator = new SpinnerAdapterGenerator<String>();

		List<String> items = new ArrayList<String>();

		items.add("No time");
		items.add("+ Select");

		spinner.setAdapter(adapterTimeGenerator.getSpinnerAdapter(items, this));

		return spinner;
	}
	//#endif

	protected abstract void persist(Reminder reminder);
}
