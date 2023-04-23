package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.stage4.*;
import edu.yu.cs.com1320.project.stage4.impl.*;
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

public class MinHeapImplTest {
    MinHeapImpl<Integer> minHeap;

    @BeforeEach
    public void setUp() {
        minHeap = new MinHeapImpl<>();
    }

    @DisplayName("Test Insert and Remove")
    @Test
    public void testOne() {
        this.minHeap.insert(1);
        Integer removed = this.minHeap.remove();
        assertEquals(1, removed);
    }
    @DisplayName("Insert Many then Remove")
    @Test
    public void testTwo() {
        this.minHeap.insert(1);
        this.minHeap.insert(5);
        this.minHeap.insert(10);
        this.minHeap.insert(4);
        Integer removed = this.minHeap.remove();
        assertEquals(1, removed);
    }
    @DisplayName("Insert Many then Remove a Second Time")
    @Test
    public void testThree() {
        this.minHeap.insert(100);
        this.minHeap.insert(5);
        this.minHeap.insert(10);
        this.minHeap.insert(4);
        Integer removed = this.minHeap.remove();
        assertEquals(4, removed);
    }
    @DisplayName("Get Array Index Test")
    @Test
    public void testFour() {
        this.minHeap.insert(100);
        this.minHeap.insert(5);
        this.minHeap.insert(10);
        this.minHeap.insert(4);
        Integer indexof10 = this.minHeap.getArrayIndex(10);
        Integer indexof100 = this.minHeap.getArrayIndex(100);
        assertEquals(indexof100,4);
        assertEquals(indexof10,3);
    }
    @DisplayName("Make sure Array Doubling Works")
    @Test
    public void testFive() {
        this.minHeap.insert(1);
        this.minHeap.insert(2);
        this.minHeap.insert(3);
        this.minHeap.insert(4);
        this.minHeap.insert(5);
        this.minHeap.insert(6);
        this.minHeap.insert(7);
        this.minHeap.insert(8);
        this.minHeap.insert(9);
        this.minHeap.insert(10);
        this.minHeap.insert(11);
        this.minHeap.insert(12);
        Integer removed = this.minHeap.remove();
        assertEquals(1, removed);
    }
    @DisplayName("Test ReHeapify")
    @Test
    public void testSix() {
        Integer one = 7;
        this.minHeap.insert(one);
        Integer two = 2;
        this.minHeap.insert(two);
        Integer three = 3;
        this.minHeap.insert(three);
        Integer four = 4;
        this.minHeap.insert(four);
        Integer five = 5;
        this.minHeap.insert(five);
        Integer removed = this.minHeap.remove();
        assertEquals(2, removed);
        System.out.println(this.minHeap.getArrayIndex(five));
        five = 1;
        this.minHeap.reHeapify(five);
    }
    @DisplayName("Test ReHeapify")
    @Test
    public void testSevenAndAHalf() {
        MinHeapImpl<String> stringMinHeap;
        Integer one = 7;
        this.minHeap.insert(one);
        Integer two = 2;
        this.minHeap.insert(two);
        Integer three = 3;
        this.minHeap.insert(three);
        Integer four = 4;
        this.minHeap.insert(four);
        Integer five = 5;
        this.minHeap.insert(five);
        Integer removed = this.minHeap.remove();
        assertEquals(2, removed);
        System.out.println(this.minHeap.getArrayIndex(five));
        five = 1;
        this.minHeap.reHeapify(five);
    }
}