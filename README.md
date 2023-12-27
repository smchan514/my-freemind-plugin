# my-freemind-plugin

Added "plugin actions":

| Plugin action                                 | Keyboard shortcut |
|-----------------------------------------------|-------------------|
|Insert date stamp                              | F12               |
|Insert local time stamp                        | SHIFT+F12         |
|Insert UTC date time                           | SHIFT+ALT+F12     |
|Open mindmap in Explorer                       | CTRL+F12          |
|Put mindmap full path in clipboard             | CTRL+SHIFT+F12    |
|Set cross links in two selected nodes          | ALT+SHIFT+L       |
|Show the most recently used nodes in a dialog  | CTRL+M            |

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

## TODO

1. Create automated build
