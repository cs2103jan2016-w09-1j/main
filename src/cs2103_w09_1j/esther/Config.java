package cs2103_w09_1j.esther;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;

public class Config {

	private int referenceID;
	private Path saveLocation;
	private HashMap<String, String> fieldNameAliases;

	private final int defaultReferenceID = 0;
	private final Path defaultSaveLocation = Paths.get("esther.txt");
	private final String[][] defaultFieldNameAliases = {	{ "taskName", "name" },
															{ "tName", "name" },
															{ "name", "name" },
															{ "nm", "name" },
															{ "n", "name" },
															{ "date", "date" },
															{ "dt", "date" },
															{ "d", "date" },
															{ "id", "id" },
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

	private final String[] attributeNames = { "ReferenceID", "SaveLocation", "FieldNameAliases" };
	private final String attributeFormat = "%1$s = %2$s\n";

	/**
	 * Constructor for default config
	 */
	public Config() {
		referenceID = getDefaultReferenceID();
		saveLocation = getDefaultSaveLocation();
		fieldNameAliases = constructDefaultFieldNameAliases();
	}

	/**
	 * Constructor for config given string input
	 * 
	 * @param configInput
	 *            String containing information for config construction
	 */
	public Config(String configInput) {

	}

	/**
	 * 
	 */
	public String toString() {
		String configStr = "";
		configStr += String.format(	attributeFormat,
									attributeNames[0],
									String.valueOf(getReferenceID()));
		configStr += String.format(	attributeFormat,
									attributeNames[1],
									getSaveLocation().toString());
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
			hashMapString += pair.getKey() + " = " + pair.getValue();
			it.remove(); // avoids a ConcurrentModificationException
		}
		return hashMapString;
	}

	/**
	 * 
	 * @return
	 */
	private HashMap<String, String> constructDefaultFieldNameAliases() {
		HashMap<String, String> fieldNameAliases = new HashMap<>();
		for (int i = 0; i < getDefaultFieldNameAliases().length; i++) {
			fieldNameAliases.put(	getDefaultFieldNameAliases()[i][0],
									getDefaultFieldNameAliases()[i][1]);
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
	public Path getSaveLocation() {
		return saveLocation;
	}

	/**
	 * @param saveLocation
	 *            the saveLocation to set
	 */
	public void setSaveLocation(Path saveLocation) {
		this.saveLocation = saveLocation;
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
	private Path getDefaultSaveLocation() {
		return defaultSaveLocation;
	}

	/**
	 * @return the defaultFieldNameAliases
	 */
	private String[][] getDefaultFieldNameAliases() {
		return defaultFieldNameAliases;
	}
}
