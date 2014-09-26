grammar Gherkin;

tagName         : ~(WS | EOL)+;

tag             : AT tagName;

lineContent     : ~EOL+;

comment         : WS? HASH lineContent EOL;

annotation      : comment
                | WS? tag (WS tag)* EOL
                | WS? EOL;

featureTitle    : lineContent (EOL lineContent)*;

feature         : annotation*
                  WS? FEATURE_KW WS? COLON WS? featureTitle EOL
                  EOL*
                  background?
                  abstractScenario*
                  EOL* EOF;

background      : annotation*
                  WS? BACKGROUND_KW WS? COLON WS? lineContent EOL
                  step+
                  EOL*;

abstractScenario: scenario | outline;

scenario        : annotation*
                  WS? SCENARIO_KW WS? COLON WS? lineContent EOL
                  step+
                  EOL*;

outline         : annotation*
                  WS? OUTLINE_KW WS? COLON WS? lineContent EOL
                  step+
                  examples+
                  EOL*;

examples        : annotation*
                  WS? EXAMPLES_KW WS? COLON WS? lineContent? EOL
                  row+;

step            : annotation*
                  WS? STEP_KW WS lineContent EOL
                  (row+ | document+)?;

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
