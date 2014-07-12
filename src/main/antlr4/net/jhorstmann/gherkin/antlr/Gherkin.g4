grammar Gherkin;

content         : CHAR+;

comments        : (COMMENT EOL | EOL)+;

tags            : TAG+;

feature         : comments*
                  FEATURE (content EOL)+
                  EOL*
                  background?
                  abstractScenario*
                  EOL* EOF;

background      : comments?
                  BACKGROUND content EOL
                  step+
                  EOL*;

abstractScenario: scenario | outline;

scenario        : comments?
                  SCENARIO content EOL
                  step+
                  EOL*;

outline         : comments?
                  SCENARIO_OUTLINE content EOL
                  step+
                  examples
                  EOL*;

examples        : comments?
                  EXAMPLES EOL row+;

step            : comments?
                  STEP content EOL
                  (table | doc)?;

table           : row+;

row             : TABLE_START cell+ EOL;

cell            : TABLE_CELL;

doc             : DOC_STRING EOL;


fragment
COLON           : ':';

fragment
WS              : [\t ]+;

EOL             : '\n' | '\r\n';

fragment
START           : {getCharPositionInLine() == 0}?;

fragment
STEP_KW         : 'Given' | 'When' | 'Then' | 'And';

FEATURE         : START WS* 'Feature' WS* COLON WS*;

STEP            : START WS* STEP_KW WS*;
BACKGROUND      : START WS* 'Background' WS* COLON WS*;
SCENARIO_OUTLINE: START WS* 'Scenario Outline' WS* COLON WS*;
SCENARIO        : START WS* 'Scenario' WS* COLON WS*;
EXAMPLES        : START WS* 'Examples' WS* COLON WS*;

fragment
PIPE            : '|';
fragment
ESCAPED_PIPE    : '\\|';
fragment
CELL_CONTENT    : ~[\r\n|];

TABLE_START     : START WS* PIPE;
TABLE_CELL      : (ESCAPED_PIPE | CELL_CONTENT)*? PIPE;

TAG             : '@' ~[@\r\n\t ]+;
COMMENT         : START WS* '#' ~[\r\n]*;

fragment
TRIPLE_QUOTE    : '"""';

DOC_STRING      : START WS* TRIPLE_QUOTE .*? TRIPLE_QUOTE;

CHAR            : ~[\r\n];
