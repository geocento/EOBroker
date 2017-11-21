package com.geocento.webapps.eobroker.common.shared.utils;

import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductDatasetDTO;

import java.util.*;

public class ListUtil {

    static public interface CheckValue<T> {
		boolean isValue(T value);
	}
	
	static public interface Mutate<O, T> {
		T mutate(O object);
	}

    static public interface MutateWithException<O, T> {
        T mutate(O object) throws Exception;
    }

    /*
	 * returns a sub list of the initial list with only the elements for which the check value function returns a true value
	 */
	static public <T extends Object> List<T> filterValues(List<T> list, CheckValue<T> check) {
		List<T> filteredList = new ArrayList<T>();
		for(T value : list) {
			if(check.isValue(value)) {
				filteredList.add(value);
			}
		}
		return filteredList;
	}

	/*
	 * trims the initial list from the elements for which the check value function returns a true value
	 * returns the list fo removed values
	 */
	static public <T extends Object> List<T> removeValues(List<T> list, CheckValue<T> check) {
		List<T> removeValuesList = new ArrayList<T>();
		for(T value : list) {
			if(check.isValue(value)) {
				removeValuesList.add(value);
			}
		}
		list.removeAll(removeValuesList);
		return removeValuesList;
	}

	/*
	 * returns a list from one element
	 */
	static public <T extends Object> List<T> toList(T value) {
		List<T> list = new ArrayList<T>();
		list.add(value);
		return list;
	}

	/*
	 * returns a list from an array of values
	 */
	static public <T extends Object> List<T> toList(T[] values) {
		List<T> list = new ArrayList<T>();
		for(T value : values) {
			list.add(value);
		}
		return list;
	}

    /*
     * returns a set from an array of values
     */
    static public <T extends Object> Set<T> toSet(T[] values) {
        Set<T> list = new HashSet<T>();
        for(T value : values) {
            list.add(value);
        }
        return list;
    }

    /**
     *
     * @return whether the collection is null or empty
     */
    public static <T extends Object> boolean isNullOrEmpty(List<T> values) {
        return values == null || values.isEmpty();
    }

    /*
	 * returns a list from a list of values
	 */
	static public <T extends Object> List<T> toListArg(T... values) {
		List<T> list = new ArrayList<T>();
		for(T value : values) {
			list.add(value);
		}
		return list;
	}

	/*
	 * creates a new list which is the merge of the two lists, avoiding duplicates in the resulting list
	 * duplicates are found by using the contains method
	 */
	public static <T extends Object> List<T> merge(List<T> firstList, List<T> secondList) {
		List<T> list = new ArrayList<T>(firstList);
		if(secondList != null) {
			for(T value : secondList) {
				if(!list.contains(value)) {
					list.add(value);
				}
			}
		}
		return list;
	}

	public static <T extends Object> int insertBefore(List<T> list, T value, CheckValue<T> checkValue) {
		int index = findIndex(list, checkValue);
		// insert only if the value was found
		if(index != -1) {
			list.add(index, value);
		}
		return index;
	}
	
	/*
	 * returns the first index in the list for which the checkValue method returns true and returns -1 if checkValue never returned true
	 */
	public static <T extends Object> int findIndex(List<T> list, CheckValue<T> checkValue) {
		for(int index = 0; index < list.size(); index++) {
			if(checkValue.isValue(list.get(index))) {
				return index;
			}
		}
		return -1;
	}

    /*
     * returns the first item in the list for which the checkValue method returns true and returns null if checkValue never returned true
     */
    public static <T extends Object> T findValue(List<T> list, CheckValue<T> checkValue) {
        for(int index = 0; index < list.size(); index++) {
            if(checkValue.isValue(list.get(index))) {
                return list.get(index);
            }
        }
        return null;
    }

    public static <O extends Object, T extends Object> List<T> mutate(List<O> firstList, Mutate<O, T> mutate) {
		List<T> list = new ArrayList<T>();
		if(firstList != null) {
			for(O value : firstList) {
                if(value != null) {
                    T mutatedValue = mutate.mutate(value);
                    if (mutatedValue != null) {
                        list.add(mutatedValue);
                    }
                }
			}
		}
		return list;
	}

    public static <O extends Object, T extends Object> List<T> mutateWithException(List<O> firstList, MutateWithException<O, T> mutate) throws Exception {
        List<T> list = new ArrayList<T>();
        if(firstList != null) {
            for(O value : firstList) {
                if(value != null) {
                    T mutatedValue = mutate.mutate(value);
                    if (mutatedValue != null) {
                        list.add(mutatedValue);
                    }
                }
            }
        }
        return list;
    }

