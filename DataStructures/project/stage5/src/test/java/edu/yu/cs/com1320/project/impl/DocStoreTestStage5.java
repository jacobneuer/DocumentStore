package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.DocumentStore;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage5.impl.DocumentStoreImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DocStoreTestStage5 {
    DocumentStoreImpl documentStore;
    MinHeapImpl<Document> minHeap;
    @BeforeEach
    public void setUp() {
        this.documentStore = new DocumentStoreImpl();
        this.minHeap = new MinHeapImpl<>();
    }

    public static URI create(String str) {
        try {
            return new URI(str);
        }
        catch (URISyntaxException x) {
            throw new IllegalArgumentException(x.getMessage(), x);
        }
    }
    @DisplayName("Test That File That's Booted Due to Memory is Stored on File")
    @Test
    public void one() throws IOException {
        String docText = "I love Torah and I love Mitzvot";
        InputStream targetStream = new ByteArrayInputStream(docText.getBytes());
        URI uri = create("DocumentURI");
        DocumentImpl doc = new DocumentImpl(uri, docText, null);
        documentStore.put(targetStream, uri, DocumentStore.DocumentFormat.TXT);
        String docText2 = "I also love cookies and cake";
        InputStream targetStream2 = new ByteArrayInputStream(docText2.getBytes());
        URI uri2 = create("DocumentURI2");
        DocumentImpl doc2 = new DocumentImpl(uri2, docText2, null);
        documentStore.put(targetStream2, uri2, DocumentStore.DocumentFormat.TXT);
        String docText3 = "love love love love";
        InputStream targetStream3 = new ByteArrayInputStream(docText3.getBytes());
        URI uri3 = create("DocumentURI3");
        DocumentImpl doc3 = new DocumentImpl(uri3, docText3, null);
        documentStore.put(targetStream3, uri3, DocumentStore.DocumentFormat.TXT);
        documentStore.setMaxDocumentCount(2);
        documentStore.get(uri);
    }
}
