/*
 * Copyright 2009 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gmetrics.metric.abc

import org.gmetrics.metric.MetricResult
import org.gmetrics.metric.Metric

/**
 * An aggregate MetricResult implementation specifically for the ABC Metric.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class AggregateAbcMetricResult implements MetricResult {
    private count
    final Metric metric
    private assignmentSum = 0
    private branchSum = 0
    private conditionSum = 0
    final Object value = null

    AggregateAbcMetricResult(Metric metric, Collection children) {
        assert metric != null
        assert children != null
        this.metric = metric
        children.each { child ->
            def abcVector = child.abcVector
            assignmentSum += abcVector.assignments
            branchSum += abcVector.branches
            conditionSum += abcVector.conditions
        }
        count = children.size()
    }

    int getCount() {
        return count
    }

    /**
     * Return the sum of this set of ABC vectors. Each component (A,B,C) of the result
     * is summed separately. The formula for each component is:
     *      A1 + A2 + .. AN
     * and likewise for B and C values.
     */
    Object getTotalAbcVector() {
        return new AbcVector(assignmentSum, branchSum, conditionSum)
    }

    /**
     * Return the average of this set of ABC vectors. Each component (A,B,C) of the result
     * is calculated and averaged separately. The formula for each component is:
     *      (A1 + A2 + .. AN) / N
     * and likewise for B and C values. Each component of the result vector is rounded down to an integer.
     */
    Object getAverageAbcVector() {
        def a = average(assignmentSum, count)
        def b = average(branchSum, count)
        def c = average(conditionSum, count)
        return new AbcVector(a, b, c)
    }

    /**
     * @return the magnitude of the sum of the set of ABC vectors; i.e., getTotalAbcVector().getMagnitude().
     */
    Object getTotalValue() {
        return getTotalAbcVector().getMagnitude()
    }

    /**
     * @return the magnitude of the average of the set of ABC vectors; i.e., getAverageAbcVector().getMagnitude().
     */
    Object getAverageValue() {
        return getAverageAbcVector().getMagnitude()
    }

    String toString() {
        "AggregateAbcMetricResult[count=$count, A=$assignmentSum, B=$branchSum, C=$conditionSum]"
    }

    private average(int sum, int count) {
        return sum && count ? sum / count as Integer : 0
    }

}