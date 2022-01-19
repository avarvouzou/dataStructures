package gr.hua.datastructures2;

import java.util.Scanner;
import java.util.StringTokenizer;
import gr.hua.datastructures2.Dictionary.Entry;

public class App {

    public static void main(String[] args ) {
        OpenMatrixHashTable<String,Integer> dict = new OpenMatrixHashTable<>();
		
		try (Scanner scanner = new Scanner(System.in)) {
			while(scanner.hasNext()) { 
				String line = scanner.nextLine();
				StringTokenizer st = new StringTokenizer(line);
				while(st.hasMoreTokens()) { 
					String word  = st.nextToken();
					Integer curFreq = dict.get(word);
					if (curFreq == null) { 
						curFreq = 1;
					} else { 
						curFreq++;
					}
					dict.put(word, curFreq);
				}
			}
		}
		
		for(Entry<String, Integer> e: dict) { 
			System.out.println("Word " + e.getKey() + " appeared " + e.getValue() + " times");
		}
    }

}
