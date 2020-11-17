package com.group8.odin;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

public interface GetServerTime {
    void onSuccess(long timestamp);
    void onFailed();
}

public void getServerCurrentTime(final GetServerTime onComplete) {
    FirebaseFunctions.getInstance().getHttpsCallable("getTime")
            .call().addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
        @Override
        public void onComplete(@NonNull Task<HttpsCallableResult> task) {
            if(task.isSuccessful()){
                long timestamp = (long) task.getResult().getData();
                if(onComplete!=null){
                    onComplete.onSuccess(timestamp);
                }
            } else {
                onComplete.onFailed();
            }
        }
    });
}
