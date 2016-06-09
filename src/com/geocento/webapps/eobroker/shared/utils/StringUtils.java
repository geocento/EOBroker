package com.geocento.webapps.eobroker.shared.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StringUtils {

	   /**
     * <p>Strips any of a set of characters from the end of a String.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.
     * An empty string ("") input returns the empty string.</p>
     *
     * <p>If the stripChars String is <code>null</code>, whitespace is
     * stripped as defined by {@link Character#isWhitespace(char)}.</p>
     *
     * <pre>
     * StringUtils.stripEnd(null, *)          = null
     * StringUtils.stripEnd("", *)            = ""
     * StringUtils.stripEnd("abc", "")        = "abc"
     * StringUtils.stripEnd("abc", null)      = "abc"
     * StringUtils.stripEnd("  abc", null)    = "  abc"
     * StringUtils.stripEnd("abc  ", null)    = "abc"
     * StringUtils.stripEnd(" abc ", null)    = " abc"
     * StringUtils.stripEnd("  abcyx", "xyz") = "  abc"
     * </pre>
     *
     * @param str  the String to remove characters from, may be null
     * @param stripChars  the characters to remove, null treated as whitespace
     * @return the stripped String, <code>null</code> if null String input
     */
    public static String stripEnd(String str, String stripChars) {
        int end;
        if (str == null || (end = str.length()) == 0) {
            return str;
        }

        if (stripChars == null || stripChars.length() == 0) {
            return str;
        } else {
            while ((end != 0) && (stripChars.indexOf(str.charAt(end - 1)) != -1)) {
                end--;
            }
        }
        return str.substring(0, end);
    }

    public static String join(List<String> strings, String separator) {
    	if(strings == null) {
    		return "";
    	}
    	return join(strings.toArray(new String[strings.size()]), separator);
    }
    
    public static String join(String[] strings, String separator) {
        StringBuffer result = new StringBuffer();
        
        if(strings == null)  {
        	return "";
        }

        for (String s : strings) {
          if (result.length() != 0) {
            result.append(separator);
          }
          result.append(s);
        }

        return result.toString();
    }

	public static String extract(String result, String leftString, String rightString) {
		int startIndex = result.indexOf(leftString);
		if(startIndex == -1) {
			return "";
		}
		int endIndex = result.indexOf(rightString, startIndex);
		if(endIndex == -1) {
			return "";
		}
		startIndex += leftString.length();
		if(startIndex >= endIndex) {
			return "";
		}
		return result.substring(startIndex, endIndex);
	}

	public static List<String> extracts(String result, String leftString, String rightString) {
		List<String> values = new ArrayList<String>();
		// max default number of iterations
		for(int index = 0; index < result.length(); index++) {
			String value = extract(result, leftString, rightString);
			if(!value.equalsIgnoreCase("")) {
				values.add(value);
				result = result.substring(result.indexOf(rightString) + 1);
			} else {
				return values;
			}
		}
		
		return values;
	}

    public static boolean areStringEqualsOrNull(String first, String second) {
        return (first == null && second == null) ||
                (first != null && second != null && first.contentEquals(second));
    }

    public static boolean isEmpty(String value) {
        return value == null || value.length() == 0;
    }

    public static String generateSelectionString(Set<String> values, Set<String> selections) {
        if(selections == null) {
            return "all";
        }
        if(selections.size() == 0) {
            return "none";
        }
        if(selections.size() == values.size()) {
            return "all";
        }
        String label = "";
        if(selections.size() > values.size() - 4) {
            label = "all except ";
            int index = 0;
            for(String value : values) {
                if(!selections.contains(value)) {
                    if(label.length() + value.length() > 90) {
                        label += (values.size() - selections.size() - index) + " more...";
                        break;
                    }
                    label += value + ", ";
                    index++;
                }
            }
        } else {
            int index = 0;
            for(String selection : selections) {
                if(label.length() + selection.length() > 90) {
                    label += (selections.size() - index) + " more...";
                    break;
                }
                label += selection + ", ";
                index++;
            }
        }
        return StringUtils.stripEnd(label, ", ");
    }
}
