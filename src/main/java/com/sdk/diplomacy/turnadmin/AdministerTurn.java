package com.sdk.diplomacy.turnadmin;

import java.io.IOException;
import java.util.List;

import org.json.simple.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.sdk.diplomacy.turnadmin.dao.GameDAO;
import com.sdk.diplomacy.turnadmin.domain.Game;
import com.sdk.diplomacy.turnadmin.model.ServerlessInput;
import com.sdk.diplomacy.turnadmin.model.ServerlessOutput;

public class AdministerTurn implements RequestHandler<ServerlessInput, ServerlessOutput> {

	@Override
	public ServerlessOutput handleRequest(ServerlessInput serverlessInput, Context context) {
		// implement your Serverless Lambda function here

		ServerlessOutput myOutput = new ServerlessOutput();

		System.out.println("I got to the lambda function");

		try {
			JSONObject gameNames = this.getGameNames(context);
			System.out.println("game names are: " + gameNames.toJSONString());
			myOutput.setBody(gameNames.toJSONString());
			myOutput.setStatusCode(200);
			System.out.println("didnt get an error in the handler");
		} catch (Exception e) {
			System.out.println("got an error in the handler: " + e);
			myOutput.setStatusCode(500);
		}

		return myOutput;
	}

	protected JSONObject getGameNames(Context context) throws IOException, ClassNotFoundException {

		LambdaLogger logger = context.getLogger();
		logger.log("Getting game names");

		JSONObject responseBody = new JSONObject();

//		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//		JSONObject responseJson = new JSONObject();
//
//		String turnIdFromQueryString = "unknown";

//			JSONObject event = (JSONObject) parser.parse(reader);
//			if (event.get("queryStringParameters") != null) {
//				JSONObject queryStringParms = (JSONObject) event.get("queryStringParameters");
//				if (queryStringParms.get("turnId") != null) {
//					turnIdFromQueryString = (String) queryStringParms.get("turnId");
//				}
//			}
		GameDAO myDAO = new GameDAO(logger);
		List<Game> myGames = myDAO.getAllGames();
		String firstGameName = "Unknown";
		if (myGames.size() > 0) {
			firstGameName = myGames.get(0).getName();
		}
		;

		responseBody.put("firstGameName", firstGameName);

//			responseJson.put("isBase64Encoded", false);
//			responseJson.put("statusCode", "200");
//			responseJson.put("body", responseBody.toString());

		return responseBody;

	}
}