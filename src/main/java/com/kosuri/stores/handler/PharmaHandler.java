package com.kosuri.stores.handler;

import com.kosuri.stores.dao.PharmacistEntity;
import com.kosuri.stores.model.request.PharmasistRequest;
import com.kosuri.stores.model.response.SearchResponse;
import com.kosuri.stores.model.search.PharmasistSearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PharmaHandler {

    @Autowired
    private RepositoryHandler repositoryHandler;

    public boolean addPharmacist(PharmasistRequest request) throws Exception{
        if (!repositoryHandler.validatePharmacist(request)) {
            return false;
        }

        PharmacistEntity entity = getEntityFromPharmaRequest(request,false);
        boolean isPharmacistAdded;
        try{
            isPharmacistAdded = repositoryHandler.addPharmacist(entity);
        }catch (DataIntegrityViolationException e) {
            throw new Exception(e.getCause().getCause().getMessage());
        }
        return isPharmacistAdded;
    }



    public boolean updatePharmacist(PharmasistRequest request) throws Exception{

       boolean isPharmacistExists =   repositoryHandler.findPharmacistBasedOnContactNumberOrEmailAddress(
                request.getPharmaUserContact(),request.getPharmaUserEmail());
        boolean isPharmacistUpdated = false;
       if(isPharmacistExists){
           PharmacistEntity entity = getEntityFromPharmaRequest(request, true);
           isPharmacistUpdated = repositoryHandler.updatePharmacist(entity);
       }

        return isPharmacistUpdated;
    }

    public SearchResponse searchPharmacist(String mobileNumber,
                                           String emailAddress,
                                           String availableLocation) throws Exception{
        List<PharmacistEntity> pharmacistEntityList = repositoryHandler.
                findPharmacist(mobileNumber,emailAddress,availableLocation);
        return setSearchResponse(pharmacistEntityList);

    }

    private SearchResponse setSearchResponse(List<PharmacistEntity> pharmacistEntityList) {
        SearchResponse searchResponse = new SearchResponse();

        List<PharmasistSearchResult> pharmacistList = new ArrayList<>();
        PharmasistSearchResult pharmacistSearchResult = new PharmasistSearchResult();
        for (PharmacistEntity pharmacistEntity: pharmacistEntityList){
            pharmacistSearchResult.setName(pharmacistEntity.getPharmacistName());
            pharmacistSearchResult.setPharmaUserEmail(pharmacistEntity.getPharmacistEmailAddress());
            pharmacistSearchResult.setPharmaUserContact(pharmacistEntity.getPharmacistContact());
            pharmacistSearchResult.setExperience(pharmacistEntity.getPharmacistExperience());
            pharmacistSearchResult.setAvailableLocation(pharmacistEntity.getPharmacistAvailableLocation());
            pharmacistSearchResult.setEducation(pharmacistEntity.getPharmacistEducation());
            pharmacistSearchResult.setPciCertified(pharmacistEntity.getPharmacistPciCertified());
            pharmacistSearchResult.setPciExpiryDate(pharmacistEntity.getPharmacistPciExpiryDate());
        }
        pharmacistList.add(pharmacistSearchResult);
        searchResponse.setPharmasistSearchResults(pharmacistList);
        return searchResponse;
    }

    private PharmacistEntity getEntityFromPharmaRequest(PharmasistRequest request, boolean isUpdate) {
        PharmacistEntity pharmacistEntity = new PharmacistEntity();

        if (!isUpdate) {
            pharmacistEntity.setPharmacistId(request.getName() + "_" + OtpHandler.generateOTP(false));
        }
        pharmacistEntity.setPharmacistName(request.getName());
        pharmacistEntity.setPharmacistContact(request.getPharmaUserContact());
        pharmacistEntity.setPharmacistEmailAddress(request.getPharmaUserEmail());
        pharmacistEntity.setPharmacistEducation(request.getEducation());
        pharmacistEntity.setPharmacistExperience(request.getExperience());
        pharmacistEntity.setPharmacistPciCertified(request.getPciCertified());
        pharmacistEntity.setPharmacistPciExpiryDate(request.getPciExpiryDate());
        pharmacistEntity.setPharmacistAvailableLocation(request.getAvailableLocation());


        return pharmacistEntity;
    }
}
