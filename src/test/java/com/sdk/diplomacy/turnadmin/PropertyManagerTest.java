package com.sdk.diplomacy.turnadmin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Properties;

import org.junit.Test;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.sdk.diplomacy.turnadmin.testutilities.TestLambdaLogger;

public class PropertyManagerTest {

	@Test
	public void getProperties() throws Exception {
		LambdaLogger testLogger = new TestLambdaLogger();
		PropertyManager myManager = new PropertyManager(testLogger);
		myManager.setPropertyFileName("propertyManagerTest.properties");
		myManager.initializeProperties();
		
		Properties theProperties = myManager.getProperties();
		
		assertNotNull("A properties object was returned", theProperties);
		assertNotNull("An expected value exists", theProperties.get("topLevelFirestoreCollectionName"));
		assertEquals("An expected value is correct", "topLevelFirestoreCollectionValue", theProperties.get("topLevelFirestoreCollectionName"));
		assertEquals("The correct number of properties exist", 1, theProperties.size());
	}
}
