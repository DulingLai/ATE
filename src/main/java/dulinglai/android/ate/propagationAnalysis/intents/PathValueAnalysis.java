/*
 * Copyright (C) 2015 The Pennsylvania State University and the University of Wisconsin
 * Systems and Internet Infrastructure Security Laboratory
 *
 * Author: Damien Octeau
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dulinglai.android.ate.propagationAnalysis.intents;

import dulinglai.android.ate.propagationAnalysis.arguments.Argument;
import dulinglai.android.ate.propagationAnalysis.arguments.ArgumentValueAnalysis;
import dulinglai.android.ate.propagationAnalysis.arguments.ArgumentValueManager;
import dulinglai.android.ate.propagationAnalysis.values.PropagationConstants;
import soot.Unit;
import soot.Value;
import soot.jimple.Stmt;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PathValueAnalysis extends ArgumentValueAnalysis {

  private static final String TOP_VALUE = PropagationConstants.ANY_STRING;

  private static final int PATTERN_LITERAL = 0;
  private static final int PATTERN_PREFIX = 1;
  private static final int PATTERN_SIMPLE_GLOB = 2;

  @Override
  public Set<Object> computeArgumentValues(Argument argument, Unit callSite) {
    Argument argument0 = new Argument(argument);
    argument0.setArgNum(new int[] { argument.getArgnum()[0] });
    argument0.setType(PropagationConstants.DefaultArgumentTypes.Scalar.STRING);
    Argument argument1 = new Argument(argument);
    argument1.setArgNum(new int[] { argument.getArgnum()[1] });
    argument1.setType(PropagationConstants.DefaultArgumentTypes.Scalar.INT);

    Set<Object> paths = ArgumentValueManager.v().getArgumentValues(argument0, callSite);
    Set<Object> types = ArgumentValueManager.v().getArgumentValues(argument1, callSite);
    Set<Object> result = new HashSet<>();

    for (Object path : paths) {
      for (Object type : types) {
        result.add(computePathForType((String) path, (Integer) type));
      }
    }

    return result;
  }

  private String computePathForType(String path, int type) {
    if (type == PATTERN_LITERAL || type == PATTERN_SIMPLE_GLOB) {
      return path;
    } else if (type == PATTERN_PREFIX || type == PropagationConstants.ANY_INT) {
      if (path.equals(PropagationConstants.ANY_STRING)) {
        return PropagationConstants.ANY_STRING;
      } else {
        return String.format("%s(.*)", path);
      }
    } else {
      throw new RuntimeException("Unknown path type: " + type);
    }
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
