import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import cs2103_w09_1j.esther.Config;
import cs2103_w09_1j.esther.Task;

/**
 * Storage class for saving and loading tasks to file, as well as program
 * configuration
 * 
 * @author Jeremy Hon
 * @@author A0127572A
 */
public class Storage {
	// global attributes used in multiple methods
	private Path savePath;
	private ArrayList<Task> tasksBuffer = new ArrayList<Task>();
	private Config currentConfig = new Config();

	// global constants
	private static final String BY_NEXTLINE = "\\n";
	private static final String configName = "estherconfig.txt";
	private static final Path configPath = Paths.get(configName);
	private static final Logger storageLogger = Logger.getLogger("storageLogger");

	// ===================PUBLIC METHODS=======================

	/**
	 * Constructor for Storage class
	 * 
	 * Checks default config location to load config options Sets the current
	 * save location correspondingly Loads file contents into task buffer
	 * 
	 * @throws ParseException
	 *             when loading tasks or config file that are wrongly formatted
	 * @throws IOException
	 *             when an IO error occurs when accessing files
	 */
	public Storage() throws ParseException, IOException {
		storageLogger.setLevel(Level.SEVERE);
		storageLogger.info("Initializing Storage");
		currentConfig = readConfigFile();
		setSavePathWithCurrentConfig();
	}

	/**
	 * If a file exists at the specified location, loads the file into a task
	 * array list and returns it.
	 * 
	 * @param filePath
	 *            Path to load the file from
	 * @return ArrayList of tasks as loaded from the file if successful
	 * @throws ParseException
	 *             when loading tasks that are wrongly formatted
	 * @throws IOException
	 *             when an IO error occurs when accessing files
	 */
	public ArrayList<Task> readSaveFile(Path filePath) throws ParseException, IOException {
		storageLogger.info("Checking if save file is valid");
		if (isValidFile(filePath)) {
			storageLogger.info("File Valid. Proceeding to load");
			loadSaveFile(filePath);
			validifyTasksBuffer();
		} else {
			storageLogger.warning("File Invalid. Returning empty list of tasks");
		}
		return tasksBuffer;
	}

	/**
	 * Alternate load method that uses a stored save Location
	 * 
	 * @return ArrayList of tasks as loaded from the file if successful
	 * @throws ParseException
	 *             when loading tasks that are wrongly formatted
	 * @throws IOException
	 *             when an IO error occurs when accessing files
	 */
	public ArrayList<Task> readSaveFile() throws ParseException, IOException {
		storageLogger.info("Loading saved file");
		assert (savePath != null);
		return readSaveFile(savePath);
	}

	/**
	 * Writes an arraylist of tasks to file
	 * 
	 * @param tasks
	 *            Array list containing tasks to write
	 * @throws IOException
	 *             when an IO error occurs when accessing files
	 */
	public void writeSaveFile(ArrayList<Task> tasks) throws IOException {
		assert (tasks != null);
		storageLogger.info("Saving tasks to save file");
		tasksBuffer = tasks;
		validifyTasksBuffer();
		writeFile(tasksToString(tasksBuffer), savePath);
	}

	/**
	 * Takes in a file path and reads a config file at that location
	 * 
	 * @param filePath
	 *            path to look for config file
	 * @return a Config object containing attributes found in the file
	 * @throws ParseException
	 *             when loading a config that was wrongly formatted
	 * @throws IOException
	 *             when an IO error occurs when accessing files
	 */
	public Config readConfigFile(Path filePath) throws ParseException, IOException {
		storageLogger.info("Checking if config file is valid");
		if (isValidFile(configPath)) {
			storageLogger.info("File Valid. Proceeding to load");
			return loadConfigFile(configPath);
		} else {
			storageLogger.warning("File Invalid. Using default config");
			Config defaultConfig = new Config();
			writeConfigFile(defaultConfig);
			return defaultConfig;
		}
	}

	/**
	 * Calls the {@link #readConfigFile(Path) readConfigFile(Path)} method with
	 * the {@link #configPath default Config path}
	 * 
	 * @param filePath
	 *            path to look for config file
	 * @return a Config object containing attributes found in the file
	 * @throws ParseException
	 *             when loading a config that was wrongly formatted
	 * @throws IOException
	 *             when an IO error occurs when accessing files
	 */
	public Config readConfigFile() throws IOException, ParseException {
		return readConfigFile(configPath);
	}

	/**
	 * Writes the given Config object into the {@link #configPath default Config
	 * path}
	 * 
	 * @param config
	 *            Config object to write to file
	 * @throws IOException
	 *             when an IO error occurs when accessing files
	 */
	public void writeConfigFile(Config config) throws IOException {
		storageLogger.info("Writing config file");
		currentConfig = config;
		String configString = config.toString();
		writeFile(configString, configPath);
	}

	/**
	 * Deletes the file at the given location
	 * 
	 * @param filePath
	 *            file to delete
	 * @throws IOException
	 *             when an IO error occurs when accessing files
	 */
	public void flushFileAtLocation(Path filePath) throws IOException {
		if (Files.exists(filePath)) {
			Files.delete(filePath);
		}
	}

	/**
	 * Same as method above but with a string parameter that is converted to a
	 * Path
	 * 
	 * @param filePath
	 *            pathstring pointing to file to delete
	 * @throws IOException
	 *             when an IO error occurs when accessing files
	 * @@A0127572A
	 */
	public void flushFileAtLocation(String filePath) throws IOException {
		flushFileAtLocation(Paths.get(filePath));
		return;
	}

