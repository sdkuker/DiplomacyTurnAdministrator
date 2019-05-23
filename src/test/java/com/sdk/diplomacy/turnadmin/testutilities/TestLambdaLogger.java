package com.sdk.diplomacy.turnadmin.testutilities;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class TestLambdaLogger implements LambdaLogger {

	@Override
	public void log(String string) {
		System.out.println(string);

	}

}
