package com.imhc2.plumboard.mvvm.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.imhc2.plumboard.mvvm.model.CampaignRepositry;
import com.imhc2.plumboard.mvvm.model.domain.CampExe;
import com.imhc2.plumboard.mvvm.model.domain.enums.HomeCampaignType;

import java.util.List;

/**
 * Created by user on 2018-09-05.
 */

public class CampaignMainViewModel extends ViewModel {
    private MutableLiveData<List<CampExe>> campaigns;
    private CampaignRepositry repositry;

    public CampaignMainViewModel() {
        repositry = new CampaignRepositry();
    }

    public MutableLiveData<List<CampExe>> getCampaigns(HomeCampaignType type) {
        if (campaigns == null) {
            campaigns = new MutableLiveData<>();
        }

        switch (type) {
            case NOTAUTHNOTLOC:
                campaigns = repositry.userNotAuthNotLocGetCampaignExe();
                break;
            case AUTHNOTLOC:
                campaigns = repositry.userAuthNotLocGetCampaignExe();
                break;
            case NOTAUTHLOC:
                campaigns = repositry.userNotAuthLocGetCampaignExe();
                break;
            case AUTHLOC:
                campaigns = repositry.userAuthLocGetCampaignExe();
                break;
        }
        return campaigns;
    }

}
