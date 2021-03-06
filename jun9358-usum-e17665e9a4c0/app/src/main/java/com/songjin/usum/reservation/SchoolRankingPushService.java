package com.songjin.usum.reservation;

import android.app.IntentService;
import android.content.Intent;

import com.securepreferences.SecurePreferences;
import com.songjin.usum.Global;
import com.songjin.usum.controllers.fragments.SettingFragment;
import com.songjin.usum.dtos.SchoolRanking;
import com.songjin.usum.entities.UserEntity;
import com.songjin.usum.gcm.PushManager;
import com.songjin.usum.managers.RequestManager;
import com.songjin.usum.managers.SchoolManager;

import java.util.ArrayList;

public class SchoolRankingPushService extends IntentService {
    private static final String TAG = "RankingPushService";
    private SchoolRankingCheckThread schoolRankingCheckThread;

    public SchoolRankingPushService() {
        super("SchoolRankingPushService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        schoolRankingCheckThread = new SchoolRankingCheckThread();
        schoolRankingCheckThread.run();
    }

    private class SchoolRankingCheckThread extends Thread {
        public void run() {
            while (true) {
                try {
                    checkSchoolRankUpdated();
                    sleep(1000 * 10); // 10초마다
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void checkSchoolRankUpdated() {
        if (Global.userEntity == null || Global.userEntity.schoolId == 0)
            return;

        SecurePreferences securePrefs = new SecurePreferences(getApplicationContext());
        boolean check = securePrefs.getBoolean(SettingFragment.PREFERENCE_USE_TIMELINE_PUSH, true);

        if (!check)
            return;

        SchoolManager schoolManager = new SchoolManager(getApplicationContext());
        RequestManager.getMySchoolRanking(new RequestManager.OnGetMySchoolRanking() {
            @Override
            public void onSuccess(int rank) {
                int lastRank = SettingFragment.getLastSchoolRank();
                int currentRank = rank;
                if (lastRank == -1 || currentRank == -1) {
                    SettingFragment.setLastSchoolRank(currentRank);
                } else if (lastRank != currentRank) {
                    PushManager.sendSchoolRankUpdatedPushToMe("학교 순위가 " + lastRank + "위에서 " + currentRank + "위로 변경되었습니다!");
                    SettingFragment.setLastSchoolRank(currentRank);
                }
            }

            @Override
            public void onException() {
            }
        }, Global.userEntity.schoolId);

    }

    private int getMyRank(ArrayList<SchoolRanking> schoolRankings) {
        UserEntity userEntity = Global.userEntity;

        int myRanking = -1;
        for (int i = 0; i < schoolRankings.size(); i++) {
            SchoolRanking schoolRanking = schoolRankings.get(i);

            if (userEntity.schoolId == schoolRanking.school_id) {
                myRanking = i + 1;
                break;
            }
        }

        return myRanking;
    }
}