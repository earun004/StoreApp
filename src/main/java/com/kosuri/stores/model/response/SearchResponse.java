package com.kosuri.stores.model.response;

import com.kosuri.stores.dao.PurchaseEntity;
import com.kosuri.stores.dao.StockEntity;
import com.kosuri.stores.model.search.PharmasistSearchResult;
import com.kosuri.stores.model.search.SearchResult;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SearchResponse extends GenericResponse {

    private List<SearchResult> searchResultList;

    private List<PharmasistSearchResult> pharmasistSearchResults;

    private List<PurchaseEntity> purchaseList;

    private List<StockEntity> stockList;
}

