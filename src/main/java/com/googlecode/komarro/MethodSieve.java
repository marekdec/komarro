package com.googlecode.komarro;

import static com.googlecode.komarro.TypeLiteral.create;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;



/**
 * A sieve that is capable of filtering methods of a given class using specified
 * criteria.
 * <p>
 * TODO: provide examples of usage here.
 * 
 * @author marekdec
 */
final class MethodSieve {

    private final Class<?> classToSift;
    private TypeLiteral<?> returnTypeLiteral;


    private MethodSieve(final Class<?> classToSift) {
        super();
        this.classToSift = classToSift;
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
        thatReturn(create(returnType));
        return this;
    }


    public MethodSieve thatReturn(final TypeLiteral<?> returnTypeLiteral) {
        this.returnTypeLiteral = returnTypeLiteral;
        return this;
    }



    public Set<Method> asSet() {
        final Set<Method> methods = new HashSet<Method>();

        final Type expectedType = returnTypeLiteral.getType();

        for (final Method method : classToSift.getMethods()) {

            if (!method.getDeclaringClass().equals(Object.class) && method.getGenericReturnType().equals(expectedType)) {
                methods.add(method);
            }
        }

        return methods;
    }
}
