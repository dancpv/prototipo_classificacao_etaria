package teste;

/* SO executar esta classe!! */
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.MasonTagTypes;
import net.htmlparser.jericho.MicrosoftConditionalCommentTagTypes;
import net.htmlparser.jericho.PHPTagTypes;
import net.htmlparser.jericho.Source;

public class ExtractText {
	private List<Element> linkElements;
	public static String url;
	public static String dir;
	private Map<String, Map<String, Integer>> maps;
	OpenRelatedFiles openRelatedFilesObject = new OpenRelatedFiles();
	VerifyExtractText verifyExtractTextObject = new VerifyExtractText();
	private static final int MIN_WORD_SIZE = 2;

	public ExtractText() {
	}

	public String getUrl() {
		return url;
	}

	public String getDir() {
		return dir;
	}

	public void execute() {
		try {
			maps = openRelatedFilesObject.getMaps();
			scanURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void scanURL() throws IOException, MalformedURLException {
		MicrosoftConditionalCommentTagTypes.register();
		PHPTagTypes.register();
		PHPTagTypes.PHP_SHORT.deregister();
		MasonTagTypes.register();
		List<String> links = scanMainURL(url);
		scanRelatedURL(links);
		System.out.println(links.size() + " related links!");
	}

	private List<String> scanMainURL(String sourceUrlString)
			throws MalformedURLException, IOException {
		List<String> links = new ArrayList<String>();
		Source source = new Source(new URL(sourceUrlString));
		source.fullSequentialParse();
		linkElements = source.getAllElements(HTMLElementName.A);

		for (Element linkElement : linkElements) {
			String href = linkElement.getAttributeValue("href");
			if (href == null)
				continue;

			if (href.startsWith("/"))
				href = sourceUrlString + href;
			links.add(href);
		}

		int countMainWords = countAndVerifyWords(source);
		System.out.println(countMainWords + " main");
		System.out.println(maps + " map main");
		calculatePercentage(maps, countMainWords);

		return links;
	}

	private int countAndVerifyWords(Source source) {
		int countWords = 0;
		String[] list = source.getRenderer().toString().toUpperCase()
				.split(" ");

		for (String word : list) {
			String cleanWord = cleanWord(word);
			if (!cleanWord.isEmpty() && cleanWord.length() > MIN_WORD_SIZE
					&& !isTag(cleanWord)) {
				countWords++;
				System.out.println(cleanWord);
				verifyExtractTextObject.verifyMaps(cleanWord, maps);
			}
		}
		return countWords;
	}

	private boolean isTag(String cleanWord) {
		return cleanWord.startsWith("<") || cleanWord.startsWith("HTTP");
	}

	private String cleanWord(String word) {
		String cleanWord = word.replaceAll("[^\\p{L}\\p{N}]", "");
		return cleanWord.trim();
	}

	private void scanRelatedURL(List<String> links) {
		if (links.isEmpty()) {
			System.out.println("Este link nao possui links relacionados!");
		} else {
			int countRelatedWords = 0;
			for (String link : links) {
				Source source = null;
				link = checkedLink(link);
				System.out.println(link + " LINK RELATED");
				try {
					source = new Source(new URL(link));
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (source != null)
					countRelatedWords += countAndVerifyWords(source);
			}
			System.out.println(countRelatedWords + " related");
			System.out.println(maps + " map related");
			calculatePercentage(maps, countRelatedWords);
		}
	}

	private String checkedLink(String link) {
		if (link.startsWith("www"))
			link = "http://" + link;
		return link;
	}

	public void calculatePercentage(Map<String, Map<String, Integer>> 
			maps, double counterWords) {
		for (Map<String, Integer> map : maps.values()) {
			Set<String> keys = map.keySet();
			for (String key : keys) {
				double value = map.get(key);
				double porcentage = ((value * 100.0) / counterWords);
				System.out.println("A palavra: " + key + 
						" foi encontrada " + 
						value + " vezes" +
						" e corresponde a "
						+ porcentage + "% de " 
						+ counterWords);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 2)
			System.out.println("Parametros faltando: url_scan");
		else {
			url = args[0];
			dir = args[1];
			ExtractText extractText = new ExtractText();
			
			long tempoInicial = System.currentTimeMillis();
			long memoriaInicial = java.lang.Runtime.getRuntime().freeMemory();
			System.out.println(memoriaInicial + " memoria livre");
			
			extractText.execute();
			
			long memoriaFinal = java.lang.Runtime.getRuntime().freeMemory();
			System.out.println(memoriaInicial - memoriaFinal + " bytes de memoria gastos!");
			long tempoFinal = System.currentTimeMillis();
			System.out.println(memoriaFinal + " memoria livre");
			long tempoTotal = tempoFinal - tempoInicial;
			System.out.println((tempoTotal/1000.0) + " segundos de execução!");
			System.out.println();
			

		}
	}
}
