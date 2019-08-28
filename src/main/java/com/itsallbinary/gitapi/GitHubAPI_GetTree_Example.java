package com.itsallbinary.gitapi;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GitHubAPI_GetTree_Example {

	private static Gson gson;

	public static void main(String[] args) throws IOException, URISyntaxException {

		// To print response JSON, using GSON. Any other JSON parser can be used here.
		gson = new GsonBuilder().setPrettyPrinting().create();

		/*
		 * Call GitHub branches API REST end point & get JSON response. This response
		 * will also provide URL with treeSha for Tree REST endpoint.
		 */
		Map jsonMap = makeRESTCall("https://api.github.com/repos/RaviKharatmal/test/branches/develop");
		System.out.println(
				"Branches API Response = \n<API RESPONSE START>\n " + gson.toJson(jsonMap) + "\n<API RESPONSE END>\n");

		/*
		 * Fetch Tree API REST endpoint URL from above response. We will use gson tree
		 * traversing methods to get this.
		 * 
		 * Path in JSON = root > commit > commit > tree > url
		 */
		String treeApiUrl = gson.toJsonTree(jsonMap).getAsJsonObject().get("commit").getAsJsonObject().get("commit")
				.getAsJsonObject().get("tree").getAsJsonObject().get("url").getAsString();
		System.out.println("TREE API URL = " + treeApiUrl + "\n");

		/*
		 * Now call GitHub Tree API to get tree of files with metadata. Added recursive
		 * parameter to get all files recursively.
		 */
		Map jsonTreeMap = makeRESTCall(treeApiUrl + "?recursive=1");
		System.out.println(
				"TREE API Response = \n<API RESPONSE START>\n " + gson.toJson(jsonMap) + "\n<API RESPONSE END>\n");

		// Get tree list & iterate over it.
		System.out.println("Directory & files list :");
		for (Object obj : ((List) jsonTreeMap.get("tree"))) {

			Map fileMetadata = (Map) obj;

			// Type tree will be directory & blob will be file. Print files & directory
			// list with file sizes.
			if (fileMetadata.get("type").equals("tree")) {
				System.out.println("Directory = " + fileMetadata.get("path"));
			} else {
				System.out.println(
						"File = " + fileMetadata.get("path") + " | Size = " + fileMetadata.get("size") + " Bytes");
			}
		}
	}

	/**
	 * This method will make a REST GET call for this URL using Apache http client &
	 * fluent library.
	 * 
	 * Then parse response using GSON & return parsed Map.
	 */
	private static Map makeRESTCall(String restUrl) throws ClientProtocolException, IOException {
		Content content = Request.Get(restUrl).execute().returnContent();
		String jsonString = content.asString();
		System.out.println("content = " + jsonString);

		// To print response JSON, using GSON. Any other JSON parser can be used here.
		Map jsonMap = gson.fromJson(jsonString, Map.class);
		return jsonMap;
	}
}
