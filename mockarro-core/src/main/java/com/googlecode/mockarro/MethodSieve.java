package com.googlecode.mockarro;

import static java.util.Arrays.asList;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MethodSieve {

    private final Class<?>       classToSift;
    private Class<?>             returnType;
    private final List<Class<?>> genericTypes = new ArrayList<Class<?>>();


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
        this.returnType = returnType;
        return this;
    }


    public MethodSieve of(final Class<?> genericType, final Class<?>... moreGenericTypes) {
        this.genericTypes.add(genericType);
        this.genericTypes.addAll(Arrays.asList(moreGenericTypes));
        return this;
    }


    public MethodSieve withParameter() {
        throw new IllegalStateException("Not implemented yet!");
    }


    public Set<Method> asSet() {
        final Set<Method> methods = new HashSet<Method>();

        for (final Method method : classToSift.getMethods()) {

            if (!method.getDeclaringClass().equals(Object.class) && returnType.equals(method.getReturnType())) {
                if (!genericTypes.isEmpty()) {
                    final ParameterizedType methodGenericReturnType = ParameterizedType.class.cast(method
                            .getGenericReturnType());
                    if (methodGenericReturnType != null) {
                        if (genericTypes.equals(asList(methodGenericReturnType.getActualTypeArguments()))) {
                            methods.add(method);
                        }
                    }
                } else {
                    methods.add(method);
                }
            }
        }

        return methods;
    }
}
