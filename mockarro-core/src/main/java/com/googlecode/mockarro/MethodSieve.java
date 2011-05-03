package com.googlecode.mockarro;

import static java.util.Arrays.asList;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A sieve that is capable of filtering methods of a given class using specified
 * criteria.
 * <p>
 * TODO: provide examples of usage here.
 * 
 * @author marekdec
 */
public class MethodSieve {

    private final Class<?>       classToSift;
    private Class<?>             returnType;
    private final List<Class<?>> genericTypes = new ArrayList<Class<?>>();


    private MethodSieve(final Class<?> classToSift) {
        super();
        this.classToSift = classToSift;
    }


    /**
     * Initializes the sieve with the class of given object.
     * 
     * @param objectToSift
     *            object that will initialize the sieve with its class.
     * @return a new methods sieve
     */
    public static MethodSieve methodsOf(final Object objectToSift) {
        return new MethodSieve(objectToSift.getClass());
    }


    /**
     * Initializes the sieve with given class.
     * 
     * @param classToSift
     *            a class to sift
     * @return a new methods sieve
     */
    public static MethodSieve methodsOf(final Class<?> classToSift) {
        return new MethodSieve(classToSift);
    }


    /**
     * Defines expected return type criteria
     * 
     * @param returnType
     *            the return type of the methods
     * @return self
     */
    public MethodSieve thatReturn(final Class<?> returnType) {
        this.returnType = returnType;
        return this;
    }


    public MethodSieve of(final Class<?>... genericTypes) {
        this.genericTypes.addAll(asList(genericTypes));
        return this;
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
