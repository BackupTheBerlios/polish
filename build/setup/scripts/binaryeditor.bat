REM Start the editor for binary data files:
cd "%{INSTALL_PATH}\bin"
java -class %CLASSPATH%;..\import\enough-j2mepolish-build.jar;..\import\jdom.jar de.enough.polish.dataeditor.swing.SwingDataEditor %1 %2 %3