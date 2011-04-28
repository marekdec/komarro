package com.googlecode.mockarro;

import static com.googlecode.mockarro.Mockarro.initSut;
import static com.googlecode.mockarro.Mockarro.whenRequested;
import static org.fest.assertions.Assertions.assertThat;

import org.testng.annotations.Test;

import com.googlecode.mockarro.testclasses.SystemUnderTest;

public class FunctionalityTest {

    SystemUnderTest sut = new SystemUnderTest();


    @Test
    public void testMockingFunctionality() {
        initSut(sut);
        whenRequested(int.class).thenReturn(4);

        final int result = sut.squarePlusOne(12);

        assertThat(result).isEqualTo(5);
    }
}