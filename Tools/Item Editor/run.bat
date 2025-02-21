@echo off
title Item Editor
set CLASSPATH=build\classes\java\main;build\libs\*
java -client -Xmx256m -cp %CLASSPATH% com.editor.ToolSelection
pause
