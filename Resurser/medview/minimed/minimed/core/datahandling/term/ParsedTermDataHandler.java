package minimed.core.datahandling.term;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import minimed.core.datahandling.NoSuchTermException;
import misc.foundation.io.IOConstants;

/**
 * Instances of this class implements the TermDataHandler
 * interface by reading and writing to two files, one of
 * which defines the terms and the other which defines
 * the values for each term.
 *
 * Writeouts of the cached term definition and value tables
 * are scheduled on a regular interval. When the actual writing
 * begins, the object is locked so that the writing writes the
 * object as it was at the start of the write, and no other
 * mutating method (add value, remove value, add term etc.) can
 * change the written objects in the middle of the write. All
 * methods that simply query are not synchronized, since they
 * can be called safely during the writeouts.
 *
 *	@author Fredrik Lindahl
 */
public class ParsedTermDataHandler extends AbstractTermDataHandler {
	public ParsedTermDataHandler() {}
	private HashMap termDefMap = null;
	private Hashtable termValTable = null;
	private Thread termDefWriterThread = null;
	private Thread termValWriterThread = null;
	private String setTermValueLocation = null;
	private String setTermDefinitionLocation = null;
	private static final String TERM_DEFINER = "$";
	private static final String TERM_DELIMITER = "\t";
	private static final String DEF_DELIMITERS = "\t";
	private Comparator comp = new StringNonCaseComparator();
	private static final String fileSep = System.getProperty("file.separator");
	
	/** 
	 * TERMHANDLER (TEXT-GENERATION FW)
	 */
	public boolean recognizesTerm(String term) {
		try	{
			return termExists(term);
		} catch (Exception exc)	{
			return false;
		}
	}
	
	/**
	 * Not implemented.
	 */
	public void shuttingDown() {}
	
	/**
	 * Checks if a term is existant in the term definition location. Note
	 * that a term can be existant without being in the value location,
	 * but a term can never be in the value location without being in the
	 * definition location. Note also that the term is deemed existant,
	 * even if it has another case than the one specified. Thus, say there
	 * exists a term "Born", then 'termExists("born")' will return true.
	 */
	public boolean termExists(String term) throws IOException {
		if (!isTermDefinitionLocationValid()) {
			throw new IOException("Term definition location not valid");
		} else {
			if (termDefMap == null) {
				termDefMap = readTermDefinitionHashMap();
			}
			
			return (getCorrespondingMapKey(term, termDefMap) != null);
		}
	}
	
