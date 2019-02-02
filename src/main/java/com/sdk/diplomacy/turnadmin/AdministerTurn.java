package com.sdk.diplomacy.turnadmin;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.sdk.diplomacy.turnadmin.model.ServerlessInput;
import com.sdk.diplomacy.turnadmin.model.ServerlessOutput;

public class AdministerTurn implements RequestHandler<ServerlessInput, ServerlessOutput> {

    @Override
    public ServerlessOutput handleRequest(ServerlessInput serverlessInput, Context context) {
        // implement your Serverless Lambda function here
    	ServerlessOutput myOutput = new ServerlessOutput();
    	myOutput.setBody("Hi I got there");
    	myOutput.setStatusCode(200);
        return myOutput;
    }
}