package br.unb.cic.reminders.view;

import android.content.Intent;
import android.os.Bundle;
import br.unb.cic.framework.persistence.DBException;
import br.unb.cic.reminders.controller.Controller;
import br.unb.cic.reminders.model.Reminder;
//#if staticCategory || manageCategory
import br.unb.cic.reminders.model.Category;
import java.util.List;
//#endif
//#ifdef priority 
import br.unb.cic.reminders.model.Priority;
//#endif

public class ExternalAddReminderActivity extends ReminderActivity {
	//#ifdef manageCategory
	private boolean isNewCategory = false;
	//#endif
	//#if staticCategory || manageCategory
	private Category newCategory = null;

	private void setNewCategory(Intent intent) throws Exception {
		String categoryName = intent.getStringExtra("category_name");
		List<Category> categories = Controller.instance(getApplicationContext()).listCategories();
		for (Category c : categories) {
			if (c.getName().equals(categoryName)) {
				newCategory = c;
				break;
			}
		}
		//#ifdef manageCategory
		if (newCategory == null) {
			isNewCategory = true;
			newCategory = new Category();
			newCategory.setName(categoryName);
		}
		//#endif
	}

	@Override
	protected List<Category> getCategories() throws Exception {
		List<Category> categories = super.getCategories();
		//#ifdef manageCategory
		if (isNewCategory) {
			categories.add(newCategory);
		}
		//#endif
		return categories;
	}

	private Category findCategory(Category category) throws Exception {
		List<Category> categories = Controller.instance(getApplicationContext()).listCategories();
		for (Category c : categories) {
			if (c.getName().equals(category.getName()))
				return c;
		}
		return null;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		reminder = new Reminder();
		try {
			setReminderFromIntent();
		} catch (Exception e) {
			Intent intent2 = new Intent(getApplicationContext(), AddReminderActivity.class);
			startActivity(intent2);
			finish();
		}
		super.onCreate(savedInstanceState);
	}

	private void setReminderFromIntent() throws Exception {
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();
		if (action.equals("br.com.positivo.reminders.ADD_REMINDER") && "text/plain".equals(type)) {
			String text = intent.getStringExtra("text");
			String details = intent.getStringExtra("details");
			reminder.setText(text);
			reminder.setDetails(details);
			reminderFromIntent(intent);
		} else
			reminder = null;
	}

	private void reminderFromIntent(Intent intent) throws Exception {
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
		int monday = intent.getIntExtra("monday", 0);
		int tuesday = intent.getIntExtra("tuesday", 0);
		int wednesday = intent.getIntExtra("wednesday", 0);
		int thursday = intent.getIntExtra("thursday", 0);
		int friday = intent.getIntExtra("friday", 0);
		int saturday = intent.getIntExtra("saturday", 0);
		int sunday = intent.getIntExtra("sunday", 0);
		reminder.setMonday(monday);
		reminder.setTuesday(tuesday);
		reminder.setWednesday(wednesday);
		reminder.setThursday(thursday);
		reminder.setFriday(friday);
		reminder.setSaturday(saturday);
		reminder.setSunday(sunday);
		//#endif
		//#if staticCategory || manageCategory
		setNewCategory(intent);
		reminder.setCategory(newCategory);
		//#endif
		//#ifdef priority
		String priority = intent.getStringExtra("priority");
		reminder.setPriority(Priority.fromCode(Integer.parseInt(priority)));
		//#endif
	}

	@Override
	protected void initializeValues() {
		if (!reminder.isValid())
			return;
		edtReminder.setText(reminder.getText());
		edtDetails.setText(reminder.getDetails());
		try {
			initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initialize() throws Exception {
		//#ifdef fixedDate
		updateSpinnerDateHour(spinnerDate, reminder.getDate());
		updateDateFromString(reminder.getDate());
		//#endif
		//#if fixedDate || dateRepeat
		updateSpinnerDateHour(spinnerTime, reminder.getHour());
		updateTimeFromString(reminder.getHour());
		//#endif
		//#ifdef dateRange
		updateSpinnerDateHour(spinnerDateStart, reminder.getDateStart());
		updateDateFromString(reminder.getDateStart(), false);
		updateSpinnerDateHour(spinnerTimeStart, reminder.getHourStart());
		updateTimeFromString(reminder.getHourStart(), false);
		updateSpinnerDateHour(spinnerDateStart, reminder.getDateFinal());
		updateDateFromString(reminder.getDateFinal(), true);
		updateSpinnerDateHour(spinnerTimeStart, reminder.getHourFinal());
		updateTimeFromString(reminder.getHourFinal(), true);
		//#endif
		//#if staticCategory || manageCategory
		spinnerCategory.setSelection(categoryToIndex(reminder.getCategory()));
		//#endif
		//#ifdef manageCategory
		if (isNewCategory)
			spinnerCategory.setSelection(spinnerCategory.getCount() - 2);
		//#endif
		//#ifdef priority
		spinnerPriority.setSelection(reminder.getPriority());
		//#endif
	}

	@Override
	protected void persist(Reminder reminder) {
		try {
			//#ifdef manageCategory
			if (isNewCategory) {
				Controller.instance(getApplicationContext()).addCategory(reminder.getCategory());
				reminder.setCategory(findCategory(reminder.getCategory()));
			}
			//#endif
			Controller.instance(getApplicationContext()).addReminder(reminder);
		} catch (DBException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
