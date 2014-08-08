grammar Gherkin;

tagName         : ~(WS | EOL)+;

tag             : AT tagName;

tags            : WS* tag ((WS | EOL)+ tag)* EOL;

lineContent     : ~EOL+;

comment         : WS* HASH lineContent EOL;

comments        : ( comment | WS* EOL)+;

feature         : comments?
                  tags?
                  WS* FEATURE_KW WS* COLON WS* (lineContent EOL)+
                  EOL*
                  background?
                  abstractScenario*
                  EOL* EOF;

background      : comments?
                  tags?
                  WS* BACKGROUND_KW WS* COLON WS* lineContent EOL
                  step+
                  EOL*;

abstractScenario: scenario | outline;

scenario        : comments?
                  tags?
                  WS* SCENARIO_KW WS* COLON WS* lineContent EOL
                  step+
                  EOL*;

outline         : comments?
                  tags?
                  WS* OUTLINE_KW WS* COLON WS* lineContent EOL
                  step+
                  examples+
                  EOL*;

examples        : comments?
                  WS* EXAMPLES_KW WS* COLON WS* lineContent? EOL
                  table;

step            : comments?
                  tags?
                  WS* STEP_KW WS+ lineContent EOL
                  (table | document)?;

table           : row+;

row             : comments?
                  tags?
                  WS* PIPE (cell PIPE)+ EOL;

cell            : ~(PIPE|EOL)*;

document        : WS* TRIPLE_QUOTE documentContent TRIPLE_QUOTE EOL;

documentContent : ~TRIPLE_QUOTE*?;

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
