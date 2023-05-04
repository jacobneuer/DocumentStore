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
        DocumentImpl doc = new DocumentImpl(uri, docText);
        this.dpm.serialize(uri, doc);
    }
    @DisplayName("Create a File The Delete It")
    @Test
    public void testTwo() throws IOException {
        String docText = "This is a Document String Text";
        URI uri = create("DocumentURI");
        DocumentImpl doc = new DocumentImpl(uri, docText);
        this.dpm.serialize(uri, doc);
        this.dpm.delete(uri);
    }
    @DisplayName("Create a File The Delete It")
    @Test
    public void testThree() throws IOException {
        String docText = "This is a Document String Text";
        URI uri = create("DocumentURI");
        DocumentImpl doc = new DocumentImpl(uri, docText);
        this.dpm.serialize(uri, doc);
        Document d = this.dpm.deserialize(uri);
        assertEquals(doc, d);
        assertEquals(doc.getKey(), d.getKey());
        assertEquals(doc.getDocumentTxt(), d.getDocumentTxt());
        assertEquals(doc.getWordMap(), d.getWordMap());
    }
}