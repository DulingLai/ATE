package dulinglai.android.ate.propagationAnalysis.intents;

import dulinglai.android.ate.propagationAnalysis.arguments.Argument;
import dulinglai.android.ate.propagationAnalysis.arguments.ArgumentValueAnalysis;
import dulinglai.android.ate.propagationAnalysis.values.PropagationConstants;
import soot.Unit;
import soot.Value;
import soot.jimple.Stmt;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ContextValueAnalysis extends ArgumentValueAnalysis {

  private static final String TOP_VALUE = PropagationConstants.ANY_STRING;

  private final String appName;

  public ContextValueAnalysis(String appName) {
    this.appName = appName != null ? appName : PropagationConstants.ANY_STRING;
  }

  @Override
  public Set<Object> computeArgumentValues(Argument argument, Unit callSite) {
    return Collections.singleton((Object) this.appName);
  }

  @Override
  public Set<Object> computeInlineArgumentValues(String[] inlineValue) {
    return new HashSet<Object>(Arrays.asList(inlineValue));
  }

  @Override
  public Object getTopValue() {
    return TOP_VALUE;
  }

  @Override
  public Set<Object> computeVariableValues(Value value, Stmt callSite) {
    throw new RuntimeException("Should not be reached.");
  }

}
