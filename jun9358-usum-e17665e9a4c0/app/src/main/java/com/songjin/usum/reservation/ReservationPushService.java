package com.songjin.usum.reservation;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.songjin.usum.Global;
import com.songjin.usum.HashBiMap;
import com.songjin.usum.constants.Category;
import com.songjin.usum.constants.Sex;
import com.songjin.usum.controllers.fragments.SettingFragment;
import com.songjin.usum.dtos.ProductCardDto;
import com.songjin.usum.entities.ProductEntity;
import com.songjin.usum.entities.ReservedCategoryEntity;
import com.songjin.usum.entities.UserEntity;
import com.songjin.usum.gcm.PushManager;
import com.songjin.usum.managers.RequestManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReservationPushService extends IntentService {


    private static final String TAG = "ReservationPushService";
    private ReservationCheckThread reservationCheckThread;
    private int objectIndex = 0;
    private JSONArray array;

    private UserEntity userEntity;

    private RequestManager.OnGetProduct onGetProduct = new RequestManager.OnGetProduct() {
        @Override
        public void onSuccess(ArrayList<ProductCardDto> productCardDtos) {
            Log.d(TAG, "getProduct");
            HashBiMap<Integer, String> categories = Category.getHashBiMap(Sex.ALL);
            for (ProductCardDto productCardDto : productCardDtos) {
                if (productCardDto.productEntity.user_id.equals(userEntity.id)) {
                    continue;
                }

                String msg = "";
                msg += categories.get(productCardDto.productEntity.category);
                msg += "에 해당하는 상품이 등록되었습니다.";
                PushManager.sendReservationPushToMe(msg);

                SettingFragment.updateReservedCategoryTimestamp(
                        productCardDto.productEntity.school_id,
                        productCardDto.productEntity.category
                );
            }

            if (array.length() > ++objectIndex) {
                try {
                    JSONObject object = array.getJSONObject(objectIndex);
                    RequestManager.getProduct(object, onGetProduct);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onException() {

        }
    };

    public ReservationPushService() {
        super("ReservationPushService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        reservationCheckThread = new ReservationCheckThread();
        reservationCheckThread.run();
    }

    private class ReservationCheckThread extends Thread {
        public void run() {
            while (true) {
                try {
                    checkRegisteredNewProduct();
                    sleep(60 * 1000);   // 60초마다
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void checkRegisteredNewProduct() {
        try {
            Log.d(TAG, "checkRegisteredNewProduct");
            ArrayList<ReservedCategoryEntity> reservedCategories = SettingFragment.getReservedCategories();
            if (reservedCategories == null || reservedCategories.size() == 0) {
                return;
            }

            if (userEntity == null || userEntity.id == null) {
                SharedPreferences preferences = getSharedPreferences(Global.APP_NAME, MODE_PRIVATE);
                String json = preferences.getString(Global.USER, null);
                if (json != null) {
                    Gson gson = new Gson();
                    userEntity = gson.fromJson(json, UserEntity.class);
                }
            }
            array = new JSONArray();
            for (ReservedCategoryEntity reservedCategory : reservedCategories) {
                JSONObject object = new JSONObject();
                object.put(ProductEntity.PROPERTY_SCHOOL_ID, reservedCategory.schoolId);
                object.put(ProductEntity.PROPERTY_CATEGORY, reservedCategory.category);
                object.put(ProductEntity.PROPERTY_CREATED, reservedCategory.lastCheckedTimestamp);
                array.put(object);
            }

            objectIndex = 0;
            final JSONObject object = array.getJSONObject(objectIndex);

            RequestManager.getProduct(object, onGetProduct);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
