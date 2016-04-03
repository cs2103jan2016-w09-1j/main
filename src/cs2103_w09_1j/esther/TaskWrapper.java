package cs2103_w09_1j.esther;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class TaskWrapper {
	private Task record;
	private SimpleStringProperty taskName = new SimpleStringProperty();
	private SimpleStringProperty startDate = new SimpleStringProperty();
	private SimpleStringProperty endDate = new SimpleStringProperty();
	private SimpleIntegerProperty id = new SimpleIntegerProperty();
	private SimpleIntegerProperty priority = new SimpleIntegerProperty();
	private SimpleStringProperty date = new SimpleStringProperty();

	public TaskWrapper(Task task) {
		record = task;
		taskName.set(task.getName());
		System.out.println("Task name is " + task.getName());
		id.set(task.getId());
		System.out.println("Task id is " + task.getId());
		priority.set(task.getPriority());
		System.out.println("Task priority is " + task.getPriority());
		endDate.set(task.eDateToString());
		System.out.println("Task endDate is " + task.getEndDate());
		
		if (!task.sDateToString().isEmpty()) {
			System.out.println("Task has start date!");
			startDate.set(task.sDateToString());
			System.out.println("Task start date is " + task.getStartDate());
			date.set("from " + startDate + " to " + endDate);
			System.out.println("Task date is " + date.get());
		} else {
			date.set(task.eDateToString());
			System.out.println("Task date is " + date.get());
		}
	}


	public Task getRecord() {
		return record;
	}


	public SimpleStringProperty getTaskName() {
		return taskName;
	}


	public SimpleIntegerProperty getId() {
		return id;
	}


	public SimpleIntegerProperty getPriority() {
		return priority;
	}

	public SimpleStringProperty getDate() {
		return date;
	}
}
