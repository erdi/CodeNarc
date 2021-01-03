/*
 * Copyright 2020 the original author or authors.
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
package org.codenarc.rule.formatting

import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Checks that there is no trailing whitespace in the method name when a method call contains parenthesis or that
 * there is at most one space after the method name if the call does not contain parenthesis
 */
class MethodCallNameTrailingWhitespaceRule extends AbstractAstVisitorRule {

    String name = 'MethodCallNameTrailingWhitespace'
    int priority = 3
    Class astVisitorClass = MethodCallNameTrailingWhitespaceRuleAstVisitor
}

class MethodCallNameTrailingWhitespaceRuleAstVisitor extends AbstractAstVisitor {

    @Override
    void visitConstructorCallExpression(ConstructorCallExpression call) {
        if (isFirstVisit(call)) {
            if (call.superCall) {
                if (isBlankAtIndexFromExpressionStart(call, -2)) {
                    addViolation(call, 'There is whitespace between super and parenthesis in a constructor call.')
                }
            } else {
                if (call.lineNumber >= 0 && hasPrecedingWhitespace(call)) {
                    addViolation(call, 'There is whitespace between class name and parenthesis in a constructor call.')
                }
            }
        }
        super.visitConstructorCallExpression(call)
    }

    private boolean hasPrecedingWhitespace(ConstructorCallExpression call) {
        sourceLine(call).substring(call.columnNumber - 1) =~ /^[^(]+\s\(/
    }

    private boolean isBlankAtIndexFromExpressionStart(Expression expression, int index) {
        def columnNumber = expression.columnNumber

        columnNumber + index >= 0 && sourceLine(expression)[expression.columnNumber + index].allWhitespace
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        def method = call.method
        def arguments = call.arguments
        if (isFirstVisit(call) &&
                method.lineNumber == arguments.lineNumber &&
                method.lastColumnNumber + 1 < arguments.columnNumber &&
                sourceLine(method)[method.lastColumnNumber - 1] != '('
        ) {
            def message = hasArgsInParenthesis(call) ?
                    'There is whitespace between method name and parenthesis in a method call.' :
                    'There is more than one space between method name and arguments in a method call.'

            addViolation(call, message)
        }

        super.visitMethodCallExpression(call)
    }

    private boolean hasArgsInParenthesis(MethodCallExpression methodCallExpression) {
        def arguments = methodCallExpression.arguments
        def charcterBeforeFirstArgument = sourceLine(arguments)[arguments.columnNumber - 2]

        charcterBeforeFirstArgument == '('
    }
}
