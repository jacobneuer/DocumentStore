package edu.yu.cs.com1320.project.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

public class HashTableImplTest {
    HashTableImpl<Integer, String> hashTable;

    @BeforeEach
    public void setUp() {
        hashTable = new HashTableImpl<>();
    }
    @DisplayName("Put and Get")
    @Test
    public void testOne() {
        hashTable.put(0, "Entry 0");
        String meep = hashTable.get(0);
        assertEquals("Entry 0", meep);
    }

    @DisplayName("Replace Key")
    @Test
    public void testTwo() {
        hashTable.put(0, "Entry 0");
        hashTable.put(0, "Entry 2");
        hashTable.put(0, "Entry 8");
        hashTable.put(0, "Entry 10");
        hashTable.put(0, "Entry 77");
        String meep = hashTable.get(0);
        assertEquals("Entry 77", meep);
    }

    @DisplayName("Overload Array Index")
    @Test
    public void testThree() {
        hashTable.put(0, "Entry 0");
        hashTable.put(5, "Entry 2");
        String meep = hashTable.get(5);
        assertEquals("Entry 2", meep);
    }

    @DisplayName("Ask for Key that Doesn't Exist")
    @Test
    public void testFour() {
        hashTable.put(0, "Entry 0");
        String meep = hashTable.get(1);
        assertNull(meep);
    }

    @DisplayName("Delete a Value")
    @Test
    public void testFive() {
        hashTable.put(0, "Yeet");
        hashTable.put(3, "Meep");
        hashTable.put(5, "Hello");
        hashTable.put(0, null);
        String meep = hashTable.get(0);
        assertNull(meep);
    }

    @DisplayName("If Replacing Key then Return Old Value")
    @Test
    public void testSix() {
        hashTable.put(0, "Meep!");
        String meep = (hashTable.put(0, "Yeet"));
        assertEquals("Meep!", meep);
    }

    @DisplayName("Request a Null Key and Get Back Illegal Argument Exception")
    @Test
    public void testEight() {
        assertThrowsExactly(IllegalArgumentException.class, () -> hashTable.get(null));
    }

    @DisplayName("Testing ContainsKey Works Correctly")
    @Test
    public void testNine() {
        hashTable.put(0, "Yeet");
        hashTable.put(3, "Meep");
        hashTable.put(5, "Hello");
        hashTable.put(0, null);
        assertTrue(hashTable.containsKey(3));
    }

    @DisplayName("Request a Null Key from ContainsKey")
    @Test
    public void testTen() {
        assertThrowsExactly(NullPointerException.class, () -> hashTable.containsKey(null));
    }
}
