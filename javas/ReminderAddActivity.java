package br.unb.cic.reminders.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;
import br.unb.cic.reminders.controller.Controller;
import br.unb.cic.reminders.model.InvalidDateException;
import br.unb.cic.reminders.model.InvalidFormatException;
import br.unb.cic.reminders.model.InvalidTextException;
import br.unb.cic.reminders.model.Reminder;
import br.unb.cic.reminders2.R;
//#if staticCategory || manageCategory
import br.unb.cic.reminders.model.Category;
//#endif
//#if staticCategory || manageCategory || priority
import android.widget.Spinner;
import java.util.List;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
//#endif
//#ifdef priority 
import br.unb.cic.reminders.model.Priority;
import java.util.Arrays;
import android.widget.ArrayAdapter;
//#endif
//#ifdef dateRepeat 
import android.widget.CheckBox;
import br.unb.cic.reminders.model.InvalidDaysException;
//#endif 

public class ReminderAddActivity extends Activity {
	private EditText edtReminder, edtDetails;
	//#ifdef fixedDate
	private EditText edtDate;
	//#endif
	//#if fixedDate || dateRepeat
	private EditText edtHour;
	//#endif
	//#ifdef dateRange
	private EditText edtDateStart, edtHourStart, edtDateFinal, edtHourFinal;
	//#endif
	//#ifdef dateRepeat
	private CheckBox cbMonday, cbTuesday, cbWednesday, cbThursday, cbFriday, cbSaturday, cbSunday;
	//#endif
	private Button btnSave, btnCancel;
	private boolean editingReminder;
	private Long previewReminderId;

	//#if staticCategory || manageCategory
	private Category selectedCategory;
	private Spinner spinnerCategory;

	private void addListenerToSpinnerCategory() {
		spinnerCategory.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<? extends Object> parent, View view, int pos, long id) {
				selectedCategory = (Category) parent.getItemAtPosition(pos);
			}

