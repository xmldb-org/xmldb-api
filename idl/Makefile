SOURCES = `find . -name "*.idl"`
BUILD = ../build/interfaces

interfaces:
	mkdir -p $(BUILD)
	rm -r $(BUILD)
	mkdir -p $(BUILD) 
	#idlj -td $(BUILD) $(SOURCES)
	idlj -f server -td $(BUILD) base.idl
	idlj -f server -td $(BUILD) XMLResource.idl
	idlj -f server -td $(BUILD) BinaryResource.idl
	idlj -f server -td $(BUILD) CollectionManagementService.idl
	idlj -f server -td $(BUILD) XPathQueryService.idl
	idlj -f server -td $(BUILD) TransactionService.idl
	idlj -f server -td $(BUILD) XUpdateQueryService.idl
	idlj -f server -td $(BUILD) XQueryService.idl
	idlj -f server -td $(BUILD) DatabaseInstanceManagerService.idl
	idlj -f server -td $(BUILD) security.idl
	idlj -f server -td $(BUILD) UserManagementService.idl
	rm $(BUILD)/org/xmldb/api/base/*POA.java
	rm $(BUILD)/org/xmldb/api/base/*Helper.java
	rm $(BUILD)/org/xmldb/api/base/*Holder.java
	rm $(BUILD)/org/xmldb/api/security/*POA.java	
	rm $(BUILD)/org/xmldb/api/modules/*POA.java

