package com.kosuri.stores.model.response;

import com.kosuri.stores.model.request.PharmasistRequest;
import lombok.Getter;
import lombok.Setter;
import java.util.*;
@Getter
@Setter
public class PharmasistResponse {

    private List<PharmasistRequest> pharmasistRequests;
}
