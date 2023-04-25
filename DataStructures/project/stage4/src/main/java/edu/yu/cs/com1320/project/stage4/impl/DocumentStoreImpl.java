package edu.yu.cs.com1320.project.stage4.impl;

import com.sun.jdi.Value;
import edu.yu.cs.com1320.project.CommandSet;
import edu.yu.cs.com1320.project.GenericCommand;
import edu.yu.cs.com1320.project.Undoable;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage4.Document;
import edu.yu.cs.com1320.project.stage4.DocumentStore;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

public class DocumentStoreImpl implements edu.yu.cs.com1320.project.stage4.DocumentStore {

    private Integer maxDocumentCount;
    private Integer maxDocumentBytes;
    private int documentInventory = 0;
    private int memoryStorage = 0;
    private HashTableImpl<URI, DocumentImpl> hashTable;
    private StackImpl<Undoable> stack;
    private TrieImpl<Document> trie;
    private MinHeapImpl<Document> minHeap;
    private HashMap<Document, Integer> memoryMap;
    public DocumentStoreImpl() {
        this.hashTable = new HashTableImpl<>();
        this.stack = new StackImpl<>();
        this.trie = new TrieImpl<>();
        this.minHeap = new MinHeapImpl<>();
        this.memoryMap = new HashMap<>();
    }

    @Override
    public int put(InputStream input, URI uri, DocumentFormat format) throws IOException {
        if (uri == null || format == null) {
            throw new IllegalArgumentException("Can't Insert Document With Missing URI or Format");
        }
        //If input is null then we delete the given URI and its associated document
        if (input == null) {
            return putDelete(uri);
        }
        this.documentInventory = this.documentInventory + 1;
        //Else put new document into the store depending on its type
        if (format == DocumentStore.DocumentFormat.TXT) {
            return putText(input, uri);
        }
        else {
            return putBinary(input, uri);
        }
    }
    private int putDelete(URI uri) {
        DocumentImpl deletedDoc = this.hashTable.put(uri, null);
        if (deletedDoc != null) {
            this.documentInventory = this.documentInventory - 1;
            //Update the memory map depending on its type of document
            if (deletedDoc.getDocumentTxt() == null) {
                int documentStorage = deletedDoc.getDocumentBinaryData().length;
                this.memoryMap.remove(deletedDoc);
                this.memoryStorage = this.memoryStorage - documentStorage;
            }
            else {
                int documentStorage = deletedDoc.getDocumentTxt().getBytes().length;
                this.memoryMap.remove(deletedDoc);
                this.memoryStorage = this.memoryStorage - documentStorage;
            }
            //Add undo function to allow the document being deleted to be put back into the store in the future
            //To undo, put the document back in the store and add the words back into the trie
            Function<URI, Boolean> putBackFunction = (x) ->
            {
                this.hashTable.put(x, deletedDoc);
                this.documentInventory = this.documentInventory + 1;
                //Update the memory
                if (deletedDoc.getDocumentTxt() == null) {
                    int documentStorage = deletedDoc.getDocumentBinaryData().length;
                    this.memoryMap.put(deletedDoc, documentStorage);
                    this.memoryStorage = this.memoryStorage + documentStorage;
                }
                else {
                    int documentStorage = deletedDoc.getDocumentTxt().getBytes().length;
                    this.memoryMap.put(deletedDoc, documentStorage);
                    this.memoryStorage = this.memoryStorage + documentStorage;
                }
                trieAddition(deletedDoc);
                //Update last time using the document
                deletedDoc.setLastUseTime(System.nanoTime());
                this.minHeap.insert(deletedDoc);
                clearUpDocuments();
                return true;
            };
            GenericCommand<URI> undoDelete = new GenericCommand<>(uri, putBackFunction);
            this.stack.push(undoDelete);
            //Delete all mentions of this document from the trie
            trieDeletion(deletedDoc);
            //Delete from the heap
            removeFromHeap(deletedDoc);
            return deletedDoc.hashCode();
        }
        //If there's no old document being returned then we didn't do anything so there's nothing to undo
        return 0;
    }

