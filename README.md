# my-freemind-plugin

Added "plugin actions":

| Plugin action                                              | Keyboard shortcut |
|------------------------------------------------------------|-------------------|
|Insert date stamp                                           | F12               |
|Insert local time stamp                                     | SHIFT+F12         |
|Insert UTC date time                                        | SHIFT+ALT+F12     |
|Open mindmap in Explorer (Windows)                          | CTRL+F12          |
|Open linked file in Explorer (Windows)                      | CTRL+SHIFT+F12    |
|Set cross links in two selected nodes                       | ALT+SHIFT+L       |
|Insert cross-linked node pair                               | CTRL+SHIFT+L      |
|Show the most recently used nodes in a dialog               | CTRL+M            |
|Toggle word cases: "UPPER CASE", "lower case", "Title Case" | SHIFT+F3          |
|Normalize white space in node text                          | CTRL+SHIFT+F3     |
|Edit encrypted attribute `__ENC__` of the selected node<sup>(1)</sup> | SHIFT+F2          |
|Reset encryption (clear cache of secrets)                   | N/A               |
|Insert random string                                        | N/A               |
|Insert 'week number' nodes                                  | N/A               |
|Show nodes stats                                            | N/A               |
|Show node path                                              | N/A               |
|Go to node ID                                               | N/A               |
|Go to node at path                                          | N/A               |
|Advanced search<sup>(2)</sup>                               | CTRL+SHIFT+F      |

Notes:
1. On the "encrypted attribute" feature:
    1. Use OpenJDK or Amazon Corretto JRE/JDK to enable use of AES-256.
    2. Use JRE/JDK 9 or later to enable auto reset on user lock screen (Tested with Amazon Corretto JDK 11.0.24.8.1).
2. The keyboard shortcut for "Advanced search" may clash with the existing for "Search and replace" in baseline Freemind, to disable the latter:
    1. Locate the user config file `C:\Users\%USERNAME%\.freemind\auto.properties` 
    2. Insert the following in a new line: `keystroke_plugins/TimeList.xml_key=invalid`
3. The keyboard shortcut for "Save All" can be overridden as follows:
    1. Locate the user config file `C:\Users\%USERNAME%\.freemind\auto.properties` 
    2. Insert the two lines below:
        1. `keystroke_accessories/plugins/SaveAll=ctrl shift S`
        2. `keystroke_saveAs=invalid`


## How to compile locally

### Setup

A working Linux environment (tested with Ubuntu 24.04.1) with the following:

1. Docker (tested with version 27.2.0)
2. Git
3. Internet connection

### Steps

1. Clone this repo, assuming at `~/git/my-freemind-plugin`
2. Pull the Docker image:
    ```
    docker pull gradle:jdk11
    ```

3. Start a Docker container:
    ```
    cd ~/git/my-freemind-plugin
    docker run --rm -it -u gradle -v $PWD:/opt/workspace -w /opt/workspace gradle:jdk11 bash
    ```

4. Run Gradle:
    ```
    gradle build --info
    ```

5. Locate the output Zip archives:
    1. `freemind-overrides/build/distributions/`
    2. `myplugin/build/distributions/`
    

## How to deploy the plugin


1. Locate the install directory of Freemind, e.g. `"C:\Users\%USERNAME%\AppData\Local\FreeMind"` or `"C:\Program Files (x86)\FreeMind"`
2. Extract the content of the Zip file in the "plugins" directory, so you end up with the following files:
    1. `C:\Users\%USERNAME%\AppData\Local\FreeMind\plugins\MyPlugin.xml`
    2. `C:\Users\%USERNAME%\AppData\Local\FreeMind\plugins\myplugin\myplugin.jar`
3. Start Freemind or restart it if it was running

To test if the plugin has been installed successfully, select any node in a mind map and press F12. You should see a date stamp "[2023-09-14]" being prepended to the node.


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
