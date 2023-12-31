package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.CommandSet;
import edu.yu.cs.com1320.project.GenericCommand;
import edu.yu.cs.com1320.project.Undoable;
import edu.yu.cs.com1320.project.impl.*;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.DocumentStore;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

public class DocumentStoreImpl implements edu.yu.cs.com1320.project.stage5.DocumentStore {

    private Integer maxDocumentCount;
    private Integer maxDocumentBytes;
    private int documentInventory = 0;
    private int memoryStorage = 0;
    private BTreeImpl<URI, Document> bTree;
    private StackImpl<Undoable> stack;
    private TrieImpl<URI> trie;
    private MinHeapImpl<MinHeapNode> minHeap;
    private HashMap<URI, Integer> memoryMap;
    private List<URI> diskURIs;
    public DocumentStoreImpl() {
        this.bTree = new BTreeImpl<>();
        this.stack = new StackImpl<>();
        this.trie = new TrieImpl<>();
        this.minHeap = new MinHeapImpl<>();
        this.memoryMap = new HashMap<>();
        DocumentPersistenceManager documentPersistenceManager = new DocumentPersistenceManager(null);
        this.bTree.setPersistenceManager(documentPersistenceManager);
        this.diskURIs = new ArrayList<>();
    }
    public DocumentStoreImpl(File baseDir) {
        this.bTree = new BTreeImpl<>();
        this.stack = new StackImpl<>();
        this.trie = new TrieImpl<>();
        this.minHeap = new MinHeapImpl<>();
        this.memoryMap = new HashMap<>();
        DocumentPersistenceManager documentPersistenceManager = new DocumentPersistenceManager(baseDir);
        this.bTree.setPersistenceManager(documentPersistenceManager);
        this.diskURIs = new ArrayList<>();
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
        DocumentImpl preDeletedDoc = (DocumentImpl) this.bTree.get(uri);
        if (preDeletedDoc != null) {
            //Delete from the heap
            try {
                removeFromHeap(preDeletedDoc);
            }
            catch (NoSuchElementException e) {

            }
        }
        DocumentImpl deletedDoc = (DocumentImpl) this.bTree.put(uri, null);
        if (deletedDoc != null) {
            if (!this.diskURIs.contains(deletedDoc.getKey())) {
                this.documentInventory = this.documentInventory - 1;
            }
            //Update the memory map depending on its type of document
            deleteMemory(deletedDoc, false);
            //Add undo function to allow the document being deleted to be put back into the store in the future
            //To undo, put the document back in the store and add the words back into the trie
            Function<URI, Boolean> putBackFunction = undoPutFunction(deletedDoc);
            GenericCommand<URI> undoDelete = new GenericCommand<>(uri, putBackFunction);
            this.stack.push(undoDelete);
            //Delete all mentions of this document from the trie
            trieDeletion(deletedDoc);
            //If the URI was in the disk, remove from list
            this.diskURIs.remove(deletedDoc.getKey());
            return deletedDoc.hashCode();
        }
        //If there's no old document being returned then we didn't do anything so there's nothing to undo
        return 0;
    }

