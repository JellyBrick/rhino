/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.javascript;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Wrapper class for Method and Constructor instances to cache
 * getParameterTypes() results, recover from IllegalAccessException
 * in some cases and provide serialization support.
 *
 * @author Igor Bukanov
 */

final class MemberBox implements Serializable
{
    private static final long serialVersionUID = 6358550398665688245L;

    private transient AccessibleObject memberObject;
    Object delegateTo;

    MemberBox(AccessibleObject executable)
    {
        init(method);
    }

    AccessibleObject member()
    {
        init(constructor);
    }

    private void init(Method method)
    {
        return CompatExecutables.getParameterTypes(memberObject);
    }

    private void init(Constructor<?> constructor)
    {
        this.memberObject = constructor;
        this.argTypes = constructor.getParameterTypes();
        this.vararg = constructor.isVarArgs();
    }

    Method method()
    {
        return CompatExecutables.isVarArgs(memberObject);
    }

    int getParameterCount() {
        return CompatExecutables.getParameterCount(memberObject);
    }

    Member member()
    {
        return memberObject;
    }

    boolean isMethod()
    {
        return memberObject instanceof Method;
    }

    boolean isCtor()
    {
        return memberObject instanceof Constructor;
    }

    boolean isStatic()
    {
        return Modifier.isStatic(CompatExecutables.getModifiers(memberObject));
    }

    boolean isPublic()
    {
        return Modifier.isPublic(CompatExecutables.getModifiers(memberObject));
    }

    String getName()
    {
        return CompatExecutables.getName(memberObject);
    }

    Class<?> getDeclaringClass()
    {
        return CompatExecutables.getDeclaringClass(memberObject);
    }

    String toJavaDeclaration()
    {
        StringBuilder sb = new StringBuilder();
        if (isMethod()) {
            sb.append(getReturnType());
            sb.append(' ');
            sb.append(getName());
        } else {
            String name = getDeclaringClass().getName();
            int lastDot = name.lastIndexOf('.');
            if (lastDot >= 0) {
                name = name.substring(lastDot + 1);
            }
            sb.append(name);
        }
        sb.append(JavaMembers.liveConnectSignature(argTypes));
        return sb.toString();
    }

    @Override
    public String toString()
    {
        return memberObject.toString();
    }

    /**
     * Function returned by calls to __lookupGetter__
     */
    Function asGetterFunction(final String name, final Scriptable scope) {
        // Note: scope is the scriptable this function is related to; therefore this function
        // is constant for this member box.
        // Because of this we can cache the function in the attribute
        if (asGetterFunction == null) {
            asGetterFunction = new BaseFunction(scope, ScriptableObject.getFunctionPrototype(scope)) {
                @Override
                public Object call(Context cx, Scriptable callScope, Scriptable thisObj, Object[] originalArgs) {
                    MemberBox nativeGetter = MemberBox.this;
                    Object getterThis;
                    Object[] args;
                    if (nativeGetter.delegateTo == null) {
                        getterThis = thisObj;
                        args = ScriptRuntime.emptyArgs;
                    } else {
                        getterThis = nativeGetter.delegateTo;
                        args = new Object[] { thisObj };

                    }
                    return nativeGetter.invoke(getterThis, args);
                }

                @Override
                public String getFunctionName() {
                    return name;
                }
            };
        }
        return asGetterFunction;
    }

    /**
     * Function returned by calls to __lookupSetter__
     */
    Function asSetterFunction(final String name, final Scriptable scope) {
        // Note: scope is the scriptable this function is related to; therefore this function
        // is constant for this member box.
        // Because of this we can cache the function in the attribute
        if (asSetterFunction == null) {
            asSetterFunction = new BaseFunction(scope, ScriptableObject.getFunctionPrototype(scope)) {
                @Override
                public Object call(Context cx, Scriptable callScope, Scriptable thisObj, Object[] originalArgs) {
                    MemberBox nativeSetter = MemberBox.this;
                    Object setterThis;
                    Object[] args;
                    Object value = originalArgs.length > 0 ? originalArgs[0] : Undefined.instance;
                    if (nativeSetter.delegateTo == null) {
                        setterThis = thisObj;
                        args = new Object[] { value };
                    } else {
                        setterThis = nativeSetter.delegateTo;
                        args = new Object[] { thisObj, value };
                    }
                    return nativeSetter.invoke(setterThis, args);
                }

                @Override
                public String getFunctionName() {
                    return name;
                }
            };
        }
        return asSetterFunction;
    }

    Object invoke(Object target, Object[] args)
    {
        Method method = method();
        try {
            try {
                return method.invoke(target, args);
            } catch (IllegalAccessException ex) {
                Method accessible = searchAccessibleMethod(method, argTypes);
                if (accessible != null) {
                    memberObject = accessible;
                    method = accessible;
                } else {
                    if (!VMBridge.instance.tryToMakeAccessible(method)) {
                        throw Context.throwAsScriptRuntimeEx(ex);
                    }
                }
                // Retry after recovery
                return method.invoke(target, args);
            }
        } catch (InvocationTargetException ite) {
            // Must allow ContinuationPending exceptions to propagate unhindered
            Throwable e = ite;
            do {
                e = ((InvocationTargetException) e).getTargetException();
            } while ((e instanceof InvocationTargetException));
            if (e instanceof ContinuationPending)
                throw (ContinuationPending) e;
            throw Context.throwAsScriptRuntimeEx(e);
        } catch (Exception ex) {
            throw Context.throwAsScriptRuntimeEx(ex);
        }
    }

