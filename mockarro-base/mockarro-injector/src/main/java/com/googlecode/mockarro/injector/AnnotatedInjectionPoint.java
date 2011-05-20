package com.googlecode.mockarro.injector;

import static java.util.Arrays.asList;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.util.List;

/**
 * An injection point that qualifies all the elements annotated with a given
 * annotation as injectable.
 * 
 * @author marekdec
 */
public final class AnnotatedInjectionPoint implements InjectionPoint {
    private final Class<? extends Annotation>             annotation;


    private final List<Class<? extends AccessibleObject>> elementsToFilter;


    /**
     * Sets the chosen injection point annotation marker (e.g. Autowired,
     * Resource, etc) and optionally filters the accessible elements by their
     * type. For example if you want to treat all the constructors as not
     * injectable add java.lang.reflect.Constructor.class to the list.
     * 
     * @param annotation
     * @param elements
     */
    public AnnotatedInjectionPoint(final Class<? extends Annotation> annotation,
            final Class<? extends AccessibleObject>... elements) {
        this.annotation = annotation;
        this.elementsToFilter = asList(elements);
    }


    /**
     * {@inheritDoc}
     * <p>
     * Verifies if the element is annotated with the annotation specified when
     * creating this InjectionPoint object.
     */
    public boolean isInjectable(final AccessibleObject element) {
        for (final Class<? extends AccessibleObject> toFilter : elementsToFilter) {
            if (element.getClass().equals(toFilter)) {
                return false;
            }
        }

        return element.isAnnotationPresent(annotation);
    }
}
