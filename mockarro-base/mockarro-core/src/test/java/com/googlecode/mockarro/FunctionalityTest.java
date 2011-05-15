package com.googlecode.mockarro;

import static com.googlecode.mockarro.Mockarro.given;
import static com.googlecode.mockarro.Mockarro.givenObjectOf;
import static com.googlecode.mockarro.Mockarro.initSut;
import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.googlecode.mockarro.testclasses.SystemUnderTest;

public class FunctionalityTest {

    private SystemUnderTest sut;


    @BeforeMethod
    public void init() {
        sut = new SystemUnderTest();
    }


    @Test
    public void testMockingFunctionality() {
        initSut(sut);
        given(int.class).isRequested().thenReturn(4);

        final int result = sut.squarePlusOne(12);

        assertThat(result).isEqualTo(5);
    }


    @Test
    public void testMockingFunctionalityUsingSynonym() {
        initSut(sut);
        givenObjectOf(int.class).isRequested().thenReturn(16);

        final int result = sut.squarePlusOne(4);

        assertThat(result).isEqualTo(17);
    }


    @Test
    public void testGenericReturnTypeSelection() {
        initSut(sut);
        givenObjectOf(new TypeLiteral<List<Integer>>() {}).isRequested().thenReturn(asList(2, 4, 8, 16, 32));

        final List<Integer> result = sut.getFivePowersOf(2);

        assertThat(result).containsSequence(2, 4, 8, 16, 32);
    }
}
