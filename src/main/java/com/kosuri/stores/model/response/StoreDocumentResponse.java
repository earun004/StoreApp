package com.kosuri.stores.model.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@ToString
public class StoreDocumentResponse extends GenericResponse {

    byte[] storeDocuments;

}
