package org.jnario.feature.jvmmodel;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Functions.Function0;
import org.jnario.feature.feature.FeaturePackage;
import org.jnario.feature.feature.Given;
import org.jnario.feature.feature.GivenReference;
import org.jnario.feature.feature.Scenario;
import org.jnario.feature.feature.Step;
import org.jnario.feature.feature.Then;
import org.jnario.feature.feature.ThenReference;
import org.jnario.feature.feature.When;
import org.jnario.feature.feature.WhenReference;

@SuppressWarnings("all")
public class StepTypeProvider {
  public final static Set<EClass> ANDS = new Function0<Set<EClass>>() {
    public Set<EClass> apply() {
      EClass _but = FeaturePackage.eINSTANCE.getBut();
      EClass _butReference = FeaturePackage.eINSTANCE.getButReference();
      EClass _and = FeaturePackage.eINSTANCE.getAnd();
      EClass _andReference = FeaturePackage.eINSTANCE.getAndReference();
      return Collections.<EClass>unmodifiableSet(Sets.<EClass>newHashSet(_but, _butReference, _and, _andReference));
    }
  }.apply();
  
  public final static Set<EClass> GIVEN = new Function0<Set<EClass>>() {
    public Set<EClass> apply() {
      EClass _given = FeaturePackage.eINSTANCE.getGiven();
      EClass _givenReference = FeaturePackage.eINSTANCE.getGivenReference();
      return Collections.<EClass>unmodifiableSet(Sets.<EClass>newHashSet(_given, _givenReference));
    }
  }.apply();
  
  public final static Set<EClass> WHEN = new Function0<Set<EClass>>() {
    public Set<EClass> apply() {
      EClass _when = FeaturePackage.eINSTANCE.getWhen();
      EClass _whenReference = FeaturePackage.eINSTANCE.getWhenReference();
      return Collections.<EClass>unmodifiableSet(Sets.<EClass>newHashSet(_when, _whenReference));
    }
  }.apply();
  
  public final static Set<EClass> THEN = new Function0<Set<EClass>>() {
    public Set<EClass> apply() {
      EClass _then = FeaturePackage.eINSTANCE.getThen();
      EClass _thenReference = FeaturePackage.eINSTANCE.getThenReference();
      return Collections.<EClass>unmodifiableSet(Sets.<EClass>newHashSet(_then, _thenReference));
    }
  }.apply();
  
  protected Set<EClass> _getExpectedTypes(final Given step) {
    return StepTypeProvider.GIVEN;
  }
  
  protected Set<EClass> _getExpectedTypes(final GivenReference step) {
    return StepTypeProvider.GIVEN;
  }
  
  protected Set<EClass> _getExpectedTypes(final WhenReference step) {
    return StepTypeProvider.WHEN;
  }
  
  protected Set<EClass> _getExpectedTypes(final When step) {
    return StepTypeProvider.WHEN;
  }
  
  protected Set<EClass> _getExpectedTypes(final Then step) {
    return StepTypeProvider.THEN;
  }
  
  protected Set<EClass> _getExpectedTypes(final ThenReference step) {
    return StepTypeProvider.THEN;
  }
  
  protected Set<EClass> _getExpectedTypes(final Step step) {
    Step _definingStep = this.getDefiningStep(step);
    Set<EClass> _expectedTypes = this.getExpectedTypes(_definingStep);
    return _expectedTypes;
  }
  
  public EClass getActualType(final Step step) {
    Step _definingStep = this.getDefiningStep(step);
    EClass _eClass = _definingStep.eClass();
    return _eClass;
  }
  
  private Step getDefiningStep(final Step step) {
    Step _xblockexpression = null;
    {
      EObject _eContainer = step.eContainer();
      final Scenario container = ((Scenario) _eContainer);
      EList<Step> _steps = container.getSteps();
      final int index = _steps.indexOf(step);
      int i = index;
      boolean _greaterEqualsThan = (i >= 0);
      boolean _while = _greaterEqualsThan;
      while (_while) {
        {
          EList<Step> _steps_1 = container.getSteps();
          final Step candidate = _steps_1.get(i);
          EClass _eClass = candidate.eClass();
          boolean _contains = StepTypeProvider.ANDS.contains(_eClass);
          boolean _not = (!_contains);
          if (_not) {
            return candidate;
          }
          int _minus = (i - 1);
          i = _minus;
        }
        boolean _greaterEqualsThan_1 = (i >= 0);
        _while = _greaterEqualsThan_1;
      }
      _xblockexpression = (step);
    }
    return _xblockexpression;
  }
  
  public Set<EClass> getExpectedTypes(final Step step) {
    if (step instanceof Given) {
      return _getExpectedTypes((Given)step);
    } else if (step instanceof GivenReference) {
      return _getExpectedTypes((GivenReference)step);
    } else if (step instanceof Then) {
      return _getExpectedTypes((Then)step);
    } else if (step instanceof ThenReference) {
      return _getExpectedTypes((ThenReference)step);
    } else if (step instanceof When) {
      return _getExpectedTypes((When)step);
    } else if (step instanceof WhenReference) {
      return _getExpectedTypes((WhenReference)step);
    } else if (step != null) {
      return _getExpectedTypes(step);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(step).toString());
    }
  }
}
