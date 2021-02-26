package com.stockcomp.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RealTimePriceResponse {

    private Integer currentPrice;

    private Integer highPrice;

    private Integer lowPrice;

    private Integer openPrice;

    private Integer previousClosePrice;

    private Integer time;

    @JsonProperty("currentPrice")
    public int getCurrentPrice() {
        return currentPrice;
    }

    @JsonProperty("c")
    public void setCurrentPrice(int currentPrice) {
        this.currentPrice = currentPrice;
    }

    @JsonProperty("highPrice")
    public int getHighPrice() {
        return highPrice;
    }

    @JsonProperty("h")
    public void setHighPrice(int highPrice) {
        this.highPrice = highPrice;
    }

    @JsonProperty("lowPrice")
    public int getLowPrice() {
        return lowPrice;
    }

    @JsonProperty("l")
    public void setLowPrice(int lowPrice) {
        this.lowPrice = lowPrice;
    }

    @JsonProperty("openPrice")
    public int getOpenPrice() {
        return openPrice;
    }

    @JsonProperty("o")
    public void setOpenPrice(int openPrice) {
        this.openPrice = openPrice;
    }

    @JsonProperty("previousClosePrice")
    public int getPreviousClosePrice() {
        return previousClosePrice;
    }

    @JsonProperty("pc")
    public void setPreviousClosePrice(int previousClosePrice) {
        this.previousClosePrice = previousClosePrice;
    }

    @JsonProperty("time")
    public int getTime() {
        return time;
    }

    @JsonProperty("t")
    public void setTime(int time) {
        this.time = time;
    }
}
