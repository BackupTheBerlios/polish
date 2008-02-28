Command line to run the converter:

java -Xms256m -Xmx256m -cp lib/ant.jar:lib/jdom.jar:bin/main:../enough-polish-build/dist/enough-j2mepolish-build.jar de.enough.polish.Converter

TODO
- Create a build.xml for running the converter.
- Update mDevInf.jar und do not include the sources directly into the project.
- Watch out for the characters "," and "/" in model and vendor names.
