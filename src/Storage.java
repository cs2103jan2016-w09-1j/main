import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import cs2103_w09_1j.esther.Task;

public class Storage {
	private Path saveLocation;
	private ArrayList<Task> tasksBuffer = new ArrayList<>();
	private boolean isRedirect = false;

	private final String defaultFileName = "esther.txt";
	private final Path defaultSaveLocation = Paths.get(defaultFileName);

	/**
	 * Constructor for Storage class
	 * 
	 * Checks default save location to see if it is a file or redirect
	 * Sets the current save location correspondingly
	 */
	public Storage() {
		saveLocation = defaultSaveLocation;
		// check default save location
		if (isValidFile(defaultSaveLocation)) {
			String firstLine = getFirstLineFromFile(defaultSaveLocation);
			if (isPath(firstLine)) {
				saveLocation = Paths.get(firstLine);
				isRedirect = true;
			}
		}
	}

	/**
	 * If a file exists at the specified location, loads the file into a task array list and returns
	 * it. Saves the path used for future usage.
	 * 
	 * @param filePath
	 *            Path to load the file from
	 * @return ArrayList of tasks as loaded from the file if successful
	 */
	public ArrayList<Task> readFromFile(Path filePath) {
		try {
			if (isValidFile(filePath)) {
				tryLoadFile(filePath);
				setSaveLocation(filePath);
				return tasksBuffer;
			}
			return tasksBuffer;
		} catch (Exception e) {
			e.printStackTrace();
			return tasksBuffer;
		}
	}

	/**
	 * Alternate load method that uses a stored save Location
	 * 
	 * @return ArrayList of tasks as loaded from the file if successful
	 * @throws IOException
	 *             if an IO error occurs during loading
	 */
	public ArrayList<Task> readFromFile() {
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
	public void writeToFile(ArrayList<Task> tasks) {
		tasksBuffer = tasks;
		checkValidSaveLocation();
		BufferedWriter writer;
		try {
			if(saveLocation == null){System.out.println("null save location");}
			writer = Files.newBufferedWriter(saveLocation);
			writer.write("");
			for (int i = 0; i < tasksBuffer.size(); i++) {
				writer.write(tasksBuffer.get(i).toString());
				writer.newLine();
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block for WriteToFile
			e.printStackTrace();
		}
	}

	/**
	 * Sets a new save location using a given path. If the new save location is different from the
	 * default, setup a redirect at the default.
	 * 
	 * @param filePath
	 *            New path to save to
	 * @throws IOException
	 *             If an IO error occurs during setting up the redirect.
	 */
	public void setSaveLocation(Path filePath) throws IOException {
		saveLocation = filePath;
		if (!saveLocation.equals(defaultSaveLocation)) {
			setupRedirect(saveLocation);
			isRedirect = true;
		}
	}

	private void setupRedirect(Path redirectLocation) {
		BufferedWriter writer;
		try {
			writer = Files.newBufferedWriter(saveLocation);
			writer.write(redirectLocation.toAbsolutePath().toString());
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block for setupRedirect
			e.printStackTrace();
		}
	}

	private void checkValidSaveLocation() throws Error {
	}

	private ArrayList<Task> tryLoadFile(Path filePath) throws ParseException {
		tasksBuffer.clear();
		BufferedReader reader;
		try {
			reader = Files.newBufferedReader(filePath);
			while (reader.ready()) {
				loadTextString(reader.readLine());
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block for tryLoadFile
			e.printStackTrace();
		}

		return tasksBuffer;
	}

	private String getFirstLineFromFile(Path path) {
		BufferedReader reader;
		String firstLine = "";
		try {
			reader = Files.newBufferedReader(path);
			if(reader.ready()){
				firstLine = reader.readLine();
			}
			reader.close();
			return firstLine;
		} catch (IOException e) {
			// TODO Auto-generated catch block for getFirstLine
			e.printStackTrace();
			return null;
		}
	}
	
	public void flushFileAtLocation(Path filePath){
		try {
			Files.delete(filePath);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void flushFile() {
		if (isRedirect) {
			flushFileAtLocation(saveLocation);
		}
		flushFileAtLocation(defaultSaveLocation);
	}

	private boolean isPath(String string) {
		return string.contains("://");
	}

	private void loadTextString(String nextLine) throws ParseException {
		if (!nextLine.isEmpty()) {
			tasksBuffer.add(new Task(nextLine));
		}
	}

	private boolean isValidFile(Path newFilePath) {
		File file = newFilePath.toFile();
		return file.exists();
	}
}
