grammar Gherkin;

tagName         : ~(WS | EOL)+;

tag             : AT tagName;

tags            : WS* tag ((WS | EOL)+ tag)* EOL;

lineContent         : ~EOL+;

comment         : WS* HASH lineContent EOL;

comments        : ( comment | WS* EOL)+;

feature         : comments?
                  tags?
                  start WS* FEATURE_KW WS* COLON WS* (lineContent EOL)+
                  EOL*
                  background?
                  abstractScenario*
                  EOL* EOF;

background      : comments?
                  tags?
                  start WS* BACKGROUND_KW WS* COLON WS* lineContent EOL
                  step+
                  EOL*;

abstractScenario: scenario | outline;

scenario        : comments?
                  tags?
                  start WS* SCENARIO_KW WS* COLON WS* lineContent EOL
                  step+
                  EOL*;

outline         : comments?
                  tags?
                  start WS* OUTLINE_KW WS* COLON WS* lineContent EOL
                  step+
                  examples
                  EOL*;

examples        : comments?
                  start WS* EXAMPLES_KW WS* COLON WS* EOL row+;

step            : comments?
                  tags?
                  start WS* STEP_KW WS+ lineContent EOL
                  (table | document)?;

table           : row+;

row             : comments?
                  tags?
                  start WS* PIPE (cell PIPE)+ EOL;

cell            : ~(PIPE|EOL)*;

document        : start WS* TRIPLE_QUOTE documentContent TRIPLE_QUOTE EOL;

documentContent : ~TRIPLE_QUOTE*?;

start           : { getCurrentToken().getCharPositionInLine() == 0}?;

STEP_KW         : 'Given' | 'When' | 'Then' | 'And';

FEATURE_KW      : 'Feature';

BACKGROUND_KW   : 'Background';

SCENARIO_KW     : 'Scenario';
OUTLINE_KW      : 'Scenario Outline';

EXAMPLES_KW     : 'Examples';

EOL             : '\n' | '\r\n';

WS              : [\t ];
PIPE            : '|';
ESCAPED_PIPE    : '\\|';
COLON           : ':';
AT              : '@';
HASH            : '#';

TRIPLE_QUOTE    : '"""';

QUOTE           : '"';

CHAR            : ~[\r\n\t :@#|\\"]+;
