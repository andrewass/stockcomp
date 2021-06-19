package com.stockcomp.repository.document;

import com.stockcomp.document.SymbolDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SymbolDocumentRepository extends ElasticsearchRepository<SymbolDocument, String> {

    SymbolDocument findSymbolDocumentByDescription(String description);

    SymbolDocument findSymbolDocumentBySymbol(String symbol);
}
