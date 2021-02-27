package external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import entity.Item;


public class GitHubClient {
	private static final String URL_TEMPLATE = "https://jobs.github.com/positions.json?description=%s&lat=%s&long=%s";
	private static final String DEFAULT_KEYWORD = "developer";
	
	public List<Item> search(double lat, double lon, String keyword) {

		
		if(keyword == null) {
			keyword = DEFAULT_KEYWORD;
		}
		
		try {
			keyword = URLEncoder.encode(keyword, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		String url = String.format(URL_TEMPLATE, keyword, lat, lon);
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			CloseableHttpResponse response = httpClient.execute(new HttpGet(url));
			
			if(response.getStatusLine().getStatusCode() != 200) {
				return new ArrayList<>();
				
			}
			
			HttpEntity entity = response.getEntity();
			if(entity == null) {
				return new ArrayList<>();
			}
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
			StringBuilder responseBody = new StringBuilder();
			String line = null;
			
			while((line = reader.readLine()) != null) {
				responseBody.append(line);
			}
			
			JSONArray array = new JSONArray(responseBody.toString());
			return getItemList(array);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new ArrayList<>();
	}


	private List<Item> getItemList(JSONArray array) {
		List<Item> itemList = new ArrayList<>();
		
		// Obtain the job descriptions out first to prepare for batch call to ml API
		List<String> jds = new ArrayList<>();
		for(int i = 0; i < array.length(); i++) {
			String description = getStringFieldOrEmpty(array.getJSONObject(i), "description");
			if(description.equals("") || description.equals("\n")) {
				jds.add(getStringFieldOrEmpty(array.getJSONObject(i), "title"));
			} else {
				jds.add(description);
			}
		}
		
		List<List<String>> keywords = MonkeyLearnClient.extractKeywords(jds.toArray(new String[jds.size()]));
//		System.out.println(keywords);
		
		
		for (int i = 0; i < array.length(); ++i) {
			JSONObject object = array.getJSONObject(i);
			Item.ItemBuilder builder = new Item.ItemBuilder();
			
			builder.setItemId(getStringFieldOrEmpty(object, "id"));
			builder.setName(getStringFieldOrEmpty(object, "title"));
			builder.setAddress(getStringFieldOrEmpty(object, "location"));
			builder.setUrl(getStringFieldOrEmpty(object, "url"));
			builder.setImageUrl(getStringFieldOrEmpty(object, "company_logo"));
			
			List<String> list = keywords.get(i);
			builder.setKeywords(new HashSet<String>(list));
			
			Item item = builder.build();
			itemList.add(item);
		}
		
		return itemList;
	}
	
	private String getStringFieldOrEmpty(JSONObject obj, String field) {
		return obj.isNull(field) ? "" : obj.getString(field);
	}

}
