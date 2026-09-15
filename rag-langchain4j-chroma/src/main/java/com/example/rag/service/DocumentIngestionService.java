package com.example.rag.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentIngestionService {

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final DocumentSplitter splitter;

    public DocumentIngestionService(EmbeddingModel embeddingModel,
                                     EmbeddingStore<TextSegment> embeddingStore) {
        this.embeddingModel = embeddingModel;
        this.embeddingStore = embeddingStore;
        // ~500 chars per chunk with 50 chars overlap between consecutive chunks
        this.splitter = DocumentSplitters.recursive(500, 50);
    }

    /**
     * Splits the given text into chunks, embeds each chunk locally,
     * and stores the (embedding, chunk) pairs in ChromaDB.
     *
     * @return number of chunks stored
     */
    public int storeText(String text) {
        Document document = Document.from(text);
        List<TextSegment> segments = splitter.split(document);

        for (TextSegment segment : segments) {
            Embedding embedding = embeddingModel.embed(segment.text()).content();
            embeddingStore.add(embedding, segment);
        }
        return segments.size();
    }
}
