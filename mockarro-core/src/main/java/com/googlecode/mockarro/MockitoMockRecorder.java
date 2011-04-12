package com.googlecode.mockarro;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyByte;
import static org.mockito.Matchers.anyChar;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyShort;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MockitoMockRecorder {


    public void record(final Object mock, final Method method, final Object value) {
        try {
            when(method.invoke(mock, createParams(method.getParameterTypes()))).thenReturn(value);
        } catch (final IllegalAccessException e) {
            reportRecordError(mock, method, value, e);
        } catch (final InvocationTargetException e) {
            reportRecordError(mock, method, value, e);
        }
    }


    private void reportRecordError(final Object mock, final Method method, final Object value, final Throwable e) {
        throw new IllegalStateException("Cannot record behavior for method " + method + " that belongs to " + mock
                + " with return value " + value, e);
    }


    private Object[] createParams(final Class<?>[] paramTypes) {
        final Object[] params = new Object[paramTypes.length];

        // It is necessary to treat primitives in a different way. anyObject
        // method may return null, and this causes the method invocation to fail
        // as the given arguments are not of a required type (null cannot be
        // translated to a primitive by the invoke method and it throws an
        // exception).
        for (int i = 0; i < paramTypes.length; i++) {
            if (paramTypes[i].equals(int.class)) {
                params[i] = anyInt();
            } else if (paramTypes[i].equals(float.class)) {
                params[i] = anyFloat();
            } else if (paramTypes[i].equals(double.class)) {
                params[i] = anyDouble();
            } else if (paramTypes[i].equals(boolean.class)) {
                params[i] = anyBoolean();
            } else if (paramTypes[i].equals(char.class)) {
                params[i] = anyChar();
            } else if (paramTypes[i].equals(byte.class)) {
                params[i] = anyByte();
            } else if (paramTypes[i].equals(short.class)) {
                params[i] = anyShort();
            } else if (paramTypes[i].equals(long.class)) {
                params[i] = anyLong();
            } else {
                params[i] = anyObject();
            }
        }
        return params;
    }

}
