package com.sdk.diplomacy.turnadmin;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.json.simple.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.sdk.diplomacy.dao.DAOWarehouse;
import com.sdk.diplomacy.turnadmin.domain.Game;
import com.sdk.diplomacy.turnadmin.domain.dao.GameDAO;
import com.sdk.diplomacy.turnadmin.model.ServerlessInput;
import com.sdk.diplomacy.turnadmin.model.ServerlessOutput;

public class AdministerTurn implements RequestHandler<ServerlessInput, ServerlessOutput> {

	@Override
	public ServerlessOutput handleRequest(ServerlessInput serverlessInput, Context context) {
		// implement your Serverless Lambda function here

		ServerlessOutput myOutput = new ServerlessOutput();

		context.getLogger().log("I got to the lambda function");

		try {
			DAOWarehouse myDAOWarehouse = initializeFirebase(context.getLogger());
			JSONObject gameNames = this.getGameNames(myDAOWarehouse.getGameDAO(), context.getLogger());
			System.out.println("game names are: " + gameNames.toJSONString());
			if (serverlessInput != null && serverlessInput.getQueryStringParameters() != null && serverlessInput.getQueryStringParameters().size() > 0) {
				gameNames.put("queryStringParms", serverlessInput.getQueryStringParameters().entrySet().toString());
			}
			myOutput.setBody(gameNames.toJSONString());
			myOutput.setStatusCode(200);
			context.getLogger().log("didnt get an error in the handler");
		} catch (Exception e) {
			context.getLogger().log("got an error in the handler: " + e);
			myOutput.setStatusCode(500);
		}

		return myOutput;
	}
	
	protected DAOWarehouse initializeFirebase(LambdaLogger logger) throws Exception {
		
		DAOWarehouse myWarehouse = new DAOWarehouse(logger, (String) getProperties(logger).get("topLevelFirestoreCollectionName"));
		myWarehouse.initializeFirebase();
		
		return myWarehouse;
		
	}
	
	protected Properties getProperties(LambdaLogger logger) throws Exception{
		
		PropertyManager myManager = new PropertyManager(logger);
		myManager.initializeProperties();
		
		return myManager.getProperties();

	}

	protected JSONObject getGameNames(GameDAO myDAO, LambdaLogger logger) throws ExecutionException, InterruptedException {

		logger.log("Getting game names");

		JSONObject responseBody = new JSONObject();

		List<Game> myGames = myDAO.getAllGames();
		String firstGameName = "Unknown";
		if (myGames.size() > 0) {
			firstGameName = myGames.get(0).getName();
		};

		responseBody.put("firstGameName", firstGameName);

		return responseBody;

	}
}