grammar Gherkin;

tagName         : ~(WS | EOL)+;

tag             : AT tagName;

tags            : WS? tag (WS tag)* EOL;

lineContent     : ~EOL+;

comment         : WS? HASH lineContent EOL;

annotation      : (comment | tags | WS? EOL);

feature         : annotation*
                  WS? FEATURE_KW WS? COLON WS? (lineContent EOL)+
                  EOL*
                  background?
                  abstractScenario*
                  EOL* EOF;

background      : annotation*
                  tags?
                  WS? BACKGROUND_KW WS? COLON WS? lineContent EOL
                  step+
                  EOL*;

abstractScenario: scenario | outline;

scenario        : annotation*
                  WS? SCENARIO_KW WS? COLON WS? lineContent EOL
                  step+
                  EOL*;

outline         : annotation*
                  tags?
                  WS? OUTLINE_KW WS? COLON WS? lineContent EOL
                  step+
                  examples+
                  EOL*;

examples        : annotation*
                  WS? EXAMPLES_KW WS? COLON WS? lineContent? EOL
                  table;

step            : annotation*
                  WS? STEP_KW WS lineContent EOL
                  (table | document)?;

table           : row+;

row             : annotation*
                  WS? PIPE (cell PIPE)+ EOL;

cell            : ~(PIPE|EOL)*;

document        : WS? TRIPLE_QUOTE documentContent TRIPLE_QUOTE EOL;

documentContent : ~TRIPLE_QUOTE*?;

STEP_KW         : 'Given' | 'When' | 'Then' | 'And';

FEATURE_KW      : 'Feature';

BACKGROUND_KW   : 'Background';

SCENARIO_KW     : 'Scenario';
OUTLINE_KW      : 'Scenario Outline';

EXAMPLES_KW     : 'Examples';

EOL             : '\n' | '\r\n';

WS              : [\t ]+;
PIPE            : '|';
ESCAPED_PIPE    : '\\|';
COLON           : ':';
AT              : '@';
HASH            : '#';

TRIPLE_QUOTE    : '"""';

QUOTE           : '"';

CHAR            : ~[\r\n\t :@#|\\"]+;
