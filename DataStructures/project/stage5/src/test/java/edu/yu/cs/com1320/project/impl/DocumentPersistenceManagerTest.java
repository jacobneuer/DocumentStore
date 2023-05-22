package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.stage5.*;
import edu.yu.cs.com1320.project.stage5.impl.*;
import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/*
Example Tests:
assertEquals - assertEquals(4, calculator.multiply(2, 2),"optional failure message");
assertNotEquals - assertNotEquals(3, calculator.multiply(2, 2),"optional failure message");
assertTrue - assertTrue('a' < 'b', () → "optional failure message");
assertFalse - assertFalse('a' > 'b', () → "optional failure message");
assertNotNull - assertNotNull(yourObject, "optional failure message");
assertNull - assertNull(yourObject, "optional failure message");
assertThrows - assertThrows(IllegalArgumentException.class, () -> user.setAge("23"));
disable test - @Disabled/@Disabled("Why Disabled")
*/

public class DocumentPersistenceManagerTest {

    private DocumentPersistenceManager dpm;
    @BeforeEach
    public void setUp() {
        String directoryPath = "/Users/yaacovneuer/CompSci/disk/";
        File directory = new File(directoryPath);
        this.dpm = new DocumentPersistenceManager(directory);
    }

    public static URI create(String str) {
        try {
            return new URI(str);
        }
        catch (URISyntaxException x) {
            throw new IllegalArgumentException(x.getMessage(), x);
        }
    }

