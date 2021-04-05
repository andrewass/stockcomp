package com.stockcomp.repository;

import com.stockcomp.document.InvestmentDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface InvestmentDocumentRepository extends ElasticsearchRepository<InvestmentDoc, String> {
}
