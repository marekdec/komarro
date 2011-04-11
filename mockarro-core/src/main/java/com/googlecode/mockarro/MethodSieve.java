package com.googlecode.mockarro;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class MethodSieve {

    private final Class<?>      classToSift;
    private final Set<Class<?>> returnTypes = new HashSet<Class<?>>();


    public MethodSieve(final Class<?> classToSift) {
        super();
        this.classToSift = classToSift;
    }


    public static MethodSieve methodsOf(final Object objectToSift) {
        return new MethodSieve(objectToSift.getClass());
    }


    public static MethodSieve methodsOf(final Class<?> classToSift) {
        return new MethodSieve(classToSift);
    }


    public MethodSieve thatReturn(final Class<?> returnType) {
        returnTypes.add(returnType);
        return this;
    }


    public MethodSieve withParameter() {
        throw new IllegalStateException("Not implemented yet!");
    }


    public Set<Method> asSet() {
        final Set<Method> methods = new HashSet<Method>();


        for (final Method method : classToSift.getMethods()) {

            if (!method.getDeclaringClass().equals(Object.class) && returnTypes.contains(method.getReturnType())) {
                methods.add(method);
            }
        }

        return methods;
    }
}
