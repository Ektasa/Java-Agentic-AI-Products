package com.example.rag.dto;

import java.util.List;

public class AskResponse {
    private List<String> relevantChunks;

    public AskResponse() {
    }

    public AskResponse(List<String> relevantChunks) {
        this.relevantChunks = relevantChunks;
    }

    public List<String> getRelevantChunks() {
        return relevantChunks;
    }

    public void setRelevantChunks(List<String> relevantChunks) {
        this.relevantChunks = relevantChunks;
    }
}
