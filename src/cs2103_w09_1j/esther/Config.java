package cs2103_w09_1j.esther;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Config {

    private int referenceID;
    private Path savePath;
    private HashMap<String, String> fieldNameAliases;

    private static final int defaultReferenceID = 0;
    private static final Path defaultSavePath = Paths.get("esther.txt");
    private static final String[][] defaultFieldNameAliases = { { "taskName", "taskName" },
								{ "tName", "taskName" },
								{ "name", "taskName" },
								{ "nm", "taskName" },
								{ "n", "taskName" },
								{ "startDate", "startDate" },
								{ "startdate", "startDate" },
								{ "sd", "startDate" },
								{ "startd", "startDate" },
								{ "d", "endDate"},
								{ "dt", "endDate"},
								{ "date", "endDate"},
								{ "Date", "endDate"},
								{ "endDate", "endDate" },
								{ "endd", "endDate" },
								{ "enddate", "endDate" },
								{ "ed", "endDate" },
								{ "startTime", "startTime" },
								{ "starttime", "startTime" },
								{ "startt", "startTime" },
								{ "st", "startTime" },
								{ "endTime", "endTime" },
								{ "endtime", "endTime" },
								{ "endt", "endTime" },
								{ "et", "endTime" },
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

    private static final String[] attributeNames = { "ReferenceID", "SaveLocation", "FieldNameAliases" };
    private static final String attributeFormat = "%1$s = %2$s;\n";
    private static final String attributeRegex = " = ([^;]+);";
    private static final String fieldNameRegex = "([\\w]+) = ([\\w]+);\n";

    /**
     * Constructor for default config
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
     * @throws Exception
     */
    public Config(String configString) throws ParseException {
	this();
	String[] resultsArray = new String[2];
	for (int i = 0; i < 2; i++) {
	    resultsArray[i] = findMatch(attributeNames[i], configString);
	    if (resultsArray[i] == null) {
		throw new ParseException("Config file load failed", i);
	    } else {
		configString = configString.replaceFirst(attributeNames[i] + attributeRegex, "");
	    }
	}

	setReferenceID(Integer.parseInt(resultsArray[0]));
	setSavePath(Paths.get(resultsArray[1]));

	Matcher fieldNameMatcher = Pattern.compile(fieldNameRegex).matcher(configString);
	while (fieldNameMatcher.find()) {
	    fieldNameAliases.put(fieldNameMatcher.group(1), fieldNameMatcher.group(2));
	}
    }

    /**
     * 
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
     * 
     * @param hashMap
     * @return
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

    private String findMatch(String regex, String input) {
	return Task.findMatch(regex + attributeRegex, input);
    }

    /**
     * 
     * @return
     */
    private HashMap<String, String> constructDefaultFieldNameAliases() {
	HashMap<String, String> fieldNameAliases = new HashMap<>();
	for (int i = 0; i < getDefaultFieldNameAliases().length; i++) {
	    fieldNameAliases.put(getDefaultFieldNameAliases()[i][0], getDefaultFieldNameAliases()[i][1]);
	}
	return fieldNameAliases;
    }

    /**
     * @return the referenceID
     */
    public int getReferenceID() {
	return referenceID;
    }

    /**
     * @param referenceID
     *            the referenceID to set
     */
    public void setReferenceID(int referenceID) {
	this.referenceID = referenceID;
    }

    /**
     * @return the fieldNameAliases
     */
    public HashMap<String, String> getFieldNameAliases() {
	return fieldNameAliases;
    }

    /**
     * @param fieldNameAliases
     *            the fieldNameAliases to set
     */
    public void setFieldNameAliases(HashMap<String, String> fieldNameAliases) {
	this.fieldNameAliases = fieldNameAliases;
    }

    /**
     * @return the saveLocation
     */
    public Path getSavePath() {
	return savePath;
    }

    /**
     * @param saveLocation
     *            the saveLocation to set
     */
    public void setSavePath(Path saveLocation) {
	this.savePath = saveLocation;
    }

    /**
     * @return the defaultReferenceID
     */
    private int getDefaultReferenceID() {
	return defaultReferenceID;
    }

    /**
     * @return the defaultSaveLocation
     */
    private Path getDefaultSavePath() {
	return defaultSavePath;
    }

    /**
     * @return the defaultFieldNameAliases
     */
    private String[][] getDefaultFieldNameAliases() {
	return defaultFieldNameAliases;
    }
}
