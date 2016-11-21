// Generated from C:/Users/thomas/IdeaProjects/EOBroker/src/com/geocento/webapps/eobroker/common/server/Utils/parsers\ImageQuery.g4 by ANTLR 4.5.3
package com.geocento.webapps.eobroker.common.server.Utils.parsers;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ImageQueryLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		INT=1, WS=2, REAL=3, RANGE=4, GT=5, LT=6, RESOLUTION=7, VHR=8, HR=9, MR=10, 
		SENSORTYPE=11, UNKNOWN=12;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"INT", "WS", "REAL", "RANGE", "GT", "LT", "RESOLUTION", "VHR", "HR", "MR", 
		"SENSORTYPE", "UNKNOWN"
	};

	private static final String[] _LITERAL_NAMES = {
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "INT", "WS", "REAL", "RANGE", "GT", "LT", "RESOLUTION", "VHR", "HR", 
		"MR", "SENSORTYPE", "UNKNOWN"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public ImageQueryLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "ImageQuery.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\16\u00e5\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\3\2\6\2\35\n\2\r\2\16\2\36\3\3\6\3\"\n\3\r\3"+
		"\16\3#\3\3\3\3\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5"+
		"\65\n\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3"+
		"\6\3\6\3\6\3\6\3\6\3\6\5\6L\n\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3"+
		"\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7b\n\7\3\b\3\b\3\b\3\b\3"+
		"\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\5\bq\n\b\3\t\3\t\3\t\3\t\3\t\3\t\3"+
		"\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t"+
		"\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\5\t\u0097\n\t\3\n\3\n"+
		"\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3"+
		"\n\3\n\3\n\3\n\3\n\3\n\5\n\u00b2\n\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\5\13\u00d1\n\13\3\f\3\f\3\f\3"+
		"\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\5\f\u00e2\n\f\3\r\3\r\2"+
		"\2\16\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\3\2\3\5"+
		"\2\13\f\17\17\"\"\u00f4\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2"+
		"\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25"+
		"\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\3\34\3\2\2\2\5!\3\2\2\2\7\'\3\2\2\2"+
		"\t\64\3\2\2\2\13K\3\2\2\2\ra\3\2\2\2\17p\3\2\2\2\21\u0096\3\2\2\2\23\u00b1"+
		"\3\2\2\2\25\u00d0\3\2\2\2\27\u00e1\3\2\2\2\31\u00e3\3\2\2\2\33\35\4\62"+
		";\2\34\33\3\2\2\2\35\36\3\2\2\2\36\34\3\2\2\2\36\37\3\2\2\2\37\4\3\2\2"+
		"\2 \"\t\2\2\2! \3\2\2\2\"#\3\2\2\2#!\3\2\2\2#$\3\2\2\2$%\3\2\2\2%&\b\3"+
		"\2\2&\6\3\2\2\2\'(\5\3\2\2()\7\60\2\2)*\5\3\2\2*\b\3\2\2\2+,\7\60\2\2"+
		",\65\7\60\2\2-.\7d\2\2./\7g\2\2/\60\7v\2\2\60\61\7y\2\2\61\62\7g\2\2\62"+
		"\63\7g\2\2\63\65\7p\2\2\64+\3\2\2\2\64-\3\2\2\2\65\n\3\2\2\2\66L\7@\2"+
		"\2\678\7o\2\289\7q\2\29:\7t\2\2:;\7g\2\2;<\7\"\2\2<=\7v\2\2=>\7j\2\2>"+
		"?\7c\2\2?L\7p\2\2@A\7j\2\2AB\7k\2\2BC\7i\2\2CD\7j\2\2DE\7g\2\2EF\7t\2"+
		"\2FG\7\"\2\2GH\7v\2\2HI\7j\2\2IJ\7c\2\2JL\7p\2\2K\66\3\2\2\2K\67\3\2\2"+
		"\2K@\3\2\2\2L\f\3\2\2\2Mb\7>\2\2NO\7n\2\2OP\7g\2\2PQ\7u\2\2QR\7u\2\2R"+
		"S\7\"\2\2ST\7v\2\2TU\7j\2\2UV\7c\2\2Vb\7p\2\2WX\7n\2\2XY\7q\2\2YZ\7y\2"+
		"\2Z[\7g\2\2[\\\7t\2\2\\]\7\"\2\2]^\7v\2\2^_\7j\2\2_`\7c\2\2`b\7p\2\2a"+
		"M\3\2\2\2aN\3\2\2\2aW\3\2\2\2b\16\3\2\2\2cd\7t\2\2de\7g\2\2eq\7u\2\2f"+
		"g\7t\2\2gh\7g\2\2hi\7u\2\2ij\7q\2\2jk\7n\2\2kl\7w\2\2lm\7v\2\2mn\7k\2"+
		"\2no\7q\2\2oq\7p\2\2pc\3\2\2\2pf\3\2\2\2q\20\3\2\2\2rs\7x\2\2st\7g\2\2"+
		"tu\7t\2\2uv\7{\2\2vw\7\"\2\2wx\7j\2\2xy\7k\2\2yz\7i\2\2z{\7j\2\2{|\7\""+
		"\2\2|}\7t\2\2}~\7g\2\2~\u0097\7u\2\2\177\u0080\7x\2\2\u0080\u0081\7g\2"+
		"\2\u0081\u0082\7t\2\2\u0082\u0083\7{\2\2\u0083\u0084\7\"\2\2\u0084\u0085"+
		"\7j\2\2\u0085\u0086\7k\2\2\u0086\u0087\7i\2\2\u0087\u0088\7j\2\2\u0088"+
		"\u0089\7\"\2\2\u0089\u008a\7t\2\2\u008a\u008b\7g\2\2\u008b\u008c\7u\2"+
		"\2\u008c\u008d\7q\2\2\u008d\u008e\7n\2\2\u008e\u008f\7w\2\2\u008f\u0090"+
		"\7v\2\2\u0090\u0091\7k\2\2\u0091\u0092\7q\2\2\u0092\u0097\7p\2\2\u0093"+
		"\u0094\7x\2\2\u0094\u0095\7j\2\2\u0095\u0097\7t\2\2\u0096r\3\2\2\2\u0096"+
		"\177\3\2\2\2\u0096\u0093\3\2\2\2\u0097\22\3\2\2\2\u0098\u0099\7j\2\2\u0099"+
		"\u009a\7k\2\2\u009a\u009b\7i\2\2\u009b\u009c\7j\2\2\u009c\u009d\7\"\2"+
		"\2\u009d\u009e\7t\2\2\u009e\u009f\7g\2\2\u009f\u00b2\7u\2\2\u00a0\u00a1"+
		"\7j\2\2\u00a1\u00a2\7k\2\2\u00a2\u00a3\7i\2\2\u00a3\u00a4\7j\2\2\u00a4"+
		"\u00a5\7\"\2\2\u00a5\u00a6\7t\2\2\u00a6\u00a7\7g\2\2\u00a7\u00a8\7u\2"+
		"\2\u00a8\u00a9\7q\2\2\u00a9\u00aa\7n\2\2\u00aa\u00ab\7w\2\2\u00ab\u00ac"+
		"\7v\2\2\u00ac\u00ad\7k\2\2\u00ad\u00ae\7q\2\2\u00ae\u00b2\7p\2\2\u00af"+
		"\u00b0\7j\2\2\u00b0\u00b2\7t\2\2\u00b1\u0098\3\2\2\2\u00b1\u00a0\3\2\2"+
		"\2\u00b1\u00af\3\2\2\2\u00b2\24\3\2\2\2\u00b3\u00b4\7o\2\2\u00b4\u00b5"+
		"\7g\2\2\u00b5\u00b6\7f\2\2\u00b6\u00b7\7k\2\2\u00b7\u00b8\7w\2\2\u00b8"+
		"\u00b9\7o\2\2\u00b9\u00ba\7\"\2\2\u00ba\u00bb\7t\2\2\u00bb\u00bc\7g\2"+
		"\2\u00bc\u00d1\7u\2\2\u00bd\u00be\7o\2\2\u00be\u00bf\7g\2\2\u00bf\u00c0"+
		"\7f\2\2\u00c0\u00c1\7k\2\2\u00c1\u00c2\7w\2\2\u00c2\u00c3\7o\2\2\u00c3"+
		"\u00c4\7\"\2\2\u00c4\u00c5\7t\2\2\u00c5\u00c6\7g\2\2\u00c6\u00c7\7u\2"+
		"\2\u00c7\u00c8\7q\2\2\u00c8\u00c9\7n\2\2\u00c9\u00ca\7w\2\2\u00ca\u00cb"+
		"\7v\2\2\u00cb\u00cc\7k\2\2\u00cc\u00cd\7q\2\2\u00cd\u00d1\7p\2\2\u00ce"+
		"\u00cf\7o\2\2\u00cf\u00d1\7t\2\2\u00d0\u00b3\3\2\2\2\u00d0\u00bd\3\2\2"+
		"\2\u00d0\u00ce\3\2\2\2\u00d1\26\3\2\2\2\u00d2\u00d3\7q\2\2\u00d3\u00d4"+
		"\7r\2\2\u00d4\u00d5\7v\2\2\u00d5\u00d6\7k\2\2\u00d6\u00d7\7e\2\2\u00d7"+
		"\u00d8\7c\2\2\u00d8\u00e2\7n\2\2\u00d9\u00da\7u\2\2\u00da\u00db\7c\2\2"+
		"\u00db\u00e2\7t\2\2\u00dc\u00dd\7t\2\2\u00dd\u00de\7c\2\2\u00de\u00df"+
		"\7f\2\2\u00df\u00e0\7c\2\2\u00e0\u00e2\7t\2\2\u00e1\u00d2\3\2\2\2\u00e1"+
		"\u00d9\3\2\2\2\u00e1\u00dc\3\2\2\2\u00e2\30\3\2\2\2\u00e3\u00e4\13\2\2"+
		"\2\u00e4\32\3\2\2\2\r\2\36#\64Kap\u0096\u00b1\u00d0\u00e1\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}