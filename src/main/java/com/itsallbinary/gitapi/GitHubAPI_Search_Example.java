package com.itsallbinary.gitapi;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GitHubAPI_Search_Example {

	private static Gson gson;

	private static String GITHUB_API_BASE_URL = "https://api.github.com/";

	private static String GITHUB_API_SEARCH_CODE_PATH = "search/code?q=";

	private static String GITHUB_API_SEARCH_ISSUES_PATH = "search/issues";

	private static String GITHUB_API_SEARCH_COMMITS_PATH = "search/commits";

	public static void main(String[] args) throws IOException, URISyntaxException {

		// Using GSON to parse or print response JSON.
		gson = new GsonBuilder().setPrettyPrinting().create();

		searchFileByFileName();

		searchCodeByContent();

		searchPullRequests();

		searchCommits();

	}

	private static void searchCommits() throws ClientProtocolException, IOException {
		/*
		 * Search commits
		 * 
		 * ">" url encoded as "%3e"
		 */
		String commitsQuery = "?q=author:garydgregory+committer-date:%3e2019-08-01";

		Map commitsSearchResult = makeRESTCall(GITHUB_API_BASE_URL + GITHUB_API_SEARCH_COMMITS_PATH + commitsQuery,
				"application/vnd.github.cloak-preview");

		System.out.println("Total number or results = " + commitsSearchResult.get("total_count"));
		gson.toJsonTree(commitsSearchResult).getAsJsonObject().get("items").getAsJsonArray()
				.forEach(r -> System.out
						.println("\n\t | Message: " + r.getAsJsonObject().get("commit").getAsJsonObject().get("message")
								+ "\n\t | Date: " + r.getAsJsonObject().get("commit").getAsJsonObject().get("committer")
										.getAsJsonObject().get("date")));
	}

	private static void searchPullRequests() throws ClientProtocolException, IOException {
		/*
		 * Search pull requests
		 * 
		 * 1) Search in repo = "apache/commons-lang", 2) Type as Pull Requests 3) Only
		 * open pull requests 4) Pull requests which are to be merged in master branch
		 * 5) Sort by created date-time in ascending order.
		 */
		String pullRequestsQuery = "?q=number+repo:apache/commons-lang+type:pr+state:open+base:master&sort=created&order=asc";

		Map pullRequestsSearchResult = makeRESTCall(
				GITHUB_API_BASE_URL + GITHUB_API_SEARCH_ISSUES_PATH + pullRequestsQuery);

		System.out.println("Total number or results = " + pullRequestsSearchResult.get("total_count"));
		gson.toJsonTree(pullRequestsSearchResult).getAsJsonObject().get("items").getAsJsonArray()
				.forEach(r -> System.out.println("\tTitle: " + r.getAsJsonObject().get("title") + "\n\t\t | By User: "
						+ r.getAsJsonObject().get("user").getAsJsonObject().get("login") + "\n\t\t | Path: "
						+ r.getAsJsonObject().get("pull_request").getAsJsonObject().get("html_url")));
	}

	private static void searchCodeByContent() throws ClientProtocolException, IOException {
		/*
		 * Search Code by content in file
		 * 
		 * 1) Search for word (method name) = "containsAny", 2) Search in files, 3)
		 * Search in file with extension ".java", 4) Search in Repository =
		 * https://github.com/apache/commons-lang
		 */
		String codeContentQuery = "containsAny+in:file+language:java+repo:apache/commons-lang";

		Map contentSearchResult = makeRESTCall(GITHUB_API_BASE_URL + GITHUB_API_SEARCH_CODE_PATH + codeContentQuery,
				"application/vnd.github.v3.text-match+json");
		// System.out.println(
		// " Response = \n<API RESPONSE START>\n " + gson.toJson(contentSearchResult) +
		// "\n<API RESPONSE END>\n");

		System.out.println("Total number or results = " + contentSearchResult.get("total_count"));
		gson.toJsonTree(contentSearchResult).getAsJsonObject().get("items").getAsJsonArray().forEach(r -> {
			System.out.println("\tFile: " + r.getAsJsonObject().get("name") + "\n\t\t | Repo: "
					+ r.getAsJsonObject().get("repository").getAsJsonObject().get("html_url") + "\n\t\t | Path: "
					+ r.getAsJsonObject().get("path"));

			r.getAsJsonObject().get("text_matches").getAsJsonArray()
					.forEach(t -> System.out.println("\t\t| Matched line: " + t.getAsJsonObject().get("fragment")));
		});
	}

	private static void searchFileByFileName() throws ClientProtocolException, IOException {
		/*
		 * Search files by file name
		 * 
		 * 1) Search for file name containing "WordUtil", 2) File extension = "java" 3)
		 * File from any repo of organization "apache"
		 */
		String codeFileQuery = "filename:WordUtil+extension:java+org:apache";

		Map fileNameSearchResult = makeRESTCall(GITHUB_API_BASE_URL + GITHUB_API_SEARCH_CODE_PATH + codeFileQuery);

		System.out.println("Total number or results = " + fileNameSearchResult.get("total_count"));
		gson.toJsonTree(fileNameSearchResult).getAsJsonObject().get("items").getAsJsonArray()
				.forEach(r -> System.out.println("\tFile: " + r.getAsJsonObject().get("name") + "\n\t\t | Repo: "
						+ r.getAsJsonObject().get("repository").getAsJsonObject().get("html_url") + "\n\t\t | Path: "
						+ r.getAsJsonObject().get("path")));
	}

	private static void test() throws ClientProtocolException, IOException {
	}

	/**
	 * This method will make a REST GET call for this URL using Apache http client &
	 * fluent library.
	 * 
	 * Then parse response using GSON & return parsed Map.
	 */
	private static Map makeRESTCall(String restUrl, String acceptHeaderValue)
			throws ClientProtocolException, IOException {
		Request request = Request.Get(restUrl);

		if (acceptHeaderValue != null && !acceptHeaderValue.isBlank()) {
			request.addHeader("Accept", acceptHeaderValue);
		}

		Content content = request.execute().returnContent();
		String jsonString = content.asString();
		// System.out.println("content = " + jsonString);

		// To print response JSON, using GSON. Any other JSON parser can be used here.
		Map jsonMap = gson.fromJson(jsonString, Map.class);
		return jsonMap;
	}

	private static Map makeRESTCall(String restUrl) throws ClientProtocolException, IOException {
		return makeRESTCall(restUrl, null);
	}
}
