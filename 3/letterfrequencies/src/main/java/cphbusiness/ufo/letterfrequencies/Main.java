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
                    freq = doWork03("FoundationSeries.txt");
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


    /**
     * Loads file with a FileReader.
     * Uses the original method of reading 1 byte at a time and increments indices in a hashmap to count occurences.
     * @param file
     * @return
     * @throws IOException
     */
    private static HashMap<Integer, Long> doWork01(String file) throws IOException
    {
        Reader reader = loadFile01(file);
        HashMap<Integer, Long> freq = new HashMap<>();
        tallyChars01(reader, freq);
        reader.close();
        return freq;
    }

    /**
     * Loads file with a FileReader.
     * Reads the file in chunks of 256 bytes, traverses the byte arrays and increments indices in a hashmap to count occurences.
     * @param file
     * @return
     * @throws IOException
     */
    private static HashMap<Integer, Long> doWork02(String file) throws IOException
    {
        Reader reader = loadFile01(file);
        HashMap<Integer, Long> freq = new HashMap<>();
        tallyChars02(reader, freq);
        reader.close();
        return freq;
    }

    /**
     * Loads file with a BufferedInputStream.
     * Uses the original method of reading 1 byte at a time and increments indices in a hashmap to count occurences.
     * @param file
     * @return
     * @throws IOException
     */
    private static HashMap<Integer, Long> doWork03(String file) throws IOException
    {
        BufferedInputStream stream = loadFile02(file);
        HashMap<Integer, Long> freq = new HashMap<>();
        tallyChars03(stream, freq);
        stream.close();
        return freq;
    }

    /**
     * Reads the file with {fileName}.
     * @param fileName
     * @return
     * @throws FileNotFoundException
     */
    private static Reader loadFile01(String fileName) throws FileNotFoundException
    {
        Reader reader;
        try
        {
            // file is in resources when running from within IDE.
            reader = new FileReader("src/main/resources/" + fileName);
        }
        catch (Exception e)
        {
            // file is in root when running from CMD.
            reader = new FileReader(fileName);
        }
        return reader;
    }

    /**
     * Reads the file with {fileName}.
     * @param fileName
     * @return
     * @throws FileNotFoundException
     */
    private static BufferedInputStream loadFile02(String fileName) throws FileNotFoundException
    {
        FileInputStream fis;
        try
        {
             fis = new FileInputStream(fileName);
        }
        catch(Exception e)
        {
            fis = new FileInputStream("src/main/resources/" + fileName);
        }
        return new BufferedInputStream(fis);
    }

    /**
     * Reads bytes from the file one at a time and increments occurences in a hashmap.
     * @param reader
     * @param freq
     * @throws IOException
     */
    private static void tallyChars01(Reader reader, Map<Integer, Long> freq) throws IOException {
        int b;
        while ((b = reader.read()) != -1) {
            try {
                freq.put(b, freq.get(b) + 1);
            } catch (NullPointerException np) {
                freq.put(b, 1L);
            };
        }
    }

    /**
     * Reads bytes from the file in chunks of 256 bytes and increments occurences in a hashmap.
     * @param reader
     * @param freq
     * @throws IOException
     */
    private static void tallyChars02(Reader reader, Map<Integer, Long> freq) throws IOException {
        char[] chars = new char[256];
        while((reader.read(chars)) != -1){
            for(char b : chars)
                try {
                    freq.put((int)b, freq.get((int)b) + 1);
                } catch (NullPointerException np) {
                    freq.put((int)b, 1L);
            };
        }

    }

    /**
     * Reads bytes from the file one at a time and increments occurences in a hashmap.
     * @param stream
     * @param freq
     * @throws IOException
     */
    private static void tallyChars03(BufferedInputStream stream, Map<Integer, Long> freq) throws IOException {
        int b;
        while ((b = stream.read()) != -1) {
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
