# Local RAG — LangChain4j + Spring Boot + ChromaDB (no API key)

No OpenAI/Azure key needed anywhere:
- **Embeddings**: `AllMiniLmL6V2EmbeddingModel` from `langchain4j-embeddings-all-minilm-l6-v2`
  runs fully in-process via a bundled ONNX model — pure local inference.
- **Vector store**: ChromaDB, run locally via Docker. Local Chroma needs no API key.

## 1. Start ChromaDB

```bash
docker run -d --name chroma -p 8000:8000 chromadb/chroma
```

## 2. Run the Spring Boot app

```bash
mvn clean install
mvn spring-boot:run
```

App starts on `http://localhost:8080`.

## 3. Store a document

```bash
curl -X POST http://localhost:8080/api/rag/store \
  -H "Content-Type: application/json" \
  -d '{"text": "Artificial Intelligence (AI) is one of the most transformative technologies of the modern era. AI enables machines to perform tasks that typically require human intelligence."}'
```

Response: `Stored 1 chunk(s) in ChromaDB.`

## 4. Ask a question

```bash
curl -X POST http://localhost:8080/api/rag/ask \
  -H "Content-Type: application/json" \
  -d '{"question": "what is artificial intelligence", "maxResults": 3}'
```

Response:
```json
{
  "relevantChunks": [
    "[score=0.812] Artificial Intelligence (AI) is one of the most transformative technologies..."
  ]
}
```

## How it works

1. `POST /store` → text is split into ~500-char overlapping chunks
   (`DocumentSplitters.recursive`) → each chunk embedded locally with
   AllMiniLM-L6-v2 → (embedding, chunk) pair stored in Chroma.
2. `POST /ask` → question embedded the same way → cosine-similarity
   search against Chroma → top-k matching chunks returned.

This gives you retrieval only (no LLM call to generate a final answer).
If you later want a generated answer instead of raw chunks, add a local
LLM (e.g. via Ollama + `langchain4j-ollama`, also no API key) and feed
the retrieved chunks into it as context.

## Notes on versions

This uses `langchain4j` **0.29.1**, where:
- `AllMiniLmL6V2EmbeddingModel` lives in `dev.langchain4j.model.embedding`
- `EmbeddingStore.findRelevant(embedding, maxResults, minScore)` is the
  similarity-search method

If you bump the LangChain4j version, these two APIs are the ones most
likely to have moved (newer versions moved local embedding models under
`dev.langchain4j.model.embedding.onnx.*` and introduced a
`store.search(EmbeddingSearchRequest...)` method instead of
`findRelevant`). Check the changelog for your chosen version if you
upgrade.
