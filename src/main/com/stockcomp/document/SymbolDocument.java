package com.stockcomp.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "stock")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SymbolDocument {

    @Id
    private String id;

    @JsonProperty("symbol")
    @Field(type = FieldType.Text, name = "symbol")
    private String symbol;

    @JsonProperty("description")
    @Field(type = FieldType.Text, name = "description")
    private String description;

    public String getSymbol() {
        return symbol;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