    public static <O extends Object, T extends Object> Set<T> mutate(Set<O> firstList, Mutate<O, T> mutate) {
        Set<T> list = new HashSet<T>();
        if(firstList != null) {
            for(O value : firstList) {
                list.add(mutate.mutate(value));
            }
        }
        return list;
    }

    public static <T extends Object> boolean listEquals(List<T> firstList, List<T> secondList) {
		if(firstList == null && secondList == null) {
			return true;
		}
		if(firstList == null || secondList == null) {
			return false;
		}
		if(firstList.size() != secondList.size()) {
			return false;
		}
		for(T first : firstList) {
			if(!secondList.contains(first)) {
				return false;
			}
		}
		for(T second : secondList) {
			if(!firstList.contains(second)) {
				return false;
			}
		}
		return true;
	}

	public static <T extends Object> List<T> diffList(List<T> firstList, List<T> secondList) {
		List<T> diffList = new ArrayList<T>();
		if(firstList == null && secondList == null) {
			return diffList;
		}
		if(firstList == null) {
			return secondList;
		}
		if(secondList == null) {
			return firstList;
		}
		for(T first : firstList) {
			if(!secondList.contains(first)) {
				diffList.add(first);
			}
		}
		for(T second : secondList) {
			if(!firstList.contains(second)) {
				diffList.add(second);
			}
		}
		return diffList;
	}

	static public interface GetLabel<T> {
		String getLabel(T value);
	}

    static public interface GetValue<T, O> {
        T getLabel(O value);

        <O extends Object> List<O> createList();
    }

    static public <T extends Object> HashMap<String, Double> createLabelCountMap(List<T> items, GetLabel<T> getLabels) {
	    HashMap<String, Double> map = new HashMap<String, Double>();
	    // check that products have actually been loaded
		if(items == null || items.size() == 0) {
			return map;
		}
		for(T item : items) {
			String label = getLabels.getLabel(item);
			Double value = map.get(label);
			if(value == null) {
				value = new Double(0);
			}
			map.put(label, new Double(value + 1));
		}
	    return map;
	}
	
	static public interface GetKeyPair<T, O> {
		String getLabel(T value);
		O getValue(T value);
	}
	
	static public <T extends Object> HashMap<String, Double> createLabelMap(List<T> items, GetKeyPair<T, Double> getValues) {
	    HashMap<String, Double> map = new HashMap<String, Double>();
	    // check that products have actually been loaded
		if(items == null || items.size() == 0) {
			return map;
		}
		for(T item : items) {
			String label = getValues.getLabel(item);
			Double value = map.get(label);
			if(value == null) {
				value = new Double(0);
			}
			map.put(label, new Double(value + getValues.getValue(item)));
		}
	    return map;
	}

    static public <T extends Object> HashSet<String> createLabelSet(List<T> items, GetLabel<T> getValues) {
        HashSet<String> valuesSet = new HashSet<String>();
        // check that products have actually been loaded
        if(items == null || items.size() == 0) {
            return valuesSet;
        }
        for(T item : items) {
            String label = getValues.getLabel(item);
            valuesSet.add(label);
        }
        return valuesSet;
    }

    static public interface AddKeyPair<T, O> {
		String getLabel(T value);
		O getValue(T value, O previousValue);
	}

    static public <T extends Object, O extends Object> HashMap<String, O> createLabelMap(List<T> items, AddKeyPair<T, O> getValues) {
	    HashMap<String, O> map = new HashMap<String, O>();
	    // check that products have actually been loaded
		if(items == null || items.size() == 0) {
			return map;
		}
		for(T item : items) {
			String label = getValues.getLabel(item);
			map.put(label, getValues.getValue(item, map.get(label)));
		}
	    return map;
	}

    static public <T extends Object, O extends Object> HashMap<T, List<O>> group(List<O> items, GetValue<T, O> getValues) {
        HashMap<T, List<O>> map = new HashMap<T, List<O>>();
        // check that products have actually been loaded
        if(items == null || items.size() == 0) {
            return map;
        }
        for(O item : items) {
            T label = getValues.getLabel(item);
            List<O> list = map.get(label);
            if(list == null) {
                list = getValues.createList();
                map.put(label, list);
            }
            list.add(item);
        }
        return map;
    }

    public static <T extends Object> String toString(List<T> items, GetLabel<T> getLabel, String delimiter) {
        List<String> values = new ArrayList<String>();
        for(T item : items) {
            values.add(getLabel.getLabel(item));
        }
        return StringUtils.join(values, delimiter);
    }

    public static <T extends Object> int count(List<T> items, CheckValue<T> checkValue) {
        int count = 0;
        for(T item : items) {
            if(checkValue.isValue(item)) {
                count++;
            }
        }
        return count;
    }

    public static interface GetValuePair<T, K, V> {
        public K getKey(T value);
        public V getValue(T value);
    }

}
