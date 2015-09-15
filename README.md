# Introduction

This is a jPOS Plugin for JRebel.

It supports:

* Redeployment of QBeans if their class is modified.
* Reloading of embedded packagers if modified.
* Reloading of embedded resources if using the "resource deployer"
* Reloading of Hibernate's session factory if dependent objects are modified.


# How do you use it?

## Assumptions

You are familiar with jRebel and have read their documentation.

## Plugin location

The approach I use is to have a directory for external JRebel plugins, and to have an entry
in $HOME/.jrebel/jrebel.properties as in:

```
rebel.plugins=/Users/salaman/Java/jrebel/plugins/jr-jpos-plugin-1.0.0.jar
```

## Activation

Plugin is activated by defining a system property at startup:

```
    java -Djpos_plugin=true ...
```

Your IDE would handle the rest :)

# Notes

I wrote this for me. You might not needs this. I'm just letting it out there in case someone else does...