	/**
	 * Deletes the file at the default save location
	 * 
	 * @throws IOException
	 *             when an IO error occurs when accessing files
	 */
	public void flushSaveFile() throws IOException {
		flushFileAtLocation(savePath);
	}

	/**
	 * Method to get stored Config object
	 * 
	 * @return stored Config object
	 */
	public Config getConfig() {
		return currentConfig;
	}

	/**
	 * Method to set internal stored Config object
	 * 
	 * @param newConfig
	 *            new Config object to replace the stored Config
	 * @throws IOException
	 *             when an IO error occurs when accessing files
	 */
	public void setConfig(Config newConfig) throws IOException {
		currentConfig = newConfig;
		setSavePathWithCurrentConfig();
		writeConfigFile(newConfig);
	}

	// =============TESTING METHODS====================

	/**
	 * Get list of storred tasks
	 * 
	 * @return list of internally stored tasks
	 */
	ArrayList<Task> getTasks() {
		return tasksBuffer;
	}

	// ===========PRIVATE METHODS==================

	/**
	 * Loads a saveFile from a filePath into an arrayList of tasks
	 * 
	 * @param savePath
	 *            Path to load file from
	 * @return Arraylist of loaded tasks
	 * @throws ParseException
	 *             when loading tasks that are wrongly formatted
	 * @throws IOException
	 *             when an IO error occurs when accessing files
	 */
	private ArrayList<Task> loadSaveFile(Path savePath) throws ParseException, IOException {
		tasksBuffer.clear();
		loadTasksString(readFile(savePath));
		return tasksBuffer;
	}

	/**
	 * Loads a config file from a filePath into a Config object
	 * 
	 * @param loadConfigPath
	 *            Path to load file from
	 * @return Config object containing parsed data
	 * @throws ParseException
	 *             when loading a config that was wrongly formatted
	 * @throws IOException
	 *             when an IO error occurs when accessing files
	 */
	private Config loadConfigFile(Path loadConfigPath) throws IOException {
		try {
			Config config = new Config(readFile(loadConfigPath));
			storageLogger.info("Config file succesfully parsed and loaded");
			return config;
		} catch (ParseException pe) {
			storageLogger.info("Error encounted parsing config file. Using default");
			return new Config();
		}
	}

	/**
	 * Reads the contents from a file at the given path into a String.
	 * 
	 * @param filePath
	 *            Path to load file from
	 * @return String containing information from file
	 * @throws IOException
	 *             when an IO error occurs when accessing files
	 */
	private String readFile(Path filePath) throws IOException {
		String outputString = "";
		storageLogger.info("Accessing save file at " + filePath.toString());
		BufferedReader reader = Files.newBufferedReader(filePath);
		while (reader.ready()) {
			outputString += reader.readLine() + "\n";
		}
		reader.close();
		return outputString;
	}

	/**
	 * Write a string into a file at the specified path (includes file name)
	 * 
	 * @param string
	 *            String that contains eventual file contents
	 * @param path
	 *            Path to file location for writing
	 * @throws IOException
	 *             when an IO error occurs when accessing files
	 */
	private void writeFile(String string, Path path) throws IOException {
		storageLogger.info("Accessing file at " + path.toString() + " for writing");
		BufferedWriter writer = Files.newBufferedWriter(path);
		writer.write(string);
		writer.close();
	}

	/**
	 * Method used to check that all tasks stored internally are valid. Invalid
	 * tasks are removed
	 * 
	 * @@A0127572A
	 */
	private void validifyTasksBuffer() {
		Iterator<Task> iter = tasksBuffer.iterator();
		while (iter.hasNext()) {
			Task task = iter.next();
			if (task == null || !task.isValid()) {
				iter.remove();
			}
		}
	}

	/**
	 * Takes the save path from the locally stored config
	 * 
	 * @@A0127572A
	 */
	private void setSavePathWithCurrentConfig() {
		assert (currentConfig != null);
		storageLogger.info("Retreiving save path from current Config");
		savePath = currentConfig.getSavePath();
	}

	/**
	 * Internal method to split a save file's contents into lines and parse each
	 * individually
	 * 
	 * @param allLines
	 *            String containing file contents
	 * @throws ParseException
	 *             If error occurs when parsing tasks
	 */
	private void loadTasksString(String allLines) throws ParseException {
		storageLogger.info("File loaded. Passing contents to load into tasks buffer.");
		String[] allLinesArray = allLines.split(BY_NEXTLINE);
		for (int i = 0; i < allLinesArray.length; i++) {
			loadTaskString(allLinesArray[i]);
		}
	}

	/**
	 * Internal method to parse an individual <code>Task</code> string
	 * 
	 * @param nextLine
	 *            String containing a Task in string form
	 * @throws ParseException
	 *             If error occurs when parsing tasks
	 */
	private void loadTaskString(String nextLine) throws ParseException {
		if (!nextLine.isEmpty()) {
			tasksBuffer.add(new Task(nextLine));
		}
	}

	/**
	 * Converts an arrayList of tasks into a string containing all the Tasks in
	 * string form
	 * 
	 * @param tasks
	 *            Arraylist of tasks to be converted
	 * @return String of all tasks converted to string form
	 * @@A0127572A
	 */
	private String tasksToString(ArrayList<Task> tasks) {
		String tasksString = "";
		for (Task task : tasks) {
			tasksString += task.toString();
		}
		return tasksString;
	}

	/**
	 * Checks if a given path contains a valid file.
	 * 
	 * @param filePath
	 *            the path to check
	 * @return true if path given is a file and false if directory or doesn't
	 *         exist.
	 * @@A0127572A
	 */
	private boolean isValidFile(Path filePath) {
		return filePath.toFile().isFile();
	}
}
