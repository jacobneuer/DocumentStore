package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.stage3.*;
import edu.yu.cs.com1320.project.stage3.impl.*;
import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
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

public class DocumentStoreImplTest {

    DocumentStoreImpl documentStore;
    @BeforeEach
    public void setUp() {
        documentStore = new DocumentStoreImpl();
    }

    public static URI create(String str) {
        try {
            return new URI(str);
        }
        catch (URISyntaxException x) {
            throw new IllegalArgumentException(x.getMessage(), x);
        }
    }

    @DisplayName("Create Text Doc, Put Text Doc in HashTable, and Get It Back")
    @Test
    public void testOne() throws IOException {
        String docText = "This is a Document String Text";
        InputStream targetStream = new ByteArrayInputStream(docText.getBytes());
        URI uri = create("DocumentURI");
        DocumentImpl doc = new DocumentImpl(uri, docText);
        documentStore.put(targetStream, uri, DocumentStore.DocumentFormat.TXT);
        Document d = documentStore.get(uri);
        assertEquals(d, doc);
    }

    @DisplayName("Create Binary Doc, Put Text Doc in HashTable, and Get It Back")
    @Test
    public void testTwo() throws IOException {
        byte[] initialArray = { 0, 1, 2 };
        InputStream targetStream = new ByteArrayInputStream(initialArray);
        URI uri = create("BinaryURI");
        DocumentImpl doc = new DocumentImpl(uri, initialArray);
        documentStore.put(targetStream, uri, DocumentStore.DocumentFormat.BINARY);
        Document d = documentStore.get(uri);
        assertEquals(d, doc);
    }

    @DisplayName("Delete a Text Document From HashTable")
    @Test
    public void testThree() throws IOException {
        String docText = "This is a Document String Text";
        InputStream targetStream = new ByteArrayInputStream(docText.getBytes());
        URI uri = create("DocumentURI");
        documentStore.put(targetStream, uri, DocumentStore.DocumentFormat.TXT);
        documentStore.put(null, uri, DocumentStore.DocumentFormat.TXT);
        Document d = documentStore.get(uri);
        assertNull(d);
    }

    @DisplayName("Delete a Binary Document From HashTable")
    @Test
    public void testFour() throws IOException {
        byte[] initialArray = { 0, 1, 2 };
        InputStream targetStream = new ByteArrayInputStream(initialArray);
        URI uri = create("BinaryURI");
        documentStore.put(targetStream, uri, DocumentStore.DocumentFormat.BINARY);
        documentStore.put(null, uri, DocumentStore.DocumentFormat.BINARY);
        Document d = documentStore.get(uri);
        assertNull(d);
    }

    @DisplayName("Put Null to HashTable for URI")
    @Test
    public void testFive() throws IOException {
        byte[] initialArray = { 0, 1, 2 };
        InputStream targetStream = new ByteArrayInputStream(initialArray);
        URI uri = null;
        assertThrowsExactly(IllegalArgumentException.class, () ->
                documentStore.put(targetStream, uri, DocumentStore.DocumentFormat.BINARY));
    }

    @DisplayName("Put Null to HashTable for Enum")
    @Test
    public void testSix() throws IOException {
        byte[] initialArray = { 0, 1, 2 };
        InputStream targetStream = new ByteArrayInputStream(initialArray);
        URI uri = create("BinaryURI");
        assertThrowsExactly(IllegalArgumentException.class, () ->
                documentStore.put(targetStream, uri, null));
    }

    @DisplayName("Deleting a Text Returns Old Document's HashCode")
    @Test
    public void testSeven() throws IOException {
        String docText = "This is a Document String Text";
        InputStream targetStream = new ByteArrayInputStream(docText.getBytes());
        URI uri = create("DocumentURI");
        DocumentImpl doc = new DocumentImpl(uri, docText);
        documentStore.put(targetStream, uri, DocumentStore.DocumentFormat.TXT);
        int oldHashcode = documentStore.put(null, uri, DocumentStore.DocumentFormat.TXT);
        assertEquals(oldHashcode, doc.hashCode());
    }