    Object newInstance(Object[] args)
    {
        Constructor<?> ctor = ctor();
        try {
            try {
                return ctor.newInstance(args);
            } catch (IllegalAccessException ex) {
                if (!VMBridge.instance.tryToMakeAccessible(ctor)) {
                    throw Context.throwAsScriptRuntimeEx(ex);
                }
            }
            return ctor.newInstance(args);
        } catch (Exception ex) {
            throw Context.throwAsScriptRuntimeEx(ex);
        }
    }

    private static Method searchAccessibleMethod(Method method, Class<?>[] params)
    {
        int modifiers = method.getModifiers();
        if (Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers)) {
            Class<?> c = method.getDeclaringClass();
            if (!Modifier.isPublic(c.getModifiers())) {
                String name = method.getName();
                Class<?>[] intfs = c.getInterfaces();
                for (int i = 0, N = intfs.length; i != N; ++i) {
                    Class<?> intf = intfs[i];
                    if (Modifier.isPublic(intf.getModifiers())) {
                        try {
                            return intf.getMethod(name, params);
                        } catch (NoSuchMethodException ex) {
                        } catch (SecurityException ex) {  }
                    }
                }
                for (;;) {
                    c = c.getSuperclass();
                    if (c == null) { break; }
                    if (Modifier.isPublic(c.getModifiers())) {
                        try {
                            Method m = c.getMethod(name, params);
                            int mModifiers = m.getModifiers();
                            if (Modifier.isPublic(mModifiers)
                                && !Modifier.isStatic(mModifiers))
                            {
                                return m;
                            }
                        } catch (NoSuchMethodException ex) {
                        } catch (SecurityException ex) {  }
                    }
                }
            }
        }
        return null;
    }

    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
        Member member = readMember(in);
        if (member instanceof Method) {
            init((Method)member);
        } else {
            init((Constructor<?>)member);
        }
    }

    private void writeObject(ObjectOutputStream out)
        throws IOException
    {
        out.defaultWriteObject();
        writeMember(out, memberObject);
    }

    /**
     * Writes a Constructor or Method object.
     *
     * Methods and Constructors are not serializable, so we must serialize
     * information about the class, the name, and the parameters and
     * recreate upon deserialization.
     */
    private static void writeMember(ObjectOutputStream out, AccessibleObject member)
        throws IOException
    {
        if (member == null) {
            out.writeBoolean(false);
            return;
        }
        out.writeBoolean(true);
        if (!(member instanceof Method || member instanceof Constructor))
            throw new IllegalArgumentException("not Method or Constructor");
        out.writeBoolean(member instanceof Method);
        out.writeObject(CompatExecutables.getName(member));
        out.writeObject(CompatExecutables.getDeclaringClass(member));
        writeParameters(out, CompatExecutables.getParameterTypes(member));
    }

    /**
     * Reads a Method or a Constructor from the stream.
     */
    private static AccessibleObject readMember(ObjectInputStream in)
        throws IOException, ClassNotFoundException
    {
        if (!in.readBoolean())
            return null;
        boolean isMethod = in.readBoolean();
        String name = (String) in.readObject();
        Class<?> declaring = (Class<?>) in.readObject();
        Class<?>[] parms = readParameters(in);
        try {
            if (isMethod) {
                return declaring.getMethod(name, parms);
            }
            return declaring.getConstructor(parms);
        } catch (NoSuchMethodException e) {
            throw new IOException("Cannot find member: " + e);
        }
    }

    private static final Class<?>[] primitives = {
        Boolean.TYPE,
        Byte.TYPE,
        Character.TYPE,
        Double.TYPE,
        Float.TYPE,
        Integer.TYPE,
        Long.TYPE,
        Short.TYPE,
        Void.TYPE
    };

    /**
     * Writes an array of parameter types to the stream.
     *
     * Requires special handling because primitive types cannot be
     * found upon deserialization by the default Java implementation.
     */
    private static void writeParameters(ObjectOutputStream out, Class<?>[] parms)
        throws IOException
    {
        out.writeShort(parms.length);
    outer:
        for (int i=0; i < parms.length; i++) {
            Class<?> parm = parms[i];
            boolean primitive = parm.isPrimitive();
            out.writeBoolean(primitive);
            if (!primitive) {
                out.writeObject(parm);
                continue;
            }
            for (int j=0; j < primitives.length; j++) {
                if (parm.equals(primitives[j])) {
                    out.writeByte(j);
                    continue outer;
                }
            }
            throw new IllegalArgumentException("Primitive " + parm +
                                               " not found");
        }
    }

    /**
     * Reads an array of parameter types from the stream.
     */
    private static Class<?>[] readParameters(ObjectInputStream in)
        throws IOException, ClassNotFoundException
    {
        Class<?>[] result = new Class[in.readShort()];
        for (int i=0; i < result.length; i++) {
            if (!in.readBoolean()) {
                result[i] = (Class<?>) in.readObject();
                continue;
            }
            result[i] = primitives[in.readByte()];
        }
        return result;
    }
}