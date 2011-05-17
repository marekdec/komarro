package com.googlecode.mockarro;

import static com.googlecode.mockarro.Mockarro.initSut;
import static com.googlecode.mockarro.MockitoMockDescriptionCreator.annotatedMocks;
import static com.googlecode.mockarro.injector.MockDescriptor.mockedObject;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

import org.mockito.Mock;
import org.testng.annotations.Test;

import com.googlecode.mockarro.testclasses.MultiplierService;
import com.googlecode.mockarro.testclasses.SystemUnderTest;

public class MockarroAndInjectorIntegrationTest {

    @Mock
    private MultiplierService multService;


    @Test
    public void verifyInitSutWithMocksInjectsGivenMocks() {

        final MultiplierService multiplierService = new MultiplierService();

        final SystemUnderTest sut = new SystemUnderTest();
        initSut(sut, mockedObject(multiplierService).ofType(MultiplierService.class));

        assertThat(sut.getService()).isSameAs(multiplierService);
    }


    @Test
    public void verifyInitSutWithMocksInjectsDiscoveredMocks() {
        initMocks(this);

        final SystemUnderTest sut = new SystemUnderTest();
        initSut(sut, annotatedMocks(this));

        assertThat(sut.getService()).isSameAs(multService);
    }
}
