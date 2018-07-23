grammar Gherkin;

tagName         : ~(WS | EOL)+;

tag             : AT tagName;

lineContent     : ~EOL+;

comment         : WS? HASH lineContent EOL;

annotation      : comment
                | WS? tag ((WS | EOL+) tag)* WS* EOL
                | WS? EOL;

trailingComment : comment;

descriptionLine : WS? ~(FEATURE_KW | BACKGROUND_KW | SCENARIO_KW | OUTLINE_KW | STEP_KW | EXAMPLES_KW | AT | HASH | PIPE | WS | EOL) ~EOL* EOL
                | WS? EOL
                ;

name     : descriptionLine+
                ;

feature         : annotation*
                  WS? FEATURE_KW WS? COLON WS? lineContent EOL
                  name?
                  background?
                  abstractScenario*
                  (WS? EOL | trailingComment)*
                  EOF;

background      : annotation*
                  WS? BACKGROUND_KW WS? COLON WS? lineContent? EOL
                  (step | WS? EOL)+;

abstractScenario: scenario | outline;

scenario        : annotation*
                  WS? SCENARIO_KW WS? COLON WS? lineContent EOL
                  (step | WS? EOL)*;

outline         : annotation*
                  WS? OUTLINE_KW WS? COLON WS? lineContent EOL
                  (step | WS? EOL)*
                  examples+
                  (WS? EOL)*;

examples        : annotation*
                  WS? EXAMPLES_KW WS? COLON WS? lineContent? EOL
                  row+;

step            : annotation*
                  WS? STEP_KW WS lineContent EOL
                  (row+ | document+)?;

row             : annotation*
                  WS? PIPE (cell PIPE)+ WS? EOL;

cell            : ~(PIPE|EOL)*;

document        : documentIndent TRIPLE_QUOTE documentContent TRIPLE_QUOTE WS? EOL;

documentIndent  : WS?;

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
