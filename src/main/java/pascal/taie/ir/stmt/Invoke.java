/*
 * Tai-e: A Static Analysis Framework for Java
 *
 * Copyright (C) 2020-- Tian Tan <tiantan@nju.edu.cn>
 * Copyright (C) 2020-- Yue Li <yueli@nju.edu.cn>
 * All rights reserved.
 *
 * Tai-e is only for educational and academic purposes,
 * and any form of commercial use is disallowed.
 * Distribution of Tai-e is disallowed without the approval.
 */

package pascal.taie.ir.stmt;

import pascal.taie.ir.exp.Exp;
import pascal.taie.ir.exp.InvokeDynamic;
import pascal.taie.ir.exp.InvokeExp;
import pascal.taie.ir.exp.InvokeInstanceExp;
import pascal.taie.ir.exp.InvokeInterface;
import pascal.taie.ir.exp.InvokeSpecial;
import pascal.taie.ir.exp.InvokeStatic;
import pascal.taie.ir.exp.InvokeVirtual;
import pascal.taie.ir.exp.Var;
import pascal.taie.ir.proginfo.MethodRef;
import pascal.taie.language.classes.JMethod;
import pascal.taie.util.collection.ListUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * Representation of invocation statement, e.g., r = o.m(..).
 */
public class Invoke extends DefinitionStmt<Var, InvokeExp> {

    private final InvokeExp invokeExp;

    private final Var result;

    /**
     * The method containing this invoke statement.
     */
    private final JMethod container;

    public Invoke(JMethod method, InvokeExp invokeExp, Var result) {
        this.invokeExp = invokeExp;
        this.result = result;
        if (invokeExp instanceof InvokeInstanceExp) {
            Var base = ((InvokeInstanceExp) invokeExp).getBase();
            base.addInvoke(this);
        }
        this.container = method;
    }

    public Invoke(JMethod method, InvokeExp invokeExp) {
        this(method, invokeExp, null);
    }

    @Override
    public @Nullable Var getLValue() {
        return result;
    }

    public @Nullable Var getResult() {
        return result;
    }

    @Override
    public InvokeExp getRValue() {
        return invokeExp;
    }

    public InvokeExp getInvokeExp() {
        return invokeExp;
    }

    public MethodRef getMethodRef() {
        return invokeExp.getMethodRef();
    }

    public boolean isVirtual() {
        return invokeExp instanceof InvokeVirtual;
    }

    public boolean isInterface() {
        return invokeExp instanceof InvokeInterface;
    }

    public boolean isSpecial() {
        return invokeExp instanceof InvokeSpecial;
    }

    public boolean isStatic() {
        return invokeExp instanceof InvokeStatic;
    }

    public boolean isDynamic() {
        return invokeExp instanceof InvokeDynamic;
    }

    public JMethod getContainer() {
        return container;
    }

    @Override
    public Optional<Exp> getDef() {
        return Optional.ofNullable(result);
    }

    @Override
    public List<Exp> getUses() {
        return ListUtils.append(invokeExp.getUses(), invokeExp);
    }

    @Override
    public void accept(StmtVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <T> T accept(StmtRVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        String ret = result == null ? "" : result + " = ";
        return String.format("%s%s@%s:%d(@L%d)",
                ret, invokeExp, container, getIndex(), getLineNumber());
    }
}