    @DisplayName("Deleting a Text With No Old Document Returns 0")
    @Test
    public void testSevenAndAHalf() throws IOException {
        String docText = "This is a Document String Text";
        InputStream targetStream = new ByteArrayInputStream(docText.getBytes());
        URI uri = create("DocumentURI");
        int oldHashcode = documentStore.put(null, uri, DocumentStore.DocumentFormat.TXT);
        assertEquals(0, oldHashcode);
    }

    @DisplayName("Deleting a Binary Returns Old Document's HashCode")
    @Test
    public void testEight() throws IOException {
        byte[] initialArray = { 0, 1, 2 };
        InputStream targetStream = new ByteArrayInputStream(initialArray);
        URI uri = create("BinaryURI");
        DocumentImpl doc = new DocumentImpl(uri, initialArray);
        documentStore.put(targetStream, uri, DocumentStore.DocumentFormat.BINARY);
        int oldHashcode = documentStore.put(null, uri, DocumentStore.DocumentFormat.BINARY);
        assertEquals(oldHashcode, doc.hashCode());
    }

    @DisplayName("Delete a Text Document From HashTable Using Delete Method")
    @Test
    public void testNine() throws IOException {
        String docText = "This is a Document String Text";
        InputStream targetStream = new ByteArrayInputStream(docText.getBytes());
        URI uri = create("DocumentURI");
        documentStore.put(targetStream, uri, DocumentStore.DocumentFormat.TXT);
        documentStore.delete(uri);
        Document d = documentStore.get(uri);
        assertNull(d);
    }

    @DisplayName("New Put in HashTable Will Return 0")
    @Test
    public void testTen() throws IOException {
        String docText = "This is a Document String Text";
        InputStream targetStream = new ByteArrayInputStream(docText.getBytes());
        URI uri = create("DocumentURI");
        int meep = documentStore.put(targetStream, uri, DocumentStore.DocumentFormat.TXT);
        assertEquals(0, meep);
    }

    @DisplayName("Put Many Entries then Ask for One Back")
    @Test
    public void testEleven() throws IOException {
        String docText = "This is a Document String Text";
        InputStream targetStream = new ByteArrayInputStream(docText.getBytes());
        URI uri = create("DocumentURI");
        documentStore.put(targetStream, uri, DocumentStore.DocumentFormat.TXT);
        String docText2 = "This is a Document String Text";
        InputStream targetStream2 = new ByteArrayInputStream(docText2.getBytes());
        URI uri2 = create("DocumentURI2");
        DocumentImpl doc = new DocumentImpl(uri2, docText2);
        documentStore.put(targetStream2, uri2, DocumentStore.DocumentFormat.TXT);
        String docText3 = "This is a Document String Text";
        InputStream targetStream3 = new ByteArrayInputStream(docText3.getBytes());
        URI uri3 = create("DocumentURI2");
        documentStore.put(targetStream3, uri3, DocumentStore.DocumentFormat.TXT);
        Document d = documentStore.get(uri2);
        assertEquals(d, doc);
    }

    @DisplayName("Overwrite a Put")
    @Test
    public void testTwelve() throws IOException {
        String docText = "This is a Document String Text";
        InputStream targetStream = new ByteArrayInputStream(docText.getBytes());
        URI uri = create("DocumentURI");
        DocumentImpl doc1 = new DocumentImpl(uri, docText);
        documentStore.put(targetStream, uri, DocumentStore.DocumentFormat.TXT);

        String docText2 = "This is a Different String Text";
        InputStream targetStream2 = new ByteArrayInputStream(docText2.getBytes());
        URI uri2 = create("DocumentURI");
        DocumentImpl doc2 = new DocumentImpl(uri2, docText2);
        documentStore.put(targetStream2, uri2, DocumentStore.DocumentFormat.TXT);

        DocumentImpl d = (DocumentImpl) documentStore.get(uri);
        String meep = d.getDocumentTxt();
        assertEquals(meep, docText2);
    }

    @DisplayName("Check DocumentImpl Equality")
    @Test
    public void testThirteen() throws IOException {
        String docText = "This is a Document String Text";
        InputStream targetStream = new ByteArrayInputStream(docText.getBytes());
        URI uri = create("DocumentURI");
        DocumentImpl doc1 = new DocumentImpl(uri, docText);

        String docText2 = "This is a Different Document String Text";
        InputStream targetStream2 = new ByteArrayInputStream(docText2.getBytes());
        URI uri2 = create("DocumentURI");
        DocumentImpl doc2 = new DocumentImpl(uri2, docText2);

        assertNotEquals(doc1, doc2);
    }

