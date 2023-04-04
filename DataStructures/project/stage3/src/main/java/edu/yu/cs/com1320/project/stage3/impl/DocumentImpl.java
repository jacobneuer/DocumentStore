package edu.yu.cs.com1320.project.stage3.impl;

import edu.yu.cs.com1320.project.HashTable;
import edu.yu.cs.com1320.project.impl.HashTableImpl;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DocumentImpl implements edu.yu.cs.com1320.project.stage3.Document {

    private URI uri;
    private String documentText;
    private byte[] byteArray;
    private HashTable<String, Integer> wordMap;

    public DocumentImpl(URI uri, String txt){
        if (uri == null || txt == null || txt.equals("")){
            throw new IllegalArgumentException("URI or Text is Null");
        }
        this.uri = uri;
        this.documentText = txt;
        this.wordMap = new HashTableImpl<>();
        String noPunctuation = txt.replaceAll("\\p{Punct}", "");
        String[] words = noPunctuation.split(" ");
        for(int i = 0; i < words.length; i++) {
            if(this.wordMap.containsKey(words[i])) {
                int count = this.wordMap.get(words[i]) + 1;
                this.wordMap.put(words[i], count);
            }
            else {
                this.wordMap.put(words[i], 1);
            }
        }
    }
    public DocumentImpl(URI uri, byte[] binaryData) {
        if (uri == null || binaryData == null || binaryData.length == 0){
            throw new IllegalArgumentException("URI or Binary Data is Null");
        }
        this.uri = uri;
        this.byteArray = binaryData;
    }

    @Override
    public String getDocumentTxt() {
        return this.documentText;
    }

    @Override
    public byte[] getDocumentBinaryData() {
        return this.byteArray;
    }

    @Override
    public URI getKey() {
        return this.uri;
    }

    @Override
    public int hashCode() {
        int result = this.uri.hashCode();
        result = 31 * result + (this.documentText != null ? this.documentText.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(this.byteArray);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        DocumentImpl otherDoc = (DocumentImpl) obj;
        if(hashCode() == otherDoc.hashCode()){
            return true;
        }
        return false;
    }

    @Override
    public int wordCount(String word) {
        //If document is a String document
        if(this.byteArray == null){
            if (this.wordMap.containsKey(word)) {
                return this.wordMap.get(word);
            }
            else {
                return 0;
            }
        }
        //If the document is a byte array so return 0
        else {
            return 0;
        }
    }

    @Override
    public Set<String> getWords() {
        if (this.documentText == null) {
            Set<String> emptySet = new HashSet<>();
            return emptySet;
        }
        String noPunctuation = this.documentText.replaceAll("\\p{Punct}", "");
        String[] words = noPunctuation.split(" ");
        Set<String> distinctWords = new HashSet<>();
        for (int i = 0; i < words.length; i++) {
            distinctWords.add(words[i]);
        }
        return distinctWords;
    }

}