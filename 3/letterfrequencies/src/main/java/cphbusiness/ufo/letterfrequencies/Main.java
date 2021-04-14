package cphbusiness.ufo.letterfrequencies;

import java.io.*;
import java.text.NumberFormat;
import java.util.*;

import static java.util.stream.Collectors.toMap;

/**
 * Frequency analysis Inspired by
 * https://en.wikipedia.org/wiki/Frequency_analysis
 *
 * @author kasper
 */
public class Main {

    public static void main(String[] args) throws IOException {

        Reader reader = null;
        Map<Integer, Long> freq = null;
        int n = 10, excel = 10;
        int count = 5;
        System.out.println("Running experiment " + n*count + " times");
        double st = 0.0, sst = 0.0;

        for (int a = 0; a < excel; a++)
        {
            for (int j = 0; j < n; j++)
            {
                // Kør eksperimentet {count} gange.
                Timer t = new Timer();
                for (int i = 0; i < count; i++)
                {
                    freq = doWork01("FoundationSeries.txt");
                }
                double time = t.check() * 1e9 / count;
                st += time; // akk. system time.
                sst += time * time; // akk. system time ^ 2
            }
            // udregn gns (mean) og standard afvigelsen i ms.
            double mean = st / n, sdev = Math.sqrt((sst - mean * mean * n) / (n - 1));
            //System.out.printf("%6.1f ms +/- %6.3f %n", mean/1_000_000, sdev/1_000_000);
            //System.out.printf(Locale.US ,"%6.1f %6.3f %n", mean / 1_000_000, sdev / 1_000_000);
            System.out.printf("%6.1f %6.3f %n", mean / 1_000_000, sdev / 1_000_000);
            st = sst = 0.0;

        }
        print_tally(freq);
    }

    private static HashMap<Integer, Long> doWork01(String file) throws IOException
    {
        Reader reader = loadFile(file);
        HashMap<Integer, Long> freq = new HashMap<>();
        tallyChars(reader, freq);
        reader.close();
        return freq;
    }
    private static Reader loadFile(String fileName)
    {
        InputStream is = Main.class.getClassLoader().getResourceAsStream(fileName);
        Reader reader = new InputStreamReader(is);
        return reader;

    }

    private static void tallyChars(Reader reader, Map<Integer, Long> freq) throws IOException {
        int b;
        while ((b = reader.read()) != -1) {
            try {
                freq.put(b, freq.get(b) + 1);
            } catch (NullPointerException np) {
                freq.put(b, 1L);
            };
        }
    }

    private static void print_tally(Map<Integer, Long> freq) {
        int dist = 'a' - 'A';
        Map<Character, Long> upperAndlower = new LinkedHashMap();
        for (Character c = 'A'; c <= 'Z'; c++) {
            upperAndlower.put(c, freq.getOrDefault(c, 0L) + freq.getOrDefault(c + dist, 0L));
        }
        Map<Character, Long> sorted = upperAndlower
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));
        for (Character c : sorted.keySet()) {
            System.out.println("" + c + ": " + sorted.get(c));;
        }
    }
}
