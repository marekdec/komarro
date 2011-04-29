package com.googlecode.mockarro.injector;

import static org.fest.assertions.Assertions.assertThat;

import javax.inject.Inject;

import org.testng.annotations.Test;

import com.googlecode.mockarro.injector.Injector.InjectorBuilder;

public class InjectorBuilderTest {

    @Inject
    public int testElementToBeInjected;


    @Test
    public void guessInjectionAnnotation() throws SecurityException, NoSuchFieldException {
        // given
        final InjectorBuilder builder = new InjectorBuilder(null);

        // when
        final Injector createdInjector = builder.createInjector();

        // then
        assertThat(createdInjector.injectionEngine).isNotNull().isInstanceOf(ReflectionInjectionEngine.class);
        assertThat(createdInjector.injectionPoint).isNotNull().isInstanceOf(AnnotatedInjectionPoint.class);
        assertThat(
                createdInjector.injectionPoint.isInjectable(InjectorBuilderTest.class
                        .getField("testElementToBeInjected"))).isTrue();
    }


    @Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = ".*\\[fakeAnnotation.*")
    public void failWhenTryingToGuessInjectionAnnotationButNoAnnotationIsPresent() throws SecurityException,
            NoSuchFieldException {
        // given
        final InjectorBuilder builder = new InjectorBuilder(null);
        setPopularAnnotationNamesToFakeOnes(builder);

        // when
        builder.createInjector();
    }


    /**
     * This method exploits the fact that the list of the popular annotations is
     * mutable and sets all the names of the popular annotations to fake ones.
     * This enforces a situation where no popular annotation can be found.
     */
    private void setPopularAnnotationNamesToFakeOnes(final InjectorBuilder builder) {
        for (int i = 0; i < builder.popularInjectionAnnotations.size(); i++) {
            builder.popularInjectionAnnotations.set(i, "fakeAnnotation");
        }
    }
}
