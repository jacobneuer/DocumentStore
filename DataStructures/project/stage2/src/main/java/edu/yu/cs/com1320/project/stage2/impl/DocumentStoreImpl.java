package edu.yu.cs.com1320.project.stage2.impl;

import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.stage2.Document;
import edu.yu.cs.com1320.project.stage2.DocumentStore;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class DocumentStoreImpl implements edu.yu.cs.com1320.project.stage2.DocumentStore{

    private HashTableImpl<URI, DocumentImpl> hashTable;
    public DocumentStoreImpl() {
        hashTable = new HashTableImpl<>();
    }

    @Override
    public int put(InputStream input, URI uri, DocumentFormat format) throws IOException {
        if (uri == null || format == null) {
            throw new IllegalArgumentException("Can't Insert Document With Missing URI or Format");
        }
        if (input == null) {
            DocumentImpl deletedDoc = hashTable.put(uri, null);
            if (deletedDoc != null){
                return deletedDoc.hashCode();
            }
            return 0;
        }
        if(format == DocumentStore.DocumentFormat.TXT){
            return putText(input, uri);
        }
        else {
            return putBinary(input, uri);
        }
    }

    private int putText(InputStream input, URI uri) throws IOException {
        String text;
        try{
            text = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
        catch (Exception e) {
            throw new IOException("String Input Stream Not Given Correctly");
        }
        DocumentImpl stringDoc = new DocumentImpl(uri, text);
        DocumentImpl oldStringDoc = hashTable.put(uri, stringDoc);
        if (oldStringDoc == null) {
            return 0;
        }
        else {
            return oldStringDoc.hashCode();
        }
    }
    private int putBinary(InputStream input, URI uri) throws IOException{
        byte[] bytes;
        try{
            bytes = input.readAllBytes();
        }
        catch (Exception e) {
            throw new IOException("Byte Array Input Stream Not Given Correctly");
        }
        DocumentImpl binaryDoc = new DocumentImpl(uri, bytes);
        DocumentImpl oldBinaryDoc = hashTable.put(uri, binaryDoc);
        if (oldBinaryDoc == null) {
            return 0;
        }
        else {
            return oldBinaryDoc.hashCode();
        }
    }
    @Override
    public Document get(URI uri) {
        return hashTable.get(uri);
    }

    @Override
    public boolean delete(URI uri) {
        DocumentImpl v = hashTable.put(uri, null);
        if (v == null) {
            return false;
        }
        return true;
    }

    @Override
    public void undo() throws IllegalStateException {

    }

    @Override
    public void undo(URI uri) throws IllegalStateException {

    }
}