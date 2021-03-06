package com.songjin.usum.dtos;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class KakaoProfile implements Parcelable {
    public static final String PROPERTY_ID = "kakao_id";
    public static final String PROPERTY_NICKNAME = "kakao_nickname";
    public static final String PROPERTY_PROFILEIMAGE = "kakao_profile_image";
    public static final String PROPERTY_PROPERTIES = "kakao_properties";
    public static final String PROPERTY_THUMBNAIL_IMAGE = "kakao_thumbnail_image";

    public int id;
    public String nickname;
    public String profileImage;
    public String thumbnailImage;

    public static final Creator<KakaoProfile> CREATOR = new Creator<KakaoProfile>() {
        public KakaoProfile createFromParcel(Parcel in) {
            return new KakaoProfile(in);
        }

        public KakaoProfile[] newArray(int size) {
            return new KakaoProfile[size];
        }
    };

    public KakaoProfile(Parcel in) {
        set(in.readBundle());
    }

    public KakaoProfile(JSONObject jsonObject) {
        set(jsonObject);
    }

    public KakaoProfile() {
        id = 0;
        nickname = null;
        profileImage = null;
        thumbnailImage = null;
    }

    public void set(Bundle bundle) {
        this.id = bundle.getInt(PROPERTY_ID);
        this.nickname = bundle.getString(PROPERTY_NICKNAME);
        this.profileImage = bundle.getString(PROPERTY_PROFILEIMAGE);
        this.thumbnailImage = bundle.getString(PROPERTY_THUMBNAIL_IMAGE);
    }

    public void set(JSONObject jsonObject) {
        try {
            this.id = jsonObject.getInt(PROPERTY_ID);
            this.nickname = jsonObject.getString(PROPERTY_NICKNAME);
            this.profileImage = jsonObject.getString(PROPERTY_PROFILEIMAGE);
            this.thumbnailImage = jsonObject.getString(PROPERTY_THUMBNAIL_IMAGE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt(PROPERTY_ID, this.id);
        bundle.putString(PROPERTY_NICKNAME, this.nickname);
        bundle.putString(PROPERTY_PROFILEIMAGE, this.profileImage);
        bundle.putString(PROPERTY_THUMBNAIL_IMAGE, this.thumbnailImage);

        return bundle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBundle(getBundle());
    }
}
