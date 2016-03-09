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
	private Path saveLocation;
	private ArrayList<Task> tasksBuffer = new ArrayList<Task>();
	private Config currentConfig = new Config();

	private static final String BY_NEXTLINE = "\\n";
	private final String configName = "esther.config";
	private final Path configPath = Paths.get(configName);

	/**
	 * Constructor for Storage class
	 * 
	 * Checks default config location to load config options
	 * Sets the current save location correspondingly
	 * Loads file contents into task buffer
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
			tryLoadFile(filePath);
		} return tasksBuffer;
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
		return readSaveFile(saveLocation);
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
		BufferedWriter writer;
		writer = Files.newBufferedWriter(saveLocation);
		for (int i = 0; i < tasksBuffer.size(); i++) {
			writer.write(tasksBuffer.get(i).toString());
		}
		writer.close();
	}
	/**
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public Config readConfigFile(Path filePath) throws IOException {
		if(isValidFile(configPath)){
			loadConfig(configPath);
		}
		return currentConfig;
	}
	
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public Config readConfigFile() throws IOException {
		return readConfigFile(configPath);
	}
	
	/**
	 * 
	 * @param config
	 * @throws IOException
	 */
	public void writeConfigFile(Config config) throws IOException {
		String configString = config.toString();
		BufferedWriter writer = Files.newBufferedWriter(configPath);
		for (int i = 0; i < tasksBuffer.size(); i++) {
			writer.write(configString);
		}
		writer.close();
	}
	
	/**
	 * Method to update config if logic or an external component changes it.
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
	public void flushFileAtLocation(Path filePath) throws IOException{
		Files.delete(filePath);
	}

	/**
	 * @throws IOException 
	 */
	public void flushFile() throws IOException {
		flushFileAtLocation(saveLocation);
	}
	
	/**
	 * @param path
	 * @return
	 * @throws IOException 
	 */
	private String loadFileIntoString(Path path) throws IOException {
		String outputString = "";
		BufferedReader reader = Files.newBufferedReader(path);
		while (reader.ready()) {
			outputString += reader.readLine();
		}
		reader.close();
		return outputString;
	}
	
	/**
	 * 
	 * @param filePath
	 * @return
	 * @throws ParseException
	 * @throws IOException 
	 */
	private ArrayList<Task> tryLoadFile(Path filePath) throws ParseException, IOException {
		tasksBuffer.clear();
		loadTasksString(loadFileIntoString(filePath));
		return tasksBuffer;
	}

	/**
	 * 
	 */
	private void processConfig() {
		saveLocation = currentConfig.getSaveLocation();
	}

	/**
	 * 
	 * @param configPath
	 * @throws IOException 
	 */
	private void loadConfig(Path loadConfigPath) throws IOException {
		currentConfig = new Config(loadFileIntoString(loadConfigPath));
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
	 * @param newFilePath
	 * @return
	 */
	private boolean isValidFile(Path newFilePath) {
		return newFilePath.toFile().exists();
	}
}
