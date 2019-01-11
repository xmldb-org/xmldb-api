# XML:DB Initiative for XML Databases

This is a coversion to Git of the `xapi` module from the XML:DB CVS repositiory anonymous@a.cvs.sourceforge.net:/cvsroot/xmldb-org.

The original project and code can be found at https://sourceforge.net/projects/xmldb-org/


## Building for eXist-db

The XML:DB API can be built as a Jar file. This is also used by eXist-db's implementation of the XML:DB API.

Java 8 and Apache Maven 3.5.3+ is required to build the JAR file.

```
$ git clone https://github.com/exist-db/xmldb-org.git
$ cd xmldb-org
$ mvn clean compile package
```

The jar file will be located in the `target/` folder.
