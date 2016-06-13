package com.geocento.webapps.eobroker.common.client.utils;

import com.geocento.webapps.eobroker.common.shared.utils.StringUtils;

import java.util.HashMap;

/**
 * Created by thomas on 03/06/2016.
 */
public class Utils {

    static public String TOKEN_DELIMITER = "&";

    // generate a set of url tokens that can be extracted with the extractTokens method
    public static String generateTokens(String... values) {
        String tokensString = "";
        if(values == null || values.length == 0 || values.length%2 != 0) {
            return "";
        }
        for(int index = 0; index < values.length; index += 2) {
            tokensString += values[index] + "=" + values[index + 1] + TOKEN_DELIMITER;
        }
        // remove trailing delimiter
        return StringUtils.stripEnd(tokensString, TOKEN_DELIMITER);
    }

    public static HashMap<String, String> extractTokens(String tokensString) {
        HashMap<String, String> tokens = new HashMap<String, String>();
        if(tokensString != null && tokensString.length() > 0) {
            // tokens are made of token1=value1&token2=value2, etc...
            String[] actions = tokensString.split(TOKEN_DELIMITER);
            for(String action : actions) {
                String[] token = action.split("=");
                if(token.length == 2) {
                    tokens.put(token[0], token[1]);
                }
                if(token.length == 1) {
                    tokens.put(token[0], "");
                }
            }
        }
        return tokens;
    }

}