    @DisplayName("Undo a New Put")
    @Test
    public void testFourteen() throws IOException {
        String docText = "This is a Document String Text";
        InputStream targetStream = new ByteArrayInputStream(docText.getBytes());
        URI uri = create("DocumentURI");
        DocumentImpl doc = new DocumentImpl(uri, docText);
        documentStore.put(targetStream, uri, DocumentStore.DocumentFormat.TXT);
        documentStore.undo();
        Document d = documentStore.get(uri);
        assertNull(d);
    }

    @DisplayName("Undo a Put that Replaces a Previously Inserted URI")
    @Test
    public void testFifteen() throws IOException {
        String docText = "This is a Document String Text";
        InputStream targetStream = new ByteArrayInputStream(docText.getBytes());
        URI uri = create("DocumentURI");
        DocumentImpl doc = new DocumentImpl(uri, docText);
        documentStore.put(targetStream, uri, DocumentStore.DocumentFormat.TXT);
        String docText2 = "This is a Different Document String Text";
        InputStream targetStream2 = new ByteArrayInputStream(docText2.getBytes());
        URI uri2 = create("DocumentURI");
        DocumentImpl doc2 = new DocumentImpl(uri2, docText2);
        documentStore.put(targetStream2, uri2, DocumentStore.DocumentFormat.TXT);
        documentStore.undo();
        Document d = documentStore.get(uri);
        assertEquals(doc, d);
    }

    @DisplayName("Undo With Nothing to Undo")
    @Test
    public void testSixteen() throws IOException {
        String docText = "This is a Document String Text";
        InputStream targetStream = new ByteArrayInputStream(docText.getBytes());
        URI uri = create("DocumentURI");
        DocumentImpl doc = new DocumentImpl(uri, docText);
        documentStore.put(targetStream, uri, DocumentStore.DocumentFormat.TXT);
        String docText2 = "This is a Different Document String Text";
        InputStream targetStream2 = new ByteArrayInputStream(docText2.getBytes());
        URI uri2 = create("DocumentURI");
        DocumentImpl doc2 = new DocumentImpl(uri2, docText2);
        documentStore.put(targetStream2, uri2, DocumentStore.DocumentFormat.TXT);
        documentStore.undo();
        documentStore.undo();
        assertThrowsExactly(IllegalStateException.class, () ->
                documentStore.undo());
    }
    @DisplayName("Undo a Delete")
    @Test
    public void testSeventeen() throws IOException {
        String docText = "This is a Document String Text";
        InputStream targetStream = new ByteArrayInputStream(docText.getBytes());
        URI uri = create("DocumentURI");
        DocumentImpl doc = new DocumentImpl(uri, docText);
        documentStore.put(targetStream, uri, DocumentStore.DocumentFormat.TXT);
        documentStore.delete(uri);
        documentStore.undo();
        assertEquals(doc, documentStore.get(uri));
    }

    @DisplayName("Undo an Action With the Given URI")
    @Test
    public void testEighteen() throws IOException {
        String docText = "This is a Document String Text";
        InputStream targetStream = new ByteArrayInputStream(docText.getBytes());
        URI uri = create("DocumentURI");
        DocumentImpl doc = new DocumentImpl(uri, docText);
        documentStore.put(targetStream, uri, DocumentStore.DocumentFormat.TXT);
        String docText2 = "This is a Different Document String Text";
        InputStream targetStream2 = new ByteArrayInputStream(docText2.getBytes());
        URI uri2 = create("DocumentURI2");
        DocumentImpl doc2 = new DocumentImpl(uri2, docText2);
        documentStore.put(targetStream2, uri2, DocumentStore.DocumentFormat.TXT);
        documentStore.undo(uri);
        Document d = documentStore.get(uri);
        assertNull(d);
    }

