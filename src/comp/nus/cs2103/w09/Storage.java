package comp.nus.cs2103.w09;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Storage {
	private Path saveLocation;
	private ArrayList<Task> tasksBuffer;
	private long mostRecentTaskID;
	
	private final String ERROR_NO_SAVE_LOCATION = "No Save Location set yet!";

	public Storage() {
	}

	/**
	 * If a file exists at the specified location, loads the file into a task array list and returns
	 * it. Saves the path used for future usage.
	 * 
	 * @param filePath
	 * 			Path to load the file from
	 * @return ArrayList of tasks as loaded from the file if successful
	 * @throws IOException
	 * 			if an IO error occus during loading
	 */
	public ArrayList<Task> loadFile(Path filePath) throws IOException {
		if (isValidFile(filePath)) {
			tryLoadFile(filePath);
			setSaveLocation(filePath);
			return tasksBuffer;
		}
		return null;
	}

	/**
	 * Alternate load method that uses a stored save Location
	 * 
	 * @return ArrayList of tasks as loaded from the file if successful
	 * @throws IOException
	 * 			if an IO error occurs during loading
	 */
	public ArrayList<Task> loadFile() throws IOException {
		checkValidSaveLocation();
		return loadFile(saveLocation);
		
	}

	private void checkValidSaveLocation() throws Error {
		if(saveLocation == null){
			throw new Error(ERROR_NO_SAVE_LOCATION);
		}
	}

	public void saveFile(ArrayList<Task> tasks) throws IOException {
		tasksBuffer = tasks;
		checkValidSaveLocation();
		BufferedWriter writer = Files.newBufferedWriter(saveLocation);
		writer.write("");
		for (int i = 0; i < tasksBuffer.size(); i++) {
			writer.write(tasksBuffer.get(i).toString());
			writer.newLine();
		}
		writer.close();
	}

	public void setSaveLocation(Path filePath) {
		saveLocation = filePath;
	}

	private ArrayList<Task> tryLoadFile(Path filePath) throws IOException {
		tasksBuffer.clear();
		BufferedReader reader = Files.newBufferedReader(filePath);
		while (reader.ready()) {
			loadTextString(reader.readLine());
		}
		reader.close();

		return tasksBuffer;
	}

	private void loadTextString(String nextLine) throws IOException {
		if (!nextLine.isEmpty()) {
			tasksBuffer.add(new Task(nextLine));
		}
	}

	private boolean isValidFile(Path newFilePath) {
		File file = newFilePath.toFile();
		return file.exists();
	}
}
