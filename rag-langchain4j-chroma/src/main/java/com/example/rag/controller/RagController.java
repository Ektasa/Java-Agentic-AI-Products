package com.example.rag.controller;

import com.example.rag.dto.AskRequest;
import com.example.rag.dto.AskResponse;
import com.example.rag.dto.StoreRequest;
import com.example.rag.service.DocumentIngestionService;
import com.example.rag.service.RagQueryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rag")
@CrossOrigin
public class RagController {

    private final DocumentIngestionService ingestionService;
    private final RagQueryService queryService;

    public RagController(DocumentIngestionService ingestionService,
                          RagQueryService queryService) {
        this.ingestionService = ingestionService;
        this.queryService = queryService;
    }

    // POST /api/rag/store  { "text": "..." }
    @PostMapping("/store")
    public String storeDocument(@RequestBody StoreRequest request) {
        int chunkCount = ingestionService.storeText(request.getText());
        return "Stored " + chunkCount + " chunk(s) in ChromaDB.";
    }

    // POST /api/rag/ask  { "question": "...", "maxResults": 3 }
    @PostMapping("/ask")
    public AskResponse ask(@RequestBody AskRequest request) {
        int k = request.getMaxResults() > 0 ? request.getMaxResults() : 3;
        return new AskResponse(queryService.findRelevantChunks(request.getQuestion(), k));
    }
}
