//#if reminder
package br.unb.cic.reminders.view;

import android.content.Intent;
import br.unb.cic.framework.persistence.DBException;
import br.unb.cic.reminders.controller.Controller;
import br.unb.cic.reminders.model.Reminder;
//#if staticCategory || manageCategory 
import br.unb.cic.reminders.model.Category;
import java.util.List;
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
		String date = intent.getStringExtra("date");
		String hour = intent.getStringExtra("hour");
		updateSpinnerDateHour(spinnerDate, date);
		updateDateFromString(date);
		updateSpinnerDateHour(spinnerTime, hour);
		updateTimeFromString(hour);

		//#if staticCategory || manageCategory
		String categoryName = intent.getStringExtra("category_name");
		String categoryId = intent.getStringExtra("category_id");
		Category category = new Category();
		category.setId(Long.parseLong(categoryId));
		category.setName(categoryName);
		spinnerCategory.setSelection(categoryToIndex(category));
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
				//#ifdef staticCategory
			reminder.setCategory(category);
				//#endif
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
//#endif