    private Function<URI, Boolean> undoPutFunction(DocumentImpl deletedDoc) {
        Function<URI, Boolean> putBackFunction = (x) ->
        {
            this.bTree.put(x, deletedDoc);
            this.documentInventory = this.documentInventory + 1;
            //Update the memory
            addMemoryBack(deletedDoc);
            trieAddition(deletedDoc);
            //Update last time using the document
            deletedDoc.setLastUseTime(System.nanoTime());
            this.minHeap.insert(new MinHeapNode(deletedDoc.getKey(), this.bTree));
            clearUpDocuments();
            return true;
        };
        return putBackFunction;
    }
    private void removeFromHeap(Document deletedDoc) {
        deletedDoc.setLastUseTime(0);
        this.minHeap.reHeapify(new MinHeapNode(deletedDoc.getKey(), this.bTree));
        this.minHeap.remove();
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
        DocumentImpl newStringDoc = new DocumentImpl(uri, text, null);
        //Update the trie
        trieAddition(newStringDoc);
        //Put the new document into the store
        DocumentImpl oldDoc = (DocumentImpl) this.bTree.put(uri, newStringDoc);
        //If there was an old document, delete references to it in memory, document space, trie, and heap
        if(oldDoc != null) {
            if (!this.diskURIs.contains(oldDoc.getKey())) {
                this.documentInventory = this.documentInventory - 1;
            }
            deleteMemory(oldDoc, true);
            //Delete all mentions of this document from the trie
            trieDeletion(oldDoc);
            try {
                //Remove from heap
                removeFromHeap(oldDoc);
            }
            catch (NoSuchElementException e) {
            }
        }
        //Update the memory map
        int documentStorage = newStringDoc.getDocumentTxt().getBytes().length;
        this.memoryMap.put(newStringDoc.getKey(), documentStorage);
        this.memoryStorage = this.memoryStorage + documentStorage;
        //Update the last time using the document then put it into the heap
        newStringDoc.setLastUseTime(System.nanoTime());
        this.minHeap.insert(new MinHeapNode(newStringDoc.getKey(), this.bTree));
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
            if (!this.diskURIs.contains(newStringDoc.getKey())) {
                this.documentInventory = this.documentInventory - 1;
            }
            //Delete from the heap
            try {
                removeFromHeap(newStringDoc);
            }
            catch (NoSuchElementException e) {

            }
            this.bTree.put(x, null);
            //Update memory
            deleteMemory(newStringDoc, false);
            trieDeletion(newStringDoc);
            this.diskURIs.remove(newStringDoc.getKey());
            return true;
        };
        GenericCommand<URI> undoNewPut = new GenericCommand<>(uri, deleteFunction);
        this.stack.push(undoNewPut);
        return 0;
    }
    private int replacePutUndo (URI uri, DocumentImpl newStringDoc, DocumentImpl oldStringDoc) {
        Function<URI, Boolean> replaceFunction = (x) -> {
            //Delete new document from the heap
            try {
                removeFromHeap(newStringDoc);
            }
            catch (NoSuchElementException e) {

            }
            //Delete new document from trie
            trieDeletion(newStringDoc);
            //Delete new document from memory and remove from heap
            deleteMemory(newStringDoc, true);
            //Put the old document back in the store
            this.bTree.put(x, oldStringDoc);
            //Add it to the trie
            trieAddition(oldStringDoc);
            //Update memory to add the old document
            addMemoryBack(oldStringDoc);
            //If the new document was in the disk, it was brought back so increase number of documents in the store
            if (this.diskURIs.contains(newStringDoc.getKey())) {
                this.documentInventory = this.documentInventory + 1;
            }
            //Add the old document back to the heap
            oldStringDoc.setLastUseTime(System.nanoTime());
            this.minHeap.insert(new MinHeapNode(oldStringDoc.getKey(), this.bTree));
            //Update space requirements
            clearUpDocuments();
            return true;
        };
        GenericCommand<URI> undoReplacePut = new GenericCommand<>(uri, replaceFunction);
        this.stack.push(undoReplacePut);
        return oldStringDoc.hashCode();
    }

    private void deleteMemory(DocumentImpl oldDocument, Boolean sameURI) {
        if (this.diskURIs.contains(oldDocument.getKey())) {
            return;
        }
        if (oldDocument.getDocumentTxt() == null) {
            int oldDocumentStorage = oldDocument.getDocumentBinaryData().length;
            if (!sameURI) {
                this.memoryMap.remove(oldDocument.getKey());
            }
            this.memoryStorage = this.memoryStorage - oldDocumentStorage;
        }
        else {
            int oldDocumentStorage = oldDocument.getDocumentTxt().getBytes().length;
            if (!sameURI) {
                this.memoryMap.remove(oldDocument.getKey());
            }
            this.memoryStorage = this.memoryStorage - oldDocumentStorage;
        }
    }

    private void addMemoryBack(DocumentImpl oldStringDoc) {
        if (oldStringDoc.getDocumentTxt() == null) {
            int oldDocumentStorage = oldStringDoc.getDocumentBinaryData().length;
            this.memoryMap.put(oldStringDoc.getKey(), oldDocumentStorage);
            this.memoryStorage = this.memoryStorage + oldDocumentStorage;
        }
        else {
            int oldDocumentStorage = oldStringDoc.getDocumentTxt().getBytes().length;
            this.memoryMap.put(oldStringDoc.getKey(), oldDocumentStorage);
            this.memoryStorage = this.memoryStorage + oldDocumentStorage;
        }
    }

    private void trieAddition(DocumentImpl doc){
        Set<String> allWords = doc.getWords();
        for(String s: allWords) {
            this.trie.put(s, doc.getKey());
        }
    }
    private void trieDeletion(DocumentImpl doc){
        Set<String> allWords = doc.getWords();
        for(String s: allWords) {
            this.trie.delete(s, doc.getKey());
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
        //Place the new binary document into the table
        DocumentImpl oldBinaryDoc = (DocumentImpl) this.bTree.put(uri, newBinaryDoc);
        //If an old binary document is being replaced, delete references to it in memory, document space, and heap
        if(oldBinaryDoc != null) {
            if (!this.diskURIs.contains(oldBinaryDoc.getKey())) {
                this.documentInventory = this.documentInventory - 1;
            }
            //Update storage of old binary document
            deleteMemory(oldBinaryDoc, true);
            try {
                //Remove from heap
                removeFromHeap(oldBinaryDoc);
            }
            catch (NoSuchElementException e) {
            }
        }
        //Update the memory map
        int documentStorage = bytes.length;
        this.memoryMap.put(newBinaryDoc.getKey(), documentStorage);
        this.memoryStorage = this.memoryStorage + documentStorage;
        //Update the last time using the document and put the new document in heap
        newBinaryDoc.setLastUseTime(System.nanoTime());
        this.minHeap.insert(new MinHeapNode(newBinaryDoc.getKey(), this.bTree));
        clearUpDocuments();
        //Create undo depending on if this is a new binary document or replacing an old one
        if (oldBinaryDoc == null) {
            return newBinaryPutUndo(uri, newBinaryDoc, documentStorage);
        }
        else {
            return replaceBinaryUndo(uri, newBinaryDoc, documentStorage, oldBinaryDoc);
        }
    }
    private int replaceBinaryUndo(URI uri, DocumentImpl newBinaryDoc, int documentStorage, DocumentImpl oldBinaryDoc) {
        //Add undo function to the stack to put back the old document associated with the URI
        //Update the old binary document's last use time and put it back in the heap
        Function<URI, Boolean> replaceFunction = (x) -> {
            this.bTree.put(x, oldBinaryDoc);
            //To undo the new document being deleted, remove it from heap and remove it from memory
            //Delete from the heap
            try {
                removeFromHeap(newBinaryDoc);
            }
            catch (NoSuchElementException e) {

            }
            deleteMemory(newBinaryDoc, true);
            //If the new document was in the disk, it was brought back so increase number of documents in the store
            if (this.diskURIs.contains(newBinaryDoc.getKey())) {
                this.documentInventory = this.documentInventory + 1;
            }
            //Update the memory of the old document being put back into the store
            this.memoryMap.put(oldBinaryDoc.getKey(), oldBinaryDoc.getDocumentBinaryData().length);
            this.memoryStorage = this.memoryStorage + oldBinaryDoc.getDocumentBinaryData().length;
            //Update the old binary document's last use time and put it back in the heap
            oldBinaryDoc.setLastUseTime(System.nanoTime());
            this.minHeap.insert(new MinHeapNode(oldBinaryDoc.getKey(), this.bTree));
            clearUpDocuments();
            return true;
        };
        GenericCommand<URI> undoReplacePut = new GenericCommand<>(uri, replaceFunction);
        this.stack.push(undoReplacePut);
        return oldBinaryDoc.hashCode();
    }

    private int newBinaryPutUndo(URI uri, DocumentImpl newBinaryDoc, int documentStorage) {
        //Add undo function to the stack to delete the new document being added to the store
        Function<URI, Boolean> deleteFunction = (x) -> {
            //Delete from the heap
            try {
                removeFromHeap(newBinaryDoc);
            }
            catch (NoSuchElementException e) {

            }
            this.bTree.put(x, null);
            if (!this.diskURIs.contains(newBinaryDoc.getKey())) {
                this.documentInventory = this.documentInventory - 1;
            }
            this.diskURIs.remove(newBinaryDoc.getKey());
            //Update the memory
            deleteMemory(newBinaryDoc, false);
            return true;
        };
        GenericCommand<URI> undoNewPut = new GenericCommand<>(uri, deleteFunction);
        this.stack.push(undoNewPut);
        return 0;
    }

    @Override
    public Document get(URI uri) {
        DocumentImpl getDocumentImpl;
        if (this.diskURIs.contains(uri)) {
            this.diskURIs.remove(uri);
            getDocumentImpl = (DocumentImpl) this.bTree.get(uri);
            this.documentInventory = this.documentInventory + 1;
            //Update memory
            addMemoryBack(getDocumentImpl);
            getDocumentImpl.setLastUseTime(System.nanoTime());
            this.minHeap.insert(new MinHeapNode(getDocumentImpl.getKey(), this.bTree));
        }
        Document getDocument = this.bTree.get(uri);
        if (getDocument != null){
            //Update last time using the document
            getDocument.setLastUseTime(System.nanoTime());
            this.minHeap.reHeapify(new MinHeapNode(getDocument.getKey(), this.bTree));
        }
        clearUpDocuments();
        return getDocument;
    }

    @Override
    public boolean delete(URI uri) {
        DocumentImpl preDeletedDoc = (DocumentImpl) this.bTree.get(uri);
        if (preDeletedDoc != null) {
            //Delete from the heap
            try {
                removeFromHeap(preDeletedDoc);
            }
            catch (NoSuchElementException e) {

            }
        }
        DocumentImpl deletedDoc = (DocumentImpl) this.bTree.put(uri, null);
        if (deletedDoc != null) {
            if (!this.diskURIs.contains(deletedDoc.getKey())) {
                this.documentInventory = this.documentInventory - 1;
            }
            //Update the memory map depending on its type of document
            deleteMemory(deletedDoc, false);
            //Add undo function to allow the document being deleted to be put back into the store in the future
            //To undo, put the document back in the store and add the words back into the trie
            Function<URI, Boolean> putBackFunction = undoPutFunction(deletedDoc);
            GenericCommand<URI> undoDelete = new GenericCommand<>(uri, putBackFunction);
            this.stack.push(undoDelete);
            //Delete all mentions of this document from the trie
            trieDeletion(deletedDoc);
            //If the URI was in the disk, remove from list
            this.diskURIs.remove(deletedDoc.getKey());
            return true;
        }
        return false;
    }

    @Override
    public void undo() throws IllegalStateException {
        if (this.stack.peek() != null) {
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
            if (commandSet.size() > 0) {
                this.stack.push(commandSet);
            }
        }
        //Put the temp stacks back on the stack
        while (tempStack.peek() != null) {
            this.stack.push(tempStack.pop());
        }
    }
    @Override
    public List<Document> search(String keyword) {
        Comparator<URI> comparator = new Comparator<URI>() {
            @Override
            public int compare(URI o1, URI o2) {
                if (bTree.get(o1).wordCount(keyword) < bTree.get(o2).wordCount(keyword)) {
                    return 1;
                }
                if (bTree.get(o1).wordCount(keyword) > bTree.get(o2).wordCount(keyword)) {
                    return -1;
                }
                return 0;
            }
        };
        List<URI> uris = this.trie.getAllSorted(keyword, comparator);
        List<Document> documents = new ArrayList<>();
        for (URI uri: uris) {
            documents.add(this.bTree.get(uri));
        }
        //Update last time using each of the documents
        long updateTime = System.nanoTime();
        for(Document d: documents) {
            if (this.diskURIs.contains(d.getKey())) {
                this.diskURIs.remove(d.getKey());
                DocumentImpl getDocumentImpl = (DocumentImpl) this.bTree.get(d.getKey());
                this.documentInventory = this.documentInventory + 1;
                //Update memory
                addMemoryBack(getDocumentImpl);
                getDocumentImpl.setLastUseTime(System.nanoTime());
                this.minHeap.insert(new MinHeapNode(getDocumentImpl.getKey(), this.bTree));
            }
            else {
                d.setLastUseTime(updateTime);
                this.minHeap.reHeapify(new MinHeapNode(d.getKey(), this.bTree));
            }
        }
        clearUpDocuments();
        return documents;
    }

    @Override
    public List<Document> searchByPrefix(String keywordPrefix) {
        Comparator<URI> comparator = searchByPrefixComparator(keywordPrefix);
        List<URI> uris = this.trie.getAllWithPrefixSorted(keywordPrefix, comparator);
        List<Document> documents = new ArrayList<>();
        for (URI uri: uris) {
            documents.add(this.bTree.get(uri));
        }
        //Update last time using each of the documents
        long updateTime = System.nanoTime();
        for(Document d: documents) {
            if (this.diskURIs.contains(d.getKey())) {
                this.diskURIs.remove(d.getKey());
                DocumentImpl getDocumentImpl = (DocumentImpl) this.bTree.get(d.getKey());
                this.documentInventory = this.documentInventory + 1;
                //Update memory
                addMemoryBack(getDocumentImpl);
                getDocumentImpl.setLastUseTime(System.nanoTime());
                this.minHeap.insert(new MinHeapNode(getDocumentImpl.getKey(), this.bTree));
            }
            else {
                d.setLastUseTime(updateTime);
                this.minHeap.reHeapify(new MinHeapNode(d.getKey(), this.bTree));
            }
        }
        clearUpDocuments();
        return documents;
    }
    private Comparator<URI> searchByPrefixComparator (String keywordPrefix) {
        Comparator<URI> comparator = new Comparator<URI>() {
            @Override
            public int compare(URI o1, URI o2) {
                //Find the number of instances of words that begin with the given prefix in document 1
                Set<String> set1 = bTree.get(o1).getWords();
                int count1 = 0;
                for(String s: set1) {
                    if(s.startsWith(keywordPrefix)) {
                        count1 = count1 + bTree.get(o1).wordCount(s);
                    }
                }
                //Find the number of instances of words that begin with the given prefix in document 2
                Set<String> set2 = bTree.get(o2).getWords();
                int count2 = 0;
                for(String s: set2) {
                    if(s.startsWith(keywordPrefix)) {
                        count2 = count2 + bTree.get(o2).wordCount(s);
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
        Set<URI> uris = this.trie.deleteAll(keyword);
        Set<Document> deletedDocuments = new HashSet<>();
        for (URI uri: uris) {
            deletedDocuments.add(this.bTree.get(uri));
        }
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
            if (!this.diskURIs.contains(d.getKey())) {
                this.documentInventory = this.documentInventory - 1;
            }
            //Delete all the words in each document that include the given keyword from the trie
            trieDeletion((DocumentImpl) d);
            //Delete from the heap
            try {
                removeFromHeap(d);
            }
            catch (NoSuchElementException e) {

            }
            //Update the memory map depending on its type of document
            deleteMemory((DocumentImpl) d, false);
            //If the URI was in the disk, remove from list
            this.diskURIs.remove(d.getKey());
            //Create undo functions
            Function<URI, Boolean> putBackFunction = createDeleteUndo(d);
            GenericCommand<URI> undoDelete = new GenericCommand<>(d.getKey(), putBackFunction);
            commandSet.addCommand(undoDelete);
            //Add the deleted documents to a set of URIs to return and then delete the doc from the store
            deletedURIs.add(d.getKey());
            this.bTree.put(d.getKey(), null);
        }
        this.stack.push(commandSet);
        return deletedURIs;
    }

    private Function<URI, Boolean> createDeleteUndo(Document d) {
        Function<URI, Boolean> putBackFunction = (x) ->
        {
            DocumentImpl implDeletedDoc = (DocumentImpl) d;
            this.bTree.put(x, implDeletedDoc);
            this.documentInventory = this.documentInventory + 1;
            //Add back into trie
            trieAddition(implDeletedDoc);
            //Update memory
            if (d.getDocumentTxt() == null) {
                int documentStorage = d.getDocumentBinaryData().length;
                this.memoryMap.put(d.getKey(), documentStorage);
                this.memoryStorage = this.memoryStorage + documentStorage;
            }
            else {
                int documentStorage = d.getDocumentTxt().getBytes().length;
                this.memoryMap.put(d.getKey(), documentStorage);
                this.memoryStorage = this.memoryStorage + documentStorage;
            }
            //Add back into heap
            d.setLastUseTime(System.nanoTime());
            this.minHeap.insert(new MinHeapNode(d.getKey(), this.bTree));
            //Update space/memory
            clearUpDocuments();
            return true;
        };
        return putBackFunction;
    }

    private Set<URI> deleteSingleDocument(Set<Document> deletedDocuments, Set<URI> deletedURIs) {
        for(Document d: deletedDocuments) {
            if (!this.diskURIs.contains(d.getKey())) {
                this.documentInventory = this.documentInventory - 1;
            }
            //Delete all the words in each document that include the given keyword from the trie
            trieDeletion((DocumentImpl) d);
            //Delete from the heap
            try {
                removeFromHeap(d);
            }
            catch (NoSuchElementException e) {

            }
            //Update the memory map depending on its type of document
            deleteMemory((DocumentImpl) d, false);
            //If the URI was in the disk, remove from list
            this.diskURIs.remove(d.getKey());
            //Create undo function
            Function<URI, Boolean> putBackFunction = createDeleteUndo(d);
            GenericCommand<URI> undoDelete = new GenericCommand<>(d.getKey(), putBackFunction);
            this.stack.push(undoDelete);
            //Add the deleted document to a set of URIs to return and then delete the doc from the store
            deletedURIs.add(d.getKey());
            this.bTree.put(d.getKey(), null);
        }
        return deletedURIs;
    }
    @Override
    public Set<URI> deleteAllWithPrefix(String keywordPrefix) {
        //First collect the documents that need to be deleted from the store
        Set<URI> uris = this.trie.deleteAllWithPrefix(keywordPrefix);
        Set<Document> deletedDocuments = new HashSet<>();
        for (URI uri: uris) {
            deletedDocuments.add(this.bTree.get(uri));
        }
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
                MinHeapNode minHeapNode = this.minHeap.remove();
                URI deletedDocURI = minHeapNode.uri;
                try {
                    this.bTree.moveToDisk(deletedDocURI);
                    this.diskURIs.add(deletedDocURI);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                this.documentInventory = this.documentInventory - 1;
                this.memoryStorage = this.memoryStorage - this.memoryMap.get(deletedDocURI);
                this.memoryMap.remove(deletedDocURI);
            }
        }
        //If storage is full then delete documents until there's enough memory
        if(this.maxDocumentBytes != null) {
            while(this.memoryStorage > this.maxDocumentBytes) {
                MinHeapNode minHeapNode = this.minHeap.remove();
                URI deletedDocURI = minHeapNode.uri;
                try {
                    this.bTree.moveToDisk(deletedDocURI);
                    this.diskURIs.add(deletedDocURI);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                this.documentInventory = this.documentInventory - 1;
                this.memoryStorage = this.memoryStorage - this.memoryMap.get(deletedDocURI);
                this.memoryMap.remove(deletedDocURI);
            }
        }
    }
    private void deleteUndo(URI uri) throws IllegalStateException {
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
        //URI has been found in the stack; Remove from stack and don't undo
        if (this.stack.peek() != null) {
            Undoable u = this.stack.pop();
        }
        //Put the temp stack values back into the main stack
        while (tempStack.peek() != null) {
            this.stack.push(tempStack.pop());
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
    private class MinHeapNode implements Comparable<MinHeapNode> {
        URI uri;
        BTreeImpl<URI, Document> nodeBTree;
        long lastUseTime; // the last use time of the URI being stored
        MinHeapNode(URI uri, BTreeImpl<URI, Document> bTree) {
            this.uri = uri;
            this.nodeBTree = bTree;
            lastUseTime = System.nanoTime();
        }
        private long getLastUseTime() {
            updateLastUseTime();
            return this.lastUseTime;
        }
        private void updateLastUseTime() {
            this.lastUseTime = this.nodeBTree.get(this.uri).getLastUseTime();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            MinHeapNode that = (MinHeapNode) o;
            return this.uri.toString().equals(that.uri.toString());
        }

        @Override
        public int compareTo(DocumentStoreImpl.MinHeapNode minHeapNode) {
            if (minHeapNode == null) {
                throw new NullPointerException("Can't compare a MinHeapNode to a null value");
            }
            updateLastUseTime();
            minHeapNode.updateLastUseTime();
            if (this.lastUseTime > minHeapNode.getLastUseTime()) {
                return 1;
            }
            if (this.lastUseTime < minHeapNode.getLastUseTime()) {
                return -1;
            }
            return 0;
        }
    }
}