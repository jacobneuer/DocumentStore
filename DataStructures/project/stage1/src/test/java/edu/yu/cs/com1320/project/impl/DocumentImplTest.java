package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.stage1.impl.DocumentImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
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
        URI newUri = create("Meep");
        String hello = "Hello!";
        DocumentImpl document2 = new DocumentImpl(newUri, hello);
        assertNotEquals(document2, this.document);
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

}
