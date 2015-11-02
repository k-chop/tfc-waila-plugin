Additional Waila plugin for TerraFirmaCraft
================================
**Latest version: 0.4.1 (TFC 0.79.25 compatible)**

**TFC already has official waila suport**, so **YOU DO NOT NEED TO INSTALL THIS PLUGIN!!!**

However, this plugin adds a tiny little bit more waila support for TFC TileEntities.
If you need additional features below, install this plugin :)

This plugin supports:

- ToolRack (can see information of tools placed in toolrack)
- IngotPile (can see total stack of ingot tower! XD)
- FoodPrep (can see information of foods placed in foodprep)
- Pottery (can see information of something that is inside a container)

Screenshot
-------
![total stack of ingot pile](https://github.com/whelmaze/tfc-waila-plugin/wiki/images/ne_ingotpile01.png)

[>>> more screenshot](https://github.com/whelmaze/tfc-waila-plugin/wiki/Screen-Shots)

Notice
------
This plugin depends on **TFC base code** (not API) partially.
Accordingly, there is no guarantee that this plugin compatible across TFC minor version change.


Requirements
--------
- Waila 1.5.10
- TFC 0.79.23+
- Minecraft 1.7.10


Download
--------
0.4.1 (for TFC 0.79.23+)

[>>> Release](https://github.com/whelmaze/tfc-waila-plugin/releases)

Build
-----
1. Clone this repo
2. Checkout **master branch** (other branches may not work well)
3. Create directory `libs` and put TFC jar file in the directory.
3. Install gradle (sorry, this repo doesn't include gradle wrapper)
4. `> gradle build`
5. Get jar from `build/libs`