	/**
	 * Checks whether a value exists for the specified term.
	 */
	public boolean valueExists(String term, Object value) throws IOException {
		if (!isTermDefinitionLocationValid()) {
			throw new IOException("Term definition location not valid");
		} else {
			if (termValTable == null) {
				termValTable = readValueHashVectorTable();
			}
			
			String key = getCorrespondingTableKey(term, termValTable);
			
			// no such term
			if (key == null) {
				return false;
			} else {
				Vector vect = (Vector) termValTable.get(key);
				Enumeration enm = vect.elements();
				
				while (enm.hasMoreElements()) {
					String curr = (String) enm.nextElement();
					
					if (curr.equalsIgnoreCase(value + "")) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Adds the specified term, exactly as it is given and with its specified
	 * type, to the term definition location. Note that this causes a term to
	 * be defined in the definition location without existing at all in the
	 * value location, but this is perfectly legal. Note also that the term will
	 * first be removed if it exists already (even if it exists with another
	 * case combination).
	 */
	public synchronized void addTerm(String term, int type) throws
	IOException, InvalidTypeException {
		if (!isTermDefinitionLocationValid()){
			throw new IOException("Term definition location not valid");
		} else {
			if (!isValidTermType(type)) {
				throw new InvalidTypeException(type + "");
			}
			
			if (termExists(term)) {
				try {
					removeTerm(term);
				} catch (NoSuchTermException e)	{
					e.printStackTrace(); // it exists - doesn't happen
				}
			}
			
			if (termDefMap == null)	{
				termDefMap = readTermDefinitionHashMap();
			}
			termDefMap.put(term, getTypeDescriptor(type));
			
			writeTermDefinitionHashMap(termDefMap);
		}
	}
	
	/**
	 * Will first remove the specified term from the value location (if
	 * existant in it), followed by removing the term from the definition
	 * location. If the term is not existant at all an exception will be
	 * thrown. Note that a term existing in the value location but not in
	 * the definition location is invalid and may cause erroneous behavior.
	 */
	public synchronized void removeTerm(String term) throws
	IOException, NoSuchTermException {
		if (!isTermDefinitionLocationValid()) {
			throw new IOException("Term definition location not valid");
		} else {
			if (termValTable == null) {
				termValTable = readValueHashVectorTable();
			}
			
			String valKey = getCorrespondingTableKey(term, termValTable);
			
			// term existant in value location...
			if (valKey != null) {
				termValTable.remove(valKey);
				writeValueHashVectorsToFile(termValTable);
			}
			
			if (termDefMap == null)	{
				termDefMap = readTermDefinitionHashMap();
			}
			
			String defKey = getCorrespondingMapKey(term, termDefMap);
			
			// term existant in definition location...
			if (defKey != null) {
				termDefMap.remove(defKey);
				
				writeTermDefinitionHashMap(termDefMap);
			} else {
				throw new NoSuchTermException(term);
			}
		}
	}
	
	
	/**
	 * Returns whether or not the currently set
	 * (or not set) term definition location is
	 * a valid one, by checking if the file it
	 * points to exists. If the definition location
	 * has not yet been set, false is returned.
	 */
	public boolean isTermDefinitionLocationValid() {
		if (setTermDefinitionLocation == null) {
			System.out.println("termdefloc is null");
			return false;
		} else {
			return new File(setTermDefinitionLocation).exists();
		}
	}
	
	/**
	 * Returns whether or not the currently set
	 * (or not set) term value location is
	 * a valid one, by checking if the file it
	 * points to exists. If the value location
	 * has not yet been set, false is returned.
	 */
	public boolean isTermValueLocationValid() {
		if (setTermValueLocation == null) {
			return false;
		} else {
			return new File(setTermValueLocation).exists();
		}
	}
	
	/**
	 * Returns whether or not the term definition
	 * location has been set.
	 * @return boolean
	 */
	public boolean isTermDefinitionLocationSet() {
		return (setTermDefinitionLocation != null);
	}
	
	/**
	 * Returns whether or not the term value location
	 * has been set.
	 * @return boolean
	 */
	public boolean isTermValueLocationSet() {
		return (setTermValueLocation != null);
	}
	
	
	/**
	 * Returns the values for the specified term. Note:
	 * the term is located case-insensitively, i.e. if
	 * a term exists in the value location called "Born",
	 * the calls 'getValues("Born") and getValues("born")'
	 * return the same array of strings. If the term is
	 * not in the definition location an exception will
	 * be thrown. An empty string array is returned if
	 * the term is defined but not existant in the value
	 * location.
	 */
	public String[] getValues(String term) throws IOException, NoSuchTermException {
		if (!isTermDefinitionLocationValid()) {
			throw new IOException("Term definition location not valid");
		} else {
			/*term not in definition location -> can't be in value... */
			if (!termExists(term)) {
				throw new NoSuchTermException(term);
			}
			
			if (termValTable == null) {
				termValTable = readValueHashVectorTable();
			}
			
			String key = getCorrespondingTableKey(term, termValTable);
			
			// term in definition location but not in value...
			if (key == null) { 
				return new String[0]; 
			}
			
			Vector valVect = (Vector) termValTable.get(key); // guaranteed non-null...
			String[] retArray = new String[valVect.size()];
			valVect.toArray(retArray);
			
			return retArray;
		}
	}
	
	
	/**
	 * Adds the specified value to the specified term. Note
	 * that the vector of values for the specified term is
	 * located by a case-insensitive search for a key equal
	 * to the term in meaning but not necessarily in case.
	 * Note also that if a value already exists (even though
	 * in another case-combination), it will be removed prior
	 * to being re-added in the case as specified.
	 */
	public synchronized void addValue(String term, Object value) throws
	IOException, NoSuchTermException, InvalidTypeException {
		if (!isTermDefinitionLocationValid()) {
			throw new IOException("Term definition location not valid");
		} else {
			if (termValTable == null) {
				termValTable = readValueHashVectorTable();
			}
			
			String key = getCorrespondingTableKey(term, termValTable);
			
			if (key == null) {
				if (!termExists(term)) {
					throw new NoSuchTermException("No such term (" + term + ")");
				} else {
					Vector valVect = new Vector();
					valVect.add(value);
					termValTable.put(term, valVect);
				}
			} else {
				Vector valVect = (Vector) termValTable.get(key); // guaranteed non-null...
				String vectVal = getCorrespondingVectorValue(value, valVect);
				
				if (vectVal != null) { 
					valVect.remove(vectVal); 
				}
				valVect.add(value);
			}
			writeValueHashVectorsToFile(termValTable);
		}
	}
	
	
	/**
	 * Removes the specified value from the specified term.
	 * Note that both the term and value will be located in
	 * a case-insensitive manner, thus if the term "Born" has
	 * a value "Sweden", the call 'removeValue("born", "sweden")'
	 * will work as expected (i.e. "Sweden" will be removed from
	 * "Born"). Furthermore, if a value removal results in the
	 * term having no values, the term is removed from the value
	 * location. Nothing is done if the term exists but has no
	 * values, or if the term has values but not the specified one.
	 */
	public synchronized void removeValue(String term, Object value) throws
	IOException, NoSuchTermException, InvalidTypeException {
		if (!isTermDefinitionLocationValid()) {
			throw new IOException("Term definition location not valid");
		} else {
			if (!termExists(term)) {
				throw new NoSuchTermException("No such term (" + term + ")");
			}
			
			if (termValTable == null) {
				termValTable = readValueHashVectorTable();
			}
			
			String key = getCorrespondingTableKey(term, termValTable);
			
			// term in definition but not in value...
			if (key == null) { 
				return; 
			}
			
			Vector valVect = (Vector) termValTable.get(key); // guaranteed non-null...
			String vectVal = getCorrespondingVectorValue(value, valVect);
			
			// if null -> value not in vector...
			if (vectVal != null) {
				valVect.remove(vectVal);
				
				if (valVect.size() == 0) { 
					termValTable.remove(key); 
				}
				
				writeValueHashVectorsToFile(termValTable);
			}
		}
	}
	
	
	/**
	 * Returns the type of the specified type. The term
	 * can be specified in whatever case, the search for
	 * the term and its type is done case-independently.
	 * An exception is thrown if the term has a type that
	 * is not recognized.
	 */
	public int getType(String term) throws IOException, NoSuchTermException,
	InvalidTypeException {
		if (!isTermDefinitionLocationValid()) {
			throw new IOException("Term definition location not valid");
		} else {
			if (termDefMap == null)	{
				termDefMap = readTermDefinitionHashMap();
			}
			
			if (!termExists(term, termDefMap)) {
				throw new NoSuchTermException("No such term (" + term + ")");
			}
			
			String key = getCorrespondingMapKey(term, termDefMap);
			String typeDesc = (String) termDefMap.get(key);
			
			if (typeDesc != null) {
				return parseType(typeDesc);
			} else {
				String err = "Unknown type for term '" + term + "': " + typeDesc;
				throw new InvalidTypeException(err);
			}
		}
	}
	
	
	/**
	 * Returns an array of the terms present in the term
	 * definition location, represented exactly as they
	 * appear in the location, case-sensitive.
	 */
	public String[] getTerms() throws IOException {
		if (!isTermDefinitionLocationValid()) {
			throw new IOException("Term definition location not valid");
		} else {
			if (termDefMap == null) {
				termDefMap = readTermDefinitionHashMap();
			}
			String[] retArray = new String[termDefMap.size()];
			termDefMap.keySet().toArray(retArray);
			
			return retArray;
		}
	}
	
	
	/**
	 * Sets the location of the term definitions.
	 */
	public void setTermDefinitionLocation(String loc) {
		setTermDefinitionLocation = loc;
	}
	
	
	/**
	 * Sets the location of the term values.
	 */
	public void setTermValueLocation(String loc) {
		setTermValueLocation = loc;
	}
	
	
	/**
	 * Returns the location of the term definitions.
	 */
	public String getTermDefinitionLocation() {
		if (isTermDefinitionLocationValid()) {
			return setTermDefinitionLocation;
		} else {
			return "";
		}
	}
	
	
	/**
	 * Returns the location of the term values.
	 */
	public String getTermValueLocation() {
		if (isTermValueLocationValid()) {
			return setTermValueLocation;
		} else {
			return "";
		}
	}
	
	/**
	 * Returns if the specified term exists in the
	 * specified map, independent of case. Thus, if
	 * the mapping "Born" -> "Sweden" exists in the map,
	 * and the call 'termExists("born")' is called, it
	 * will return the value 'true'.
	 */
	private boolean termExists(String term, HashMap map) {
		return (getCorrespondingMapKey(term, map) != null);
	}
	
	/**
	 * Returns the key as it is used in the specified map, located
	 * in a case-insensitive manner. If the key is not in the map
	 * (regardless of the combination of case) a null value will be
	 * returned. For instance, if the map a = ["Born" -> ["Sweden",
	 * ,"Iraq","Finland"], "Animal" -> ["Rabbit","Hawk"]], the method
	 * call 'getCorrespondingMapKey("born", a)' will return "Born".
	 */
	private String getCorrespondingMapKey(String key, HashMap map) {
		Object[] inMap = map.keySet().toArray();
		
		for (int ctr=0; ctr<inMap.length; ctr++) {
			if (((String)inMap[ctr]).equalsIgnoreCase(key)) {
				return (String) inMap[ctr];
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the key as it is used in the specified table, located
	 * in a case-insensitive manner. If the key is not in the table
	 * (regardless of the combination of case) a null value will be
	 * returned. For instance, if the table a = ["Born" -> ["Sweden",
	 * ,"Iraq","Finland"], "Animal" -> ["Rabbit","Hawk"]], the method
	 * call 'getCorrespondingTableKey("born", a)' will return "Born".
	 */
	private String getCorrespondingTableKey(String key, Hashtable table) {
		Object[] inTable = table.keySet().toArray();
		
		for (int ctr=0; ctr<inTable.length; ctr++) {
			if (((String)inTable[ctr]).equalsIgnoreCase(key)) {
				return (String) inTable[ctr];
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the value as it is used in the specified vector, located
	 * in a case-insensitive manner. If the value is not in the vector
	 * (regardless of the combination of case) a null value will be
	 * returned. For instance, if the vector a = ["Born", "Grey", "Rabbit"],
	 * the method call 'getCorrespondingVectorValue("born", a)' will return
	 * "Born".
	 */
	private String getCorrespondingVectorValue(Object value, Vector vector) {
		Object[] vectorValues = vector.toArray(); // must be a String[] (from file)...
		
		for (int ctr=0; ctr<vectorValues.length; ctr++)	{
			if (((String)vectorValues[ctr]).equalsIgnoreCase(value.toString()))	{
				return (String) vectorValues[ctr];
			}
		}
		return null;
	}
	
	/**
	 * Returns a hash table of vectors corresponding to
	 * the value location's terms and their values. The
	 * location is read case-sensitively and the keys are
	 * exactly as they appear in the value location. Note
	 * that if a key is existant in the returned hash table,
	 * it is guaranteed that there is a vector associated
	 * with it. A null value is never mapped to.
	 */
	private Hashtable readValueHashVectorTable() throws IOException {
		if (!isTermValueLocationValid()) {
			throw new IOException("Term value location not valid");
		} else {
			FileInputStream valFileInputStream = new FileInputStream(setTermValueLocation);
			String enc = IOConstants.ISO_LATIN_1_CHARACTER_ENCODING;
			InputStreamReader iSR = new InputStreamReader(valFileInputStream, enc);
			BufferedReader in = new BufferedReader(iSR);
			Hashtable termValMap = new Hashtable();
			String line = null;
			
			/* While not eof...*/
			while ((line = in.readLine()) != null) {
				// term-line...?
				if (line.startsWith(this.TERM_DEFINER)) {
					String currentTerm = line.substring(1).trim();
					Vector currentTermVector = new Vector();
					
					while (((line = in.readLine()) != null) && (line.trim().length() != 0))	{
						currentTermVector.add(line.trim()); // term values...
					}
					
					termValMap.put(currentTerm, currentTermVector);
				}
			}
			valFileInputStream.close();
			return termValMap;
		}
	}
	
	
	/**
	 * Writes the hash table of vectors back to file, in most
	 * cases called after it has been read and modified. This
	 * is done on a new, separate thread. Note that this method
	 * will not fire an IOException if the separate thread could
	 * not write out the hashtable.
	 */
	private void writeValueHashVectorsToFile(final Hashtable table) throws IOException {
		if (!isTermValueLocationValid()) {
			throw new IOException("Term value location not valid");
		} else {
			if ((termValWriterThread != null) && (termValWriterThread.isAlive())) {
				try {
					termValWriterThread.interrupt();
					termValWriterThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			termValWriterThread = new Thread(new Runnable()	{
				public void run() {
					try	{
						Thread.sleep(5000); // write out (if necessary) every 5 seconds
						
						// lock object during write (see top)
						synchronized(ParsedTermDataHandler.this) {
							FileOutputStream streamOut = new FileOutputStream(setTermValueLocation);
							String enc = IOConstants.ISO_LATIN_1_CHARACTER_ENCODING;
							OutputStreamWriter writer = new OutputStreamWriter(streamOut, enc);
							PrintWriter out = new PrintWriter(writer, true);
							String f = "# MedView term value file - to be used in conjunction " +
							"with the term definition file\n#\n";
							
							String g = "# CAUTION: THIS FILE SHOULD NOT BE MODIFIED " +
							"NON-PROGRAMATICALLY, AS IT MAY LEAD TO\n";
							
							String h = "# ERRORS WHILE RUNNING THE PROGRAMS, OR THE " +
							"PROGRAMS BEING UNABLE TO START\n";
							
							String i = "# -----------------------------------------" +
							"----------------------------------------\n";
							
							out.println(f + g + h + i);
							String[] keyArray = new String[table.size()];
							table.keySet().toArray(keyArray);
							Arrays.sort(keyArray, comp);
							Vector currentVector = null;
							
							for (int ctr1=0; ctr1<keyArray.length; ctr1++) {
								out.println(TERM_DEFINER + keyArray[ctr1]);
								currentVector = (Vector) table.get(keyArray[ctr1]);
								String[] valueArray = new String[currentVector.size()];
								int type = -1;
								
								try {
									type = getType(keyArray[ctr1]);
								} catch (Exception e) { /* default sorting (type = -1) */ }
								
								switch (type)
								{
								
								default: 
								{
									currentVector.toArray(valueArray);
									Arrays.sort(valueArray, comp);
									break;
								}
								}
								for (int ctr2=0; ctr2<valueArray.length; ctr2++) {
									out.println(valueArray[ctr2]);
								}
								out.println(""); // new line between terms...
							}
							streamOut.close();
						}
					} catch (InterruptedException e) {
						// this is normal if additions are done within interval
					}
					catch (Exception e)	{
						e.printStackTrace();
					}
				}
			});
			
			termValWriterThread.start();
		}
	}
	
	/**
	 * Returns a hash map of strings corresponding to
	 * the definition location's list of terms and their
	 * corresponding textual type representations. The
	 * terms are read exactly as-is, i.e. case-sensitive.
	 */
	private HashMap readTermDefinitionHashMap() throws IOException {
		if (!isTermDefinitionLocationValid()) {
			throw new IOException("Term definition location not valid");
		} else {
			FileInputStream fIS = new FileInputStream(setTermDefinitionLocation);
			String enc = IOConstants.ISO_LATIN_1_CHARACTER_ENCODING;
			InputStreamReader iSR = new InputStreamReader(fIS, enc);
			BufferedReader in = new BufferedReader(iSR);
			HashMap termDefMap = new HashMap();
			String line = null;
			
			while ((line = in.readLine()) != null) {
				StringTokenizer tokenizer = new StringTokenizer(line, DEF_DELIMITERS);
				
				if (tokenizer.countTokens() != 2) {
					throw new IOException("Invalid line in definition file: '" + line + "'");
				}
				
				String term = tokenizer.nextToken();
				String type = tokenizer.nextToken();
				termDefMap.put(term, type);
			}
			
			fIS.close();
			
			return termDefMap;
		}
	}
	
	/**
	 * Writes the hash map of strings back to file, in most
	 * cases called after it has been read and modified. This
	 * is done on a new, separate thread. Note that this method
	 * will not fire an IOException if the separate thread could
	 * not write out the hashmap.
	 */
	private void writeTermDefinitionHashMap(final HashMap termDefMap) throws IOException {
		if (!isTermDefinitionLocationValid()) {
			throw new IOException("Term definition location not valid");
		} else {
			if ((termDefWriterThread != null) && (termDefWriterThread.isAlive())) {
				try	{
					termDefWriterThread.interrupt();
					termDefWriterThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			termDefWriterThread = new Thread(new Runnable()	{
				public void run() {
					try	{
						Thread.sleep(5000); // write out (if necessary) every 5 seconds
						
						//lock object during write (see top)
						synchronized(ParsedTermDataHandler.this) {
							FileOutputStream fOS = new FileOutputStream(setTermDefinitionLocation);
							String enc = IOConstants.ISO_LATIN_1_CHARACTER_ENCODING;
							OutputStreamWriter writer = new OutputStreamWriter(fOS, enc);
							PrintWriter out = new PrintWriter(writer, true); // autoflush...
							String[] termArr = new String[termDefMap.size()];
							termDefMap.keySet().toArray(termArr);
							Arrays.sort(termArr, comp); // sort terms...
							String currentTerm = null;
							int preferredTabs = 4;
							int termTabs = -1;
							int tabs = -1;
							
							for (int ctr=0; ctr<termArr.length; ctr++) {
								currentTerm = termArr[ctr];
								termTabs = currentTerm.length() / 8;
								tabs = preferredTabs - termTabs;
								out.print(currentTerm);
								for (int tc=0; tc<tabs; tc++) { 
									out.print("\t"); 
								}
								out.println(termDefMap.get(currentTerm));
							}
							
							fOS.close();
						}
					} catch (InterruptedException e) {
						// this is normal if additions are done within interval
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			termDefWriterThread.start();
		}
	}
	
	private class StringNonCaseComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			return ((((String)o1).toLowerCase()).compareTo(((String)o2).toLowerCase()));
		}
	}
}