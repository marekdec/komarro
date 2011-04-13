package com.googlecode.mockarro.testclasses;

import java.util.List;

import javax.inject.Inject;


public class SystemUnderTest {

    @Inject
    private MultiplierService service;


    public int squarePlusOne(final int base) {
        return service.multiplier(base, base) + 1;
    }


    public Class<?> getHiddenClass() {
        return HiddenClass.class;
    }


    private static class HiddenClass {
        public List<Integer> getList() {
            return null;
        }
    }
}
