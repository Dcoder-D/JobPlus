package external;

import java.util.ArrayList;
import java.util.List;

import com.monkeylearn.ExtraParam;
import com.monkeylearn.MonkeyLearn;
import com.monkeylearn.MonkeyLearnResponse;
import com.monkeylearn.MonkeyLearnException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class MonkeyLearnClient {
	
	private static final String API_KEY = "24d47dfd8e6f0a84d2aff4062813d8c42814a356";
	private static final String MODEL_ID = "ex_YCya9nrn";
	
	public static List<List<String>> extractKeywords(String[] text) {
		if(text == null || text.length == 0) {
			return new ArrayList<>();
		}
		
		MonkeyLearn ml = new MonkeyLearn(API_KEY);
        String modelId = MODEL_ID;
        
//        ExtraParam[] extraParams = {new ExtraParam("max_keywords", "3")};
        ExtraParam extraParam = new ExtraParam("max_keywords", "3");
        try {
			MonkeyLearnResponse res = ml.extractors.extract(modelId, text, extraParam);
			JSONArray resultArray = res.arrayResult;
			return getKeywords(resultArray);
		} catch (MonkeyLearnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static List<List<String>> getKeywords(JSONArray mlResultArray) {
		List<List<String>> topKeywords = new ArrayList<>();
		// Iterate the result array and convert it to our format.
		// i represents number of job descriptions in each batch
		for(int i = 0; i < mlResultArray.size(); i++) {
			List<String> keywords = new ArrayList<>();
			JSONArray keywordsArray = (JSONArray) mlResultArray.get(i);
			// Every job description has j keywords
			for(int j = 0; j < keywordsArray.size(); j++) {
				JSONObject keywordObject = (JSONObject) keywordsArray.get(j);
				// We just need the keyword, excluding other fields
				String keyword = (String) keywordObject.get("keyword");
				keywords.add(keyword);
			}
			topKeywords.add(keywords);
		}
		
		return topKeywords;
	}
	
    public static void main( String[] args ) throws MonkeyLearnException {
        MonkeyLearn ml = new MonkeyLearn(API_KEY);
        String modelId = MODEL_ID;
        String[] data = {"Google has many openings as Senior Software Developers",
        		"Elon Musk has shared a photo of the spacesuit designed by SpaceX. This is the second image shared of the new design and the first to feature the spacesuit’s full-body look."};
//        MonkeyLearnResponse res = ml.extractors.extract(modelId, data);
//        System.out.println( res.arrayResult );
        
        List<List<String>> words = extractKeywords(data);
        for(List<String> list: words) {
        	for(String word: list) {
        		System.out.print(word + ", ");
        	}
        	System.out.println();
        }
    }
}

