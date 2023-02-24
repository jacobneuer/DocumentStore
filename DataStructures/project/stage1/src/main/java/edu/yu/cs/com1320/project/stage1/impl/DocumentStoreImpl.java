package edu.yu.cs.com1320.project.stage1.impl;

import edu.yu.cs.com1320.project.stage1.Document;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class DocumentStoreImpl implements edu.yu.cs.com1320.project.stage1.DocumentStore{

    public DocumentStoreImpl() {
        
    }

    @Override
    public int put(InputStream input, URI uri, DocumentFormat format) throws IOException {
        return 0;
    }

    @Override
    public Document get(URI uri) {
        return null;
    }

    @Override
    public boolean delete(URI uri) {
        return false;
    }
}
