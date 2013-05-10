grammar workflowGrammar;
file: workflowDefinition* EOF;
workflowDefinition: 'workflow' ID '{' stateDefinition*?'}' ';';
stateDefinition: 'start'? 'state' ID goes? ';';
goes: 'goes' 'to' (ID',')* ID;
ID: [a-zA-Z]([a-z0-9_])* ;
WS: [ \t\r\n]+ -> skip;
