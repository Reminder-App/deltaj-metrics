package br.unb.cic.reminders.model.cp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import br.unb.cic.framework.persistence.DBException;
import br.unb.cic.reminders.model.Reminder;
import br.unb.cic.reminders.model.db.DBConstants;
import br.unb.cic.reminders.model.db.DefaultDBFactory;
import br.unb.cic.reminders.model.db.ReminderDAO;
//#if staticCategory || manageCategory
import br.unb.cic.reminders.model.Category;
import br.unb.cic.reminders.model.db.CategoryDAO;
//#endif 

public class ReminderContentProvider extends ContentProvider {
	private static final int REMINDERS = 10;
	private static final String SECURITY_EXCEPTION = "You are not allowed to call this method";
	private static final String AUTHORITY = "br.com.positivo.reminders.contentprovider";
	private static final String BASE_PATH = "reminders";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
	//#if staticCategory || manageCategory
	private CategoryDAO cdao;
	//#endif

	public static final String text() {
		return DBConstants.REMINDER_TEXT_COLUMN;
	}

	//#ifdef fixedDate
	public static final String date() {
		return DBConstants.REMINDER_DATE_COLUMN;
	}
	//#endif

	//#if fixedDate || dateRepeat
	public static final String hour() {
		return DBConstants.REMINDER_HOUR_COLUMN;
	}
	//#endif

	//#ifdef dateRange
	public static final String dateStart() {
		return DBConstants.REMINDER_INITIAL_DATE_COLUMN;
	}

	public static final String hourStart() {
		return DBConstants.REMINDER_INITIAL_HOUR_COLUMN;
	}

	public static final String dateFinal() {
		return DBConstants.REMINDER_FINAL_DATE_COLUMN;
	}

	public static final String hourFinal() {
		return DBConstants.REMINDER_FINAL_HOUR_COLUMN;
	}
	//#endif

	private ReminderDAO rdao;
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, REMINDERS);
	}

	@Override
	public boolean onCreate() {
		rdao = DefaultDBFactory.factory(getContext()).createReminderDAO();
		//#if staticCategory || manageCategory
		cdao = DefaultDBFactory.factory(getContext()).createCategoryDAO();
		//#endif
		return false;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		try {
			Reminder reminder = createReminderInsert(values);
			reminder.setText(values.getAsString(text()));
			Long id = rdao.saveReminder(reminder);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(BASE_PATH + "/" + id);
		} catch (DBException e) {
			throw new RuntimeException(e.getLocalizedMessage());
		}
	}

	private Reminder createReminderInsert(ContentValues values) throws DBException {
		Reminder reminder = new Reminder();
		//#ifdef fixedDate
		reminder.setDate(values.getAsString(date()));
		//#endif
		//#if fixedDate || dateRepeat
		reminder.setHour(values.getAsString(hour()));
		//#endif
		//#ifdef dateRange
		reminder.setDateStart(values.getAsString(dateStart()));
		reminder.setHourStart(values.getAsString(hourStart()));
		reminder.setDateFinal(values.getAsString(dateFinal()));
		reminder.setHourFinal(values.getAsString(hourFinal()));
		//#endif
		//#if staticCategory || manageCategory
		Category category = createCategoryInsert(values);
		reminder.setCategory(category);
		//#endif
		return reminder;
	}

	@Override
	public Cursor query(Uri arg0, String[] arg1, String arg2, String[] arg3, String arg4) {
		return null;
	}

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		throw new SecurityException(SECURITY_EXCEPTION);
	}

	@Override
	public String getType(Uri arg0) {
		return null;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		throw new SecurityException(SECURITY_EXCEPTION);
	}

	//#if staticCategory || manageCategory
	public static final String category() {
		return DBConstants.CATEGORY_NAME_COLUMN;
	}

	private Category createCategoryInsert(ContentValues values) throws DBException {
		Category category = cdao.findCategory(values.getAsString(category()));
		//#ifdef manageCategory
		if (category == null) {
			Category auxCategory = new Category();
			auxCategory.setName(values.getAsString(category()));
			cdao.saveCategory(auxCategory);
			category = cdao.findCategory(values.getAsString(category()));
		}
		//#endif
		return category;
	}
	//#endif
}