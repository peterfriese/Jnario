package de.bmw.carit.jnario.runner;

import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

public class JnarioRunner extends ExampleGroupRunner {

	private final class RunnerWrapper implements
	TestInstantiator {

		private TestInstantiator instantiator;
		private Object test;

		public RunnerWrapper(TestInstantiator instantiator){
			this.instantiator = instantiator;
		}

		@Override
		public Object createTest(TestClass klass) throws Exception {
			if(test == null){
				test = instantiator.createTest(klass);
			}
			return test;
		}

		@Override
		public void beforeTestRun() {
			instantiator.beforeTestRun();
		}

		@Override
		public void afterTestRun() {
			instantiator.afterTestRun();
		}
	}

	private TestInstantiator delegate;

	public JnarioRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}

	protected ExampleRunner createExampleRunner(
			FrameworkMethod from) throws InitializationError,
			NoTestsRemainException {
		if(delegate == null){
			createTestWrapper();
		}
		return new ExampleRunner(from, getNameProvider(), delegate);
	}
	
	private void createTestWrapper() throws InitializationError{
		TestInstantiator createTestInstantiator = createTestInstantiator();
		delegate = new RunnerWrapper(createTestInstantiator);
	}
}