![logo](http://mods.atomicbase.com/enhancedportals/forum_files/ep_banner.png)
[![Build Status](https://travis-ci.org/enhancedportals/enhancedportals.svg)](https://travis-ci.org/enhancedportals/enhancedportals)

Useful Links:
* [License](LICENSE)
* [Downloads](http://www.curse.com/mc-mods/minecraft/225921-enhanced-portals-3#t1:other-downloads)
* [Changelog](https://github.com/enhancedportals/VERSION/blob/master/CHANGELOG%20-%20Enhanced%20Portals.md#enhanced-portals-changelog)
* [Minecraft Forums Thread](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/1292751)


###Compiling

1. Clone Enhanced Portals into any directory.
2. Open up a command line or terminal window and navigate to that directory.
3. Execute `gradlew build`.
4. If `BUILD SUCCESSFUL` appears, you'll find the `EnhancedPortals-{mcversion}-{version}.jar` in `build\libs\`.


###Developing

1. Fork Enhanced Portals and clone it into any directory.
2. Open up a command line or terminal window and navigate to that directory.
3. Execute `gradlew setupDecompWorkspace eclipse` or `gradlew setupDecompWorkspace idea`.