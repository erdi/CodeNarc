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
package org.codenarc.rule.concurrency

import java.lang.reflect.Modifier
import org.codehaus.groovy.ast.MethodNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Class contains similarly-named get and set methods where the set method is synchronized and the get method is not, or the get method is synchronized and the set method is not. 
 *
 * @author 'Hamlet D'Arcy'
 * @version $Revision$ - $Date$
 */
class InconsistentPropertySynchronizationRule extends AbstractAstVisitorRule {
    String name = 'InconsistentPropertySynchronization'
    int priority = 2
    Class astVisitorClass = InconsistentPropertySynchronizationAstVisitor
}

class InconsistentPropertySynchronizationAstVisitor extends AbstractAstVisitor {
    private Map<String, MethodNode> synchronizedMethods = [:]
    private Map<String, MethodNode> unsynchronizedMethods = [:]

    @Override
    void visitMethodEx(MethodNode node) {
        if (node.name.startsWith('get') && node.name.size() > 3 && AstUtil.getParameterNames(node).isEmpty()) {
            // is a getter
            def propName = node.name[3..-1]
            saveMethodInfo(node)
            addViolationOnMismatch(node.name, "set$propName")
        }

        if (node.name.startsWith('is') && node.name.size() > 2 && AstUtil.getParameterNames(node).isEmpty()) {
            // is a getter
            def propName = node.name[2..-1]
            saveMethodInfo(node)
            addViolationOnMismatch(node.name, "set$propName")
        }

        if (node.name.startsWith('set') && node.name.size() > 3 && AstUtil.getParameterNames(node).size() == 1) {
            // is a setter
            def propName = node.name[3..-1]
            saveMethodInfo(node)
            addViolationOnMismatch("get$propName", node.name)
            addViolationOnMismatch("is$propName", node.name)
        }

        super.visitMethodEx(node)
    }

    private saveMethodInfo(MethodNode node) {
        if (Modifier.isSynchronized(node.modifiers)) {
            synchronizedMethods.put(node.name, node)
        } else {
            unsynchronizedMethods.put(node.name, node)
        }
    }

    void addViolationOnMismatch(String getterName, String setterName) {
        if (synchronizedMethods.containsKey(getterName) && unsynchronizedMethods.containsKey(setterName)) {
            MethodNode node = unsynchronizedMethods.get(setterName)
            addViolation(node, "The getter method $getterName is synchronized but the setter method $setterName is not")
        }
        if (unsynchronizedMethods.containsKey(getterName) && synchronizedMethods.containsKey(setterName)) {
            MethodNode node = unsynchronizedMethods.get(getterName)
            addViolation(node, "The setter method $setterName is synchronized but the getter method $getterName is not")
        }
    }
}