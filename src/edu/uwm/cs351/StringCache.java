package edu.uwm.cs351;

import java.util.function.Consumer;

import edu.uwm.cs.util.Primes;

/*
 * Andrew Le
 * Homework 11 CS 351
 */

/**
 * A class to manage string instances.
 * All equal string instances that are interned will be identical.
 */
public class StringCache {
	// even with a Spy, we still use "private":
	private String[] table;
	private int numEntries;
	

	// TODO: hash helper function used by wellFormed and intern
	
	private int findIndex(String[] array, String key) {
		int hash = Math.abs(key.hashCode() % array.length);
			
		
		int count = 0;
		while (count < array.length) {
			if (key.equals(array[hash])) {
//				for (int i = hash-1; i >= 0; i--) {
//					if (table[i] == key) return -1;
//					if (i < 0) {
//						i = table.length -1;
//					}
//					if (i == hash) {
//						break;
//					}
//				}
				return hash;
			}
			if (array[hash] == null) break;
			count++;
			hash--;
			if (hash < 0) {
				hash = array.length - 1;
			}
		}

		return hash;
	}

	private static Consumer<String> reporter = (s) -> { System.err.println("Invariant error: " + s); };
	
	private boolean report(String error) {
		reporter.accept(error);
		return false;
	}
	
	private boolean wellFormed() {
		// 1. table is non-null and prime length
		// 2. number of entries is never more half the table size
		// 3. number of non-null entries in the table is numEntries
		// 4. every string in the array is hashed into the correct place
		//    using backward linear probing
		// TODO
		
		//Invariant 1
		if (table == null || !Primes.isPrime(table.length)) return report("table length is not prime");
		
		
		//Invariant 2
		if (numEntries > table.length/2) return report("numEntries is more than half of table size");
		
		//Invariant 3
		int count = 0;
		for (int i = 0; i < table.length; i++) {
			if (table[i] != null) {
				count++;
			}
		}
		if (count != numEntries) return report("number of non-null entries does not equal numEntries");
		
		//Invariant 4
		for (int i = 0; i < table.length; i++) {
			if (table[i] != null) {
				if (findIndex(table, table[i]) != i) return report("incorrect string in the array index");
			}
		}
		
		return true;
	}
	
	private StringCache(boolean ignored) {} // do not change
	
	/**
	 * Create an empty string cache.
	 */
	public StringCache() {
		// TODO
		table = new String[2];
		numEntries = 0;
		assert wellFormed() : "invariant broken in constructor"; 
	}
	
	// TODO: declare rehash helper method
	private int rehash(String[] array, String value) {
		int index = Math.abs(value.hashCode() % array.length);
		

		if (array[index] == null) {
			array[index] = value;
		}
		else {
			array[findIndex(array, value)] = value;
		}
		
		return -1;
	}
	
	
	/**
	 * Return a string equal to the argument.  
	 * For equal strings, the same (identical) result is always returned.
	 * As a special case, if null is passed, it is returned.
	 * @param value string, may be null
	 * @return a string equal to the argument (or null if the argument is null).
	 */
	public String intern(String value) {
		assert wellFormed() : "invariant broken before intern";
		// TODO, including calling rehash if needed
		if (value == null) {
			return null;
		}
		
		if (table[findIndex(table, value)] != null) {
			value = table[findIndex(table, value)];
		}
		else {
			rehash(table, value);
			numEntries++;
		}
		
		
		if (numEntries > table.length/2) {
		String[] temp = new String[Primes.nextPrime(4*numEntries)];
		for (int i = 0; i < table.length; i++) {
			if (table[i] != null) {
				rehash(temp, table[i]);
			}
		}
		table = temp;
		}
		
		
		assert wellFormed() : "invariant broken after intern";
		return value;
	}
	
	public static class Spy { // do not modify (or use!) this class
		/**
		 * Create a String Cache with the given data structure,
		 * that has not been checked.
		 * @return new debugging version of a StringCache
		 */
		public StringCache create(String[] t, int c) {
			StringCache sc = new StringCache(false);
			sc.table = t;
			sc.numEntries = c;
			return sc;
		}
		
		/**
		 * Return the number of entries in the string cache
		 * @param sc string cache, must not be null
		 * @return number of entries in the cache.
		 */
		public int getSize(StringCache sc) {
			return sc.numEntries;
		}
		
		/**
		 * Return capacity of the table in the cache
		 * @param sc cache to examine, must not be null
		 * @return capacity
		 */
		public int getCapacity(StringCache sc) {
			return sc.table.length;
		}
		
		public boolean wellFormed(StringCache sc) {
			return sc.wellFormed();
		}
		
		public Consumer<String> getReporter() {
			return reporter;
		}
		
		public void setReporter(Consumer<String> c) {
			reporter = c;
		}

	}
}
