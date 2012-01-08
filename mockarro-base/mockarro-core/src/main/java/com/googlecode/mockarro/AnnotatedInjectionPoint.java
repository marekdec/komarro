package com.googlecode.mockarro;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * An injection point that qualifies all the {@link AccessibleObject}s annotated
 * with a given annotations as "injectable".
 * 
 * @author marekdec
 */
public final class AnnotatedInjectionPoint implements InjectionPoint {
	private final Set<Class<? extends Annotation>> annotations;

	/**
	 * Sets a single annotation (e.g. Autowired, Resource, etc) as the injection
	 * point marker.
	 * 
	 * @param annotation
	 */
	public AnnotatedInjectionPoint(final Class<? extends Annotation> annotation) {
		this.annotations = new HashSet<Class<? extends Annotation>>();
		this.annotations.add(annotation);
	}

	/**
	 * Sets multiple annotations as the injection point markers.
	 * 
	 * @param annotation
	 */
	public AnnotatedInjectionPoint(
			final Collection<Class<? extends Annotation>> annotations) {
		this.annotations = new HashSet<Class<? extends Annotation>>(annotations);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Verifies if the element is annotated with any of the annotations
	 * specified on this object creation.
	 */
	public boolean isInjectable(final AccessibleObject element) {
		for (Class<? extends Annotation> annotation : annotations) {
			if (element.isAnnotationPresent(annotation)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "AnnotatedInjectionPoint [for annotations= " + annotations + "]";
	}

}
