package org.lc.db.dto;

import java.io.Serializable;

import org.lc.web.controller.BaseController;

public class DataTableDTO implements Serializable {
	
	public DataTableDTO() {
	}
	public DataTableDTO(int draw, 
			int recordsTotal,
			int recordsFiltered,
			Object[][] data) {
		this.draw = draw;
		this.recordsTotal = recordsTotal;
		this.recordsFiltered = recordsFiltered;
		this.data = data;
	}
	public DataTableDTO(int draw) {
		this.draw = draw;
	}
	
	int draw = 1;
	int recordsTotal = 0;
	int recordsFiltered = 0;
	Object[][] data = new Object[][]{};
	
	public int getDraw() {
		return draw;
	}
	public void setDraw(int draw) {
		this.draw = draw;
	}
	public int getRecordsTotal() {
		return recordsTotal;
	}
	public void setRecordsTotal(int recordsTotal) {
		this.recordsTotal = recordsTotal;
	}
	public int getRecordsFiltered() {
		return recordsFiltered;
	}
	public void setRecordsFiltered(int recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}
	public Object[][] getData() {
		return data;
	}
	public void setData(Object[][] data) {
		this.data = data;
	}
	
	public static class EmptyDataTableDTO extends DataTableDTO {
		public static final long serialVersionUID = 0l;
		
		public EmptyDataTableDTO() {
			super();
			try {
				setDraw((Integer)BaseController.getParam("draw"));
			} catch(Exception e) {
				setDraw(1);
			}
		}
		public EmptyDataTableDTO(int draw, 
				int recordsTotal,
				int recordsFiltered,
				Object[][] data) {
			this();
		}
		public EmptyDataTableDTO(int draw) {
			super(draw);
		}
	}
	
	public static final EmptyDataTableDTO EMPTY_DATATABLE_DTO = new EmptyDataTableDTO();
}
