# my-freemind-plugin

Added "plugin actions":

| Plugin action                                              | Keyboard shortcut |
|------------------------------------------------------------|-------------------|
|Insert date stamp                                           | F12               |
|Insert local time stamp                                     | SHIFT+F12         |
|Insert UTC date time                                        | SHIFT+ALT+F12     |
|Open mindmap in Explorer                                    | CTRL+F12          |
|Put mindmap full path in clipboard                          | CTRL+SHIFT+F12    |
|Set cross links in two selected nodes                       | ALT+SHIFT+L       |
|Insert cross-linked node pair                               | CTRL+SHIFT+L      |
|Show the most recently used nodes in a dialog               | CTRL+M            |
|Toggle word cases: "UPPER CASE", "lower case", "Title Case" | SHIFT+F3          |
|Normalize white space in node text                          | CTRL+SHIFT+F3     |
|Edit encrypted attribute `__ENC__` of the selected node     | SHIFT+F2          |
|Reset encryption (clear cache of secrets)                   | N/A               |
|Insert random string                                        | N/A               |
|Insert 'week number' nodes                                  | N/A               |
|Show nodes stats                                            | N/A               |
|Show node path                                              | N/A               |
|Go to node ID                                               | N/A               |

Notes:
1. On the "encrypted attribute" feature:
    1. Use OpenJDK or Amazon Corretto JRE/JDK to enable use of AES-256.
    2. Use JRE/JDK 9 or later to enable auto reset on user lock screen (Tested with Amazon Corretto JDK 11.0.24.8.1).

## How to compile and deploy manually

1. Download Freemind binaries (version 1.0.0 used)
2. Run Eclipse (version : 2020-09 (4.17.0) used)
3. Create Java project using JavaSE-1.8
4. Add referenced libraries:
    1. `freemind.jar`
    2. `bindings.jar`
    3. `jibx-run.jar`
    4. `xpp3.jar`
5. Collect compiled class files into a `myplugin.jar`
6. Copy the following files in an installed instance of Freemind, assuming `C:\Program Files (x86)\FreeMind\`
    1. `C:\Program Files (x86)\FreeMind\myplugin\myplugin.jar`
    2. `C:\Program Files (x86)\FreeMind\MyPlugin.xml`


## How to Enable Dark Theme

1. Assuming the following:
    1. You have compiled the "Freemind override" classes under `src/freemind` and packaged the compiled classes in a Jar.
    2. You have downloaded `com.formdev.flatlaf` (version 3.5.2 referenced here).
        1. https://mvnrepository.com/artifact/com.formdev/flatlaf/3.5.2
2. Put the following Jar files under the "lib" folder in your installed instance of Freemind:
    1. "Freemind override" Jar
    2. FlatLaf Jar
3. Put the batch file `freemind.bat` in your installed instance of Freemind, edit it as follows:
    1. Uncomment (remove the REM prefix) the SET statements under "Optional support for FlatLaf"
    2. Update as required the Jar names in the `SET CLASSPATH` statement
4. Update the Freemind icon on your desk to start Freemind with the batch file
5. Start Freemind and modify Preferences via menu item Tools / Preferences, with suggested values below:
    1. Under tab "Appearance":
        1. Under section "Look and Feel":
            1. For "Look and Feel", select "FlatLaf Dark"
        1. Under section "Section "Selection Colors":
            1. For "Selected Node Bubble Color":
                1. Dark theme: `#002080`
                2. Light theme: `#002080`
            2. For "Standard Selected Node Color":
                1. Dark theme: `#333333`
                2. Light theme: `#D2D2D2`
    2. Under tab "Defaults", under section Section "Default Colors"
        1. For "Standard Node Color" (node text color):
            1. Dark theme: `#808080`
            2. Light theme: `#000000`
        2. For "Standard Edge Color":
            1. Dark theme: `#808080`
            2. Light theme: `#808080`
        3. For "Standard Link Color":
            1. Dark theme: `#b0b0b0`
            2. Light theme: `#b0b0b0`
        4. For "Standard Background Color":
            1. Dark theme: `#000000`
            2. Light theme: `#ffffff`
        5. For "Standard Cloud Color":
            1. Dark theme: `#f0f0f0`
            2. Light theme: `#f0f0f0`
5. Restart Freemind for the L&F settings to take effect.


## TODO

1. Create automated build
