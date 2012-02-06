package com.googlecode.mockarro.testclasses;

import java.util.List;

import javax.inject.Inject;


public class SystemUnderTest {

    @Inject
    private MultiplierService service;


    public int squarePlusOne(final int base) {
        return getService().multiplier(base, base) + 1;
    }


    public List<Integer> getFivePowersOf(final int base) {
        return getService().listOfMultiplicatons(base, base, base);
    }


    public MultiplierService getService() {
        return service;
    }

}
