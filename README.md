# XML:DB Initiative for XML Databases

This is a conversion to Git of the `xapi` module from the XML:DB CVS repositiory
via `anonymous@a.cvs.sourceforge.net:/cvsroot/xmldb-org`.

The archived project and code can be found at https://sourceforge.net/projects/xmldb-org/


## Content

The API interfaces are what driver developers must implement when creating a
new driver and are the interfaces that applications are developed against.
Along with the interfaces a concrete DriverManager implementation is also
provides.


## Building for eXist-db

The XML:DB API can be built as a JAR file. This is also used by eXist-db's 
implementation of the XML:DB API.

Java 8 or later is required to build the JAR file.

```
$ git clone https://github.com/xmldb-org/xmldb-api.git
$ cd xmldb-api
$ gradlew build
```

The JAR file will be located in the `build/libs` folder.


## Getting the binaries for your build:

The latest versions of the API are available at https://search.maven.org/search?q=g:net.sf.xmldb-org


## Contribute

Contributions are always welcome.


## License

The project is licensed under the `XML:DB Initiative Software License`
