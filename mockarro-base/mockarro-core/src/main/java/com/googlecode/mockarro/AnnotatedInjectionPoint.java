package com.googlecode.mockarro;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;

/**
 * An injection point that qualifies all the elements annotated with a given
 * annotation as injectable.
 * 
 * @author marekdec
 */
public final class AnnotatedInjectionPoint implements InjectionPoint {
	private final Class<? extends Annotation> annotation;

	/**
	 * Sets the chosen injection point annotation marker (e.g. Autowired,
	 * Resource, etc).
	 * 
	 * @param annotation
	 */
	public AnnotatedInjectionPoint(final Class<? extends Annotation> annotation) {
		this.annotation = annotation;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Verifies if the element is annotated with the annotation specified when
	 * creating this InjectionPoint object.
	 */
	public boolean isInjectable(final AccessibleObject element) {

		return element.isAnnotationPresent(annotation);
	}
}
