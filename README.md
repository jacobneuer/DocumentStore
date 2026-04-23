# Document Store

A Java document storage system built incrementally across five milestones, evolving from a basic in-memory store into a persistent search engine with undo support, memory limits, and disk-backed storage.

## Overview

The project stores both text and binary documents and exposes a `DocumentStore` abstraction that grows in capability over time. As the implementation matures, it adds richer indexing, better mutation control, memory-aware eviction, and persistence to disk.

Core ideas in the final version:

- hash-table backed document lookup
- undoable commands with stack-based history
- trie-based keyword and prefix search
- min-heap based least-recently-used eviction
- B-tree backed persistence and on-disk document storage

## Repository Layout

```text
DocumentStore/
├── document-store-evolution/
│   ├── hash-table-store/
│   ├── undoable-store/
│   ├── searchable-store/
│   ├── memory-managed-store/
│   └── persistent-document-store/
└── DataStructures/
    └── se-practice/
```

The main work is in `document-store-evolution`. The `DataStructures/se-practice` folder is leftover course material and is not part of the main Document Store implementation.

## Milestone Progression

- `hash-table-store`: introduces the base `Document` and `DocumentStore` model with hash-table backed storage.
- `undoable-store`: adds command-based undo support using a stack.
- `searchable-store`: adds trie-backed keyword and prefix search.
- `memory-managed-store`: adds memory limits and least-recently-used eviction with a min-heap.
- `persistent-document-store`: adds persistence to disk using a B-tree and a `DocumentPersistenceManager`.

## Data Structures Used

- `HashTable`: primary lookup by document key
- `Stack`: undo history
- `Trie`: full-word and prefix-based search
- `MinHeap`: eviction based on last use time
- `BTree`: storage abstraction for persistence-aware document management

## Final Architecture

In the final module, documents can move between memory and disk depending on configured limits. Search and access operations update recency, and persistence support allows the store to scale beyond in-memory capacity.

Important final-stage responsibilities:

- store text and binary documents
- index text for search
- support undo operations
- enforce document count and byte limits
- evict least-recently-used documents when limits are exceeded
- serialize documents to disk and restore them on demand

## Build And Test

Each milestone is an independent Maven module. For example, to build and test the final implementation:

```bash
cd document-store-evolution/persistent-document-store
mvn test
```

You can also work through the modules one at a time to see how the system evolves.

## Notes

The Java package names inside the source still use the original milestone naming scheme such as `edu.yu.cs.com1320.project.stage5`. The top-level folder names were cleaned up to better describe what each module actually accomplishes.

## Author

Jacob Neuer
