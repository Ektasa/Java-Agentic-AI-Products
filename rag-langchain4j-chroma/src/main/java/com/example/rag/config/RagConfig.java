package com.example.rag.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RagConfig {

    @Value("${chroma.base-url:http://localhost:8000}")
    private String chromaBaseUrl;

    @Value("${chroma.collection-name:rag_documents}")
    private String collectionName;

    /**
     * Runs fully in-process using an ONNX model bundled inside the
     * langchain4j-embeddings-all-minilm-l6-v2 jar.
     * No network call, no API key, no external service required.
     */
    @Bean
    public EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }

    /**
     * Talks to a ChromaDB server over HTTP (run it locally via Docker,
     * see README). Chroma itself requires no API key for local use.
     */
    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return ChromaEmbeddingStore.builder()
                .baseUrl(chromaBaseUrl)
                .collectionName(collectionName)
                .build();
    }
}
