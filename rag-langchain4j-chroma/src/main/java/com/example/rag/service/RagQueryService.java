package com.example.rag.service;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagQueryService {

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;

    public RagQueryService(EmbeddingModel embeddingModel,
                            EmbeddingStore<TextSegment> embeddingStore) {
        this.embeddingModel = embeddingModel;
        this.embeddingStore = embeddingStore;
    }

    /**
     * Embeds the question locally and does a similarity search against
     * the chunks previously stored in ChromaDB.
     */
    public List<String> findRelevantChunks(String question, int maxResults) {
        Embedding queryEmbedding = embeddingModel.embed(question).content();

        List<EmbeddingMatch<TextSegment>> matches =
                embeddingStore.findRelevant(queryEmbedding, maxResults, 0.0);

        return matches.stream()
                .map(match -> String.format("[score=%.3f] %s", match.score(), match.embedded().text()))
                .collect(Collectors.toList());
    }
}
