/*******************************************************************************
 * Copyright (c) 2012 BMW Car IT and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.jnario.jnario.tests.unit;

import com.google.inject.Inject;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.jnario.ExampleColumn;
import org.jnario.jnario.test.util.ModelStore;
import org.jnario.jnario.test.util.Query;
import org.jnario.jnario.test.util.SpecTestInstantiator;
import org.jnario.jnario.tests.unit.ExampleColumnSpecExamples;
import org.jnario.lib.ExampleTable;
import org.jnario.lib.ExampleTableIterators;
import org.jnario.lib.MatcherChain;
import org.jnario.lib.Should;
import org.jnario.runner.ExampleGroupRunner;
import org.jnario.runner.Extension;
import org.jnario.runner.InstantiateWith;
import org.jnario.runner.Named;
import org.jnario.runner.Order;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ExampleGroupRunner.class)
@Named("ExampleColumn")
@InstantiateWith(SpecTestInstantiator.class)
public class ExampleColumnSpec {
  @Inject
  @Extension
  public ModelStore _modelStore;
  
  @Inject
  @Extension
  public ISerializer _iSerializer;
  
  @Before
  public void _initExampleColumnSpecExamples() {
    examples = ExampleTable.create("examples", 
      java.util.Arrays.asList("columnIndex", "cellIndex", "value"), 
      new ExampleColumnSpecExamples(  java.util.Arrays.asList("0", "0", "\"1\""), 0, 0, "1"),
      new ExampleColumnSpecExamples(  java.util.Arrays.asList("0", "1", "\"3\""), 0, 1, "3"),
      new ExampleColumnSpecExamples(  java.util.Arrays.asList("1", "0", "\"2\""), 1, 0, "2"),
      new ExampleColumnSpecExamples(  java.util.Arrays.asList("1", "1", "\"4\""), 1, 1, "4")
    );
  }
  
  private ExampleTable<ExampleColumnSpecExamples> examples;
  
  @Test
  @Named("calculates cells based on table")
  @Order(3)
  public void calculatesCellsBasedOnTable() throws Exception {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("package bootstrap");
      _builder.newLine();
      _builder.append("describe \"ExampleTable\"{");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("def{");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("| a | b |");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("| 1 | 2 |");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("| 3 | 4 |");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("}");
      _builder.newLine();
      _builder.append("}");
      _builder.newLine();
      this._modelStore.parseSpec(_builder);
      final Procedure1<ExampleColumnSpecExamples> _function = new Procedure1<ExampleColumnSpecExamples>() {
          public void apply(final ExampleColumnSpecExamples it) {
            {
              Query _query = ExampleColumnSpec.this._modelStore.query();
              org.jnario.ExampleTable _first = _query.<org.jnario.ExampleTable>first(org.jnario.ExampleTable.class);
              EList<ExampleColumn> _columns = _first.getColumns();
              final EList<ExampleColumn> columns = _columns;
              ExampleColumn _get = columns.get(it.columnIndex);
              final ExampleColumn column = _get;
              EList<XExpression> _cells = column.getCells();
              XExpression _get_1 = _cells.get(it.cellIndex);
              final XExpression cell = _get_1;
              String _serialize = ExampleColumnSpec.this._iSerializer.serialize(cell);
              String _trim = _serialize.trim();
              MatcherChain<String> _should = Should.<String>should(_trim);
              Should.<String>be(_should, it.value);
            }
          }
        };
      ExampleTableIterators.<ExampleColumnSpecExamples>forEach(this.examples, _function);
  }
}
