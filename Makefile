STYLEBOOK = org.apache.stylebook.StyleBook
CLASSPATH = ../lib/xerces.jar:../lib/xalan.jar:../lib/stylebook-1.0-b2.jar
SKIN = ../styles/xmldb
SOURCES = `find src/ -name "*.java"`

all:	javadocs
	java -classpath $(CLASSPATH) $(STYLEBOOK) ./book.xml $(SKIN)

javadocs:
	-rm api/*.html
	-mkdir -p api
	javadoc -classpath $(CLASSPATH) -public\
	   -d api -version -author\
	   -doctitle "XML:DB API Specification"\
	   -header "<b>XML:DB API</b>"\
	   -sourcepath src/api\
	   -bottom "<font size=2>Copyright (C) <a href=http://www.xmldb.org>XML:DB Initiative</a>. All rights reserved.</font>"\
	   org.xmldb.api\
	   org.xmldb.api.base\
	   org.xmldb.api.modules\

interfaces:
	javac -classpath $(CLASSPATH) $(SOURCES)

dist:
	mkdir dist
	mkdir dist/xmldb
	cp -pr src/api/org dist/xmldb
	cd src ; ant ; cd ..
	cp src/lib/xmldb-api-`date +%Y%m%d`.jar dist/xmldb/xmldb.jar
	cd dist ; tar cvzf ../downloads/xmldb-api-`date +%m%d%Y`.tar.gz .
#	cvs add -kb downloads/xmldb-api-`date +%m%d%Y`.tar.gz
	rm -rf dist