    @DisplayName("Search to Undo a Given URI that Does Not Exist in the Store")
    @Test
    public void testNineteen() throws IOException {
        String docText = "This is a Document String Text";
        InputStream targetStream = new ByteArrayInputStream(docText.getBytes());
        URI uri = create("DocumentURI");
        DocumentImpl doc = new DocumentImpl(uri, docText);
        documentStore.put(targetStream, uri, DocumentStore.DocumentFormat.TXT);
        String docText2 = "This is a Different Document String Text";
        InputStream targetStream2 = new ByteArrayInputStream(docText2.getBytes());
        URI uri2 = create("DocumentURI2");
        DocumentImpl doc2 = new DocumentImpl(uri2, docText2);
        assertThrowsExactly(IllegalStateException.class, () ->
                documentStore.undo(uri2));
    }

    @DisplayName("Do a lot of Actions then Undo a Given URI's Action")
    @Test
    public void twenty() throws IOException {
        String docText = "This is a Document String Text";
        InputStream targetStream = new ByteArrayInputStream(docText.getBytes());
        URI uri = create("DocumentURI");
        DocumentImpl doc = new DocumentImpl(uri, docText);
        documentStore.put(targetStream, uri, DocumentStore.DocumentFormat.TXT);
        String docText2 = "This is a Different Document String Text";
        InputStream targetStream2 = new ByteArrayInputStream(docText2.getBytes());
        URI uri2 = create("DocumentURI2");
        DocumentImpl doc2 = new DocumentImpl(uri2, docText2);
        documentStore.put(targetStream2, uri2, DocumentStore.DocumentFormat.TXT);
        documentStore.put(null, uri, DocumentStore.DocumentFormat.TXT);
        documentStore.put(null, uri2, DocumentStore.DocumentFormat.TXT);
        InputStream targetStream3 = new ByteArrayInputStream(docText2.getBytes());
        documentStore.put(targetStream3, uri2, DocumentStore.DocumentFormat.TXT);
        documentStore.undo(uri2);
        Document d = documentStore.get(uri2);
        assertNull(d);
    }
    @DisplayName("Undo a New Put with Byte Array")
    @Test
    public void twentyOne() throws IOException {
        byte[] initialArray = { 0, 1, 2 };
        InputStream targetStream = new ByteArrayInputStream(initialArray);
        URI uri = create("BinaryURI");
        DocumentImpl doc = new DocumentImpl(uri, initialArray);
        documentStore.put(targetStream, uri, DocumentStore.DocumentFormat.BINARY);
        documentStore.undo();
        Document d = documentStore.get(uri);
        assertNull(d);
    }
    @DisplayName("Undo a New Put with Byte Array")
    @Test
    public void twentyTwo() throws IOException {
        String docText = "This is a Document String Text";
        InputStream targetStream = new ByteArrayInputStream(docText.getBytes());
        URI uri = create("DocumentURI");
        DocumentImpl doc = new DocumentImpl(uri, docText);
        documentStore.put(targetStream, uri, DocumentStore.DocumentFormat.TXT);
    }
    @DisplayName("Test Search Method")
    @Test
    public void twentyThree() throws IOException {
        String docText = "I love Torah and I love Mitzvot";
        InputStream targetStream = new ByteArrayInputStream(docText.getBytes());
        URI uri = create("DocumentURI");
        DocumentImpl doc = new DocumentImpl(uri, docText);
        documentStore.put(targetStream, uri, DocumentStore.DocumentFormat.TXT);
        String docText2 = "I also love cookies and cake";
        InputStream targetStream2 = new ByteArrayInputStream(docText2.getBytes());
        URI uri2 = create("DocumentURI2");
        DocumentImpl doc2 = new DocumentImpl(uri2, docText2);
        documentStore.put(targetStream2, uri2, DocumentStore.DocumentFormat.TXT);
        String docText3 = "love love love love";
        InputStream targetStream3 = new ByteArrayInputStream(docText3.getBytes());
        URI uri3 = create("DocumentURI3");
        DocumentImpl doc3 = new DocumentImpl(uri3, docText3);
        documentStore.put(targetStream3, uri3, DocumentStore.DocumentFormat.TXT);
        String docText4 = "no Love over here";
        InputStream targetStream4 = new ByteArrayInputStream(docText4.getBytes());
        URI uri4 = create("DocumentURI4");
        DocumentImpl doc4 = new DocumentImpl(uri4, docText4);
        documentStore.put(targetStream4, uri4, DocumentStore.DocumentFormat.TXT);
        List<Document> searchResults = documentStore.search("love");
        for(Document d: searchResults){
            System.out.println(d.getDocumentTxt());
        }
    }
    @DisplayName("Test SearchByPrefix Method")
    @Test
    public void twentyFour() throws IOException {
        String docText = "I love Torah and I love Mitzvot";
        InputStream targetStream = new ByteArrayInputStream(docText.getBytes());
        URI uri = create("DocumentURI");
        DocumentImpl doc = new DocumentImpl(uri, docText);
        documentStore.put(targetStream, uri, DocumentStore.DocumentFormat.TXT);
        String docText2 = "I also love cookies and cake";
        InputStream targetStream2 = new ByteArrayInputStream(docText2.getBytes());
        URI uri2 = create("DocumentURI2");
        DocumentImpl doc2 = new DocumentImpl(uri2, docText2);
        documentStore.put(targetStream2, uri2, DocumentStore.DocumentFormat.TXT);
        String docText3 = "love love love love";
        InputStream targetStream3 = new ByteArrayInputStream(docText3.getBytes());
        URI uri3 = create("DocumentURI3");
        DocumentImpl doc3 = new DocumentImpl(uri3, docText3);
        documentStore.put(targetStream3, uri3, DocumentStore.DocumentFormat.TXT);
        String docText4 = "no Love over here";
        InputStream targetStream4 = new ByteArrayInputStream(docText4.getBytes());
        URI uri4 = create("DocumentURI4");
        DocumentImpl doc4 = new DocumentImpl(uri4, docText4);
        documentStore.put(targetStream4, uri4, DocumentStore.DocumentFormat.TXT);
        List<Document> searchResults = documentStore.searchByPrefix("l");
        for(Document d: searchResults){
            System.out.println(d.getDocumentTxt());
        }
    }
    @DisplayName("Test DeleteAll Method")
    @Test
    public void twentyFive() throws IOException {
        String docText = "I love Torah and I love Mitzvot";
        InputStream targetStream = new ByteArrayInputStream(docText.getBytes());
        URI uri = create("DocumentURI");
        DocumentImpl doc = new DocumentImpl(uri, docText);
        documentStore.put(targetStream, uri, DocumentStore.DocumentFormat.TXT);
        String docText2 = "I also love cookies and cake";
        InputStream targetStream2 = new ByteArrayInputStream(docText2.getBytes());
        URI uri2 = create("DocumentURI2");
        DocumentImpl doc2 = new DocumentImpl(uri2, docText2);
        documentStore.put(targetStream2, uri2, DocumentStore.DocumentFormat.TXT);
        String docText3 = "love love love love";
        InputStream targetStream3 = new ByteArrayInputStream(docText3.getBytes());
        URI uri3 = create("DocumentURI3");
        DocumentImpl doc3 = new DocumentImpl(uri3, docText3);
        documentStore.put(targetStream3, uri3, DocumentStore.DocumentFormat.TXT);
        String docText4 = "no Love over here";
        InputStream targetStream4 = new ByteArrayInputStream(docText4.getBytes());
        URI uri4 = create("DocumentURI4");
        DocumentImpl doc4 = new DocumentImpl(uri4, docText4);
        documentStore.put(targetStream4, uri4, DocumentStore.DocumentFormat.TXT);
        Set<URI> deletedURIs = documentStore.deleteAll("love");
        List<Document> searchResults = documentStore.search("love");
        assert(searchResults.isEmpty());
    }
    @DisplayName("Test DeleteAllWithPrefix Method")
    @Test
    public void twentySix() throws IOException {
        String docText = "I love Torah and I love Mitzvot";
        InputStream targetStream = new ByteArrayInputStream(docText.getBytes());
        URI uri = create("DocumentURI");
        DocumentImpl doc = new DocumentImpl(uri, docText);
        documentStore.put(targetStream, uri, DocumentStore.DocumentFormat.TXT);
        String docText2 = "I also love cookies and cake";
        InputStream targetStream2 = new ByteArrayInputStream(docText2.getBytes());
        URI uri2 = create("DocumentURI2");
        DocumentImpl doc2 = new DocumentImpl(uri2, docText2);
        documentStore.put(targetStream2, uri2, DocumentStore.DocumentFormat.TXT);
        String docText3 = "love love love love";
        InputStream targetStream3 = new ByteArrayInputStream(docText3.getBytes());
        URI uri3 = create("DocumentURI3");
        DocumentImpl doc3 = new DocumentImpl(uri3, docText3);
        documentStore.put(targetStream3, uri3, DocumentStore.DocumentFormat.TXT);
        String docText4 = "no Love over here";
        InputStream targetStream4 = new ByteArrayInputStream(docText4.getBytes());
        URI uri4 = create("DocumentURI4");
        DocumentImpl doc4 = new DocumentImpl(uri4, docText4);
        documentStore.put(targetStream4, uri4, DocumentStore.DocumentFormat.TXT);
        Set<URI> deletedURIs = documentStore.deleteAllWithPrefix("c");
        for(URI u: deletedURIs){
            System.out.println(u);
        }
        List<Document> searchResults = documentStore.searchByPrefix("c");
        assert(searchResults.isEmpty());
    }
    @DisplayName("Test Undo Method")
    @Test
    public void twentySeven() throws IOException {
        String docText = "I love Torah and I love Mitzvot";
        InputStream targetStream = new ByteArrayInputStream(docText.getBytes());
        URI uri = create("DocumentURI");
        DocumentImpl doc = new DocumentImpl(uri, docText);
        documentStore.put(targetStream, uri, DocumentStore.DocumentFormat.TXT);
        String docText2 = "I also love cookies and cake";
        InputStream targetStream2 = new ByteArrayInputStream(docText2.getBytes());
        URI uri2 = create("DocumentURI2");
        DocumentImpl doc2 = new DocumentImpl(uri2, docText2);
        documentStore.put(targetStream2, uri2, DocumentStore.DocumentFormat.TXT);
        Set<URI> deletedURIs = documentStore.deleteAllWithPrefix("c");
        documentStore.undo();
        List<Document> searchResults = documentStore.searchByPrefix("c");
        for(Document d: searchResults){
            System.out.println(d.getDocumentTxt());
        }
    }
    @DisplayName("Test Undo(uri) Method")
    @Test
    public void twentyEight() throws IOException {
        String docText = "I love Torah and I love Mitzvot";
        InputStream targetStream = new ByteArrayInputStream(docText.getBytes());
        URI uri = create("DocumentURI");
        DocumentImpl doc = new DocumentImpl(uri, docText);
        documentStore.put(targetStream, uri, DocumentStore.DocumentFormat.TXT);
        String docText2 = "I also love cookies and cake";
        InputStream targetStream2 = new ByteArrayInputStream(docText2.getBytes());
        URI uri2 = create("DocumentURI2");
        DocumentImpl doc2 = new DocumentImpl(uri2, docText2);
        documentStore.put(targetStream2, uri2, DocumentStore.DocumentFormat.TXT);
        String docText3 = "love love love love";
        InputStream targetStream3 = new ByteArrayInputStream(docText3.getBytes());
        URI uri3 = create("DocumentURI3");
        DocumentImpl doc3 = new DocumentImpl(uri3, docText3);
        documentStore.put(targetStream3, uri3, DocumentStore.DocumentFormat.TXT);
        String docText4 = "no Love over here";
        InputStream targetStream4 = new ByteArrayInputStream(docText4.getBytes());
        URI uri4 = create("DocumentURI4");
        DocumentImpl doc4 = new DocumentImpl(uri4, docText4);
        documentStore.put(targetStream4, uri4, DocumentStore.DocumentFormat.TXT);
        Set<URI> deletedURIs = documentStore.deleteAllWithPrefix("l");
        for(URI u: deletedURIs){
            System.out.println(u);
        }
        documentStore.undo(uri2);
        List<Document> searchResults = documentStore.searchByPrefix("l");
        for(Document d: searchResults){
            System.out.println(d.getDocumentTxt());
        }
    }
}
