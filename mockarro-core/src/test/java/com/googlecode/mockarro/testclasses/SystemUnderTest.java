package com.googlecode.mockarro.testclasses;

import javax.inject.Inject;


public class SystemUnderTest {

    @Inject
    private MultiplierService service;


    public int squarePlusOne(final int base) {
        return service.multiplier(base, base) + 1;
    }
}
