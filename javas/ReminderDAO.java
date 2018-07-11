package br.unb.cic.reminders.model.db;

import java.util.List;
import br.unb.cic.framework.persistence.DBException;
import br.unb.cic.reminders.model.Reminder;
//#if staticCategory || manageCategory
import br.unb.cic.reminders.model.Category;
//#endif 

public interface ReminderDAO {
	public Long saveReminder(Reminder r) throws DBException;

	public List<Reminder> listReminders() throws DBException;

	public void updateReminder(Reminder reminder) throws DBException;

	public void deleteReminder(Reminder reminder) throws DBException;

	public void persistReminder(Reminder reminder) throws DBException;

	//#if staticCategory || manageCategory
	public List<Reminder> listRemindersByCategory(Category category) throws DBException;
	//#endif
}