package org.jnario.feature.tests.unit.linking

import org.jnario.feature.jvmmodel.StepArgumentsProvider
import org.jnario.runner.InstantiateWith
import com.google.inject.Inject
import org.jnario.jnario.test.util.FeatureTestInstantiator
import org.jnario.feature.feature.Step 
import org.eclipse.xtext.linking.lazy.LazyLinkingResource
import org.eclipse.xtext.util.StringInputStream
import org.jnario.jnario.test.util.Query
import java.io.InputStreamReader
import static org.jnario.lib.JnarioCollectionLiterals.*

@InstantiateWith(typeof(FeatureTestInstantiator))
describe StepArgumentsProvider {

	@Inject LazyLinkingResource resource

	def examples{
		| step										| expectedArgs  			|
		| null    									| list()		 			|
		| 'Given no values'    						| list()		 			|
		| 'Given "hello"'     						| list("hello") 			|
		| 'Given "hello" and "world"'				| list("hello", "world") 	|
		| 'Given "hello" and "world"'				| list("hello", "world") 	|
		| 'Given a multiline \n"hello"'				| list("hello") 			|
		| 'Given a multiline \n 1+1 => 2 "hello"' 	| list() 					|
	}

	fact "extracts arguments from step descriptions"{
		examples.forEach[
			subject.findStepArguments(create(step)) => expectedArgs
		]
	}
 
	def create(String step){
		if(step == null){
			return null
		}
		val scenario = '''
			Feature: dummy
				Scenario: dummy 
					�step�
					1 + 1 => 1
		'''
		// we have to access the model before linking
		val parseResult = resource.parser.parse(new InputStreamReader(new StringInputStream(scenario.toString)))
		Query::query(parseResult.rootASTElement.eAllContents.toList).first(typeof(Step))
	}

}