/*
 * Copyright 2010 the original author or authors.
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
package org.codenarc.rule.basic

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule

/**
 * Tests for ExplicitCreationOfStackRule
 *
 * @author Hamlet D'Arcy
 * @version $Revision: 329 $ - $Date: 2010-04-29 04:20:25 +0200 (Thu, 29 Apr 2010) $
 */
class ExplicitCreationOfStackRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == "ExplicitCreationOfStack"
    }

    void testSuccessScenario() {
        final SOURCE = '''
        	def x = [] as Stack
            class MyClass {
                def x = [] as Stack
                def m(foo = [] as Stack) {
                    def x = [] as Stack
                    def y = new Stack() {   // anony inner class OK
                    }
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testVariableDeclarations() {
        final SOURCE = '''
        	def x = new Stack()
            class MyClass {
                def m() {
                    def x = new Stack()
                }
            }
        '''
        assertTwoViolations(SOURCE,
                2, 'def x = new Stack()',
                5, 'def x = new Stack()')
    }

    void testInClassUsage() {
        final SOURCE = '''
            class MyClass {
                def x = new Stack()
                def m(foo = new Stack()) {
                }
            }
        '''
        assertTwoViolations(SOURCE,
                3, 'def x = new Stack()',
                4, 'def m(foo = new Stack())')
    }

    protected Rule createRule() {
        new ExplicitCreationOfStackRule()
    }
}