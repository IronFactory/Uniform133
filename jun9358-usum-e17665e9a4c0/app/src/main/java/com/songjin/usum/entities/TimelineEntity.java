package com.songjin.usum.entities;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class TimelineEntity implements Parcelable {
    // FIXME: timeline_items => timelines
    public static final String COLLECTION_NAME = "timeline_items";

//    public static final String PROPERTY_UUID = BaasioEntity.PROPERTY_UUID;
    public static final String PROPERTY_ID = "timeline_id";
    public static final String PROPERTY_SCHOOL_ID = "timeline_school_id";
    public static final String PROPERTY_user_id = "timeline_user_id";
//    public static final String PROPERTY_CREATED = BaasioEntity.PROPERTY_CREATED;
    public static final String PROPERTY_CREATED = "timeline_created";
    public static final String PROPERTY_CONTENTS = "timeline_content";

//    public String uuid;
    public String id;
    public String user_id;
    public int school_id;
    public long created;
    public String contents;

    public static final Creator<TimelineEntity> CREATOR = new Creator<TimelineEntity>() {
        public TimelineEntity createFromParcel(Parcel in) {
            return new TimelineEntity(in);
        }

        public TimelineEntity[] newArray(int size) {
            return new TimelineEntity[size];
        }
    };

    public TimelineEntity() {

    }

    public TimelineEntity(Parcel in) {
        set(in.readBundle());
    }

    public TimelineEntity(JSONObject object) {
        set(object);
    }

    public void set(JSONObject object) {
        try {
            this.id = object.getString(PROPERTY_ID);
            if (object.getString(PROPERTY_user_id) != null) {
                this.user_id = object.getString(PROPERTY_user_id);
            }
            if (object.getInt(PROPERTY_SCHOOL_ID) != -1) {
                this.school_id = object.getInt(PROPERTY_SCHOOL_ID);
            }
            this.created = object.getLong(PROPERTY_CREATED);
            if (object.getString(PROPERTY_CONTENTS) != null) {
                this.contents = object.getString(PROPERTY_CONTENTS);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getuser_id() {
        return user_id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public int getSchool_id() {
        return school_id;
    }

    public void setSchoolId(int school_id) {
        this.school_id = school_id;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public void set(Bundle bundle) {
        this.id = bundle.getString(PROPERTY_ID);
        this.user_id = bundle.getString(PROPERTY_user_id);
        this.school_id = bundle.getInt(PROPERTY_SCHOOL_ID);
        this.created = bundle.getLong(PROPERTY_CREATED);
        this.contents = bundle.getString(PROPERTY_CONTENTS);
    }

//    public BaasioBaseEntity getBaasioBaseEntity() {
//        BaasioBaseEntity entity = new BaasioBaseEntity();
//        entity.setType(COLLECTION_NAME);
//        entity.setUuid(UUID.fromString(this.uuid));
//        entity.setProperty(PROPERTY_user_id, this.user_id);
//        entity.setProperty(PROPERTY_SCHOOL_ID, this.school_id);
//        entity.setProperty(PROPERTY_CREATED, this.created);
//        entity.setProperty(PROPERTY_CONTENTS, this.contents);
//
//        return entity;
//    }

//    public BaasioEntity getBaasioEntity() {
//        BaasioEntity entity = new BaasioEntity();
//        entity.setType(COLLECTION_NAME);
//        entity.setUuid(UUID.fromString(this.uuid));
//        entity.setProperty(PROPERTY_user_id, this.user_id);
//        entity.setProperty(PROPERTY_SCHOOL_ID, this.school_id);
//        entity.setProperty(PROPERTY_CREATED, this.created);
//        entity.setProperty(PROPERTY_CONTENTS, this.contents);
//
//        return entity;
//    }

    public boolean isSame(TimelineEntity timelineEntity) {
        if (!id.equals(timelineEntity.id))
            return false;
        if (!user_id.equals(timelineEntity.user_id))
            return false;
        if (school_id != timelineEntity.school_id)
            return false;
        if (created != timelineEntity.created)
            return false;
        if (!contents.equals(timelineEntity.contents))
            return false;
        return true;
    }


    public Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(PROPERTY_ID, this.id);
        bundle.putString(PROPERTY_user_id, this.user_id);
        bundle.putInt(PROPERTY_SCHOOL_ID, this.school_id);
        bundle.putLong(PROPERTY_CREATED, this.created);
        bundle.putString(PROPERTY_CONTENTS, this.contents);

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
