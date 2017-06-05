package org.lc.db.interfaces;

public interface IDataTableRecord {

	public Object[] toObjectArray();
	public String[] getUIColumns();
	public String[] getSortableColumns();
}
