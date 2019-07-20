package com.sdk.diplomacy.turnadmin;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.json.simple.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.sdk.diplomacy.dao.DAOWarehouse;
import com.sdk.diplomacy.turnadmin.domain.Game;
import com.sdk.diplomacy.turnadmin.domain.Turn;
import com.sdk.diplomacy.turnadmin.model.ServerlessInput;
import com.sdk.diplomacy.turnadmin.model.ServerlessOutput;

public class AdministerTurn implements RequestHandler<ServerlessInput, ServerlessOutput> {	

	@Override
	public ServerlessOutput handleRequest(ServerlessInput serverlessInput, Context context) {
		
		context.getLogger().log("Starting to process the request in the lambda function");

		ServerlessOutput myOutput = new ServerlessOutput();
		JSONObject responseBody = new JSONObject();
		
		Map<String, String> myHeaders = new HashMap<String, String>();
		myHeaders.put("Access-Control-Allow-Origin", "*");
		myOutput.setHeaders(myHeaders);

		try {
			DAOWarehouse myDAOWarehouse = initializeFirebase(context.getLogger());
			
			if (serverlessInput != null && serverlessInput.getQueryStringParameters() != null && serverlessInput.getQueryStringParameters().size() > 0) {
				String idOfGameToProcess = serverlessInput.getQueryStringParameters().get("generateNextPhaseForGame");
				if (idOfGameToProcess != null) {
					context.getLogger().log("Received request to process next phase for game: " + idOfGameToProcess);
					Game gameToProcess = myDAOWarehouse.getGameDAO().getGame(idOfGameToProcess);
					if (gameToProcess != null) {
						context.getLogger().log("Found game with ID: " + idOfGameToProcess + " and starting to process it");
						boolean openTurnFound = processNextPhaseForGameId(idOfGameToProcess, myDAOWarehouse, context.getLogger());
						if (openTurnFound) {
							responseBody.put("status", "complete");
							myOutput.setBody(responseBody.toJSONString());
							myOutput.setStatusCode(200);
						} else {
							responseBody.put("status", "Not processed - no open turn found or it has an invalid phase");
							myOutput.setBody(responseBody.toJSONString());
							myOutput.setStatusCode(412);
						}
						context.getLogger().log("Finished processing game with ID: " + idOfGameToProcess);

					} else {
						context.getLogger().log("Could not find game with ID: " + idOfGameToProcess + " to process");
						myOutput.setStatusCode(404);
					}
				} else {
					context.getLogger().log("No generatedNextPhaseForGame query string parameter found");
					myOutput.setStatusCode(400);
				}
			} else {
				context.getLogger().log("No query string parms found");
				myOutput.setStatusCode(400);
			}
			
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
	
	protected boolean processNextPhaseForGameId(String aGameId, DAOWarehouse myDAOWarehouse, LambdaLogger aLogger) throws Exception {
		
		boolean processedSuccessfully = false;
		Turn openTurn = myDAOWarehouse.getTurnDAO().getOpenTurnForGame(aGameId);
		
		if (openTurn != null) {
			if (openTurn.getPhase() != null) {
				processedSuccessfully = true;
				switch (openTurn.getPhase()) {
				case DIPLOMATIC: 
					aLogger.log("Processing Diplomatic Phase");
					myDAOWarehouse.getTurnDAO().updatePhase(openTurn, Turn.Phases.ORDER_WRITING);
					break;
				case ORDER_WRITING:
					aLogger.log("Processing Order Writing Phase");
					myDAOWarehouse.getTurnDAO().updatePhase(openTurn, Turn.Phases.ORDER_RESOLUTION);
					break;
				case ORDER_RESOLUTION:
					aLogger.log("Started processing Order Resolution Phase");
					ExecuteTurn aTurnExecuter = new ExecuteTurn(myDAOWarehouse, aLogger);
					aTurnExecuter.executeOrderResolutionPhase(aGameId, openTurn.getId());
					break;
				case RETREAT_AND_DISBANDING:
					aLogger.log("Started processing Retreat and Disbanding Phase");
					break;
				case GAINING_AND_LOSING_UNITS:
					aLogger.log("Started processing Gaining and Losing Units Phase");
					break;
				}
			}
		}
		
		return processedSuccessfully;
	}


}