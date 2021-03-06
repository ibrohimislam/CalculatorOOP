/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CalculatorCounter;

import ExpressionCalculator.Arab;
import ExpressionCalculator.Bilangan;
import ExpressionCalculator.Expression;
import ExpressionCalculator.Operator;
import ExpressionCalculator.Token;
import java.util.EmptyStackException;
import java.util.Stack;

/**
 *
 * @author Satria
 */
public class Penghitung {

    Expression.EnumSintaks ModeSintaks;

    Penghitung() {
        ModeSintaks = Expression.EnumSintaks.infix;
    }

    Penghitung(Penghitung p) {
        ModeSintaks = p.ModeSintaks;
    }

// operator= tidak diperlukan karena tidak ada assignment
    public double Calculate(Expression E) throws PenghitungException {
        switch (ModeSintaks) {
            case prefix:
                E = ConvertPrefix(E);
                break;
            case postfix:
                break;
            case infix:
                E = ConvertInfix(E);
                break;
        }
        return CalculatePostfix(E);
    }

    public void SetSintaks(Expression.EnumSintaks Mode) {
        ModeSintaks = Mode;
    }

    public double CalculateAtom(double a, double b, Operator o) throws PenghitungException {
        if (o.GetJenisOperator() == Operator.EnumOperator.Plus) {
            return a + b;
        } else if (o.GetJenisOperator() == Operator.EnumOperator.Minus) {
            return a - b;
        } else if (o.GetJenisOperator() == Operator.EnumOperator.bagi) {
            return a / b;
        } else if (o.GetJenisOperator() == Operator.EnumOperator.kali) {
            return a * b;
        } else if (o.GetJenisOperator() == Operator.EnumOperator.Div) {
            return Math.floor(a / b);
        } else if (o.GetJenisOperator() == Operator.EnumOperator.Mod) {
            return a - Math.floor(a / b) * b;
        } else if (o.GetJenisOperator() == Operator.EnumOperator.And)
            return (int)a & (int)b;
        else if (o.GetJenisOperator() == Operator.EnumOperator.Or) 
            return (int)a | (int)b;
	else if (o.GetJenisOperator() == Operator.EnumOperator.Xor) 
            return (int)a ^ (int)b;
	else if (o.GetJenisOperator() == Operator.EnumOperator.equal) 
            return (a == b)?1.0:0.0;
	else if (o.GetJenisOperator() == Operator.EnumOperator.ge) 
            return (a >= b)?1.0:0.0;
	else if (o.GetJenisOperator() == Operator.EnumOperator.le) 
            return (a <= b)?1.0:0.0;
	else if (o.GetJenisOperator() == Operator.EnumOperator.greater) 
            return (a > b)?1.0:0.0;
	else if (o.GetJenisOperator() == Operator.EnumOperator.less) 
            return (a < b)?1.0:0.0;
	else if (o.GetJenisOperator() == Operator.EnumOperator.nequal) 
            return (a != b)?1.0:0.0;
	else
            throw new PenghitungException("Terdapat kesalahan pada ekspresi.");
    }

    public double CalculatePostfix(Expression E) throws PenghitungException {
        Stack <Double> s = new Stack<>();
        for (int i = 0; i < E.GetLength(); ++i) {
            Token cur = E.GetToken(i);
            if (cur instanceof Operator) {
                double b2 = s.pop();
                double b1 = s.pop();
                Operator op = (Operator) cur;
                s.push(CalculateAtom(b1, b2, op));
            } else {
                if (!(cur instanceof Bilangan)) {
                    throw new PenghitungException("Terdapat kesalahan pada sintaks.");
                }
                
                Bilangan op = (Bilangan) cur;
                s.push(op.GetValue());
            }
        }

        if (s.size() == 1) {
            return s.peek();
        } else {
            throw new PenghitungException("Terdapat kesalahan pada sintaks.");
        }
    }

    /**
     * algo : 1.If the prefix string is a single variable, it is its own postfix
     * equivalent 2.Let op be the first operator of the prefix string 3.Find the
     * first operand, opnd1 of the string.Convert it to postfix and call it
     * post1. 4.Find the second operand, opnd2, of the string.Convert it to
     * postfix and cal it post2. 5.Concatenate post1, post2, and op.
     * 
     * @param prefix
     * @return Expression
     */
    
    public Expression ConvertPrefix(Expression prefix) {
        Expression postfix = new Expression();
        Stack<Token> s = new Stack<>();
        int n = prefix.GetLength();

        for (int i = 0; i < n; i++) {
            Token cur = prefix.GetToken(i);
            if (cur instanceof Operator) {
                s.push(cur);
            } else {
                postfix.AddToken(cur);
                
                while ((!s.empty()) && (((Operator)s.peek()).GetJenisOperator() == Operator.EnumOperator.unknown)){
                    s.pop();
                    postfix.AddToken(s.pop());
                    s.pop();
                }
                
                s.push(new Operator());
            }
        }
        return postfix;
    }

    public Expression ConvertInfix(Expression E) throws PenghitungException {
        Stack <Token> s1 = new Stack<>();
        Expression s2 = new Expression();

        for (int i = 0; i < E.GetLength(); ++i) {
            Token cur = E.GetToken(i);
            if (cur instanceof Bilangan) {
                s2.AddToken(cur);
            } else {
                Operator op = (Operator)cur;
		if (op.GetJenisOperator() == Operator.EnumOperator.kurungBuka) {
                    s1.push(cur);
                } else if (op.GetJenisOperator() == Operator.EnumOperator.kurungTutup) {
                    while (((Operator)s1.peek()).GetJenisOperator() != Operator.EnumOperator.kurungBuka) {
                        s2.AddToken(s1.pop());
                    }
                    s1.pop();	// pop kurungBuka
                } else if (op.GetJenisOperator() == Operator.EnumOperator.Not) {		// Not x = x - 2x + 1
                    i++;
                    if (i == E.GetLength()) {
                        throw new PenghitungException("expression incomplete");
                    }
                    cur = E.GetToken(i);
                    s2.AddToken(cur);
                    Bilangan curBilangan = (Bilangan)cur;
                    cur = new Arab(2*curBilangan.GetValue());
                    s2.AddToken(cur);
                    cur = new Operator("-");
                    s2.AddToken(cur);
                    cur = new Arab(1);
                    s2.AddToken(cur);
                    cur = new Operator("+");
                    s2.AddToken(cur);
                } else {
                    if (op.GetJenisOperator() == Operator.EnumOperator.kali || op.GetJenisOperator() == Operator.EnumOperator.bagi) {
                        s1.push(cur);
                    } else {
                        try {
                            Operator op2 = (Operator) s1.peek();
                            
                            while ((Operator)s1.peek() instanceof Operator &&
                            (op2.GetJenisOperator() == Operator.EnumOperator.kali || op2.GetJenisOperator() == Operator.EnumOperator.bagi))
                            {
                                s2.AddToken(op2);
                                s1.pop();
                            }
                        } catch (EmptyStackException e) {}      // do nothing
                        
                        s1.push(cur);
                    }
                }
            }
        }

        while (!s1.empty()) {
            s2.AddToken(s1.pop());
        }
        return s2;        
    }
}
