package br.unb.cic.reminders.view;

import br.unb.cic.framework.persistence.DBException;
import br.unb.cic.reminders.controller.Controller;
import br.unb.cic.reminders.model.Reminder;
//#if staticCategory || manageCategory 
import br.unb.cic.reminders.model.Category;
import java.util.List;
//#endif 

public class AddReminderActivity extends ReminderActivity {
	@Override
	protected void initializeValues() {
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
			Controller.instance(getApplicationContext()).addReminder(reminder);
		} catch (DBException e) {
			e.printStackTrace();
		}
	}

	//#if staticCategory || manageCategory
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