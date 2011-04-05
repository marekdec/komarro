package com.googlecode.mockarro.injector;

import java.lang.reflect.AccessibleObject;

/**
 * An object that is capable of deciding if given element (i.e. field, method or
 * constructor) is an injection point.
 * 
 * @author marekdec
 */
public interface InjectionPoint {

    /**
     * Determines if a given element (i.e. field, method or constructor) can be
     * injected with a mock.
     * 
     * @param element
     *            a field, a method or a constructor
     * @return <code>true</code> if the element should be injected with a mock,
     *         <code>false</code> otherwise
     */
    public boolean isInjectable(AccessibleObject element);

}
