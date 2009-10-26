function Assert(){
	this.equals = function(comment,expected,actual){
		if(expected !== actual){
			throw {comment:comment,expected:expected,actual:actual}
		}
	}
	this.notNull = function(comment,object){
		if(object !== null && object !== undefined){
		} else{
			throw {comment:comment,expected:true,actual:actual}
		}
	}
	this.isTrue = function(comment,object){
		if(!object){
			throw {comment:comment,expected:true,actual:object}
		}
	}
	this.isNull = function(comment,object){
		if(object !== null){
			throw {comment:comment,expected:null,actual:object}
		}
	}
}

function UnitTest(){
	this.tests = new Array();
	that = this;
	this.addTest = function(test){
		that.tests.push(test);
	}
	this.run = function(){
		println("Number of tests: "+that.tests.length);
		for(var i = 0; i < that.tests.length; i++){
			println("Running test #"+(i+1)+" '"+that.tests[i].comment+"'");
			try{
				that.tests[i].run();
			} catch(e){
				var comment = e.comment || "";
				println("  Exception occured:"+comment+".Expected:"+e.expected+".Actual:"+e.actual);
			}
			println("  Ending test #"+(i+1));
		}
	}
	"";
};

/* This code is not working here. When calling add normally with add() it is working.
function add(){
	throw {name:"addException"};
}
try{
	add.call(null);
} catch(e){
	
}
*/

var assert = new Assert();
var unitTest = new UnitTest();