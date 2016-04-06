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
		System.out.println("Task name is " + task.getName());
		id.set(Integer.toString(task.getId()));
		System.out.println("Task id is " + task.getId());
		priority.set(Integer.toString(task.getPriority()));
		System.out.println("Task priority is " + task.getPriority());
		endDate.set(task.eDateToString());
		System.out.println("Task endDate is " + task.getEndDate());
		
		if (!task.sDateToString().isEmpty()) {
			System.out.println("Task has start date!");
			startDate.set(task.sDateToString());
			System.out.println("Task start date is " + task.getStartDate());
			date.set("from " + startDate.get() + " to " + endDate.get());
			System.out.println("Task date is " + date.get());
		} else {
			date.set(task.eDateToString());
			System.out.println("Task date is " + date.get());
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
