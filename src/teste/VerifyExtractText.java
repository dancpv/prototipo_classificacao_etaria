package teste;

import java.util.Map;

public class VerifyExtractText {

	public VerifyExtractText()  {
	}

	public void verifyMaps(String word, Map<String, 
			Map<String, Integer>> maps) {
		for (Map<String, Integer> map : maps.values()) {
			if (map.containsKey(word)) {
				map.put(word, map.get(word) + 1);
			}
		}
	}

	public static void main(String[] args) {
	}
}
