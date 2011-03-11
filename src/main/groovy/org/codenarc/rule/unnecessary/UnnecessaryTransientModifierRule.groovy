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
package org.codenarc.rule.unnecessary

import java.lang.reflect.Modifier
import org.codehaus.groovy.ast.FieldNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * The field is marked as transient, but the class isn't Serializable, so marking it as transient should have no effect.
 *
 * @author Hamlet D'Arcy
 * @version $Revision$ - $Date$
 */
class UnnecessaryTransientModifierRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryTransientModifier'
    int priority = 2
    Class astVisitorClass = UnnecessaryTransientModifierAstVisitor
}

class UnnecessaryTransientModifierAstVisitor extends AbstractAstVisitor {
    @Override
    void visitFieldEx(FieldNode node) {

        if (Modifier.isTransient(node.modifiers)) {
            if (!AstUtil.classNodeImplementsType(node.owner, Serializable)) {
                addViolation(node, "The field $node.name in class $node.owner.name is marked transient, but $node.owner.name does not implement Serializable")
            }

        }
        super.visitFieldEx(node)
    }
}