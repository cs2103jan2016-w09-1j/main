import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;

import cs2103_w09_1j.esther.Config;
import cs2103_w09_1j.esther.Task;

public class Storage {
	private Path savePath;
	private ArrayList<Task> tasksBuffer = new ArrayList<Task>();
	private Config currentConfig = new Config();

	private static final String BY_NEXTLINE = "\\n";
	private static final String configName = "esther.config";
	private static final Path configPath = Paths.get(configName);

	/**
	 * Constructor for Storage class
	 * 
	 * Checks default config location to load config options
	 * Sets the current save location correspondingly
	 * Loads file contents into task buffer
	 * 
	 * @throws ParseException
	 * @throws IOException
	 */
	public Storage() throws ParseException, IOException {
		// check config location
		processConfig();
		readSaveFile();
	}

	/**
	 * If a file exists at the specified location, loads the file into a task array list and returns
	 * it.
	 * 
	 * @param filePath
	 *            Path to load the file from
	 * @return ArrayList of tasks as loaded from the file if successful
	 * @throws ParseException
	 * @throws IOException
	 */
	public ArrayList<Task> readSaveFile(Path filePath) throws ParseException, IOException {
		if (isValidFile(filePath)) {
			loadSaveFile(filePath);
		}
		return tasksBuffer;
	}

	/**
	 * Alternate load method that uses a stored save Location
	 * 
	 * @return ArrayList of tasks as loaded from the file if successful
	 * @throws ParseException
	 * @throws IOException
	 *             if an IO error occurs during loading
	 */
	public ArrayList<Task> readSaveFile() throws ParseException, IOException {
		return readSaveFile(savePath);
	}

	/**
	 * Writes an arraylist of tasks to file
	 * 
	 * @param tasks
	 *            Array list containing tasks to write
	 * @throws IOException
	 *             if an IO error occurs during writing
	 */
	public void writeSaveFile(ArrayList<Task> tasks) throws IOException {
		tasksBuffer = tasks;
		writeFile(tasksToString(tasksBuffer), savePath);
	}

	/**
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 * @throws ParseException 
	 */
	public Config readConfigFile(Path filePath) throws IOException, ParseException {
		if (isValidFile(configPath)) {
			loadConfigFile(configPath);
		}
		return currentConfig;
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 * @throws ParseException 
	 */
	public Config readConfigFile() throws IOException, ParseException {
		return readConfigFile(configPath);
	}

	/**
	 * 
	 * @param config
	 * @throws IOException
	 */
	public void writeConfigFile(Config config) throws IOException {
		String configString = config.toString();
		writeFile(configString, configPath);
	}

	/**
	 * Method to update config if logic or an external component changes it.
	 * 
	 * @param newConfig
	 */
	public void updateConfig(Config newConfig) {
		currentConfig = newConfig;
		processConfig();
	}

	/**
	 * 
	 * @param filePath
	 * @throws IOException
	 */
	public void flushFileAtLocation(Path filePath) throws IOException {
		Files.delete(filePath);
	}

	/**
	 * @throws IOException
	 */
	public void flushFile() throws IOException {
		flushFileAtLocation(savePath);
	}
	
	//===========PRIVATE METHODS BELOW==================

	/**
	 * @param path
	 * @return
	 * @throws IOException
	 */
	private String readFile(Path path) throws IOException {
		String outputString = "";
		BufferedReader reader = Files.newBufferedReader(path);
		while (reader.ready()) {
			outputString += reader.readLine();
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
		BufferedWriter writer = Files.newBufferedWriter(configPath);
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
	private void loadConfigFile(Path loadConfigPath) throws IOException, ParseException {
		currentConfig = new Config(readFile(loadConfigPath));
	}

	/**
	 * 
	 */
	private void processConfig() {
		savePath = currentConfig.getSavePath();
	}

	/**
	 * 
	 * @param allLines
	 * @throws ParseException
	 */
	private void loadTasksString(String allLines) throws ParseException {
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
	 * @param newFilePath
	 * @return
	 */
	private boolean isValidFile(Path newFilePath) {
		return newFilePath.toFile().exists();
	}
}
