package com.googlecode.komarro;

import static java.util.Arrays.asList;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

final class Injector {

	final InjectionEngine injectionEngine;
	final InjectionPoint injectionPoint;

	/**
	 * Creates an injector builder.
	 * 
	 * @param mockEngine
	 *            a {@link MockEngine} that will be used to create the mocks.
	 * @return a new injection builder initialised with given {@link MockEngine}
	 */
	public static InjectorBuilder withMockEngine(final MockEngine mockEngine) {
		return new InjectorBuilder(mockEngine);
	}

	private Injector(final InjectionEngine injectionEngine,
			final InjectionPoint injectionPoint) {
		this.injectionEngine = injectionEngine;
		this.injectionPoint = injectionPoint;
	}

	/**
	 * Performs mock injection using the injector.
	 * 
	 * @param typeOfSystemUnderTest
	 *            the type of the unit under test that is to be created
	 * @param mocks
	 *            mock object that are to be used to inject into the injection
	 *            points, if no mocks are given or the list of mocks is not
	 *            complete the missing mocks will be created using the mock
	 *            engine
	 * @return a set of objects of the {@link Injection} type
	 */
	public <T> SutDescriptor<T> instantiateAndInject(
			final Class<T> typeOfSystemUnderTest, final MockDescriptor... mocks) {
		return injectionEngine.createAndInject(typeOfSystemUnderTest,
				injectionPoint, mocks);
	}

	/**
	 * Injector builder.
	 * 
	 * @author marekdec
	 */
	final static class InjectorBuilder {

		/**
		 * Defines a list of popular injection annotations e.g. JEE6 @Inject,
		 * Seam 2 @In or Spring @Autowired annotations.
		 */
		final List<String> popularInjectionAnnotations = asList(
				"org.jboss.seam.annotations.In",
				"org.jboss.seam.annotations.Out",
				"org.springframework.beans.factory.annotation.Autowired",
				"javax.annotation.Resource", "com.google.inject.Inject",
				"javax.inject.Inject");

		private InjectionEngine injectionEngine;
		private final MockEngine mockEngine;
		private InjectionPoint injectionPoint;

		/**
		 * Initialises the injector builder and sets the only required
		 * parameter.
		 * 
		 * @param engine
		 *            the engine that will be used to create mocks
		 */
		public InjectorBuilder(final MockEngine engine) {
			this.mockEngine = engine;
		}

		/**
		 * Sets the injection point. This parameter is optional and if not given
		 * the injection point will default to {@link AnnotatedInjectionPoint}
		 * and the annotation used for injection will be guessed.
		 * 
		 * @param injectionPoint
		 *            an {@link InjectionPoint}
		 * @return ongoing injector builder
		 */
		public InjectorBuilder withInjectionPointAt(
				final InjectionPoint injectionPoint) {
			this.injectionPoint = injectionPoint;
			return this;
		}

		/**
		 * Sets the injection engine to use. This parameter is optional. If not
		 * given it will default to {@link InjectionEngine}.
		 * 
		 * @param engine
		 *            an injection engine to use
		 * @return ongoing injection builder
		 */
		public InjectorBuilder using(final InjectionEngine engine) {
			this.injectionEngine = engine;
			return this;
		}

		/**
		 * Creates the injector using specified parameters or setting defaults
		 * if no parameters were passed to the builder. By default a
		 * {@link InjectionEngine} is used and the injection point is set to a
		 * default {@link AnnotatedInjectionPoint}. The annotation used to
		 * inject mocks is tried to be guessed by searching the classpath for a
		 * popular injection annotations.
		 * <p>
		 * Throws an {@link IllegalStateException} if no InjectionPoint has been
		 * specified and no popular injection annotation is found on the
		 * classpath. Searched annotation are listed in the exception message in
		 * the order they were searched if the search process ends with a
		 * failure.
		 * 
		 * @return a new {@link Injector}
		 */
		public Injector createInjector() {
			if (injectionPoint == null) {
				injectionPoint = guessAnnotatedInjectionPoint();
			}
			if (injectionEngine == null) {
				injectionEngine = new InjectionEngine(mockEngine);
			}

			return new Injector(injectionEngine, injectionPoint);
		}

		private InjectionPoint guessAnnotatedInjectionPoint() {
			List<Class<? extends Annotation>> injectionAnnotationsInClasspath = new ArrayList<Class<? extends Annotation>>();

			for (final String injectionAnnotationName : popularInjectionAnnotations) {
				try {
					// If the cast fails current annotation will be ignored due
					// to the catch clause, therefore it is safe to suppress
					// this warning
					@SuppressWarnings("unchecked")
					final Class<? extends Annotation> annotation = (Class<? extends Annotation>) Class
							.forName(injectionAnnotationName);

					// the execution flow will get to this point only if the
					// annotation is present in the classpath
					injectionAnnotationsInClasspath.add(annotation);
				} catch (final ClassNotFoundException e) {
					// Do nothing as this is completely normal that the
					// annotation is not present on the classpath
				} catch (final ClassCastException e) {
					// Ignore this exception, it is unlikely gets thrown, but it
					// could happen if there is a class with the name of a
					// popular annotation name that is not an annotation.
				}
			}

			if (injectionAnnotationsInClasspath.isEmpty()) {
				throw new IllegalStateException(
						"No injection point cannot be determined as no popular injection "
								+ "annotations has been found on the classpath. "
								+ popularInjectionAnnotations
								+ " were tried to be found. In case you do not use any of the listed above"
								+ " popular injection annotations, you can implement your own InjectionPoint.");
			}

			return new AnnotatedInjectionPoint(injectionAnnotationsInClasspath);
		}
	}
}
