package com.googlecode.mockarro;

import static com.googlecode.mockarro.MethodSieve.methodsOf;
import static org.fest.assertions.Assertions.assertThat;

import org.testng.annotations.Test;

public class MockarroSieveTest {

    @Test
    public void siftMethodsUsingReturnTypeCriteria() {
        assertThat(methodsOf(TestClass.class).thatReturn(int.class).asSet()).hasSize(1);
        assertThat(methodsOf(TestClass.class).thatReturn(int.class).asSet()).onProperty("name").containsOnly(
                "returnInt");

        assertThat(methodsOf(TestClass.class).thatReturn(String.class).asSet()).hasSize(2);
        assertThat(methodsOf(TestClass.class).thatReturn(String.class).asSet()).onProperty("name").containsOnly(
                "returnAnotherString", "returnString");

        assertThat(methodsOf(TestClass.class).thatReturn(TestClassParent.class).asSet()).onProperty("name")
                .containsOnly("returnParent");
    }

    private static class TestClassParent {

        public TestClassParent returnParent() {
            return this;
        }
    }

    private static class TestClass extends TestClassParent {
        public void doesNotReturnAnything() {
            // Do nothing
        }


        public int returnInt() {
            return 1;
        }


        public String returnString() {
            return "A string";
        }


        public String returnAnotherString() {
            return "Another string";
        }
    }

}
