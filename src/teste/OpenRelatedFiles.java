package teste;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OpenRelatedFiles {


	public OpenRelatedFiles() {
	}
	
	private Map<String, Map<String, Integer>> maps = 
			new HashMap<String, Map<String, Integer>>();

	public Map<String, Map<String, Integer>> getMaps() {
		openFolder();
		return maps;
	}

	private void openFolder() {
		String subs = ExtractText.dir.replaceAll("\"", "\\");
		File baseFolder = new File(subs);
		File[] files = baseFolder.listFiles();
		for (File file : files) {
			String nameFile = file.getName();
			readFile(nameFile, subs + "\\" + nameFile);
		}
	}
	
	private void readFile(String name, String file) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		maps.put(name, map);
		FileReader fileReader;
		
		try {
			fileReader = new FileReader(file);
			BufferedReader br = new BufferedReader(fileReader);
			String aux = "";
			while ((aux = br.readLine()) != null) {
				String[] words = aux.split(";");
				for (String word : words) {
					word = word.toUpperCase();
					map.put(word, 0);
				}
			}
			System.out.println(maps);
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		}
	}


