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

public class Storage {
    private Path savePath;
    private ArrayList<Task> tasksBuffer = new ArrayList<Task>();
    private Config currentConfig = new Config();

    private static final String BY_NEXTLINE = "\\n";
    private static final String configName = "estherconfig.txt";
    private static final Path configPath = Paths.get(configName);
    /**
     * STOP COMMENTING OUT MY LOGGER LINES. SEE STORAGE() AND SET TO WARNING OR SEVERE.
     */
    private static final Logger storageLogger = Logger.getLogger("storageLogger");

    /**
     * Constructor for Storage class
     * 
     * Checks default config location to load config options Sets the current
     * save location correspondingly Loads file contents into task buffer
     * 
     * @throws ParseException
     * @throws IOException
     */
    public Storage() throws ParseException, IOException {
	storageLogger.setLevel(Level.SEVERE);
	storageLogger.info("Initializing Storage");
	currentConfig = readConfigFile();
	processConfig();
    }

    /**
     * If a file exists at the specified location, loads the file into a task
     * array list and returns it.
     * 
     * @param filePath
     *            Path to load the file from
     * @return ArrayList of tasks as loaded from the file if successful
     * @throws ParseException
     * @throws IOException
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
     * @throws IOException
     *             if an IO error occurs during loading
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
     *             if an IO error occurs during writing
     */
    public void writeSaveFile(ArrayList<Task> tasks) throws IOException {
	assert (tasks != null);
	storageLogger.info("Saving tasks to save file");
	tasksBuffer = tasks;
	validifyTasksBuffer();
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
	storageLogger.info("Writing config file");
	String configString = config.toString();
	writeFile(configString, configPath);
    }

    /**
     * Method to update config if logic or an external component changes it.
     * 
     * @param newConfig
     * @throws IOException
     */
    public void updateConfig(Config newConfig) throws IOException {
	currentConfig = newConfig;
	processConfig();
	writeConfigFile(newConfig);
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

    public Config getConfig() {
	return currentConfig;
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
    private void processConfig() {
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
}
