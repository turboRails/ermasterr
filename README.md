# ermasterr
A fork of ERMaster. Faster, Better, VCS-friendly.

Original version is http://ermaster.sourceforge.net  
> ERMaster is GUI editor for ER diagram.  
> It runs as Eclipse plug-in.  
> It can be done graphically to making ER diagram, printing ER diagram, exporting the DDL from ER diagram, etc. .  
> Moreover, importing from DB, management of the group, and the historical management, etc. are supported.  

# Install
1. Install Eclipse  
Package: Eclipse IDE for Java Developers
2. Install PDE plug-in  
Plug-in name: Eclipse PDE Plug-in Developer Resources
3. Install ermasterr  
Copy the contents of the [dropins folder](https://github.com/roundrop/ermasterr/tree/master/dropins/ermasterr/eclipse/plugins) to the dropins folder located in the root of your Eclipse installation.

# Features

## Faster
* ermasterr prevented a increase of the drawing time by getting rid of tabs of each category and skipping expensive initialization for each category.

## Better
* ermasterr fixed the issue that erdiagram data file size becomes gigantic in sometimes by writing a large amount of same xml tags.

## VCS-friendly
* ermasterr can write git-mergable erdiagram data as far as possible.

# 1.1.x migration for 1.0.x
Re-save .erm file using 1.1.x.

# License
Apache License V2.0
