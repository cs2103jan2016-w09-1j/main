import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import cs2103_w09_1j.esther.Task;

public class Storage {
	private Path saveLocation;
	private ArrayList<Task> tasksBuffer;

	private final String defaultFileName = "esther.txt";
	private final Path defaultSaveLocation = Paths.get(defaultFileName);

	/**
	 * Empty constructor
	 * 
	 * @throws IOException
	 */
	public Storage() throws IOException {
		// check default save location
		if (isValidFile(defaultSaveLocation)) {
			String firstLine = getFirstLineFromFile(defaultSaveLocation);
			if (isPath(firstLine)) {
				saveLocation = Paths.get(firstLine);
			}
		} else {
			saveLocation = defaultSaveLocation;
		}
	}

	/**
	 * If a file exists at the specified location, loads the file into a task array list and returns
	 * it. Saves the path used for future usage.
	 * 
	 * @param filePath
	 *            Path to load the file from
	 * @return ArrayList of tasks as loaded from the file if successful
	 * @throws IOException
	 *             if an IO error occurs during loading
	 */
	public ArrayList<Task> readFromFile(Path filePath) throws IOException {
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
	 *             if an IO error occurs during loading
	 */
	public ArrayList<Task> readFromFile() throws IOException {
		checkValidSaveLocation();
		return readFromFile(saveLocation);
	}

	/**
	 * Writes an arraylist of tasks to file
	 * 
	 * @param tasks
	 *            Array list containing tasks to write
	 * @throws IOException
	 *             if an IO error occurs during writing
	 */
	public void writeToFile(ArrayList<Task> tasks) throws IOException {
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

	/**
	 * Sets a new save location using a given path. If the new save location is different from the
	 * default, setup a redirect at the default.
	 * 
	 * @param filePath
	 * 			New path to save to
	 * @throws IOException
	 * 			If an IO error occurs during setting up the redirect.
	 */
	public void setSaveLocation(Path filePath) throws IOException {
		saveLocation = filePath;
		if (!saveLocation.equals(defaultSaveLocation)) {
			setupRedirect(saveLocation);
		}
	}

	private void setupRedirect(Path redirectLocation) throws IOException {
		BufferedWriter writer = Files.newBufferedWriter(saveLocation);
		writer.write(redirectLocation.toAbsolutePath().toString());
		writer.close();
	}

	private void checkValidSaveLocation() throws Error {
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

	private String getFirstLineFromFile(Path path) throws IOException {
		BufferedReader reader = Files.newBufferedReader(path);
		String firstLine = reader.readLine();
		reader.close();
		return firstLine;

	public void flushFile() {
		// TODO Auto-generated method stub
	}

	private boolean isPath(String string) {
		return string.contains("://");
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
