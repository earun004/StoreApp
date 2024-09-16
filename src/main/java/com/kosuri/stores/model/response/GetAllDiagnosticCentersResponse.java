package com.kosuri.stores.model.response;

import com.kosuri.stores.dao.DiagnosticServicesEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class GetAllDiagnosticCentersResponse extends GenericResponse{
    private List<DiagnosticServicesEntity> diagnosticCenters;
}
