package com.googlecode.mockarro;

import static com.googlecode.mockarro.MockitoMockDescriptionCreator.annotatedMocks;
import static org.fest.assertions.Assertions.assertThat;

import org.mockito.Mock;
import org.testng.annotations.Test;

import com.googlecode.mockarro.injector.MockDescriptor;
import com.googlecode.mockarro.testclasses.MultiplierService;

public class MockitoMockDescriptionCreatorTest {


    @Mock
    private final MultiplierService service = new MultiplierService();

    @Mock
    protected String                string  = "test";


    @Test
    public void verifyCorrectMockDescriptorCreation() {

        final MockDescriptor[] descriptors = annotatedMocks(this);

        assertThat(descriptors.length).isEqualTo(2);
        assertThat(descriptors).onProperty("name").containsOnly("service", "string");
        assertThat(descriptors).onProperty("mock").containsOnly(service, string);
        assertThat(descriptors).onProperty("type").containsOnly(MultiplierService.class, String.class);
    }
}
