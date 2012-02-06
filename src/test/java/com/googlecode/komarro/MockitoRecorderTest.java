package com.googlecode.komarro;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.testng.annotations.Test;

import com.googlecode.komarro.MockitoMockRecorder;

public class MockitoRecorderTest {

    MockitoMockRecorder recorder = new MockitoMockRecorder();


    @Test
    public void recordBehaviorForAMethodWithNoParameters() throws SecurityException, NoSuchMethodException {
        final TestClass mock = mock(TestClass.class);

        recorder.record(mock, mock.getClass().getDeclaredMethod("returnInt"), 12);

        assertThat(mock.returnInt()).isEqualTo(12);
    }


    @Test
    public void recordBehaviorForAMethodWithParameters() throws SecurityException, NoSuchMethodException {
        final TestClass mock = mock(TestClass.class);

        recorder.record(mock, mock.getClass().getDeclaredMethod("returnInt", MockitoMockRecorder.class, String.class),
                5);

        assertThat(mock.returnInt(null, "string")).isEqualTo(5);
    }


    @Test
    public void recordBehaviorForAMethodWithPrimitiveParameters() throws SecurityException, NoSuchMethodException {
        final TestClass mock = mock(TestClass.class);

        recorder.record(
                mock,
                mock.getClass().getDeclaredMethod("methodWithPrimitiveParams", int.class, short.class, long.class,
                        byte.class, double.class, float.class, boolean.class, char.class), "test");

        assertThat(mock.methodWithPrimitiveParams(12, (short) 12, 12l, (byte) 1, 1.0d, 1.0f, true, 'a')).isEqualTo(
                "test");
    }

    static class TestClass {
        public int returnInt() {
            return 1;
        }


        public int returnInt(final MockitoMockRecorder mockitoMockRecorder, final String param2) {
            return 2;
        }




        public String methodWithPrimitiveParams(final int i, final short s, final long l, final byte b, final double d,
                final float f, final boolean bl, final char c) {
            return "test";
        }
    }
}
