#OTRA routing protocol

## Description

The main goal is to design an application layer protocol that provides file transmission among hosts which are connected in a tree-shaped network. Each host offers the capability (service) of forwarding a file by consulting a routing table which maintains references to neighboring hosts (parent and children), then it decides the next hop toward the destination in the tree topology.

## Building and use

fast way to compile without an IDE
```bash
mkdir bin
find ./src -name *.java > sources_list.txt
javac -d bin/ @sources_list.txt
```
to run the application
```bash
cd bin
java app.Main -id <number> -interface <interface>
```

since we use two consoles (handle commands and operations debug printing), redirect std.err to a different tty on unix systems
```bash
java app.Main -id <number> -interface <interface> 2> /dev/pts/<second_tty>
```
