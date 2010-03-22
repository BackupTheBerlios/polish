
Running the Sputnik Test Suite
==============================

http://code.google.com/p/sputniktests

Sputnik is a JavaScript test suite developed by Google.
Run it with:

cd sputnik
./tools/sputnik.py --command ../enough-skylight/dist/js.sh S7.8.4_A2.2_T1

In order to work you need the Skylight project as an executable. Make sure that
the enough-skylight/resources/META-INF/MANIFEST.MF file is included in the js.jar file.

