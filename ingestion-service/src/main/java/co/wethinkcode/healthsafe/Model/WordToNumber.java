package co.wethinkcode.healthsafe.Model;

import java.util.HashMap;
import java.util.Map;

public class WordToNumber {

    private static final Map<String, Integer> numbers = new HashMap<>();

    static {
        numbers.put("zero", 0);
        numbers.put("one", 1);
        numbers.put("two", 2);
        numbers.put("three", 3);
        numbers.put("four", 4);
        numbers.put("five", 5);
        numbers.put("six", 6);
        numbers.put("seven", 7);
        numbers.put("eight", 8);
        numbers.put("nine", 9);
        numbers.put("ten", 10);
        numbers.put("eleven", 11);
        numbers.put("twelve", 12);
        numbers.put("thirteen", 13);
        numbers.put("fourteen", 14);
        numbers.put("fifteen", 15);
        numbers.put("sixteen", 16);
        numbers.put("seventeen", 17);
        numbers.put("eighteen", 18);
        numbers.put("nineteen", 19);

        numbers.put("twenty", 20);
        numbers.put("thirty", 30);
        numbers.put("forty", 40);
        numbers.put("fifty", 50);
        numbers.put("sixty", 60);
        numbers.put("seventy", 70);
        numbers.put("eighty", 80);
        numbers.put("ninety", 90);
    }

    public static int wordsToNumber(String text) {

        

        String[] words = text.toLowerCase().split("\\s+");

        int result = 0;
        int current = 0;

        for (String word : words) {
            switch (word) {
                case "hundred":
                    current *= 100;
                    break;

                case "thousand":
                    result += current * 1000;
                    current = 0;
                    break;

                case "and":
                    break;

                default:
                    Integer value = numbers.get(word);
                    if (value == null) {
                        throw new IllegalArgumentException("Unknown word: " + word);
                    }
                    current += value;
            }
        }

        return result + current;
    }
}