			public void onNothingSelected(AdapterView<? extends Object> parent) {

			}
		});
	}

	private Spinner getSpinnerCategory() throws Exception {
		Spinner spinner = (Spinner) findViewById(R.id.spinnerCategories);

		SpinnerAdapterGenerator<Category> adapterCategoryGenerator = new SpinnerAdapterGenerator<Category>();

		List<Category> categories = Controller.instance(getApplicationContext()).listCategories();

		spinner.setAdapter(adapterCategoryGenerator.getSpinnerAdapter(categories, this));

		return spinner;
	}

	private int categoryToIndex(Category category) throws Exception {
		List<Category> categories = Controller.instance(getApplicationContext()).listCategories();
		int i = 0;
		for (Category c : categories) {
			if (c.getName().equals(category.getName())) {
				return i;
			}
			i++;
		}
		return 0;
	}
	//#endif

	//#ifdef priority
	private Priority selectedPriority;
	private Spinner spinnerPriority;

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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reminder_add);
		Reminder existingReminder = getReminderFromIntent();
		if (existingReminder == null) {
			editingReminder = true;
			Reminder editReminder = getExistingReminder();
			initialize(editReminder);
		} else {
			editingReminder = false;
			initialize(existingReminder);
		}
		configureActionListener();
	}

	private void configureActionListener() {
		addListenerToBtnSave();
		addListenerToBtnCancel();
		//#if staticCategory || manageCategory
		addListenerToSpinnerCategory();
		//#endif
		//#ifdef priority
		addListenerToSpinnerPriority();
		//#endif
	}

	private void addListenerToBtnSave() {
		btnSave.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				try {
					Reminder reminder = createReminder();
					if (editingReminder) {
						reminder.setId(previewReminderId);
						Controller.instance(getApplicationContext()).updateReminder(reminder);
					} else {
						Controller.instance(getApplicationContext()).addReminder(reminder);
					}
					finish();
				} catch (Exception e) {
					Log.e("ReminderAddActivity", e.getMessage());
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

	private Reminder createReminder() {
		Reminder reminder = new Reminder();
		try {
			reminder = createReminderAux();
			reminder.setText(edtReminder.getText().toString());
			reminder.setDetails(edtDetails.getText().toString());
		} catch (InvalidTextException e) {
			Toast.makeText(getApplicationContext(), "Invalid text.", Toast.LENGTH_SHORT).show();
		} catch (InvalidDateException e) {
			Toast.makeText(getApplicationContext(), "Invalid date.", Toast.LENGTH_SHORT).show();
		} catch (InvalidHourException e) {
			Toast.makeText(getApplicationContext(), "Invalid time.", Toast.LENGTH_SHORT).show();
		}
		return reminder;
	}

	private Reminder createReminderAux() {
		Reminder reminder = new Reminder();
		//#ifdef fixedDate
		reminder.setDate(edtDate.getText().toString());
		//#endif
		//#if fixedDate || dateRepeat
		reminder.setHour(edtHour.getText().toString());
		//#endif
		//#ifdef dateRange
		reminder.setDateStart(edtDateStart.getText().toString());
		reminder.setHourStart(edtHourStart.getText().toString());
		reminder.setDateFinal(edtDateFinal.getText().toString());
		reminder.setHourFinal(edtHourFinal.getText().toString());
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
		reminder.setCategory(selectedCategory);
		//#endif
		//#ifdef priority
		reminder.setPriority(selectedPriority);
		//#endif
		return reminder;
	}

	private Reminder getExistingReminder() {
		Reminder reminder = null;
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();
		if (Intent.ACTION_SEND.equals(action) && "text/plain".equals(type)) {
			previewReminderId = intent.getLongExtra("id", 0);
			String text = intent.getStringExtra("text");
			reminder = createReminderExisting(intent);
			reminder.setText(text);
			reminder.setId(previewReminderId);
		}
		return reminder;
	}

	private Reminder createReminderExisting(Intent intent) {
		Reminder reminder = new Reminder();
		//#ifdef fixedDate
		String date = intent.getStringExtra("date");
		reminder.setDate(date);
		//#endif
		//#if fixedDate || dateRepeat
		String hour = intent.getStringExtra("hour");
		reminder.setHour(hour);
		//#endif
		//#ifdef dateRange
		String dateStart = intent.getStringExtra("dateStart");
		String hourStart = intent.getStringExtra("hourStart");
		String dateFinal = intent.getStringExtra("dateFinal");
		String hourFinal = intent.getStringExtra("hourFinal");
		reminder.setDateStart(dateStart);
		reminder.setHourStart(hourStart);
		reminder.setDateFinal(dateFinal);
		reminder.setHourFinal(hourFinal);
		//#endif
		//#ifdef dateRepeat
		boolean monday = intent.getBooleanExtra("monday", false);
		boolean tuesday = intent.getBooleanExtra("tuesday", false);
		boolean wednesday = intent.getBooleanExtra("wednesday", false);
		boolean thursday = intent.getBooleanExtra("thursday", false);
		boolean friday = intent.getBooleanExtra("friday", false);
		boolean saturday = intent.getBooleanExtra("saturday", false);
		boolean sunday = intent.getBooleanExtra("sunday", false);
		reminder.setMonday(monday);
		reminder.setTuesday(tuesday);
		reminder.setWednesday(wednesday);
		reminder.setThursday(thursday);
		reminder.setFriday(friday);
		reminder.setSaturday(saturday);
		reminder.setSunday(sunday);
		//#endif
		//#if staticCategory || manageCategory
		String categoryName = intent.getStringExtra("category_name");
		String categoryId = intent.getStringExtra("category_id");
		Category category = new Category();
		category.setName(categoryName);
		category.setId(Long.parseLong(categoryId));
		reminder.setCategory(category);
		//#endif
		//#ifdef priority
		String priority = intent.getStringExtra("priority");
		reminder.setPriority(Priority.fromCode(Integer.parseInt(priority, 10)));
		//#endif
		return reminder;
	}

	private Reminder getReminderFromIntent() {
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();
		if ("br.com.positivo.reminders.ADD_REMINDER".equals(action) && "text/plain".equals(type)) {
			try {
				String text = intent.getStringExtra("text");
				String details = intent.getStringExtra("details");
				Reminder reminder = createReminderIntent(intent);
				reminder.setText(text);
				reminder.setDetails(details);
				return reminder;
			} catch (InvalidFormatException e) {
			}
		}
		return null;
	}

	private Reminder createReminderIntent(Intent intent) {
		Reminder reminder = new Reminder();
		//#ifdef fixedDate
		String date = intent.getStringExtra("date");
		reminder.setDate(date);
		//#endif
		//#if fixedDate || dateRepeat
		String hour = intent.getStringExtra("hour");
		reminder.setHour(hour);
		//#endif
		//#ifdef dateRange
		String dateStart = intent.getStringExtra("dateStart");
		String hourStart = intent.getStringExtra("hourStart");
		String dateFinal = intent.getStringExtra("dateFinal");
		String hourFinal = intent.getStringExtra("hourFinal");
		reminder.setDateStart(dateStart);
		reminder.setHourStart(hourStart);
		reminder.setDateFinal(dateFinal);
		reminder.setHourFinal(hourFinal);
		//#endif
		//#ifdef dateRepeat
		boolean monday = intent.getBooleanExtra("monday", false);
		boolean tuesday = intent.getBooleanExtra("tuesday", false);
		boolean wednesday = intent.getBooleanExtra("wednesday", false);
		boolean thursday = intent.getBooleanExtra("thursday", false);
		boolean friday = intent.getBooleanExtra("friday", false);
		boolean saturday = intent.getBooleanExtra("saturday", false);
		boolean sunday = intent.getBooleanExtra("sunday", false);
		reminder.setMonday(monday);
		reminder.setTuesday(tuesday);
		reminder.setWednesday(wednesday);
		reminder.setThursday(thursday);
		reminder.setFriday(friday);
		reminder.setSaturday(saturday);
		reminder.setSunday(sunday);
		//#endif
		//#if staticCategory || manageCategory
		String category = intent.getStringExtra("category");
		Category auxCategory = new Category();
		auxCategory.setName(category);
		reminder.setCategory(auxCategory);
		//#endif
		//#ifdef priority
		String priority = intent.getStringExtra("priority");
		reminder.setPriority(Priority.fromCode(Integer.parseInt(priority, 10)));
		//#endif
		return reminder;
	}

	private void initialize(Reminder reminder) {
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

		try {
			edtReminder = (EditText) findViewById(R.id.edtReminder);
			edtDetails = (EditText) findViewById(R.id.edtDetails);
			if (reminder != null) {
				updateFieldsFromReminder(reminder);
			}
			btnSave = (Button) findViewById(R.id.btnSave);
			btnCancel = (Button) findViewById(R.id.btnCancel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateFieldsFromReminder(Reminder reminder) throws Exception {
		edtReminder.setText(reminder.getText());
		edtDetails.setText(reminder.getDetails());
		//#ifdef fixedDate
		edtDate.setText(reminder.getDate());
		//#endif
		//#if fixedDate || dateRepeat
		edtHour.setText(reminder.getHour());
		//#endif
		//#ifdef dateRepeat
		cbMonday.setChecked(reminder.isMonday());
		cbTuesday.setChecked(reminder.isTuesday());
		cbWednesday.setChecked(reminder.isWednesday());
		cbThursday.setChecked(reminder.isThursday());
		cbFriday.setChecked(reminder.isFriday());
		cbSaturday.setChecked(reminder.isSaturday());
		cbSunday.setChecked(reminder.isSunday());
		//#endif
		//#ifdef dateRange
		edtDateStart.setText(reminder.getDateStart());
		edtHourStart.setText(reminder.getHourStart());
		edtDateFinal.setText(reminder.getDateFinal());
		edtHourFinal.setText(reminder.getHourFinal());
		//#endif
		//#if staticCategory || manageCategory
		spinnerCategory.setSelection(categoryToIndex(reminder.getCategory()));
		//#endif
		//#ifdef priority
		spinnerPriority.setSelection(reminder.getPriority());
		//#endif
	}
}
