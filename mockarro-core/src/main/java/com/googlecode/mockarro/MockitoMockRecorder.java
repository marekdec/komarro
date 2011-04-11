package com.googlecode.mockarro;

import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.mockito.Matchers;

public class MockitoMockRecorder {

    private static Object any = Matchers.any();


    public void record(final Object mock, final Method method, final Object value) {
        final int paramsCount = method.getParameterTypes().length;
        try {
            when(method.invoke(mock, createParams(paramsCount))).thenReturn(value);
        } catch (final IllegalAccessException e) {
            e.printStackTrace();
        } catch (final InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    private Object[] createParams(final int count) {
        final Object[] params = new Object[count];
        for (int i = 0; i < count; i++) {
            params[i] = any;
        }
        return params;
    }
}
