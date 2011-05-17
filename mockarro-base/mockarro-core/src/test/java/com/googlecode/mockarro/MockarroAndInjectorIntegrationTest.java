package com.googlecode.mockarro;

import static com.googlecode.mockarro.Mockarro.initSut;
import static com.googlecode.mockarro.injector.MockDescriptor.mockedObject;
import static org.fest.assertions.Assertions.assertThat;

import org.testng.annotations.Test;

import com.googlecode.mockarro.testclasses.MultiplierService;
import com.googlecode.mockarro.testclasses.SystemUnderTest;

public class MockarroAndInjectorIntegrationTest {

    @Test
    public void verifyInitSutWithMocksInjectsGivenMocks() {

        final MultiplierService multiplierService = new MultiplierService();

        final SystemUnderTest sut = new SystemUnderTest();
        initSut(sut, mockedObject(multiplierService).ofType(MultiplierService.class));

        assertThat(sut.getService()).isSameAs(multiplierService);
    }
}
