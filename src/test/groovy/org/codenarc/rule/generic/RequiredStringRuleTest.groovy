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
package org.codenarc.rule.generic

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule

/**
 * Tests for RequiredStringRule
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class RequiredStringRuleTest extends AbstractRuleTestCase {

    static skipTestThatUnrelatedCodeHasNoViolations
    static skipTestThatInvalidCodeHasNoViolations

    private static final TEXT = '@author Joe'

    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'RequiredString'
    }

    void testStringIsNull() {
        final SOURCE = 'class MyClass { } '
        rule.string = null
        assert !rule.ready
        assertNoViolations(SOURCE)
    }

    void testStringIsPresent() {
        final SOURCE = '''
            /** @author Joe */
            class MyClass {
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testStringIsNotPresent() {
        final SOURCE = '''
            /** @author Mike */
            class MyClass {
            }
        '''
//        assertSingleViolation(SOURCE) { v -> containsAll(v.message, ['string', TEXT]) }
        assertSingleViolation(SOURCE, null, null, ['string', TEXT])
    }

    protected Rule createRule() {
        new RequiredStringRule(string:TEXT)
    }

}