package edu.yu.cs.com1320.project.stage3.impl;

import com.sun.jdi.Value;
import edu.yu.cs.com1320.project.CommandSet;
import edu.yu.cs.com1320.project.GenericCommand;
import edu.yu.cs.com1320.project.Undoable;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage3.Document;
import edu.yu.cs.com1320.project.stage3.DocumentStore;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class DocumentStoreImpl implements edu.yu.cs.com1320.project.stage3.DocumentStore {

    private HashTableImpl<URI, DocumentImpl> hashTable;
    private StackImpl<Undoable> stack;
    private TrieImpl<Document> trie;
    public DocumentStoreImpl() {
        this.hashTable = new HashTableImpl<>();
        this.stack = new StackImpl<>();
        this.trie = new TrieImpl<>();
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
        //Else put new document into the store depending on its type
        if (format == DocumentStore.DocumentFormat.TXT) {
            return putText(input, uri);
        }
        else {
            return putBinary(input, uri);
        }
    }
    private int putDelete(URI uri) {
        DocumentImpl deletedDoc = hashTable.put(uri, null);
        if (deletedDoc != null) {
            //Add undo function to allow the document being deleted to be put back into the store in the future
            //To undo, put the document back in the store and add the words back into the trie
            Function<URI, Boolean> putBackFunction = (x) ->
            {
                this.hashTable.put(x, deletedDoc);
                trieAddition(deletedDoc);
                return true;
            };
            GenericCommand<URI> undoDelete = new GenericCommand<>(uri, putBackFunction);
            this.stack.push(undoDelete);
            //Delete all mentions of this document from the trie
            trieDeletion(deletedDoc);
            return deletedDoc.hashCode();
        }
        //If there's no old document being returned then we didn't do anything so there's nothing to undo
        return 0;
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
        //Update the trie
        trieAddition(newStringDoc);
        //Put the new document into the store
        DocumentImpl oldStringDoc = hashTable.put(uri, newStringDoc);
        if (oldStringDoc == null) {
            return newPutUndo(uri, newStringDoc);
        }
        else {
            return replacePutUndo(uri, newStringDoc, oldStringDoc);
        }
    }
    private int newPutUndo (URI uri, DocumentImpl newStringDoc){
        Function<URI, Boolean> deleteFunction = (x) -> {
            this.hashTable.put(x, null);
            trieDeletion(newStringDoc);
            return true;
        };
        GenericCommand<URI> undoNewPut = new GenericCommand<>(uri, deleteFunction);
        this.stack.push(undoNewPut);
        return 0;
    }
    private int replacePutUndo (URI uri, DocumentImpl newStringDoc, DocumentImpl oldStringDoc) {
        //Add undo function to the stack to put back the old document associated with the URI
        //Undo deletes all the new words added to the trie and puts back the old words deleted
        Function<URI, Boolean> replaceFunction = (x) -> {
            this.hashTable.put(x, oldStringDoc);
            trieAddition(oldStringDoc);
            trieDeletion(newStringDoc);
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
        DocumentImpl oldBinaryDoc = hashTable.put(uri, newBinaryDoc);
        if (oldBinaryDoc == null) {
            //Add undo function to the stack to delete the new document being added to the store
            Function<URI, Boolean> deleteFunction = (x) -> {
                this.hashTable.put(x, null);
                return true;
            };
            GenericCommand<URI> undoNewPut = new GenericCommand<>(uri, deleteFunction);
            this.stack.push(undoNewPut);
            return 0;
        }
        else {
            //Add undo function to the stack to put back the old document associated with the URI
            Function<URI, Boolean> replaceFunction = (x) -> {
                this.hashTable.put(x, oldBinaryDoc);
                return true;
            };
            GenericCommand<URI> undoReplacePut = new GenericCommand<>(uri, replaceFunction);
            this.stack.push(undoReplacePut);
            return oldBinaryDoc.hashCode();
        }
    }
    @Override
    public Document get(URI uri) {
        return hashTable.get(uri);
    }

    @Override
    public boolean delete(URI uri) {
        DocumentImpl deletedDoc = hashTable.put(uri, null);
        if (deletedDoc == null) {
            //If there's no old document being returned then we didn't do anything so there's nothing to undo
            return false;
        }
        //Else add undo function to allow the document being deleted to be put back into the store in the future
        Function<URI, Boolean> putBackFunction = (x) ->
        {
            this.hashTable.put(x, deletedDoc);
            trieAddition(deletedDoc);
            return true;
        };
        GenericCommand<URI> undoDelete = new GenericCommand<>(uri, putBackFunction);
        this.stack.push(undoDelete);
        //Delete all mentions of the document from the trie
        trieDeletion(deletedDoc);
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
        return documents;
    }

    @Override
    public List<Document> searchByPrefix(String keywordPrefix) {
        Comparator<Document> comparator = searchByPrefixComparator(keywordPrefix);
        List<Document> documents = this.trie.getAllWithPrefixSorted(keywordPrefix, comparator);
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
        //Delete all the documents that include the given keyword from the trie
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
            Function<URI, Boolean> putBackFunction = (x) ->
            {
                DocumentImpl implDeletedDoc = (DocumentImpl) d;
                this.hashTable.put(x, implDeletedDoc);
                trieAddition(implDeletedDoc);
                return true;
            };
            GenericCommand<URI> undoDelete = new GenericCommand<>(d.getKey(), putBackFunction);
            commandSet.addCommand(undoDelete);
            //Add the deleted documents to a set of URIs to return and then delete the doc from the store
            deletedURIs.add(d.getKey());
            delete(d.getKey());
        }
        this.stack.push(commandSet);
        return deletedURIs;
    }
    private Set<URI> deleteSingleDocument(Set<Document> deletedDocuments, Set<URI> deletedURIs) {
        for(Document d: deletedDocuments) {
            Function<URI, Boolean> putBackFunction = (x) ->
            {
                DocumentImpl implDeletedDoc = (DocumentImpl) d;
                this.hashTable.put(x, implDeletedDoc);
                trieAddition(implDeletedDoc);
                return true;
            };
            GenericCommand<URI> undoDelete = new GenericCommand<>(d.getKey(), putBackFunction);
            this.stack.push(undoDelete);
            //Add the deleted document to a set of URIs to return and then delete the doc from the store
            deletedURIs.add(d.getKey());
            delete(d.getKey());
        }
        return deletedURIs;
    }
    @Override
    public Set<URI> deleteAllWithPrefix(String keywordPrefix) {
        Set<Document> deletedDocuments = this.trie.deleteAllWithPrefix(keywordPrefix);
        Set<URI> deletedURIs = new HashSet<>();
        if(deletedDocuments.size() > 1) {
            return deleteMultipleDocuments(deletedDocuments, deletedURIs);
        }
        else {
            return deleteSingleDocument(deletedDocuments, deletedURIs);
        }
    }
}