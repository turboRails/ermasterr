# ermasterr
A fork of ERMaster(http://ermaster.sourceforge.net). It's solved some issues of the original version.

# Install
1. Install Eclipse  
Package: Eclipse IDE for Java Developers
2. Install PDE plug-in  
Plug-in name: Eclipse PDE Plug-in Developer Resources
3. Install ermasterr  
Copy the contents of the dropins folder to the dropins folder located in the root of your Eclipse installation.

# What is ERMaster?
ERMaster is GUI editor for ER diagram. 
It runs as Eclipse plug-in. 
It can be done graphically to making ER diagram, printing ER diagram, exporting the DDL from ER diagram, etc. . 
Moreover, importing from DB, management of the group, and the historical management, etc. are supported. 

# ERMaster is very useful, but ...

## Draw speed is so slow when you open a ER diagram that has many categories
ERMaster has a serious issue that increases drawing time in proportion to the number of categories.
The cause of this problem is ERMaster processes expensive initialization for each category tabs.

## ER diagram file size becomes gigantic in sometimes
ERMaster has another problem that ER diagram file(xml) size becomes gigantic in sometimes ( https://sourceforge.net/p/ermaster/bugs/119/ ).
The cause of this problem is ERMaster writes a large amount of same xml tags sometimes.

# ermasterr solved this issues
* ermasterr got rid of tabs of each category, and skipped this expensive initialization, prevent a increase of drawing time.
* ermasterr fixed the issue that writes a large amount of same xml tags.

# License
Apache License V2.0