    private void removeFromHeap(Document deletedDoc) {
        //Search for document from the heap and remove it
        List<Document> tempDocs = new ArrayList<>();
        for(int i = 0; i < this.documentInventory; i++) {
            Document d = this.minHeap.remove();
            if(d.equals(deletedDoc)) {
                for(Document doc: tempDocs) {
                    this.minHeap.insert(doc);
                }
                return;
            }
            tempDocs.add(d);
        }
        //Was not found so put the temps back into the heap and return
        for(Document doc: tempDocs) {
            this.minHeap.insert(doc);
        }
    }
    private int putText(InputStream input, URI uri) throws IOException {
        String text;
        try {
            text = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
        catch (Exception e) {
            throw new IOException("String Input Stream Not Given Correctly");
        }
        //Create the new document
        DocumentImpl newStringDoc = new DocumentImpl(uri, text);
        //Update the memory map
        int documentStorage = newStringDoc.getDocumentTxt().getBytes().length;
        this.memoryMap.put(newStringDoc, documentStorage);
        this.memoryStorage = this.memoryStorage + documentStorage;
        //Update the trie
        trieAddition(newStringDoc);
        //Put the new document into the store
        DocumentImpl oldDoc = this.hashTable.put(uri, newStringDoc);
        //Update the memory of deleting the old document if there was one
        if (oldDoc != null) {
            this.documentInventory = this.documentInventory - 1;
            if (oldDoc.getDocumentTxt() == null) {
                int oldDocumentStorage = oldDoc.getDocumentBinaryData().length;
                this.memoryMap.remove(oldDoc);
                this.memoryStorage = this.memoryStorage - oldDocumentStorage;
            }
            else {
                int oldDocumentStorage = oldDoc.getDocumentTxt().getBytes().length;
                this.memoryMap.remove(oldDoc);
                this.memoryStorage = this.memoryStorage - oldDocumentStorage;
            }
        }
        //Update the last time using the document then put it into the heap
        newStringDoc.setLastUseTime(System.nanoTime());
        this.minHeap.insert(newStringDoc);
        //Update storage/memory
        clearUpDocuments();
        //Create undo situation depending on if it is a new put or a replacement put
        if (oldDoc == null) {
            return newPutUndo(uri, newStringDoc);
        }
        else {
            return replacePutUndo(uri, newStringDoc, oldDoc);
        }
    }
    private int newPutUndo (URI uri, DocumentImpl newStringDoc){
        //Creates an Undo function to delete the new document that was put in the store
        Function<URI, Boolean> deleteFunction = (x) -> {
            this.hashTable.put(x, null);
            this.documentInventory = this.documentInventory - 1;
            //Update memory
            if (newStringDoc.getDocumentTxt() == null) {
                int oldDocumentStorage = newStringDoc.getDocumentBinaryData().length;
                this.memoryMap.remove(newStringDoc);
                this.memoryStorage = this.memoryStorage - oldDocumentStorage;
            }
            else {
                int oldDocumentStorage = newStringDoc.getDocumentTxt().getBytes().length;
                this.memoryMap.remove(newStringDoc);
                this.memoryStorage = this.memoryStorage - oldDocumentStorage;
            }
            trieDeletion(newStringDoc);
            removeFromHeap(newStringDoc);
            return true;
        };
        GenericCommand<URI> undoNewPut = new GenericCommand<>(uri, deleteFunction);
        this.stack.push(undoNewPut);
        return 0;
    }
    private int replacePutUndo (URI uri, DocumentImpl newStringDoc, DocumentImpl oldStringDoc) {
        //Add undo function to the stack to put back the old document associated with the URI
        //Undo deletes all the new words added to the trie and puts back the old words deleted
        //Update the last time using the old document and put it back in the heap
        //Delete the put document from the store and remove from the heap
        Function<URI, Boolean> replaceFunction = (x) -> {
            //Then put the old document back in the store
            this.hashTable.put(x, oldStringDoc);
            trieAddition(oldStringDoc);
            //Update memory
            if (oldStringDoc.getDocumentTxt() == null) {
                int oldDocumentStorage = oldStringDoc.getDocumentBinaryData().length;
                this.memoryMap.put(oldStringDoc, oldDocumentStorage);
                this.memoryStorage = this.memoryStorage + oldDocumentStorage;
            }
            else {
                int oldDocumentStorage = oldStringDoc.getDocumentTxt().getBytes().length;
                this.memoryMap.put(oldStringDoc, oldDocumentStorage);
                this.memoryStorage = this.memoryStorage + oldDocumentStorage;
            }
            oldStringDoc.setLastUseTime(System.nanoTime());
            this.minHeap.insert(oldStringDoc);
            trieDeletion(newStringDoc);
            //Update memory
            if (newStringDoc.getDocumentTxt() == null) {
                int oldDocumentStorage = newStringDoc.getDocumentBinaryData().length;
                this.memoryMap.remove(newStringDoc);
                this.memoryStorage = this.memoryStorage - oldDocumentStorage;
            }
            else {
                int oldDocumentStorage = newStringDoc.getDocumentTxt().getBytes().length;
                this.memoryMap.remove(newStringDoc);
                this.memoryStorage = this.memoryStorage - oldDocumentStorage;
            }
            removeFromHeap(newStringDoc);
            clearUpDocuments();
            return true;
        };
        GenericCommand<URI> undoReplacePut = new GenericCommand<>(uri, replaceFunction);
        this.stack.push(undoReplacePut);
        //Delete all mentions of the old document from the trie
        trieDeletion(oldStringDoc);
        return oldStringDoc.hashCode();
    }
    private void trieAddition(DocumentImpl doc){
        Set<String> allWords = doc.getWords();
        for(String s: allWords) {
            this.trie.put(s, doc);
        }
    }
    private void trieDeletion(DocumentImpl doc){
        Set<String> allWords = doc.getWords();
        for(String s: allWords) {
            this.trie.delete(s, doc);
        }
    }
    private int putBinary(InputStream input, URI uri) throws IOException{
        byte[] bytes;
        try {
            bytes = input.readAllBytes();
        }
        catch (Exception e) {
            throw new IOException("Byte Array Input Stream Not Given Correctly");
        }
        DocumentImpl newBinaryDoc = new DocumentImpl(uri, bytes);
        //Update the memory map
        int documentStorage = bytes.length;
        this.memoryMap.put(newBinaryDoc, documentStorage);
        this.memoryStorage = this.memoryStorage + documentStorage;
        //Place the new binary document into the table
        DocumentImpl oldBinaryDoc = this.hashTable.put(uri, newBinaryDoc);
        //Update storage of old binary document
        if (oldBinaryDoc != null) {
            this.documentInventory = this.documentInventory - 1;
            if (oldBinaryDoc.getDocumentTxt() == null) {
                int oldDocumentStorage = oldBinaryDoc.getDocumentBinaryData().length;
                this.memoryMap.remove(oldBinaryDoc);
                this.memoryStorage = this.memoryStorage - oldDocumentStorage;
            }
            else {
                int oldDocumentStorage = oldBinaryDoc.getDocumentTxt().getBytes().length;
                this.memoryMap.remove(oldBinaryDoc);
                this.memoryStorage = this.memoryStorage - oldDocumentStorage;
            }
        }
        //Update the last time using the document
        newBinaryDoc.setLastUseTime(System.nanoTime());
        this.minHeap.insert(newBinaryDoc);
        clearUpDocuments();
        //Create undo depending on if this is a new binary document or replacing an old one
        if (oldBinaryDoc == null) {
            //Add undo function to the stack to delete the new document being added to the store
            Function<URI, Boolean> deleteFunction = (x) -> {
                this.hashTable.put(x, null);
                this.documentInventory = this.documentInventory - 1;
                removeFromHeap(newBinaryDoc);
                //Update the memory
                this.memoryMap.remove(newBinaryDoc);
                this.memoryStorage = this.memoryStorage - documentStorage;
                return true;
            };
            GenericCommand<URI> undoNewPut = new GenericCommand<>(uri, deleteFunction);
            this.stack.push(undoNewPut);
            return 0;
        }
        else {
            //Add undo function to the stack to put back the old document associated with the URI
            //Update the old binary document's last use time and put it back in the heap
            Function<URI, Boolean> replaceFunction = (x) -> {
                this.hashTable.put(x, oldBinaryDoc);
                //To undo the new document being deleted, remove it from heap and remove it from memory
                removeFromHeap(newBinaryDoc);
                this.memoryMap.remove(newBinaryDoc);
                this.memoryStorage = this.memoryStorage - documentStorage;
                //Update the memory of the old document being put back into the store
                this.memoryMap.put(oldBinaryDoc, oldBinaryDoc.getDocumentBinaryData().length);
                this.memoryStorage = this.memoryStorage + oldBinaryDoc.getDocumentBinaryData().length;
                //Update the old binary document's last use time and put it back in the heap
                oldBinaryDoc.setLastUseTime(System.nanoTime());
                this.minHeap.insert(oldBinaryDoc);
                clearUpDocuments();
                return true;
            };
            GenericCommand<URI> undoReplacePut = new GenericCommand<>(uri, replaceFunction);
            this.stack.push(undoReplacePut);
            return oldBinaryDoc.hashCode();
        }
    }
    @Override
    public Document get(URI uri) {
        Document getDocument = this.hashTable.get(uri);
        if (getDocument != null){
            //Update last time using the document
            getDocument.setLastUseTime(System.nanoTime());
            this.minHeap.reHeapify(getDocument);
        }
        return getDocument;
    }

    @Override
    public boolean delete(URI uri) {
        //Remove document from the store
        DocumentImpl deletedDoc = this.hashTable.put(uri, null);
        //If there's no old document being returned then we didn't do anything so there's nothing to undo
        if (deletedDoc == null) {
            return false;
        }
        this.documentInventory = this.documentInventory - 1;
        //Update the memory map depending on its type of document
        if (deletedDoc.getDocumentTxt() == null) {
            int documentStorage = deletedDoc.getDocumentBinaryData().length;
            this.memoryMap.remove(deletedDoc);
            this.memoryStorage = this.memoryStorage - documentStorage;
        }
        else {
            int documentStorage = deletedDoc.getDocumentTxt().getBytes().length;
            this.memoryMap.remove(deletedDoc);
            this.memoryStorage = this.memoryStorage - documentStorage;
        }
        //Else add undo function to allow the document being deleted to be put back into the store in the future
        //Update the deleted document's last use time and add back into the heap
        Function<URI, Boolean> putBackFunction = (x) ->
        {
            this.hashTable.put(x, deletedDoc);
            this.documentInventory = this.documentInventory + 1;
            trieAddition(deletedDoc);
            //Update memory
            if (deletedDoc.getDocumentTxt() == null) {
                int documentStorage = deletedDoc.getDocumentBinaryData().length;
                this.memoryMap.put(deletedDoc, documentStorage);
                this.memoryStorage = this.memoryStorage + documentStorage;
            }
            else {
                int documentStorage = deletedDoc.getDocumentTxt().getBytes().length;
                this.memoryMap.put(deletedDoc, documentStorage);
                this.memoryStorage = this.memoryStorage + documentStorage;
            }
            deletedDoc.setLastUseTime(System.nanoTime());
            this.minHeap.insert(deletedDoc);
            clearUpDocuments();
            return true;
        };
        GenericCommand<URI> undoDelete = new GenericCommand<>(uri, putBackFunction);
        this.stack.push(undoDelete);
        //Delete all mentions of the document from the trie
        trieDeletion(deletedDoc);
        //Delete from the heap
        removeFromHeap(deletedDoc);
        return true;
    }

    @Override
    public void undo() throws IllegalStateException {
        if (this.stack.peek() != null){
            Undoable undoCommand = this.stack.pop();
            undoCommand.undo();
            return;
        }
        //No frames to undo
        throw new IllegalStateException("Can't undo with no frames in the stack");
    }

    @Override
    public void undo(URI uri) throws IllegalStateException {
        StackImpl<Undoable> tempStack = new StackImpl<>();
        //Search the stack to find the given URI while popping the frames into a temporary stack to not lose them
        while (this.stack.peek() != null) {
            if (this.stack.peek() instanceof GenericCommand<?>) {
                GenericCommand<URI> command = (GenericCommand<URI>) this.stack.peek();
                if (!command.getTarget().equals(uri)) {
                    tempStack.push(this.stack.pop());
                    continue;
                }
                break;
            }
            else {
                CommandSet<URI> commandSet = (CommandSet<URI>) this.stack.peek();
                if (!commandSet.containsTarget(uri)) {
                    tempStack.push(this.stack.pop());
                    continue;
                }
                break;
            }
        }
        //URI has been found in the stack; Undo the frame
        if (this.stack.peek() != null) {
            undoCommand(tempStack, uri);
            return;
        }
        //No URI was found in the frame so put the removed frames back in the stack and throw an exception
        while (tempStack.peek() != null) {
            this.stack.push(tempStack.pop());
        }
        throw new IllegalStateException("No given URI exists in the frame to undo");
    }
    private void undoCommand(StackImpl<Undoable> tempStack, URI uri) {
        if (this.stack.peek() instanceof GenericCommand<?>) {
            GenericCommand<URI> undoCommand = (GenericCommand<URI>) this.stack.pop();
            undoCommand.undo();
        }
        else {
            CommandSet<URI> commandSet = (CommandSet<URI>) this.stack.pop();
            commandSet.undo(uri);
            this.stack.push(commandSet);
        }
        //Put the temp stacks back on the stack
        while (tempStack.peek() != null) {
            this.stack.push(tempStack.pop());
        }
    }
    @Override
    public List<Document> search(String keyword) {
        Comparator<Document> comparator = new Comparator<Document>() {
            @Override
            public int compare(Document o1, Document o2) {
                if (o1.wordCount(keyword) < o2.wordCount(keyword)) {
                    return 1;
                }
                if (o1.wordCount(keyword) > o2.wordCount(keyword)) {
                    return -1;
                }
                return 0;
            }
        };
        List<Document> documents = this.trie.getAllSorted(keyword, comparator);
        //Update last time using each of the documents
        long updateTime = System.nanoTime();
        for(Document d: documents) {
            d.setLastUseTime(updateTime);
            this.minHeap.reHeapify(d);
        }
        return documents;
    }

    @Override
    public List<Document> searchByPrefix(String keywordPrefix) {
        Comparator<Document> comparator = searchByPrefixComparator(keywordPrefix);
        List<Document> documents = this.trie.getAllWithPrefixSorted(keywordPrefix, comparator);
        //Update last time using each of the documents
        long updateTime = System.nanoTime();
        for(Document d: documents) {
            d.setLastUseTime(updateTime);
            this.minHeap.reHeapify(d);
        }
        return documents;
    }
    private Comparator<Document> searchByPrefixComparator (String keywordPrefix) {
        Comparator<Document> comparator = new Comparator<Document>() {
            @Override
            public int compare(Document o1, Document o2) {
                //Find the number of instances of words that begin with the given prefix in document 1
                Set<String> set1 = o1.getWords();
                int count1 = 0;
                for(String s: set1) {
                    if(s.startsWith(keywordPrefix)) {
                        count1 = count1 + o1.wordCount(s);
                    }
                }
                //Find the number of instances of words that begin with the given prefix in document 2
                Set<String> set2 = o2.getWords();
                int count2 = 0;
                for(String s: set2) {
                    if(s.startsWith(keywordPrefix)) {
                        count2 = count2 + o2.wordCount(s);
                    }
                }
                //Compare each of those totals
                if (count1 < count2) {
                    return 1;
                }
                if (count1 > count2) {
                    return -1;
                }
                return 0;
            }
        };
        return comparator;
    }
    @Override
    public Set<URI> deleteAll(String keyword) {
        //First collect the documents that need to be deleted from the store
        Set<Document> deletedDocuments = this.trie.deleteAll(keyword);
        Set<URI> deletedURIs = new HashSet<>();
        if(deletedDocuments.size() > 1) {
            return deleteMultipleDocuments(deletedDocuments, deletedURIs);
        }
        else {
            return deleteSingleDocument(deletedDocuments, deletedURIs);
        }
    }
    private Set<URI> deleteMultipleDocuments(Set<Document> deletedDocuments, Set<URI> deletedURIs){
        //Create undo logic to put back all the deleted documents in the store
        //and add their words back to the trie
        CommandSet<URI> commandSet = new CommandSet<>();
        for(Document d: deletedDocuments){
            this.documentInventory = this.documentInventory - 1;
            //Update the memory map depending on its type of document
            if (d.getDocumentTxt() == null) {
                int documentStorage = d.getDocumentBinaryData().length;
                this.memoryMap.remove(d);
                this.memoryStorage = this.memoryStorage - documentStorage;
            }
            else {
                int documentStorage = d.getDocumentTxt().getBytes().length;
                this.memoryMap.remove(d);
                this.memoryStorage = this.memoryStorage - documentStorage;
            }
            //Create undo functions
            Function<URI, Boolean> putBackFunction = (x) ->
            {
                DocumentImpl implDeletedDoc = (DocumentImpl) d;
                this.hashTable.put(x, implDeletedDoc);
                this.documentInventory = this.documentInventory + 1;
                trieAddition(implDeletedDoc);
                //Update memory
                if (d.getDocumentTxt() == null) {
                    int documentStorage = d.getDocumentBinaryData().length;
                    this.memoryMap.put(d, documentStorage);
                    this.memoryStorage = this.memoryStorage + documentStorage;
                }
                else {
                    int documentStorage = d.getDocumentTxt().getBytes().length;
                    this.memoryMap.put(d, documentStorage);
                    this.memoryStorage = this.memoryStorage + documentStorage;
                }
                d.setLastUseTime(System.nanoTime());
                this.minHeap.insert(d);
                clearUpDocuments();
                return true;
            };
            GenericCommand<URI> undoDelete = new GenericCommand<>(d.getKey(), putBackFunction);
            commandSet.addCommand(undoDelete);
            //Delete all the words in each document that include the given keyword from the trie
            trieDeletion((DocumentImpl) d);
            //Delete from the heap
            removeFromHeap(d);
            //Add the deleted documents to a set of URIs to return and then delete the doc from the store
            deletedURIs.add(d.getKey());
            this.hashTable.put(d.getKey(), null);
        }
        this.stack.push(commandSet);
        return deletedURIs;
    }
    private Set<URI> deleteSingleDocument(Set<Document> deletedDocuments, Set<URI> deletedURIs) {
        for(Document d: deletedDocuments) {
            this.documentInventory = this.documentInventory - 1;
            //Update the memory map depending on its type of document
            if (d.getDocumentTxt() == null) {
                int documentStorage = d.getDocumentBinaryData().length;
                this.memoryMap.remove(d);
                this.memoryStorage = this.memoryStorage - documentStorage;
            }
            else {
                int documentStorage = d.getDocumentTxt().getBytes().length;
                this.memoryMap.remove(d);
                this.memoryStorage = this.memoryStorage - documentStorage;
            }
            Function<URI, Boolean> putBackFunction = (x) ->
            {
                DocumentImpl implDeletedDoc = (DocumentImpl) d;
                this.hashTable.put(x, implDeletedDoc);
                this.documentInventory = this.documentInventory + 1;
                trieAddition(implDeletedDoc);
                //Update memory
                if (d.getDocumentTxt() == null) {
                    int documentStorage = d.getDocumentBinaryData().length;
                    this.memoryMap.put(d, documentStorage);
                    this.memoryStorage = this.memoryStorage + documentStorage;
                }
                else {
                    int documentStorage = d.getDocumentTxt().getBytes().length;
                    this.memoryMap.put(d, documentStorage);
                    this.memoryStorage = this.memoryStorage + documentStorage;
                }
                d.setLastUseTime(System.nanoTime());
                this.minHeap.insert(d);
                clearUpDocuments();
                return true;
            };
            GenericCommand<URI> undoDelete = new GenericCommand<>(d.getKey(), putBackFunction);
            this.stack.push(undoDelete);
            //Delete all the words in each document that include the given keyword from the trie
            trieDeletion((DocumentImpl) d);
            //Delete from the heap
            removeFromHeap(d);
            //Add the deleted document to a set of URIs to return and then delete the doc from the store
            deletedURIs.add(d.getKey());
            this.hashTable.put(d.getKey(), null);
        }
        return deletedURIs;
    }
    @Override
    public Set<URI> deleteAllWithPrefix(String keywordPrefix) {
        //First collect the documents that need to be deleted from the store
        Set<Document> deletedDocuments = this.trie.deleteAllWithPrefix(keywordPrefix);
        Set<URI> deletedURIs = new HashSet<>();
        if(deletedDocuments.size() > 1) {
            return deleteMultipleDocuments(deletedDocuments, deletedURIs);
        }
        else {
            return deleteSingleDocument(deletedDocuments, deletedURIs);
        }
    }

    @Override
    public void setMaxDocumentCount(int limit) {
        if(limit < 0) {
            throw new IllegalArgumentException("Can't set a max document level of less than 0");
        }
        this.maxDocumentCount = limit;
        clearUpDocuments();
    }
    private void clearUpDocuments() {
        //If number of documents are full then remove until space is cleared
        if(this.maxDocumentCount != null) {
            while(this.documentInventory > this.maxDocumentCount) {
                Document deletedDoc = this.minHeap.remove();
                this.hashTable.put(deletedDoc.getKey(), null);
                this.documentInventory = this.documentInventory - 1;
                this.memoryStorage = this.memoryStorage - this.memoryMap.get(deletedDoc);
                this.memoryMap.remove(deletedDoc);
            }
        }
        //If storage is full then delete documents until there's enough memory
        if(this.maxDocumentBytes != null) {
            while(this.memoryStorage > this.maxDocumentBytes) {
                Document deletedDoc = this.minHeap.remove();
                this.hashTable.put(deletedDoc.getKey(), null);
                this.documentInventory = this.documentInventory - 1;
                this.memoryStorage = this.memoryStorage - this.memoryMap.get(deletedDoc);
                this.memoryMap.remove(deletedDoc);
            }
        }
    }
    @Override
    public void setMaxDocumentBytes(int limit) {
        if(limit < 0) {
            throw new IllegalArgumentException("Can't set a memory limit of less than 0");
        }
        this.maxDocumentBytes = limit;
        clearUpDocuments();
    }
}