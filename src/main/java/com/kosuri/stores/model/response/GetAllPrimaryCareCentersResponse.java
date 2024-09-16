package com.kosuri.stores.model.response;

import com.kosuri.stores.dao.PrimaryCareEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class GetAllPrimaryCareCentersResponse extends GenericResponse{
    private List<PrimaryCareEntity> primaryCareCenters;
}
