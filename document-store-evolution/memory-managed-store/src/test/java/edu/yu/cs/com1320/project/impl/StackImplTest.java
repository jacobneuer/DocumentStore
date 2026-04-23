package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.stage4.*;
import edu.yu.cs.com1320.project.stage4.impl.*;
import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

public class StackImplTest {
    private StackImpl<String> stack;
    @BeforeEach
    public void setUp() {
        this.stack = new StackImpl<>();
    }
    @DisplayName("Push an Item Into the Stack and Then Pop It")
    @Test
    public void testOne() {
        this.stack.push("First element");
        String test = this.stack.pop();
        assertEquals("First element", test);
    }
    @DisplayName("Push two Items Into the Stack and Then Pop Them")
    @Test
    public void testTwo() {
        this.stack.push("First element");
        this.stack.push("Second element");
        String firstPop = this.stack.pop();
        String secondPop = this.stack.pop();
        assertEquals("Second element", firstPop);
        assertEquals("First element", secondPop);
    }

    @DisplayName("Test Size Method With Nonzero Amount")
    @Test
    public void testThree() {
        this.stack.push("First element");
        this.stack.push("Second element");
        int size = this.stack.size();
        assertEquals(2, size);
    }
    @DisplayName("Test Size Method With Zero Elements")
    @Test
    public void testFour() {
        int size = this.stack.size();
        assertEquals(0, size);
    }
    @DisplayName("Push no Items and then Pop")
    @Test
    public void testFive() {
        String test = this.stack.pop();
        assertNull(test);
    }
    @DisplayName("Test Peek Method")
    @Test
    public void testSix() {
        this.stack.push("First element");
        this.stack.push("Second element");
        String peek = this.stack.peek();
        assertEquals("Second element", peek);
    }
    @DisplayName("Make Sure Peek Didn't Remove the Frame")
    @Test
    public void testSeven() {
        this.stack.push("First element");
        this.stack.push("Second element");
        String peek = this.stack.peek();
        String test = this.stack.pop();
        assertEquals("Second element", test);
    }
}
