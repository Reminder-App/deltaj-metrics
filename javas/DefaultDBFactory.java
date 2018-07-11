package br.unb.cic.reminders.model.db;

import android.content.Context;

public class DefaultDBFactory extends DBFactory {
	public DefaultDBFactory(Context context) {
		super(context);
	}

	@Override
	public ReminderDAO createReminderDAO() {
		return new DefaultReminderDAO(context);
	}

	//#if staticCategory || manageCategory
	@Override
	public CategoryDAO createCategoryDAO() {
		return new DefaultCategoryDAO(context);
	}
	//#endif
}