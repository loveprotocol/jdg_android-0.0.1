package com.imhc2.plumboard.commons.functions;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 2018-09-18.
 */

public class FunctionEvents {
    com.google.firebase.functions.FirebaseFunctions functions = com.google.firebase.functions.FirebaseFunctions.getInstance("asia-northeast1");

    @NonNull
    public Task<Map<Object, Object>> emailCheckFun(String text) {
        // Create the arguments to the callable function, which is just one string
        Map<String, Object> data = new HashMap<>();
        data.put("email", text);
        return functions
                .getHttpsCallable("isSignUp")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, Map<Object, Object>>() {
                    @Override
                    public Map<Object, Object> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        return (Map<Object, Object>) task.getResult().getData();
                    }
                });
    }


    @NonNull
    public Task<Map<Object, Object>> signUpUsr(Map<String, String> data) {
        return functions
                .getHttpsCallable("signUpJudge")
                .call(data)
                .continueWith(task -> {
                    Map<Object, Object> result = (Map<Object, Object>) task.getResult().getData();
                    return result;
                });
    }



    @NonNull
    public Task<Map<Object, Object>> disabledAccount() {
        return functions
                .getHttpsCallable("requestDisableAccount")
                .call()
                .continueWith(new Continuation<HttpsCallableResult, Map<Object, Object>>() {
                    @Override
                    public Map<Object, Object> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        return (Map<Object, Object>) task.getResult().getData();
                    }
                });
    }

    @NonNull
    public Task<Map<Object, Object>> cancelDisabledAccount() {
        return functions
                .getHttpsCallable("requestCancelDisableAccount")
                .call()
                .continueWith(new Continuation<HttpsCallableResult, Map<Object, Object>>() {
                    @Override
                    public Map<Object, Object> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        return (Map<Object, Object>) task.getResult().getData();
                    }
                });
    }

    @NonNull
    public Task<Map<Object, Object>> findUserId(String uid) {
        Map<String, Object> data2 = new HashMap<>();
        data2.put("iamportUID",uid);
        return functions
                .getHttpsCallable("findJudgeAccount")
                .call(data2)
                .continueWith(new Continuation<HttpsCallableResult, Map<Object, Object>>() {
                    @Override
                    public Map<Object, Object> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        return (Map<Object, Object>) task.getResult().getData();
                    }
                });
    }

    @NonNull
    public Task<Map<Object, Object>> startCampFun(String campId) {
        Map<String, Object> data = new HashMap<>();
        data.put("campaignDocumentId", campId);
        return functions
                .getHttpsCallable("participateCampaign")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, Map<Object, Object>>() {
                    @Override
                    public Map<Object, Object> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        return (Map<Object, Object>) task.getResult().getData();
                    }
                });
    }

    @NonNull
    public Task<Map<Object, Object>> cancelCampFun(String campaignId) {
        Map<String, Object> data = new HashMap<>();
        data.put("campaignDocumentId", campaignId);
        return functions
                .getHttpsCallable("cancelParticipateCampaign")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, Map<Object, Object>>() {
                    @Override
                    public Map<Object, Object> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        return (Map<Object, Object>) task.getResult().getData();
                    }
                });
    }

    @NonNull
    public Task<Map<Object, Object>> endCampFun(String campId, float rating, List<Object> list, String prj) {
        // Create the arguments to the callable function, which is just one string
        Map<String, Object> data = new HashMap<>();
        data.put("campaignDocumentId", campId);
        data.put("projectDocumentId", prj);
        data.put("rate", rating);
        data.put("result", list);
        return functions
                .getHttpsCallable("completeCampaign")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, Map<Object, Object>>() {
                    @Override
                    public Map<Object, Object> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        return (Map<Object, Object>) task.getResult().getData();
                    }
                });
    }


    @NonNull
    public Task<Map<Object, Object>> requestWithdraw(int point,String bank, String account, String name) {
        Map<String, Object> data = new HashMap<>();
        data.put("point", point);
        data.put("bank", bank);
        data.put("accountHolder", name);
        data.put("account", account);
        return functions
                .getHttpsCallable("requestWithdraw")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, Map<Object, Object>>() {
                    @Override
                    public Map<Object, Object> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        return (Map<Object, Object>) task.getResult().getData();
                    }
                });
    }

    @NonNull
    public Task<Map<Object, Object>> cancelWithdraw(String docId) {
        Map<String, Object> data = new HashMap<>();
        data.put("withdrawDocumentId",docId);
        return functions
                .getHttpsCallable("withdrawDocumentId")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, Map<Object, Object>>() {
                    @Override
                    public Map<Object, Object> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        return (Map<Object, Object>) task.getResult().getData();
                    }
                });
    }


    @NonNull
    public Task<Map<Object, Object>> registerUserAuth(String uid) {
        Map<String, Object> data = new HashMap<>();
        data.put("iamportUID", uid);
        return functions
                .getHttpsCallable("registerJudgeAuth")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, Map<Object, Object>>() {
                    @Override
                    public Map<Object, Object> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        return (Map<Object, Object>) task.getResult().getData();
                    }
                });
    }

    @NonNull
    public Task<Map<Object, Object>> registerUserLocationAuth(String address) {
        Map<String, Object> data = new HashMap<>();
        data.put("postcode", address);
        return functions
                .getHttpsCallable("registerJudgeLocation")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, Map<Object, Object>>() {
                    @Override
                    public Map<Object, Object> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        return (Map<Object, Object>) task.getResult().getData();
                    }
                });
    }



    @NonNull
    public Task<Map<Object, Object>> recommendUser(String code) {
        Map<String, Object> data = new HashMap<>();
        data.put("code", code);
        return functions
                .getHttpsCallable("recommendUser")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, Map<Object, Object>>() {
                    @Override
                    public Map<Object, Object> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        return (Map<Object, Object>) task.getResult().getData();
                    }
                });
    }


    @NonNull
    public Task<Map<Object, Object>> completeTutorial(Integer tutNum,Float rate) {
        Map<String, Object> data = new HashMap<>();
        data.put("tutorial", tutNum);
        data.put("rate", rate);
        return functions
                .getHttpsCallable("completeTutorial")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, Map<Object, Object>>() {
                    @Override
                    public Map<Object, Object> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        return (Map<Object, Object>) task.getResult().getData();
                    }
                });
    }

}
