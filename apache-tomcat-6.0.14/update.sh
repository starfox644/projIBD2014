#!/bin/bash

if [ -z $CATALINA_HOME ]
then
	echo "Error : CATALINA_HOME variable not defined.";
else
	$CATALINA_HOME/bin/shutdown.sh
	ant -buildfile $CATALINA_HOME/webapps/ROOT/build.xml compile
	$CATALINA_HOME/bin/startup.sh
fi