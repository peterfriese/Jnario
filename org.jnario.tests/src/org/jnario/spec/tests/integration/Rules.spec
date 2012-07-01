/*******************************************************************************
 * Copyright (c) 2012 BMW Car IT and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.jnario.spec.tests.integration

import static extension org.jnario.jnario.test.util.SpecExecutor.*

/* 
 * JUnit rules work exactly as in JUnit. Just declare a public field with the rule annotation.
 * 
 */
describe "Using JUnit Rules in Specs"{

	/*
     * @filter('''|.executesSuccessfully)  
     */
	fact "Example Specification:"{
		'''
		package test
				
		import org.junit.Rule
		import org.junit.rules.TemporaryFolder
		
		describe Rule{
		  @Rule public val folder = new TemporaryFolder	
		
		  fact folder.root should not be null
		}		
		'''.executesSuccessfully
	}


}