package com.geocento.webapps.eobroker.common.server.Utils.parsers;

import com.geocento.webapps.eobroker.common.shared.imageapi.SENSOR_TYPE;
import com.geocento.webapps.eobroker.common.shared.imageapi.SensorFilters;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SensorQuery {

    Double minResolution;
    Double maxResolution;
    String sensor_type;

    Vocabulary vocabulary;
    List<? extends Token> tokens;

    private int index;
    private String queryString;
    private String suggestions;
    private String error;
    private String lastkeywords;

    public SensorQuery(String keywordsString) {
        ImageQueryLexer lex = new ImageQueryLexer(new ANTLRInputStream(keywordsString));
        vocabulary = lex.getVocabulary();
        this.tokens = lex.getAllTokens();
        ingestTokens();
    }

    public void ingestTokens() {
        queryString = "";
        error = "";
        suggestions = "";
        lastkeywords = "";
        Token token = getNextToken();
        while (token != null) {
            String tokenName = vocabulary.getDisplayName(token.getType());
            switch(tokenName) {
                case "SENSORTYPE":
                    sensor_type = token.getText();
                    queryString += token.getText() + " imagery ";
                    break;
                case "VHR":
                    minResolution = 0.0;
                    maxResolution = 1.0;
                    queryString += " resolution < 1.0";
                    break;
                case "HR":
                    minResolution = 1.0;
                    maxResolution = 10.0;
                    queryString += " resolution >= 1.0 and resolution < 10.0";
                    break;
                case "MR":
                    minResolution = 10.0;
                    queryString += " resolution >= 10.0";
                    break;
                case "RESOLUTION":
                    queryString += " " + token.getText();
                    // look for comparator next
                    token = getNextToken();
                    if(token == null) {
                        // add some suggestion
                        suggestions = ">;<;";
                    } else {
                        queryString += " " + token.getText();
                        switch (vocabulary.getDisplayName(token.getType())) {
                            case "GT":
                                token = getNextToken();
                                if (token == null) {
                                    suggestions = "0.5;1.0;10.0;";
                                } else {
                                    switch (vocabulary.getDisplayName(token.getType())) {
                                        case "REAL":
                                        case "INT":
                                            minResolution = Double.parseDouble(token.getText());
                                            queryString += " " + token.getText();
                                            break;
                                        default:
                                            error = "I expect a number in meters";
                                            suggestions = "0.5;1.0;10.0;";
                                    }
                                }
                                break;
                            case "LT":
                                token = getNextToken();
                                if (token == null) {
                                    suggestions = "0.5;1.0;10.0;";
                                } else {
                                    switch (vocabulary.getDisplayName(token.getType())) {
                                        case "REAL":
                                        case "INT":
                                            maxResolution = Double.parseDouble(token.getText());
                                            queryString += " " + token.getText();
                                            break;
                                        default:
                                            error = "I expect a number in meters";
                                            suggestions = "0.5;1.0;10.0;";
                                    }
                                }
                                break;
                            default:
                                suggestions = "<;>;very high resolution;high resolution;medium resolution";
                        }
                    }
                    break;
            }
            token = getNextToken();
        }
        if(suggestions.length() == 0) {
            if(lastkeywords.length() == 0) {
                if (sensor_type == null) {
                    suggestions += "optical imagery;radar imagery;";
                }
                if (minResolution == null && maxResolution == null) {
                    suggestions += "very high resolution;high resolution;medium resolution;resolution < 10.0";
                }
            } else {
                // try to match the keyword
                List<String> keywords = new ArrayList<String>();
                keywords.addAll(Arrays.asList("between", ">", "more than","higher than","<","less than","lower than"));
                if(minResolution == null && maxResolution == null) {
                    keywords.addAll(Arrays.asList("res", "resolution", "very high res", "very high resolution", "vhr", "high res", "high resolution", "hr", "medium res", "medium resolution", "mr"));
                }
                if(sensor_type == null) {
                    keywords.addAll(Arrays.asList("optical imagery", "sar imagery", "radar imagery"));
                }
                for(String keyword : keywords) {
                    if(keyword.startsWith(lastkeywords)) {
                        suggestions += keyword + ";";
                    }
                }
            }
        }
    }

    private Token getNextToken() {
        if(index >= tokens.size()) {
            return null;
        }
        Token token = tokens.get(index++);
        String tokenName = vocabulary.getDisplayName(token.getType());
        switch (tokenName) {
            // skip the unknown token
            case "UNKNOWN":
                lastkeywords += token.getText();
                return getNextToken();
        }
        // reset last keywords
        lastkeywords = "";
        return token;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(String suggestions) {
        this.suggestions = suggestions;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public SensorFilters getSensorFilters() {
        SensorFilters sensorFilters = new SensorFilters();
        sensorFilters.setType(sensor_type == null ? SENSOR_TYPE.All : sensor_type.startsWith("opt") ? SENSOR_TYPE.Optical : SENSOR_TYPE.Radar);
        sensorFilters.setMinResolution(minResolution == null ? 0 : minResolution);
        sensorFilters.setMaxResolution(maxResolution == null ? 10000 : maxResolution);
        return sensorFilters;
    }
}

