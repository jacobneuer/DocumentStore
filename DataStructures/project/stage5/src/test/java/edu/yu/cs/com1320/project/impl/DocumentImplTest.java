package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/*
Example Tests:
assertEquals - assertEquals(4, calculator.multiply(2, 2),"optional failure message");
assertTrue - assertTrue('a' < 'b', () → "optional failure message");
assertFalse - assertFalse('a' > 'b', () → "optional failure message");
assertNotNull - assertNotNull(yourObject, "optional failure message");
assertNull - assertNull(yourObject, "optional failure message");
assertThrows - assertThrows(IllegalArgumentException.class, () -> user.setAge("23"));
disable test - @Disabled/@Disabled("Why Disabled")
*/

public class DocumentImplTest {
    URI uriString;
    String text = "Hello!";
    DocumentImpl document;

    public static URI create(String str) {
        try {
            return new URI(str);
        }
        catch (URISyntaxException x) {
            throw new IllegalArgumentException(x.getMessage(), x);
        }
    }

    @BeforeEach
    public void setUp() throws URISyntaxException {
        this.uriString = create(text);
        this.document = new DocumentImpl(this.uriString, text);
    }

    @DisplayName("Get Document Text")
    @Test
    public void testOne() {
        assertEquals("Hello!", this.document.getDocumentTxt());
    }

    @DisplayName("Check Equality of Documents")
    @Test
    public void testTwo() {
        URI newUri = create("Hello!");
        String hello = "Hello!";
        Document document2 = new DocumentImpl(newUri, hello);
        assertEquals(document2, this.document);
    }

    @DisplayName("Check That Null String Returns An Exception")
    @Test
    public void testThree() {
        URI newUri = create("Meep");
        String hello = null;
        AtomicReference<DocumentImpl> document2 = null;
        assertThrowsExactly(IllegalArgumentException.class, () ->
                document2.set(new DocumentImpl(newUri, hello)));
    }

    @DisplayName("Check That Null Byte Array Returns An Exception")
    @Test
    public void testFour() {
        URI newUri = create("Meep");
        byte[] hello = null;
        AtomicReference<DocumentImpl> document2 = null;
        assertThrowsExactly(IllegalArgumentException.class, () ->
                document2.set(new DocumentImpl(newUri, hello)));
    }

    @DisplayName("Check That Empty Byte Array Returns An Exception")
    @Test
    public void testFive() {
        URI newUri = create("Meep");
        byte[] hello = new byte[0];
        AtomicReference<DocumentImpl> document2 = null;
        assertThrowsExactly(IllegalArgumentException.class, () ->
                document2.set(new DocumentImpl(newUri, hello)));
    }

    @DisplayName("Check That Empty String Returns An Exception")
    @Test
    public void testSix() {
        URI newUri = create("Meep");
        String hello = "";
        AtomicReference<DocumentImpl> document2 = null;
        assertThrowsExactly(IllegalArgumentException.class, () ->
                document2.set(new DocumentImpl(newUri, hello)));
    }

    @DisplayName("Check That Empty URI Returns An Exception")
    @Test
    public void testSeven() {
        URI newUri = null;
        String hello = "meep";
        AtomicReference<DocumentImpl> document2 = null;
        assertThrowsExactly(IllegalArgumentException.class, () ->
                document2.set(new DocumentImpl(newUri, hello)));
    }

    @DisplayName("Get all the Words in a String Document")
    @Test
    public void testEight() {
        URI newUri = create("Hello!");
        String hello = "Hello there. My name is Yaacov.";
        Document document2 = new DocumentImpl(newUri, hello);
        Set<String> words = new HashSet<>();
        words.add("Hello");
        words.add("there");
        words.add("My");
        words.add("name");
        words.add("is");
        words.add("Yaacov");
        assertEquals(words, document2.getWords());
    }

    @DisplayName("Find Word Count For Specific Word in String Document")
    @Test
    public void testNine() {
        URI newUri = create("Hello!");
        String hello = "How are you! doing today? I am doing quite well, thank you for asking. you you";
        Document document2 = new DocumentImpl(newUri, hello);
        int count = document2.wordCount("you");
        assertEquals(4, count);
    }

    @DisplayName("Word Count Returns 0 for Binary Document")
    @Test
    public void testTen() {
        byte[] initialArray = { 0, 1, 2 };
        URI uri = create("BinaryURI");
        DocumentImpl binaryDoc = new DocumentImpl(uri, initialArray);
        int count = binaryDoc.wordCount("1");
        assertEquals(0, count);
    }

    @DisplayName("Testing Case Sensitivity")
    @Test
    public void testEleven() {
        URI newUri = create("Hello!");
        String hello = "How are You! doing today? I am doing quite well, thank you for asking.";
        Document document2 = new DocumentImpl(newUri, hello);
        int count = document2.wordCount("You");
        assertEquals(1, count);
    }

    @DisplayName("Make sure all Punctuation was Removed")
    @Test
    public void testTwelve() {
        URI newUri = create("Hello!");
        String hello = "How are you! doing today? I'm doing quite well, thank you for asking. you you";
        Document document2 = new DocumentImpl(newUri, hello);
        int count = document2.wordCount("Im");
        assertEquals(1, count);
    }
    @DisplayName("Playing Around With NanoSeconds")
    @Test
    public void testThirteen() {
        URI newUri = create("Hello!");
        String hello = "How are you! doing today? I'm doing quite well, thank you for asking. you you";
        Document document2 = new DocumentImpl(newUri, hello);
        System.out.println(System.nanoTime());
        System.out.println(System.nanoTime());
    }
}
