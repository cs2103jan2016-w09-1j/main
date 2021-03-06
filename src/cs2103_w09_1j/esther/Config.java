package cs2103_w09_1j.esther;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class handles and stores the configuration metadata of Esther. The three
 * main things stored are (1) the reference ID, (2) the save path of the save
 * file and (3) field name aliases for commands
 * 
 * @author Jeremy Hon
 * @@author A0127572A
 */
public class Config {

	private int referenceID;
	private Path savePath;
	private HashMap<String, String> fieldNameAliases;

	private static final int defaultReferenceID = 1;
	private static final Path defaultSavePath = Paths.get("esther.txt");
	private static final String[] attributeNames = { "ReferenceID", "SaveLocation", "FieldNameAliases" };
	private static final String attributeFormat = "%1$s = %2$s;\n";
	private static final String attributeRegex = " = ([^;]+);";
	private static final String fieldNameRegex = "([\\w]+) = ([\\w]+);\n";
	private static final String[][] defaultFieldNameAliases = {	{ "taskname", "taskName" },
																{ "tname", "taskName" },
																{ "name", "taskName" },
																{ "nm", "taskName" },
																{ "n", "taskName" },
																{ "startdate", "startDate" },
																{ "startd", "startDate" },
																{ "sdate", "startDate" },
																{ "sd", "startDate" },
																{ "d", "endDate" },
																{ "dt", "endDate" },
																{ "date", "endDate" },
																{ "enddate", "endDate" },
																{ "endd", "endDate" },
																{ "ed", "endDate" },
																{ "starttime", "startTime" },
																{ "startt", "startTime" },
																{ "stime", "startTime" },
																{ "st", "startTime" },
																{ "endtime", "endTime" },
																{ "etime", "endTime" },
																{ "endt", "endTime" },
																{ "et", "endTime" },
																{ "time", "endTime" },
																{ "tm", "endTime" },
																{ "t", "endTime" },
																{ "id", "taskID" },
																{ "taskid", "taskID" },
																{ "priority", "priority" },
																{ "prio", "priority" },
																{ "pri", "priority" },
																{ "pr", "priority" },
																{ "p", "priority" },
																{ "completed", "completed" },
																{ "complete", "completed" },
																{ "comp", "completed" },
																{ "cp", "completed" },
																{ "done", "completed" },
																{ "dn", "completed" } };

	/**
	 * Constructor for default config with default values
	 */
	public Config() {
		setReferenceID(getDefaultReferenceID());
		setSavePath(getDefaultSavePath());
		setFieldNameAliases(constructDefaultFieldNameAliases());
	}

	/**
	 * Constructor for config given string input
	 * 
	 * @param configString
	 *            String containing information for config construction
	 * @throws ParseException 
	 * 			If no match is found for the Ref ID or the Save Location 
	 */
	public Config(String configString) throws ParseException {
		this();
		String[] resultsArray = new String[2];
		
		//Parse for the first two elements: ref ID and save path
		for (int i = 0; i < 2; i++) {
			resultsArray[i] = findMatch(attributeNames[i], configString);
			if (resultsArray[i] == null) {
				throw new ParseException("Config file load failed", i);
			} else {
				configString = configString.replaceFirst(attributeNames[i] + attributeRegex, "");
			}
		}

		//Set first two elements
		setReferenceID(Integer.parseInt(resultsArray[0]));
		
		try {
			setSavePath(Paths.get(resultsArray[1]));
		} catch (InvalidPathException ipe) {
			setSavePath(getDefaultSavePath());
		}

		//Parse and set all field names
		findAndSetFieldNames(configString);
	}

	/**
	 * Formats the Config object into a string for writing to string.
	 */
	public String toString() {
		String configStr = "";
		configStr += String.format(attributeFormat, attributeNames[0], String.valueOf(getReferenceID()));
		configStr += String.format(attributeFormat, attributeNames[1], getSavePath().toString());
		configStr += "\n";
		configStr += attributeNames[2] + ":\n";
		configStr += printHashMap(getFieldNameAliases());
		return configStr;
	}

