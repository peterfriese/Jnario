package org.jnario.spec.tests.integration

import java.util.Stack

/*
 * In Xtend you can use the [with operator](http://www.eclipse.org/xtend/new_and_noteworthy/index.html#withOperator)
 *  `=>` as a let-expression, which allows binding any object to the scope of 
 * the block, which can be handy when initializing objects. In Jnario `=>` also
 * can be used to describe the expected behavior of objects.  
 * It works great together with Xtend's with operator. You can use it to:
 */
describe "Using Xtend's with Operator"{
	
	fact "initialize fixtures"{
		val stackWithTwoElements = new Stack<String> => [
			add("red")
			add("blue")
		]
		stackWithTwoElements.size => 2
	}
	fact "write multiple assertions"{
		"hello world" => [
			length => 11
			it should startWith("hello")
			it should endWith("world")
		]
	} 
}

 