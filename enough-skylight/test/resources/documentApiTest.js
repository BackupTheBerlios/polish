unitTest.addTest({comment:"document API",run:function(){
	assert.notNull("document is not present",document);
	assert.notNull("document.doctype is null",document);
	/*assert.equals("failing test",'document',null); */
}});

unitTest.addTest({comment:"document.doctype API",run:function(){
	assert.isTrue("document.doctype is not null",document.doctype == null);
	document.doctype = 3;
	assert.isNull("document.doctype was altered although it is read only",document.doctype);
}});

unitTest.addTest({comment:"document.getElementById API",run:function(){
	var element = document.getElementById("id2");
	assert.notNull("no element with id 'id2'", element);
	assert.equals("element with id 'id2' is not a div","div",element.nodeName);
}});

/*
unitTest.addTest({comment:"",run:function(){
}});
*/

unitTest.addTest({comment:"document.documentElement API",run:function(){
	var element = document.documentElement;
	assert.notNull("document.documentElement is null", element);
	assert.equals("The element has not name 'html'", "html", element.nodeName);
}});

