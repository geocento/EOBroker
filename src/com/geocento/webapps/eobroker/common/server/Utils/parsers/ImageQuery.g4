grammar ImageQuery;

/*
Parser
*/
r: (INT)*;

/*
Lexer
*/

INT : ('0'..'9')+;
WS : [ \t\r\n]+ -> skip;
REAL:   INT '.' INT;
RANGE:   ('..' | 'between');
GT: ('>' | 'more than' | 'higher than');
LT: ('<' | 'less than' | 'lower than');
RESOLUTION: ('res' | 'resolution');
VHR: ('very high res' | 'very high resolution' | 'vhr');
HR: ('high res' | 'high resolution' | 'hr');
MR: ('medium res' | 'medium resolution' | 'mr');
SENSORTYPE: ('optical' | 'sar' | 'radar');
UNKNOWN: .;
