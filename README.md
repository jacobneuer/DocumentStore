<h1> <Strong>Document Storage System</Strong></h1>

  <li><a href = "https://www.linkedin.com/in/jacob-neuer-671a081a8/">Link to visit my LinkedIn</a></li>
  
  <p><br>•	Built a Document Storage System with Java in Data Structures. It utilized various Data Structures we learned about in that course: Trie (searching), Stack (undo actions), Heap (last use time of a document), BTree (storage). The stored documents are instances of the <i>Document</i> and <i>DocumentImpl</i> classes and can be either text documents or documents of binary data. The <i>DocumentImpl</i> has multiple methods for returning specific and important information of the document like its wordCounts, the data stored, and the last time it was used. The <i>DocumentPersistenceManager</i> class manipulates the state of these Documents by either serializing documents to the disk or deserializing them to bring them back to memory, and even deleting them when a deleteMethod is called. This is utilized by the Btree to manage its space in memory so that if the number of documents exceed a set limit, then instead of storing the document it will store a refrence to it on disk. Documents can be brought to memory when they are searched for by the Trie since their last use time will get updated and other documents can then be placed on disk to make space for this searched document.<br></p>
