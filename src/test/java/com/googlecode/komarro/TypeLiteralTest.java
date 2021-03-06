package com.googlecode.komarro;

import static org.fest.assertions.Assertions.assertThat;

import org.testng.annotations.Test;

import com.googlecode.komarro.TypeLiteral;

public class TypeLiteralTest {

    @Test
    public void testStaticLiteralCreationForNonGenericType() {
        final TypeLiteral<?> literal = TypeLiteral.create(String.class);

        assertThat(literal.getType()).isEqualTo(String.class);
    }


    @Test(expectedExceptions = NullPointerException.class)
    public void testStaticLiteralCreationForNonGenericTypeWhenNullIsGiven() {
        TypeLiteral.create(null);
    }
}
