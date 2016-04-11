package cs2103_w09_1j.esther;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class TaskWrapper {
	private Task record;
	private SimpleStringProperty taskName = new SimpleStringProperty();
	private SimpleStringProperty startDate = new SimpleStringProperty();
	private SimpleStringProperty endDate = new SimpleStringProperty();
	private SimpleStringProperty id = new SimpleStringProperty();
	private SimpleStringProperty priority = new SimpleStringProperty();
	private SimpleStringProperty date = new SimpleStringProperty();

	public TaskWrapper(Task task) {
		record = task;
		taskName.set(task.getName());
		id.set(Integer.toString(task.getId()));
		priority.set(Integer.toString(task.getPriority()));
		endDate.set(task.eDateToString());
		
		if (!task.sDateToString().isEmpty()) {
			startDate.set(task.sDateToString());
			date.set("from " + startDate.get() + " to " + endDate.get());
		} else {
			date.set(task.eDateToString());
		}
	}

	// for TreeTableView's sub headings
	public TaskWrapper(String title) {
		id.set(title);
		taskName.set("");
		startDate.set("");
	}

	public Task getRecord() {
		return record;
	}


	public String getTaskName() {
		return taskName.get();
	}


	public String getId() {
		return id.get();
	}


	public String getPriority() {
		return priority.get();
	}

	public String getDate() {
		return date.get();
	}
}
