grammar workflowGrammar;
workflow: 'workflow' ID '{'
   (statesList)+
'}';
state: 'state' ID ('goes to' STATES_LIST)?;
statesList: (ID ',')* ID ';'; 
ID: [a-z]+ ;
WS: [ \t\r\n]+ -> skip;
