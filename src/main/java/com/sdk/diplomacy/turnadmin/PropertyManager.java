package com.sdk.diplomacy.turnadmin;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.sdk.diplomacy.dao.DAOUtilities;

public class PropertyManager {

	private Properties properties;
	private String propertyFileName = "diplomacy.properties";
	private LambdaLogger logger;
	
	public PropertyManager(LambdaLogger logger) {
		super();
		this.logger = logger;
	}
	
	public Properties getProperties() {

		return properties;
	}

	public void initializeProperties() throws Exception {

		try {
			logger.log("Started initializing the property manager for AWS deployment");
			Properties tempProps = new Properties();
			InputStream propInputStream = getClass().getClassLoader().getResourceAsStream("resources/" + propertyFileName);
			tempProps.load(propInputStream);
			propInputStream.close();
			properties = tempProps;
		} catch (Exception e) {
			try {
				logger.log("Started initializing the property manager for local deployment");
				Properties tempProps = new Properties();
				InputStream propInputStream = getClass().getClassLoader().getResourceAsStream(propertyFileName);
				tempProps.load(propInputStream);
				propInputStream.close();
				properties = tempProps;
			} catch (Exception e2) {
				logger.log("unable to get get properties: " + DAOUtilities.printStackTrace(e2));
				throw e2;
			}
		}

	}

	public String getPropertyFileName() {
		return propertyFileName;
	}

	public void setPropertyFileName(String propertyFileName) {
		this.propertyFileName = propertyFileName;
	}

}
