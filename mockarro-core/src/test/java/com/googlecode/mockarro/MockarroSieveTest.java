package com.googlecode.mockarro;

import static com.googlecode.mockarro.MethodSieve.methodsOf;
import static org.fest.assertions.Assertions.assertThat;

import org.testng.annotations.Test;

public class MockarroSieveTest {

    @Test
    public void siftMethodsUsingReturnTypeCriteria() {
        assertThat(methodsOf(TestClass.class).thatReturn(Integer.class).asSet()).hasSize(1);
    }

    private static class TestClassParent {

        protected TestClassParent returnParent() {
            return this;
        }
    }

    private static class TestClass extends TestClassParent {
        private void doesNotReturnAnything() {
            // Do nothing
        }


        private int returnInt() {
            return 1;
        }


        protected String returnString() {
            return "A string";
        }


        public String returnAnotherString() {
            return "Another string";
        }
    }

}
