package br.unb.cic.reminders.view;

import android.content.Intent;
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

public class EditReminderActivity extends ReminderActivity {
	@Override
	protected void initializeValues() {
		Intent intent = getIntent();
		long reminderId = intent.getLongExtra("id", 0);
		String text = intent.getStringExtra("text");
		String details = intent.getStringExtra("details");
		reminder.setId(reminderId);
		edtReminder.setText(text);
		edtDetails.setText(details);
		try {
			initializeValues(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initializeValues(Intent intent) throws Exception {
		//#ifdef fixedDate
		String date = intent.getStringExtra("date");
		updateSpinnerDateHour(spinnerDate, date);
		updateDateFromString(date);
		//#endif

		//#if fixedDate || dateRepeat
		String hour = intent.getStringExtra("hour");
		updateSpinnerDateHour(spinnerTime, hour);
		updateTimeFromString(hour);
		//#endif

		//#ifdef dateRange
		String dateStart = intent.getStringExtra("dateStart");
		String hourStart = intent.getStringExtra("hourStart");
		String dateFinal = intent.getStringExtra("dateFinal");
		String hourFinal = intent.getStringExtra("hourFinal");
		updateSpinnerDateHour(spinnerDateStart, dateStart);
		updateDateFromString(dateStart, false);
		updateSpinnerDateHour(spinnerTimeStart, hourStart);
		updateTimeFromString(hourStart, false);
		updateSpinnerDateHour(spinnerDateFinal, dateFinal);
		updateDateFromString(dateFinal, true);
		updateSpinnerDateHour(spinnerTimeFinal, hourFinal);
		updateTimeFromString(hourFinal, false);
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
		cbMonday.setChecked(monday);
		cbTuesday.setChecked(tuesday);
		cbWednesday.setChecked(wednesday);
		cbThursday.setChecked(thursday);
		cbFriday.setChecked(friday);
		cbSaturday.setChecked(saturday);
		cbSunday.setChecked(sunday);
		//#endif

		//#if staticCategory || manageCategory
		String categoryName = intent.getStringExtra("category_name");
		String categoryId = intent.getStringExtra("category_id");
		Category category = new Category();
		category.setId(Long.parseLong(categoryId));
		category.setName(categoryName);
		spinnerCategory.setSelection(categoryToIndex(category));
		//#endif

		//#ifdef priority
		String priority = intent.getStringExtra("priority");
		spinnerPriority.setSelection(Priority.fromCode(Integer.parseInt(priority, 10)).getCode());
		//#endif
	}

	@Override
	protected void persist(Reminder reminder) {
		//#if staticCategory || manageCategory
		try {
			Category category = findCategory(reminder.getCategory());
			//#ifdef manageCategory
			if (category != null) {
				reminder.setCategory(category);
			} else {
				Controller.instance(getApplicationContext()).addCategory(reminder.getCategory());
				reminder.setCategory(findCategory(reminder.getCategory()));
			}
			//#elifdef staticCategory
			reminder.setCategory(category);
			//#endif
		} catch (Exception e) {
			e.printStackTrace();
		}
		//#endif

		try {
			Controller.instance(getApplicationContext()).updateReminder(reminder);
		} catch (DBException e) {
			e.printStackTrace();
		}
	}

	//#if staticCategory || manageCategory
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

	private Category findCategory(Category category) throws Exception {
		List<Category> categories = Controller.instance(getApplicationContext()).listCategories();
		for (Category c : categories) {
			if (c.getName().equals(category.getName()))
				return c;
		}
		return null;
	}
	//#endif
}