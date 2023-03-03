package edu.yu.cs.com1320.project.stage2.impl;

import java.net.URI;
import java.util.Arrays;

public class DocumentImpl implements edu.yu.cs.com1320.project.stage2.Document {

    private URI uri;
    private String documentText;
    private byte[] byteArray;

    public DocumentImpl(URI uri, String txt){
        if (uri == null || txt == null || txt.equals("")){
            throw new IllegalArgumentException("URI or Text is Null");
        }
        this.uri = uri;
        this.documentText = txt;
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
}