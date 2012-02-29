package com.googlecode.komarro.testclasses;

import javax.inject.Inject;

public class SUTWithAllPrimitveTypesToInject {

	@Inject
	private String stringParam;

	@Inject
	private byte byteParam;

	@Inject
	private byte shortParam;

	@Inject
	private int intParam;

	@Inject
	private long longParam;

	@Inject
	private float floatParam;

	@Inject
	private double doubleParam;

	@Inject
	private boolean booleanParam;

	@Inject
	private char charParam;

	public String getStringParam() {
		return stringParam;
	}

	public byte getByteParam() {
		return byteParam;
	}

	public byte getShortParam() {
		return shortParam;
	}

	public int getIntParam() {
		return intParam;
	}

	public long getLongParam() {
		return longParam;
	}

	public float getFloatParam() {
		return floatParam;
	}

	public double getDoubleParam() {
		return doubleParam;
	}

	public boolean isBooleanParam() {
		return booleanParam;
	}

	public char getCharParam() {
		return charParam;
	}
}
