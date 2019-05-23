package com.sdk.diplomacy.dao;

import java.io.PrintWriter;
import java.io.StringWriter;

public class DAOUtilities {

	public static String printStackTrace(Exception exception) {
		
		StringWriter errors = new StringWriter();
		exception.printStackTrace(new PrintWriter(errors));
		return errors.toString();
		
		
	}
}
