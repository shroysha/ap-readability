package dev.shroysha.ap.readability.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class ReadabilityDocument {

    private final int numOfSentences;
    private final ArrayList<String> allowed = new ArrayList<>();
    private int numOfSyls;
    private String type;
    private int numOfWords;

    public ReadabilityDocument(String document) {
        type = "text";
        numOfSyls = 0;
        numOfWords = 0;
        numOfSentences = countSentences(document);
        //System.err.println(allowed);
    }

    public ReadabilityDocument(URL url) throws IOException {
        this(getURLContent(url));
        type = "URL";
    }

    public ReadabilityDocument(File file) throws FileNotFoundException {
        this(getFileContent(file));
        type = "file";
    }

    private static String getFileContent(File file) throws FileNotFoundException {
        StringBuilder content = new StringBuilder();

        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            content.append(scanner.nextLine());
        }

        return content.toString();
    }

    private static String getURLContent(URL url) throws IOException {
        StringBuilder content = new StringBuilder();
        Scanner scanner = new Scanner(url.openStream());

        while (scanner.hasNextLine()) {
            content.append(scanner.nextLine());
        }

        // Remove body
        String bodyStart = "body";
        String bodyStop = "</body>";
        //Finds "body"
        int beginning = content.indexOf(bodyStart);
        final char closeTag = '>';
        // If it has a body
        if (beginning == -1) {
            //Then finds the next close tag

            for (int i = beginning; true; i++) {
                char charAt = content.charAt(i);
                if (charAt == closeTag) {
                    break;
                } else {
                    beginning++;
                }
            }

            int end = content.indexOf(bodyStop);

            content = new StringBuilder(content.substring(beginning, end));
        }
        String body = content.toString();

        // Renove HTML tags
        StringBuilder formatted = new StringBuilder();
        boolean append = true;
        final char openTag = '<';
        for (int i = 0; i < body.length(); i++) {
            if (body.charAt(i) == openTag) {
                append = false;
            }
            if (append) {
                formatted.append(body.charAt(i));
            }
            if (body.charAt(i) == closeTag) {
                append = true;
            }
        }

        //System.err.println(formatted);

        return formatted.toString();
    }

    public static void main(String[] args) {
        /*
         * Sentence = "Holy crap I'm really cool. You should see how cool I am: really cool. Are."
         * Words = 15
         * Sentences = 4
         * Syllables = 7+7+3+1=18
         */
        //Document document = new Document("Holy crap I'm reaily cool. You should see how cool I am: really cool. Are.");

        /*
         * Sentence = 1
         * Words = 13
         * Syllables = 25
         *
         */
        ReadabilityDocument document = new ReadabilityDocument("The Australian platypus is seemingly a hybrid of a mammal and reptilian creature.");
        System.out.println("Readability: " + document.getReadability());

        /*try {
            document = new Document(new File("src/readiblity/test.txt"));
            document.getReadability();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Document.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            //URL url = (new File("src/readiblity/test.txt").toURI().toURL());
            URL url = new URL("http://www.yoyoguy.com/info/ball/index2.html");
            document = new Document(url);
            document.getReadability();
        } catch (MalformedURLException ex) {
            Logger.getLogger(Document.class.getName()).log(Level.SEVERE, null, ex);
        } catch(IOException ex){
            Logger.getLogger(Document.class.getName()).log(Level.SEVERE, null, ex);
        }*/

    }

    public int getNumberOfSyllables() {
        return numOfSyls;
    }

    public int getNumberOfWords() {
        return numOfWords;
    }

    public int getNumberOfSentences() {
        return numOfSentences;
    }

    public String getType() {
        return type;
    }

    public double getReadability() {
        return 206.835
                - 84.6 * (getNumberOfSyllables() * 1.0 / getNumberOfWords())
                - 1.015 * (getNumberOfWords() * 1.0 / getNumberOfSentences());
    }

    private int countSentences(String document) {
        final String punctuation = ".!?:;";
        StringTokenizer sentenceTokenizer = new StringTokenizer(document, punctuation);
        int numOfSent = sentenceTokenizer.countTokens();

        while (sentenceTokenizer.hasMoreTokens()) {
            numOfWords += countWords(sentenceTokenizer.nextToken());
        }

        return numOfSent;
    }

    private int countWords(String sentence) {
        StringTokenizer wordTokenizer = new StringTokenizer(sentence, " ");
        int numWords = wordTokenizer.countTokens();
        while (wordTokenizer.hasMoreTokens()) {

            String wordText = wordTokenizer.nextToken().trim();
            ReadabilityWord word = new ReadabilityWord(wordText);

            if (word.isWord()) {
                numOfSyls += word.getNumberOfSyllables();
                allowed.add(wordText);
            } else {
                numWords--;
            }
        }

        return numWords;
    }
}
