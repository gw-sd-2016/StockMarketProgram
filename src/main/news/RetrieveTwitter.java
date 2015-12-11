package main.news;

import java.util.Map;

import popupmessages.CheckInternet;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;

public class RetrieveTwitter {
	private static final String CONSUMER_KEY = "2pSpxRW6lim5j5Gvl2lo5odUs";
	private static final String CONSUMER_SECRET = "vQbQ7cfIE961xeKD4V0VznFYOMwNF9cTMBwUikXz0JJFEFKXhW";
	private static final int MAX_QUERIES = 5;
	private static final int TWEETS_PER_QUERY = 100;
	private static String SEARCH_TERM;

	public RetrieveTwitter(String symbol) {
		SEARCH_TERM = "$" + symbol;
	}

	public static String returnListOfTweets() {
		return null;

	}

	public static String cleanText(String text) {
		
		text = text.replace("\n", "\\n");
		text = text.replace("\t", "\\t");
		
		return text;
	}

	public static OAuth2Token getOAuth2Token() {
		
		OAuth2Token token = null;
		ConfigurationBuilder cb;
		cb = new ConfigurationBuilder();
		cb.setApplicationOnlyAuthEnabled(true);
		cb.setOAuthConsumerKey(CONSUMER_KEY).setOAuthConsumerSecret(CONSUMER_SECRET);
		
		try {
			
			token = new TwitterFactory(cb.build()).getInstance().getOAuth2Token();
		} catch (Exception e) {
			
			new CheckInternet();
		}
		return token;
	}

	public static Twitter getTwitter() {
		
		OAuth2Token token;
		token = getOAuth2Token();

		ConfigurationBuilder cb = new ConfigurationBuilder();

		cb.setApplicationOnlyAuthEnabled(true);
		cb.setOAuthConsumerKey(CONSUMER_KEY);
		cb.setOAuthConsumerSecret(CONSUMER_SECRET);
		cb.setOAuth2TokenType(token.getTokenType());
		cb.setOAuth2AccessToken(token.getAccessToken());

		return new TwitterFactory(cb.build()).getInstance();
	}

	public String retrieveTweets() {
		String result = "";
		int totalTweets = 0;
		long maxID = -1;

		Twitter twitter = getTwitter();
		try {
			Map<String, RateLimitStatus> rateLimitStatus = twitter.getRateLimitStatus("search");
			RateLimitStatus searchTweetsRateLimit = rateLimitStatus.get("/search/tweets");

			for (int queryNumber = 0; queryNumber < MAX_QUERIES; queryNumber++) {
				System.out.printf("\n\n!!! Starting loop %d\n\n", queryNumber);

				if (searchTweetsRateLimit.getRemaining() == 0) {
					System.out.printf("!!! Sleeping for %d seconds due to rate limits\n",
							searchTweetsRateLimit.getSecondsUntilReset());
					Thread.sleep((searchTweetsRateLimit.getSecondsUntilReset() + 2) * 1000l);
				}

				Query q = new Query(SEARCH_TERM);
				q.setCount(TWEETS_PER_QUERY);
				q.setLang("en");

				if (maxID != -1) {
					q.setMaxId(maxID - 1);
				}

				QueryResult r = twitter.search(q);

				if (r.getTweets().size() == 0) {
					break;
				}
				for (Status s : r.getTweets()) {
					totalTweets++;

					if (maxID == -1 || s.getId() < maxID) {
						maxID = s.getId();
					}

					result += "\n" + s.getCreatedAt().toString() + " - " + s.getUser().getScreenName() + " = "
							+ cleanText(s.getText());
					System.out.printf("At %s, @%-20s said: %s\n", s.getCreatedAt().toString(),
							s.getUser().getScreenName(), cleanText(s.getText()));

				}
				searchTweetsRateLimit = r.getRateLimitStatus();
			}

			return result + "\n\n\n\n\n\n\n\n\n\n" + String.valueOf(totalTweets);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "No Tweets!";
	}
}
