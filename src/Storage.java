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
	 *             when loading tasks that are wrongly formatted
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
	 *             when loading tasks that are wrongly formatted
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
		Files.delete(filePath);
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

	// ===========PRIVATE METHODS BELOW==================

	/**
	 * Reads the contents from a file at the given path into a String.
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	private String readFile(Path path) throws IOException {
		String outputString = "";
		storageLogger.info("Accessing save file at " + path.toString());
		BufferedReader reader = Files.newBufferedReader(path);
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
	 */
	private void writeFile(String string, Path path) throws IOException {
		storageLogger.info("Accessing file at " + path.toString() + " for writing");
		BufferedWriter writer = Files.newBufferedWriter(path);
		writer.write(string);
		writer.close();
	}

	/**
	 * 
	 * @param filePath
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	private ArrayList<Task> loadSaveFile(Path filePath) throws ParseException, IOException {
		tasksBuffer.clear();
		loadTasksString(readFile(filePath));
		return tasksBuffer;
	}

	/**
	 * 
	 * @param configPath
	 * @throws IOException
	 * @throws ParseException
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
	 * 
	 */
	private void setSavePathWithCurrentConfig() {
		assert (currentConfig != null);
		storageLogger.info("Retreiving save path from current Config");
		savePath = currentConfig.getSavePath();
	}

	/**
	 * 
	 * @param allLines
	 * @throws ParseException
	 */
	private void loadTasksString(String allLines) throws ParseException {
		storageLogger.info("File loaded. Passing contents to load into tasks buffer.");
		String[] allLinesArray = allLines.split(BY_NEXTLINE);
		for (int i = 0; i < allLinesArray.length; i++) {
			loadTaskString(allLinesArray[i]);
		}
	}

	/**
	 * 
	 * @param nextLine
	 * @throws ParseException
	 */
	private void loadTaskString(String nextLine) throws ParseException {
		if (!nextLine.isEmpty()) {
			tasksBuffer.add(new Task(nextLine));
		}
	}

	/**
	 * 
	 */
	private String tasksToString(ArrayList<Task> tasks) {
		String tasksString = "";
		for (Task task : tasks) {
			tasksString += task.toString();
		}
		return tasksString;
	}

	/**
	 * 
	 * @param filePath
	 * @return
	 */
	private boolean isValidFile(Path filePath) {
		return filePath.toFile().exists();
	}

	public void flushFileAtLocation(String string) throws IOException {
		flushFileAtLocation(Paths.get(string));
		return;
	}
}