    @DisplayName("Test if File is Created in Proper Directory")
    @Test
    public void testOne() throws IOException {
        String docText = "This is a Document String Text";
        URI uri = create("DocumentURI");
        DocumentImpl doc = new DocumentImpl(uri, docText, null);
        this.dpm.serialize(uri, doc);
    }
    @DisplayName("Create a File Then Delete It")
    @Test
    public void testTwo() throws IOException {
        String docText = "This is a Document String Text";
        URI uri = create("DocumentURI");
        DocumentImpl doc = new DocumentImpl(uri, docText, null);
        this.dpm.serialize(uri, doc);
        this.dpm.delete(uri);
    }
    @DisplayName("Create a Text File Then Deserialize It")
    @Test
    public void testThree() throws IOException {
        String docText = "This is a Document String Text";
        URI uri = create("DocumentURI");
        DocumentImpl doc = new DocumentImpl(uri, docText, null);
        this.dpm.serialize(uri, doc);
        Document d = this.dpm.deserialize(uri);
        assertEquals(doc, d);
        assertEquals(doc.getKey(), d.getKey());
        assertEquals(doc.getDocumentTxt(), d.getDocumentTxt());
        assertEquals(doc.getWordMap(), d.getWordMap());
    }
    @DisplayName("Test if Byte File is Created in Proper Directory")
    @Test
    public void testFour() throws IOException {
        byte[] initialArray = { 0, 1, 2 };
        URI uri = create("BinaryURI");
        DocumentImpl doc = new DocumentImpl(uri, initialArray);
        this.dpm.serialize(uri, doc);
        this.dpm.delete(uri);
    }
    @DisplayName("Create a Byte File Then Delete It")
    @Test
    public void testFive() throws IOException {
        byte[] initialArray = { 0, 1, 2 };
        URI uri = create("BinaryURI");
        DocumentImpl doc = new DocumentImpl(uri, initialArray);
        this.dpm.serialize(uri, doc);
        this.dpm.delete(uri);
    }
    @DisplayName("Create a Byte File Then Deserialize It")
    @Test
    public void testSix() throws IOException {
        byte[] initialArray = {0, 1, 2};
        URI uri = create("BinaryURI");
        DocumentImpl doc = new DocumentImpl(uri, initialArray);
        this.dpm.serialize(uri, doc);
        Document d = this.dpm.deserialize(uri);
        assertEquals(doc, d);
        assertEquals(doc.getKey(), d.getKey());
        assert(Arrays.equals(doc.getDocumentBinaryData(), d.getDocumentBinaryData()));
    }
    @DisplayName("Store Json File With http://")
    @Test
    public void testSeven() throws IOException {
        String docText = "This is a Document String Text";
        URI uri = create("https://DocumentURI");
        DocumentImpl doc = new DocumentImpl(uri, docText, null);
        this.dpm.serialize(uri, doc);
    }
    @DisplayName("Create an http:// File Then Delete It")
    @Test
    public void testEight() throws IOException {
        String docText = "This is a Document String Text";
        URI uri = create("http://DocumentURI.com");
        DocumentImpl doc = new DocumentImpl(uri, docText, null);
        this.dpm.serialize(uri, doc);
        this.dpm.delete(uri);
    }
    @DisplayName("Create an http:// Text File Then Deserialize It")
    @Test
    public void testNine() throws IOException {
        String docText = "This is a Document String Text";
        URI uri = create("http://DocumentURI");
        DocumentImpl doc = new DocumentImpl(uri, docText, null);
        this.dpm.serialize(uri, doc);
        Document d = this.dpm.deserialize(uri);
        assertEquals(doc, d);
        assertEquals(doc.getKey(), d.getKey());
        assertEquals(doc.getDocumentTxt(), d.getDocumentTxt());
        assertEquals(doc.getWordMap(), d.getWordMap());
    }
    @DisplayName("Test if File will Create Folders to be Put in Proper Directory")
    @Test
    public void testTen() throws IOException {
        String docText = "This is a Document String Text";
        URI uri = create("http://www.yu.edu/documents/doc1");
        DocumentImpl doc = new DocumentImpl(uri, docText, null);
        this.dpm.serialize(uri, doc);
    }
    @DisplayName("Create an http:// File Then Delete It")
    @Test
    public void testEleven() throws IOException {
        String docText = "This is a Document String Text";
        URI uri = create("http://www.yu.edu/documents/doc1");
        DocumentImpl doc = new DocumentImpl(uri, docText, null);
        this.dpm.serialize(uri, doc);
        this.dpm.delete(uri);
    }
    @DisplayName("Create an http:// Text File Then Deserialize It")
    @Test
    public void testTwelve() throws IOException {
        String docText = "This is a Document String Text";
        URI uri = create("http://www.yu.edu/documents/doc1");
        DocumentImpl doc = new DocumentImpl(uri, docText, null);
        this.dpm.serialize(uri, doc);
        Document d = this.dpm.deserialize(uri);
        assertEquals(doc, d);
        assertEquals(doc.getKey(), d.getKey());
        assertEquals(doc.getDocumentTxt(), d.getDocumentTxt());
        assertEquals(doc.getWordMap(), d.getWordMap());
    }
    @DisplayName("Create multiple http:// Text Files Then Deserialize Them")
    @Test
    public void testThirteen() throws IOException {
        String docText = "This is a Document String Text";
        URI uri = create("http://www.yu.edu/documents/doc1");
        DocumentImpl doc = new DocumentImpl(uri, docText, null);
        this.dpm.serialize(uri, doc);
        String docText2 = "Professor Wymore is the Best Professor in the World";
        URI uri2 = create("http://www.yu.edu/Wymore/Is/The/Best/Professor/Ever/doc2");
        DocumentImpl doc2 = new DocumentImpl(uri2, docText2, null);
        this.dpm.serialize(uri2, doc2);
        Document d = this.dpm.deserialize(uri);
        Document d2 = this.dpm.deserialize(uri2);
        assertEquals(doc, d);
        assertEquals(doc.getKey(), d.getKey());
        assertEquals(doc.getDocumentTxt(), d.getDocumentTxt());
        assertEquals(doc.getWordMap(), d.getWordMap());
        assertEquals(doc2, d2);
        assertEquals(doc2.getKey(), d2.getKey());
        assertEquals(doc2.getDocumentTxt(), d2.getDocumentTxt());
        assertEquals(doc2.getWordMap(), d2.getWordMap());
    }
    @DisplayName("Create a mailto: Text File Then Deserialize It")
    @Test
    public void testFourteen() throws IOException {
        String docText = "This is a Document String Text";
        URI uri = create("mailto:java-net@www.example.com");
        DocumentImpl doc = new DocumentImpl(uri, docText, null);
        this.dpm.serialize(uri, doc);
        Document d = this.dpm.deserialize(uri);
        assertEquals(doc, d);
        assertEquals(doc.getKey(), d.getKey());
        assertEquals(doc.getDocumentTxt(), d.getDocumentTxt());
        assertEquals(doc.getWordMap(), d.getWordMap());
    }
    @DisplayName("Create files to be sent to disk from weird uris")
    @Test
    public void testFifteen() throws IOException {
        String docText = "This is a Document String Text";
        URI uri = create("mailto:java-net@www.example.com");
        DocumentImpl doc = new DocumentImpl(uri, docText, null);
        System.out.println("scheme: " + uri.getScheme());
        System.out.println("SSP: " + uri.getSchemeSpecificPart());
        System.out.println("authority: " + uri.getAuthority());
        System.out.println("user info: " + uri.getUserInfo());
        System.out.println("host: " + uri.getHost());
        System.out.println("port: " + uri.getPort());
        System.out.println("path: " + uri.getPath());
        System.out.println("query: " + uri.getQuery());
        System.out.println("fragment: " + uri.getFragment());
    }
}