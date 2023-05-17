package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.BTree;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage5.impl.DocumentPersistenceManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

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
public class BTreeImplTest {
    BTreeImpl<Integer, String> bTree;

    @BeforeEach
    public void setUp() {
        bTree = new BTreeImpl<>();
    }
    public static URI create(String str) {
        try {
            return new URI(str);
        }
        catch (URISyntaxException x) {
            throw new IllegalArgumentException(x.getMessage(), x);
        }
    }
    @DisplayName("Put and Get")
    @Test
    public void testOne() {
        bTree.put(0, "Entry 0");
        String meep = bTree.get(0);
        assertEquals("Entry 0", meep);
    }
    @DisplayName("Replace Key")
    @Test
    public void testTwo() {
        bTree.put(0, "Entry 0");
        bTree.put(0, "Entry 2");
        bTree.put(0, "Entry 8");
        bTree.put(0, "Entry 10");
        bTree.put(0, "Entry 77");
        String meep = bTree.get(0);
        assertEquals("Entry 77", meep);
    }

    @DisplayName("Overload Array Index")
    @Test
    public void testThree() {
        bTree.put(0, "Entry 0");
        bTree.put(5, "Entry 2");
        String meep = bTree.get(5);
        assertEquals("Entry 2", meep);
    }

    @DisplayName("Ask for Key that Doesn't Exist")
    @Test
    public void testFour() {
        bTree.put(0, "Entry 0");
        String meep = bTree.get(1);
        assertNull(meep);
    }

    @DisplayName("Delete a Value")
    @Test
    public void testFive() {
        bTree.put(0, "Yeet");
        bTree.put(3, "Meep");
        bTree.put(5, "Hello");
        bTree.put(0, null);
        String meep = bTree.get(0);
        assertNull(meep);
    }

    @DisplayName("If Replacing Key then Return Old Value")
    @Test
    public void testSix() {
        bTree.put(0, "Meep!");
        String meep = (bTree.put(0, "Yeet"));
        assertEquals("Meep!", meep);
    }

    @DisplayName("Request a Null Key and Get Back Null Value")
    @Test
    public void testSeven() {
        assertNull (bTree.get(null));
    }

    @DisplayName("Test Doubling Array")
    @Test
    public void testEight() {
        bTree.put(0, "A");
        bTree.put(1, "B");
        bTree.put(2, "C");
        bTree.put(3, "D");
        bTree.put(4, "A");
        bTree.put(5, "B");
        bTree.put(6, "C");
        bTree.put(7, "D");
        bTree.put(8, "A");
        bTree.put(9, "B");
        bTree.put(10, "C");
        bTree.put(11, "D");
        bTree.put(12, "A");
        bTree.put(13, "B");
        bTree.put(14, "C");
        bTree.put(15, "D");
        bTree.put(16, "A");
        bTree.put(17, "B");
        bTree.put(18, "C");
        bTree.put(19, "D");
        bTree.put(20, "A");
        String answer = bTree.get(8);
        assertEquals("A", answer);
    }
    @DisplayName("Testing Move to Disk Method")
    @Test
    public void testNine() throws Exception {
        String docText = "I love Torah and I love Mitzvot";
        URI uri = create("http://www.yu.edu/Wymore/Is/The/Best/Professor/Ever/Document1");
        DocumentImpl doc = new DocumentImpl(uri, docText);
        BTreeImpl<URI, Document> documentBTree = new BTreeImpl<>();
        documentBTree.put(uri, doc);
        DocumentPersistenceManager dpm = new DocumentPersistenceManager(null);
        documentBTree.setPersistenceManager(dpm);
        documentBTree.moveToDisk(uri);
    }
}
