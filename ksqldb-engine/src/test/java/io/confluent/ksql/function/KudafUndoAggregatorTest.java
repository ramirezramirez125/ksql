/*
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */

package io.confluent.ksql.function;

import static io.confluent.ksql.GenericRow.genericRow;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import com.google.common.collect.ImmutableList;
import io.confluent.ksql.GenericRow;
import io.confluent.ksql.execution.function.TableAggregationFunction;
import io.confluent.ksql.execution.function.udaf.KudafUndoAggregator;
import io.confluent.ksql.name.FunctionName;
import io.confluent.ksql.schema.ksql.types.SqlTypes;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class KudafUndoAggregatorTest {

  private static final InternalFunctionRegistry FUNCTION_REGISTRY = new InternalFunctionRegistry();
  private static final TableAggregationFunction<?, ?, ?> SUM_INFO =
      (TableAggregationFunction<?, ?, ?>) FUNCTION_REGISTRY
          .getAggregateFunction(
              FunctionName.of("SUM"),
              SqlTypes.INTEGER,
              new AggregateFunctionInitArguments(2)
          );

  private KudafUndoAggregator aggregator;

  @Before
  public void init() {
    final List<TableAggregationFunction<?, ?, ?>> functions = ImmutableList.of(SUM_INFO);
    aggregator = new KudafUndoAggregator(2, functions);
  }

  @Test
  public void shouldApplyUndoableAggregateFunctions() {
    // Given:
    final GenericRow row = genericRow("snow", "jon", 3);
    final GenericRow aggRow = genericRow("snow", "jon", 5);

    // When:
    final GenericRow resultRow = aggregator.apply(null, row, aggRow);

    // Then:
    assertThat(resultRow, equalTo(genericRow("snow", "jon", 2)));
  }
}