	/**
	 * Prints all elements in a hashmap. A specific order is not guaranteed.
	 * 
	 * @param hashMap
	 *            The hashmap to print
	 * @return A string containing all elements of the hashmap
	 */
	public String printHashMap(HashMap<String, String> hashMap) {
		String hashMapString = "";
		Iterator<HashMap.Entry<String, String>> it = hashMap.entrySet().iterator();
		while (it.hasNext()) {
			HashMap.Entry<String, String> pair = (HashMap.Entry<String, String>) it.next();
			hashMapString += pair.getKey() + " = " + pair.getValue() + ";\n";
		}
		return hashMapString;
	}

	/**
	 * Gets the locally stored reference ID used by Logic to set task IDs
	 * 
	 * @return the referenceID
	 */
	public int getReferenceID() {
		return referenceID;
	}

	/**
	 * Sets the locally stored reference ID used by Logic to set task IDs
	 * 
	 * @param referenceID
	 *            the referenceID to set
	 */
	public void setReferenceID(int referenceID) {
		this.referenceID = referenceID;
	}

	/**
	 * Gets the hashmap of fieldname aliases used by Parser when parsing
	 * commands that have fieldnames (like update)
	 * 
	 * @return the fieldNameAliases
	 */
	public HashMap<String, String> getFieldNameAliases() {
		return fieldNameAliases;
	}

	/**
	 * Sets the hashmap of fieldname aliases used by Parser when parsing
	 * commands that have fieldnames (like update)
	 * 
	 * @param fieldNameAliases
	 *            the fieldNameAliases to set
	 */
	public void setFieldNameAliases(HashMap<String, String> fieldNameAliases) {
		this.fieldNameAliases = fieldNameAliases;
	}

	/**
	 * Gets the stored save location for the save file
	 * 
	 * @return the saveLocation
	 */
	public Path getSavePath() {
		return savePath;
	}

	/**
	 * Sets the stored save location for the save file
	 * 
	 * @param saveLocation
	 *            the saveLocation to set (must be a Path)
	 */
	public void setSavePath(Path saveLocation) {
		this.savePath = saveLocation;
	}

	/**
	 * Sets the stored save location for the save file using a string
	 * 
	 * @param saveLocation
	 *            the saveLocation to set (must be a String)
	 * @throws InvalidPathException
	 *             If an invalid path was given
	 */
	public void setSavePath(String saveLocation) throws InvalidPathException {
		this.savePath = Paths.get(saveLocation);
	}

	// ==================PRIVATE METHODS=================
	// These methods are mostly used in the construction of the default Config
	// object

	/**
	 * Gets the default reference ID for use in the default constructor
	 * 
	 * @return the defaultReferenceID
	 */
	private int getDefaultReferenceID() {
		return defaultReferenceID;
	}

	/**
	 * Gets the default save location for use in the default constructor
	 * 
	 * @return the defaultSaveLocation
	 */
	private Path getDefaultSavePath() {
		return defaultSavePath;
	}

	/**
	 * Gets the default field name aliases in the form of a 2D array
	 * 
	 * @return the defaultFieldNameAliases
	 */
	private String[][] getDefaultFieldNameAliases() {
		return defaultFieldNameAliases;
	}

	/**
	 * Finds field names and their aliases in the given string and sets the
	 * field name hashmap
	 * 
	 * @param configString
	 *            String to be parsed containing field names and their aliases
	 */
	private void findAndSetFieldNames(String configString) {
		Matcher fieldNameMatcher = Pattern.compile(fieldNameRegex).matcher(configString);
		while (fieldNameMatcher.find()) {
			fieldNameAliases.put(fieldNameMatcher.group(1), fieldNameMatcher.group(2));
		}
	}

	/**
	 * Returns the matching string given a regex and a string
	 * 
	 * @param regex
	 * @param input
	 * @return
	 * @@author A0127572A
	 */
	private String findMatch(String regex, String input) {
		Matcher matcher = Pattern.compile(regex + attributeRegex).matcher(input);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			return null;
		}
	}

	/**
	 * Constructs the default field name aliases hashmap. Uses an array of field
	 * name aliases.
	 * 
	 * @return Constructed hashmap of field name and their aliases
	 */
	private HashMap<String, String> constructDefaultFieldNameAliases() {
		HashMap<String, String> fieldNameAliases = new HashMap<>();
		for (int i = 0; i < getDefaultFieldNameAliases().length; i++) {
			fieldNameAliases.put(getDefaultFieldNameAliases()[i][0], getDefaultFieldNameAliases()[i][1]);
		}
		return fieldNameAliases;
	}
}
