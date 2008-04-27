This example shows a small range of the possiblities 
in using themes with J2ME Polish.

For starters, adopt the Administrator.properties to your 
username and paths and build with "ant clean theme j2mepolish" 
once with "ID" set to "blipsystems" and once with "ID" set to 
"enough" to see an example.


 
To use themes in your project a theme build file is needed.
In this example it's theme.xml. It compiles the source files 
of the project and bundles the style and resource files to 
a .theme file. 

To create a theme build file you can follow these steps :

- Copy your existing build.xml with all variables and 
  configurations in it to your new theme build file.

- Rename the "j2mepolish" task in the theme build file to "theme" 
  so the ThemeTask is used instead of the PolishTask. 
  
- In "resources" define the directories which files and styles of
  the polish.css in the corresponding directory should be bundled.
  
  NOTE: Always used "always-include: true;" in the styles you want to bundle.

- Create <serialize> in <build>. This is the default entry :
  
  <serialize regex="^.*Style" target="de.enough.polish.ui.StyleSheet" type="de.enough.polish.ui.Style" />
  
- Define the output file with <file> 

  <file file="myTheme.theme" />
  
  
  
Now you have to adopt your build.xml to themes:

- Extend your <resource> entries of the resources and polish.css you want 
  to bundle in a theme file with "unless='polish.useThemes'" so it's raw 
  resources are not included if themes are used. 
  
  'polish.useThemes' should be set in your .properties file. See
  Administrator.properties for an example.
  
- Create a task that calls the theme.xml to run the theme bundling.
  See the target named "theme" in the build.xml for an example.

  <theme> in theme.xml places the resulting theme file
  in the build folder of the built versions of the midlet.
  For example, if you build for the Sony Ericsson K850 with
  en_US as the localization, the resulting path for the theme
  file would be :
  
  <base directory>/build/SonyEricsson/K850/en_US/theme

- To run the theme bundling and the compilation of the application
  run from ant from the command line like this :
  
  ant clean theme j2mepolish
  
  With this command the project is first cleaned, the resources
  are bundled within a .theme file and the application is build
  with the .theme file in it.
  
  
  
To use the .theme file in your application, two classes are used :
ThemeStream and ThemeController. Here's an example :

  ThemeStream stream = new ThemeStream(midlet, "/myTheme.theme");
  
  Style myStyle = ThemeController.getStyle(stream, "myStyle");
  
The method getStyle() is one of the convinience methods in 
ThemeController. For example, getImage() returns an Image object
of a bundled .png image, please see ThemeMidlet for an example.

You can easily create new methods that use getData() or getObject() 
for methods to read any resources you like to bundle.


 
If you got some questions or experience any bugs in the theme mechanism 
please contact andre.schmidt@enough.de.