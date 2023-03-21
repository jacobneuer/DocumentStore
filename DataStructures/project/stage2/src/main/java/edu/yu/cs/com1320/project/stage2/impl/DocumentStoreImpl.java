package edu.yu.cs.com1320.project.stage2.impl;

import edu.yu.cs.com1320.project.Command;
import edu.yu.cs.com1320.project.Stack;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.stage2.Document;
import edu.yu.cs.com1320.project.stage2.DocumentStore;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

public class DocumentStoreImpl implements edu.yu.cs.com1320.project.stage2.DocumentStore {

    private HashTableImpl<URI, DocumentImpl> hashTable;
    private StackImpl<Command> stack;
    public DocumentStoreImpl() {
        this.hashTable = new HashTableImpl<>();
        this.stack = new StackImpl<>();
    }

    @Override
    public int put(InputStream input, URI uri, DocumentFormat format) throws IOException {
        if (uri == null || format == null) {
            throw new IllegalArgumentException("Can't Insert Document With Missing URI or Format");
        }
        //If input is null then we delete the given URI and its associated document
        if (input == null) {
            DocumentImpl deletedDoc = hashTable.put(uri, null);
            if (deletedDoc != null){
                //Add undo function to allow the document being deleted to be put back into the store in the future
                Function<URI, Boolean> putBackFunction = (x) ->
                {
                    this.hashTable.put(x, deletedDoc);
                    return true;
                };
                Command undoDelete = new Command(uri, putBackFunction);
                this.stack.push(undoDelete);
                return deletedDoc.hashCode();
            }
            //If there's no old document being returned then we didn't do anything there's nothing to undo
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
        try {
            text = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
        catch (Exception e) {
            throw new IOException("String Input Stream Not Given Correctly");
        }
        DocumentImpl newStringDoc = new DocumentImpl(uri, text);
        DocumentImpl oldStringDoc = hashTable.put(uri, newStringDoc);
        if (oldStringDoc == null) {
            //Add undo function to the stack to delete the new document being added to the store
            Function<URI, Boolean> deleteFunction = (x) -> {
                this.hashTable.put(x, null);
                return true;
            };
            Command undoNewPut = new Command(uri, deleteFunction);
            this.stack.push(undoNewPut);
            return 0;
        }
        else {
            //Add undo function to the stack to put back the old document associated with the URI
            Function<URI, Boolean> replaceFunction = (x) -> {
                this.hashTable.put(x, oldStringDoc);
                return true;
            };
            Command undoReplacePut = new Command(uri, replaceFunction);
            this.stack.push(undoReplacePut);
            return oldStringDoc.hashCode();
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
            Command undoNewPut = new Command(uri, deleteFunction);
            this.stack.push(undoNewPut);
            return 0;
        }
        else {
            //Add undo function to the stack to put back the old document associated with the URI
            Function<URI, Boolean> replaceFunction = (x) -> {
                this.hashTable.put(x, oldBinaryDoc);
                return true;
            };
            Command undoReplacePut = new Command(uri, replaceFunction);
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
        DocumentImpl v = hashTable.put(uri, null);
        if (v == null) {
            //If there's no old document being returned then we didn't do anything so there's nothing to undo
            return false;
        }
        //Else add undo function to allow the document being deleted to be put back into the store in the future
        Function<URI, Boolean> putBackFunction = (x) ->
        {
            this.hashTable.put(x, v);
            return true;
        };
        Command undoDelete = new Command(uri, putBackFunction);
        this.stack.push(undoDelete);
        return true;
    }

    @Override
    public void undo() throws IllegalStateException {
        if (this.stack.peek() != null){
            Command undoCommand = this.stack.pop();
            undoCommand.undo();
            return;
        }
        //No frames to undo
        throw new IllegalStateException("Can't undo with no frames in the stack");
    }

    @Override
    public void undo(URI uri) throws IllegalStateException {
        StackImpl<Command> tempStack = new StackImpl<>();
        //Search the stack to find the given URI while popping the frames into a temporary stack to not lose them
        while (this.stack.peek() != null && !this.stack.peek().getUri().equals(uri)) {
            tempStack.push(this.stack.pop());
        }
        //URI has been found in the stack; Undo the frame
        if (this.stack.peek() != null) {
            Command undoCommand = this.stack.pop();
            undoCommand.undo();
            while (tempStack.peek() != null){
                this.stack.push(tempStack.pop());
            }
            return;
        }
        //No URI was found in the frame so put the frames back in the stack and throw an exception
        while (tempStack.peek() != null){
            this.stack.push(tempStack.pop());
        }
        throw new IllegalStateException("No given URI exists in the frame to undo");
    }
}