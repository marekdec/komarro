package com.googlecode.mockarro;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Specifies an exact type literal along with the generic parameters. As the
 * generic types in java are erased at runtime TypeLiteral exploits the fact
 * that the generic information of the parent classes is maintained at runtime.
 * <p>
 * This is an abstract class and it is intended to be extended by anonymous
 * classes in a following way:
 * <p>
 * <code>
 * {@code new TypeLiteral<SomeGenericClass<Parameter>>() {};}
 * </code>
 * <p>
 * For example following declaration allows retaining the information about the
 * types of the map parameters:
 * <p>
 * <code>
 * {@code new TypeLiteral<Map<String, List<Integer>>>() {};}
 * </code>
 * <p>
 * TypeLiteral also provides a static factory method that is intended to be used
 * to specify non-generic class literals. When declaring a non-generic type
 * literal it is preferable to use the {@link #create(Class)} factory method.
 * 
 * 
 * @author marekdec
 * 
 * @param <T>
 */
public abstract class TypeLiteral<T> {

    private final Type type;


    protected TypeLiteral() {
        final ParameterizedType pt = ParameterizedType.class.cast(this.getClass().getGenericSuperclass());
        if (pt != null) {
            type = pt.getActualTypeArguments()[0];
        } else {
            throw new IllegalArgumentException(
                    "Every instance of the sub types of the TypeLiteral has to define the generic parameter. Did you forget to put <YourType> after TypeLiteral?");
        }
    }


    private TypeLiteral(final Class<?> clazz) {
        if (clazz == null) {
            throw new NullPointerException("Type literal cannot be instantiated given a null class literal.");
        }
        type = clazz;
    }


    /**
     * Creates the a TypeLiteral for a non-generic class. This should be used
     * only when a literal for a non-generic class is inteded to be created.
     * <p>
     * TODO: this method could be given a better name
     * 
     * @param clazz
     *            a non-generic class
     * @return a new subclass of TypeLiteral
     */
    public static <T> TypeLiteral<T> create(final Class<T> clazz) {
        return new TypeLiteral<T>(clazz) {
        };
    }


    public Type getType() {
        return type;
    }
}
