package com.imhc2.plumboard.commons.eventbus;

import com.imhc2.plumboard.mvvm.model.domain.CampExe;

import java.util.List;
import java.util.Map;

/**
 * Created by user on 2018-09-04.
 */

public class Events {
    public static final class FilterEvent{}
    public static final class Gallery{
        private String imgUrl;

        public Gallery(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public String getImgUrl() {
            return imgUrl;
        }
    }
    public static final class ListChange{
        boolean campaignKind;

        public ListChange(boolean campaignKind) {
            this.campaignKind = campaignKind;
        }

        public boolean isCampaignKind() {
            return campaignKind;
        }
    }
    public static final class HomeToActivityPage{}
    public static final class ActivityToPoint{}
    public static final class HistoryPosition{
        private int position;

        public HistoryPosition(int position) {
            this.position = position;
        }

        public int getPosition() {
            return position;
        }

    }
    public static final class Bank{
        private String bankName;

        public Bank(String bankName) {
            this.bankName = bankName;
        }

        public String getBankName() {
            return bankName;
        }
    }
    public static final class ScrollDown{}
    public static final class CampaignScrollUp{}
    public static final class CardResult{
        Object cardData;

        public CardResult(Object cardData) {
            this.cardData = cardData;
        }

        public Object getCardData() {
            return cardData;
        }
    }
    public static final class HistoryData{
        List<String> titles;
        List<String> types;
        int position;
        CampExe campExe;
        Map<Integer,Object> resultMap;

        public HistoryData(List<String> titles, List<String> types, int position, CampExe campExe, Map<Integer, Object> resultMap) {
            this.titles = titles;
            this.types = types;
            this.position = position;
            this.campExe = campExe;
            this.resultMap = resultMap;
        }

        public List<String> getTitles() {
            return titles;
        }

        public List<String> getTypes() {
            return types;
        }

        public int getPosition() {
            return position;
        }

        public CampExe getCampExe() {
            return campExe;
        }

        public Map<Integer, Object> getResultMap() {
            return resultMap;
        }
    }
}
