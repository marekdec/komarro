package com.googlecode.mockarro;

import static com.googlecode.mockarro.MethodSieve.methodsOf;
import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

public class MockarroSieveTest {

	@Test
	public void siftMethodsUsingReturnTypeCriteria() {
		assertThat(methodsOf(TestClass.class).thatReturn(int.class).asSet())
				.hasSize(1);
		assertThat(methodsOf(TestClass.class).thatReturn(int.class).asSet())
				.onProperty("name").containsOnly("returnInt");

		assertThat(methodsOf(TestClass.class).thatReturn(String.class).asSet())
				.hasSize(2);
		assertThat(methodsOf(TestClass.class).thatReturn(String.class).asSet())
				.onProperty("name").containsOnly("returnAnotherString",
						"returnString");

		assertThat(
				methodsOf(TestClass.class).thatReturn(TestClassParent.class)
						.asSet()).onProperty("name").containsOnly(
				"returnParent");
	}

	@Test
	public void siftMethodsSpecifingGenericReturnType() {
		assertThat(
				methodsOf(TestClass.class).thatReturn(
						new TypeLiteral<Map<String, List<Integer>>>() {
						}).asSet()).hasSize(1);

		assertThat(
				methodsOf(TestClass.class).thatReturn(new TypeLiteral<Map>() {
				}).asSet()).isEmpty();
		assertThat(
				methodsOf(TestClass.class).thatReturn(
						new TypeLiteral<Map<String, List>>() {
						}).asSet()).isEmpty();
	}

	@Test
	public void siftMethodsSpecifingUnsafeGenericType() {
		assertThat(
				methodsOf(TestClass.class).thatReturn(new TypeLiteral<List>() {
				}).asSet()).hasSize(1);
	}

	private static class TestClassParent {

		// Reflection uses this in order to test sieve functionality
		@SuppressWarnings("unused")
		public TestClassParent returnParent() {
			return this;
		}
	}

	// Reflection uses method of this Class in order to test sieve functionality
	@SuppressWarnings("unused")
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

		// List's generic parameter is missing on purposes
		@SuppressWarnings("unchecked")
		public List returnUnsafeList() {
			return new ArrayList();
		}

		public Map<String, List<Integer>> returnMap() {
			return null;
		}
	}

}
