package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.stage5.*;
import edu.yu.cs.com1320.project.stage5.impl.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

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

public class TrieImplTest {

    TrieImpl<Integer> trie;

    @BeforeEach
    public void setUp() {
        this.trie = new TrieImpl<>();
    }

    @DisplayName("Create Trie and Test getAllSorted")
    @Test
    public void testOne() {
        this.trie.put("One", 1);
        this.trie.put("Two", 2);
        this.trie.put("Three", 3);
        this.trie.put("One", 13);
        this.trie.put("One", 7);
        Comparator<Integer> comparator = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                if (o1 > o2) {
                    return 1;
                }
                if (o1 < o2) {
                    return -1;
                }
                return 0;
            }
        };
        List<Integer> numbers = this.trie.getAllSorted("One", comparator);
        List<Integer> correct = new ArrayList<>();
        correct.add(1);
        correct.add(7);
        correct.add(13);
        assertEquals(correct, numbers);
    }
    @DisplayName("Testing getAllWithPrefixSorted Method")
    @Test
    public void testTwo() {
        this.trie.put("One", 1);
        this.trie.put("Two", 2);
        this.trie.put("Three", 3);
        this.trie.put("One", 13);
        this.trie.put("One", 7);
        Comparator<Integer> comparator = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                if (o1 > o2) {
                    return 1;
                }
                if (o1 < o2) {
                    return -1;
                }
                return 0;
            }
        };
        List<Integer> numbers = this.trie.getAllWithPrefixSorted("O", comparator);
        List<Integer> correct = new ArrayList<>();
        correct.add(1);
        correct.add(7);
        correct.add(13);
        assertEquals(correct, numbers);
    }

    @DisplayName("Testing deleteAll Method Returns the Deleted Items")
    @Test
    public void testThree() {
        this.trie.put("One", 1);
        this.trie.put("Two", 2);
        this.trie.put("Three", 3);
        this.trie.put("One", 13);
        this.trie.put("One", 7);
        Set<Integer> numbers = this.trie.deleteAll("One");
        Set<Integer> correct = new HashSet<>();
        correct.add(1);
        correct.add(13);
        correct.add(7);
        assertEquals(correct, numbers);
    }

    @DisplayName("Testing deleteAll Method Removes the Items from the Trie")
    @Test
    public void testFour() {
        this.trie.put("One", 1);
        this.trie.put("Two", 2);
        this.trie.put("Three", 3);
        this.trie.put("One", 13);
        this.trie.put("One", 7);
        this.trie.deleteAll("One");
        Comparator<Integer> comparator = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                if (o1 > o2) {
                    return 1;
                }
                if (o1 < o2) {
                    return -1;
                }
                return 0;
            }
        };
        List<Integer> numbers = this.trie.getAllSorted("One", comparator);
        List<Integer> correct = new ArrayList<>();
        assertEquals(correct, numbers);
    }

    @DisplayName("Testing getAllWithPrefixSorted Method with No Hits")
    @Test
    public void testFive() {
        this.trie.put("One", 1);
        this.trie.put("Two", 2);
        this.trie.put("Three", 3);
        this.trie.put("One", 13);
        this.trie.put("One", 7);
        Comparator<Integer> comparator = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                if (o1 > o2) {
                    return 1;
                }
                if (o1 < o2) {
                    return -1;
                }
                return 0;
            }
        };
        List<Integer> numbers = this.trie.getAllWithPrefixSorted("Meep", comparator);
        List<Integer> correct = new ArrayList<>();
        assertEquals(correct, numbers);
    }
    @DisplayName("Testing deleteAllWithPrefix Method")
    @Test
    public void testSix() {
        this.trie.put("One", 1);
        this.trie.put("Two", 2);
        this.trie.put("Three", 3);
        this.trie.put("One", 13);
        this.trie.put("One", 7);
        Set<Integer> numbers = this.trie.deleteAllWithPrefix("O");
        Set<Integer> correct = new HashSet<>();
        correct.add(1);
        correct.add(13);
        correct.add(7);
        assertEquals(correct, numbers);
        Comparator<Integer> comparator = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                if (o1 > o2) {
                    return 1;
                }
                if (o1 < o2) {
                    return -1;
                }
                return 0;
            }
        };
        List<Integer> empty = this.trie.getAllSorted("One", comparator);
        List<Integer> correcto = new ArrayList<>();
        assertEquals(correcto, empty);
    }
    @DisplayName("Testing delete Method")
    @Test
    public void testSeven() {
        this.trie.put("One", 1);
        this.trie.put("Two", 2);
        this.trie.put("Three", 3);
        this.trie.put("One", 13);
        this.trie.put("One", 7);
        Integer deleted = this.trie.delete("One", 13);
        assertEquals(13, deleted);
        Comparator<Integer> comparator = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                if (o1 > o2) {
                    return 1;
                }
                if (o1 < o2) {
                    return -1;
                }
                return 0;
            }
        };
        List<Integer> oneValues = this.trie.getAllSorted("One", comparator);
        List<Integer> correct = new ArrayList<>();
        correct.add(1);
        correct.add(7);
        assertEquals(correct, oneValues);
    }
    @DisplayName("Testing delete Method")
    @Test
    public void testEight() {
        this.trie.put("One", 1);
        this.trie.put("Two", 2);
        this.trie.put("Three", 3);
        this.trie.put("One", 13);
        this.trie.put("One", 7);
        this.trie.put("O", 21);
        Integer deleted = this.trie.delete("One", 13);
        assertEquals(13, deleted);
        Comparator<Integer> comparator = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                if (o1 > o2) {
                    return 1;
                }
                if (o1 < o2) {
                    return -1;
                }
                return 0;
            }
        };
        List<Integer> oneValues = this.trie.getAllWithPrefixSorted("O", comparator);
        List<Integer> correct = new ArrayList<>();
        correct.add(1);
        correct.add(7);
        correct.add(21);
        assertEquals(correct, oneValues);
    }
}