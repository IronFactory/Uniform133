package com.ironfactory.donation.socketIo;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.ironfactory.donation.Global;
import com.ironfactory.donation.controllers.activities.AddProductsActivity;
import com.ironfactory.donation.controllers.activities.EditProfileActivity;
import com.ironfactory.donation.controllers.activities.LoginActivity;
import com.ironfactory.donation.controllers.activities.MainActivity;
import com.ironfactory.donation.controllers.activities.ProductDetailActivity;
import com.ironfactory.donation.controllers.activities.SignUpActivity;
import com.ironfactory.donation.controllers.activities.TimelineActivity;
import com.ironfactory.donation.controllers.activities.TimelineDetailActivity;
import com.ironfactory.donation.controllers.activities.TimelineWriteActivity;
import com.ironfactory.donation.dtos.ProductCardDto;
import com.ironfactory.donation.dtos.SchoolRanking;
import com.ironfactory.donation.dtos.TimelineCardDto;
import com.ironfactory.donation.dtos.TimelineCommentCardDto;
import com.ironfactory.donation.entities.FileEntity;
import com.ironfactory.donation.entities.LikeEntity;
import com.ironfactory.donation.entities.ProductEntity;
import com.ironfactory.donation.entities.SchoolEntity;
import com.ironfactory.donation.entities.TransactionEntity;
import com.ironfactory.donation.entities.UserEntity;
import com.ironfactory.donation.reservation.ReservationPushService;
import com.ironfactory.donation.reservation.SchoolRankingPushService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by ironFactory on 2015-08-03.
 */
public class SocketIO {

    private Handler handler = new Handler();
    private static final String URL = "http://uniform-test.herokuapp.com";
    private static final String TAG = "SocketIO";

    public static Socket socket;
    private Context context;

    public SocketIO(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        Log.d(TAG, "init");
        try {
            socket = IO.socket(URL);
        } catch (Exception e) {
            Log.e(TAG, "init 에러 = " + e.getMessage());
        }

        if (socket != null) {
            socketConnect();
        }
    }


    public void setListener() {
        Log.d(TAG, "setListener");
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                // 연결
                Log.d(TAG, "소켓 연결");
            }
        }).on(Global.LOGIN, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                // 로그인
                JSONObject object = (JSONObject) args[0];
                processLogin(object);
            }
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                // 연결 끊김
                Log.d(TAG, "소켓 연결 끊김");
                socketConnect();
            }
        }).on(Global.SIGN_UP, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                processSingUp(object);
            }
        }).on(Global.SIGN_IN, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                processSignIn(object);
            }
        }).on(Global.GET_SCHOOL_RANKING, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                processGetSchoolRanking(object);
            }
        }).on(Global.UPDATE_USER_PROFILE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                processUpdateUserProfile(object);
            }
        }).on(Global.INSERT_TIMELINE_COMMENT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                processInsertTimelineComment(object);
            }
        }).on(Global.GET_TIMELINE_COMMENT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                processGetTimelineComment(object);
            }
        }).on(Global.GET_ALL_TIMELINE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                processGetAllTimeline(object);
            }
        }).on(Global.GET_MY_TIMELINE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                processGetMyTimeline(object);
            }
        }).on(Global.UPDATE_TIMELINE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                processUpdateTimeline(object);
            }
        }).on(Global.INSERT_PRODUCT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                processInsertProduct(object);
            }
        }).on(Global.SEARCH_PRODUCT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                processSearchProduct(object);
            }
        }).on(Global.GET_MY_PRODUCT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                processGetMyProduct(object);
            }
        }).on(Global.DELETE_COMMENT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                processDeleteComment(object);
            }
        }).on(Global.UPDATE_TRANSACTION_STATUS, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                processUpdateTransactionStatus(object);
            }
        }).on(Global.DELETE_PRODUCT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                processDeleteProduct(object);
            }
        }).on(Global.DELETE_FILE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                processDeleteFile(object);
            }
        }).on(Global.UPDATE_PRODUCT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                processUpdateProduct(object);
            }
        }).on(Global.DELETE_TIMELINE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                processDeleteTimeline(object);
            }
        }).on(Global.DELETE_LIKE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                processDeleteLike(object);
            }
        }).on(Global.INSERT_LIKE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                processInsertLike(object);
            }
        }).on(Global.GET_PRODUCT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                processGetProduct(object);
            }
        }).on(Global.SIGN_IN_KAKAO, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                processSignInKakao(object);
            }
        }).on(Global.GET_SCHOOL, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                processGetSchool(object);
            }
        }).on(Global.INSERT_TIMELINE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                processInsertTimeline(object);
            }
        }).on(Global.INSERT_FILE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                processInsertFile(object);
            }
        });
    }


    // TODO: 15. 12. 2. 파일 입력 응답
    private void processInsertFile(JSONObject object) {
        try {
            int code = object.getInt(Global.CODE);
            if (code == SocketException.SUCCESS)
                Global.OnInsertFile.onSuccess();
            else
                Global.OnInsertFile.onException(code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    // TODO: 15. 12. 2. 타임라인 글쓰기 응답
    private void processInsertTimeline(JSONObject object) {
        try {
            int code = object.getInt(Global.CODE);
            Log.d(TAG, "타임라인 글쓰기 응답");
            Log.d(TAG, "object = " + object);

            Intent intent = new Intent(context, TimelineWriteActivity.class);
            intent.putExtra(Global.COMMAND, Global.INSERT_TIMELINE);
            intent.putExtra(Global.CODE, code);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            if (code == SocketException.SUCCESS) {
                TimelineCardDto dto = new TimelineCardDto(object);
                intent.putExtra(Global.TIMELINE, dto);
            }
            context.startActivity(intent);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 28. 카카오 로그인 응답
    private void processSignInKakao(JSONObject object) {
        try {
            int code = object.getInt(Global.CODE);
            Log.d(TAG, "카카오 로그인 응답");
            Log.d(TAG, "object = " + object);

            Intent intent = new Intent(context, SignUpActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Global.COMMAND, Global.SIGN_IN_KAKAO);
            intent.putExtra(Global.CODE, code);
            if (code == SocketException.SUCCESS) {
                // 성공
                JSONObject userObject = object.getJSONObject(Global.USER);
//                String id = userObject.getString(Global.ID);
//                String picture = userObject.getString(Global.PICTURE);
//                String phone = userObject.getString(Global.PHONE);
//                String realName = userObject.getString(Global.REAL_NAME);
//                String name = userObject.getString(Global.NAME);
//                String hasExtraProfile = userObject.getString(Global.HAS_EXTRA_PROFILE);
//                int sex = userObject.getInt(Global.SEX);
//                int userType = userObject.getInt(Global.USER_TYPE);
//                int schoolId = userObject.getInt(Global.SCHOOL_ID);
                UserEntity user = new UserEntity(userObject);
                intent.putExtra(Global.USER, user);
            }
            context.startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 25. 제품 요청 응답
    private void processGetProduct(JSONObject object) {
        try {
            int code = object.getInt(Global.CODE);
            Log.d(TAG, "제품 요청 응답");
            Log.d(TAG, "object = " + object);

            Intent intent = new Intent(context, ReservationPushService.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Global.COMMAND, Global.INSERT_LIKE);
            intent.putExtra(Global.CODE, code);

            if (code == SocketException.SUCCESS) {
                JSONArray productArray = object.getJSONArray(Global.PRODUCT);
                Gson gson = new Gson();
                ArrayList<ProductCardDto> productCardDtos = new ArrayList<>();

                for (int i = 0; i < productArray.length(); i++) {
                    JSONObject productObject = productArray.getJSONObject(i);
                    ProductCardDto dto = gson.fromJson(productObject.toString(), ProductCardDto.class);
                    productCardDtos.add(dto);
                }
                intent.putExtra(Global.PRODUCT, productCardDtos);
            }
            context.startService(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 25. 좋아요
    private void processInsertLike(JSONObject object) {
        try {
            int code = object.getInt(Global.CODE);
            Log.d(TAG, "좋아요 응답");
            Log.d(TAG, "object = " + object);

            Intent intent = new Intent(context, TimelineDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Global.COMMAND, Global.INSERT_LIKE);
            intent.putExtra(Global.CODE, code);

            if (code == SocketException.SUCCESS) {
                JSONObject likeObject = object.getJSONObject(Global.LIKE);
                Gson gson = new Gson();
                LikeEntity likeEntity = gson.fromJson(likeObject.toString(), LikeEntity.class);
                intent.putExtra(Global.LIKE, likeEntity);
            }
            context.startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 25. 좋아요 삭제
    private void processDeleteLike(JSONObject object) {
        try {
            int code = object.getInt(Global.CODE);
            Log.d(TAG, "좋아요 삭제 응답");
            Log.d(TAG, "object = " + object);

            Intent intent = new Intent(context, TimelineDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Global.COMMAND, Global.DELETE_LIKE);
            intent.putExtra(Global.CODE, code);
            context.startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 25. 타임라인 지우기
    private void processDeleteTimeline(JSONObject object) {
        try {
            final int code = object.getInt(Global.CODE);
            Log.d(TAG, "타임라인 지우기 응답");
            Log.d(TAG, "object = " + object);

            Intent intent = new Intent(context, TimelineDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Global.COMMAND, Global.DELETE_TIMELINE);
            intent.putExtra(Global.CODE, code);
//            context.startActivity(intent);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (code == SocketException.SUCCESS)
                        Global.OnDeleteTimeline.onSuccess();
                    else
                        Global.OnDeleteTimeline.onException(code);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 25. 제품 업데이트
    private void processUpdateProduct(JSONObject object) {
        try {
            int code = object.getInt(Global.CODE);
            Log.d(TAG, "제품 업데이트 응답");
            Log.d(TAG, "object = " + object);

            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Global.COMMAND, Global.UPDATE_PRODUCT);
            intent.putExtra(Global.CODE, code);
            context.startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 25. 파일 삭제
    private void processDeleteFile(JSONObject object) {
        try {
            int code = object.getInt(Global.CODE);
            int from = object.getInt(Global.FROM);
            Log.d(TAG, "파일삭제 응답");
            Log.d(TAG, "object = " + object);

            Intent intent;
            if (from == 1)
                intent = new Intent(context, TimelineWriteActivity.class);
            else
                intent = new Intent(context, ProductDetailActivity.class);

            intent.putExtra(Global.COMMAND, Global.DELETE_FILE);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Global.CODE, code);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 25. 제품 삭제 응답
    private void processDeleteProduct(JSONObject object) {
        try {
            int code = object.getInt(Global.CODE);
            Log.d(TAG, "제품 삭제 응답");
            Log.d(TAG, "object = " + object);

            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Global.COMMAND, Global.DELETE_PRODUCT);
            intent.putExtra(Global.CODE, code);
            context.startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void processUpdateTransactionStatus(JSONObject object) {
        try {
            int code = object.getInt(Global.CODE);
            int status = object.getInt(Global.STATUS);
            Log.d(TAG, "updateTransactionStatus 응답");
            Log.d(TAG, "object = " + object);

            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Global.CODE, code);
            intent.putExtra(Global.STATUS, status);

            if (code == SocketException.SUCCESS) {
                // 성공
                JSONObject transactionJson = object.getJSONObject(Global.TRANSACTION);
                Gson gson = new Gson();
                TransactionEntity transactionEntity = gson.fromJson(transactionJson.toString(), TransactionEntity.class);
                intent.putExtra(Global.TRANSACTION, transactionEntity);
            }
            context.startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    // TODO: 15. 11. 24. 댓글 삭제
    private void processDeleteComment(JSONObject object) {
        try {
            int code = object.getInt(Global.CODE);
            int from = object.getInt(Global.FROM);
            Log.d(TAG, "댓글 삭제 응답");
            Log.d(TAG, "object = " + object);

            if (code == SocketException.SUCCESS)
                Global.OnDeleted.onSuccess();
            else
                Global.OnDeleted.onException();

            Intent intent;
            if (from == 1)
                intent = new Intent(context, ProductDetailActivity.class);
            else
                intent = new Intent(context, TimelineDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Global.CODE, code);
            context.startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 24. 내 제품 요청 응답
    private void processGetMyProduct(JSONObject object) {
        try {
            int code = object.getInt(Global.CODE);
            Log.d(TAG, "내 제품 요청 응답");
            Log.d(TAG, "object = " + object);

            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Global.CODE, code);

            if (code == SocketException.SUCCESS) {
                ArrayList<ProductCardDto> productCardDtos = new ArrayList<>();
                JSONArray array = object.getJSONArray(Global.PRODUCT_CARD);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject productObject = array.getJSONObject(i);
                    Gson gson = new Gson();
                    ProductCardDto dto = gson.fromJson(productObject.toString(), ProductCardDto.class);
                    productCardDtos.add(dto);
                }
                intent.putExtra(Global.PRODUCT_CARD, productCardDtos);
            }

            context.startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 23. 제품 검색 응답
    private void processSearchProduct(JSONObject object) {
        try {
            int code = object.getInt(Global.CODE);
            Log.d(TAG, "제품 검색 응답");
            Log.d(TAG, "object = " + object);

            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Global.COMMAND, Global.SEARCH_PRODUCT);
            intent.putExtra(Global.CODE, code);

            if (code == SocketException.SUCCESS) {
                ArrayList<ProductCardDto> products = new ArrayList<>();
                JSONArray array = object.getJSONArray(Global.PRODUCT_CARD);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject productJson = array.getJSONObject(i);
                    Gson gson = new Gson();
                    ProductCardDto dto = gson.fromJson(productJson.toString(), ProductCardDto.class);
                    products.add(dto);
                }
                intent.putExtra(Global.PRODUCT_CARD, products);
            }
            context.startActivity(intent);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 23. 타임라인 글쓰기 응답
    private void processInsertProduct(JSONObject object) {
        try {
            int code = object.getInt(Global.CODE);
            Log.d(TAG, "타임라인 글쓰기 응답");
            Log.d(TAG, "object = " + object);

            Intent intent = new Intent(context, AddProductsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Global.COMMAND, Global.INSERT_PRODUCT);
            intent.putExtra(Global.CODE, code);

            if (code == SocketException.SUCCESS) {
                JSONArray array = object.getJSONArray(Global.PRODUCT);
                ArrayList<ProductEntity> productEntities = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject productJson = array.getJSONObject(i);
                    Gson gson = new Gson();
                    ProductEntity productEntity = gson.fromJson(productJson.toString(), ProductEntity.class);
                    productEntities.add(productEntity);
                }
                intent.putExtra(Global.PRODUCT, productEntities);
            }

            context.startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 23. 타임라인 업데이트 응답
    private void processUpdateTimeline(JSONObject object) {
        try {
            int code = object.getInt(Global.CODE);
            Log.d(TAG, "타임라인 업데이트 응답");
            Log.d(TAG, "object = " + object);

            Intent intent = new Intent(context, TimelineWriteActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Global.COMMAND, Global.UPDATE_TIMELINE);
            intent.putExtra(Global.CODE, code);
            context.startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 23. 타임라인 글 모두 불러오기 응답
    private void processGetAllTimeline(JSONObject object) {
        try {
            int code = object.getInt(Global.CODE);
            Log.d(TAG, "타임라인 글 모두 불러오기 응답");
            Log.d(TAG, "object = " + object);

            Intent intent = new Intent(context, TimelineActivity.class);
            intent.putExtra(Global.COMMAND, Global.GET_ALL_TIMELINE);
            intent.putExtra(Global.CODE, code);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            if (code == SocketException.SUCCESS) {
                // 성공
                ArrayList<TimelineCardDto> timelineCardDtos = new ArrayList<>();
                JSONArray timelineArray = object.getJSONArray(Global.TIMELINE);
                for (int i = 0; i < timelineArray.length(); i++) {
                    JSONObject timelineObject = timelineArray.getJSONObject(i);
                    TimelineCardDto timelineCardDto = new TimelineCardDto();
                    timelineCardDto.setTimeline(timelineObject);
                    timelineCardDto.setUser(timelineObject);
                    timelineCardDto.setLike(timelineObject);
                    timelineCardDto.setFile(timelineObject);
                    timelineCardDtos.add(timelineCardDto);
                }
                intent.putExtra(Global.TIMELINE, timelineCardDtos);
            }
            context.startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 23. 타임라인 내 글 불러오기 응답
    private void processGetMyTimeline(JSONObject object) {
        try {
            int code = object.getInt(Global.CODE);
            Log.d(TAG, "타임라인 내 글 불러오기 응답");
            Log.d(TAG, "object = " + object);

            Intent intent = new Intent(context, TimelineActivity.class);
            intent.putExtra(Global.COMMAND, Global.GET_MY_TIMELINE);
            intent.putExtra(Global.CODE, code);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            if (code == SocketException.SUCCESS) {
                // 성공
                ArrayList<TimelineCardDto> timelineCardDtos = new ArrayList<>();
                JSONArray timelineArray = object.getJSONArray(Global.TIMELINE);
                for (int i = 0; i < timelineArray.length(); i++) {
                    JSONObject timelineObject = timelineArray.getJSONObject(i);
                    TimelineCardDto timelineCardDto = new TimelineCardDto();
                    timelineCardDto.setTimeline(timelineObject);
                    timelineCardDto.setUser(timelineObject);
                    timelineCardDto.setLike(timelineObject);
                    timelineCardDto.setFile(timelineObject);
                    timelineCardDtos.add(timelineCardDto);
                }
                intent.putExtra(Global.TIMELINE, timelineCardDtos);
            }
            context.startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 23. 타임라인 글 댓글 불러오기 응답
    private void processGetTimelineComment(JSONObject object) {
        try {
            int code = object.getInt(Global.CODE);
            Log.d(TAG, "타임라인 글 댓글 불러오기 응답");
            Log.d(TAG, "object = " + object);

            Intent intent;
            if (object.getInt(Global.FROM) == 1 || object.getInt(Global.FROM) == 3)
                intent = new Intent(context, ProductDetailActivity.class);
            else
                intent = new Intent(context, TimelineDetailActivity.class);
            intent.putExtra(Global.COMMAND, Global.GET_TIMELINE_COMMENT);
            intent.putExtra(Global.CODE, code);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            if (code == SocketException.SUCCESS) {
                // 성공
                ArrayList<TimelineCommentCardDto> timelineCommentCardDtos = new ArrayList<>();
                JSONArray timelineCommentArray = object.getJSONArray(Global.TIMELINE_COMMENT);
                for (int i = 0; i < timelineCommentArray.length(); i++) {
                    JSONObject timelineCommentObject = timelineCommentArray.getJSONObject(i);
                    TimelineCommentCardDto comment = new TimelineCommentCardDto(timelineCommentObject);
                    timelineCommentCardDtos.add(comment);
                }
                intent.putExtra(Global.TIMELINE_COMMENT, timelineCommentCardDtos);
            }

            context.startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 23. 타임라인 게시글에 댓글 달기 응답
    private void processInsertTimelineComment(JSONObject object) {
        try {
            int code = object.getInt(Global.CODE);
            int from = object.getInt(Global.FROM);
            Log.d(TAG, "타임라인 게시글에 댓글달기 응답");
            Log.d(TAG, "object = " + object);

            Intent intent;
            if (from == 1)
                intent = new Intent(context, ProductDetailActivity.class);
            else
                intent = new Intent(context, TimelineDetailActivity.class);

            intent.putExtra(Global.COMMAND, Global.INSERT_TIMELINE_COMMENT);
            intent.putExtra(Global.CODE, code);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 21. 유저 프로필 업데이트 응답
    private void processUpdateUserProfile(JSONObject object) {
        try {
            int code = object.getInt(Global.CODE);
            Log.d(TAG, "유저 프로필 업데이트 응답");
            Log.d(TAG, "object = " + object);

            Intent intent = new Intent(context, EditProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Global.CODE, code);

            if (code == SocketException.SUCCESS) {
                // 성공
                Gson gson = new Gson();
                JSONObject userObject = object.getJSONObject(Global.USER);
                UserEntity user = gson.fromJson(userObject.toString(), UserEntity.class);
                intent.putExtra(Global.USER, user);
            }
            context.startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 20. 학교 랭킹 응답
    private void processGetSchoolRanking(JSONObject object) {
        try {
            int code = object.getInt(Global.CODE);
            int from = object.getInt(Global.FROM);
            Log.d(TAG, "학교 랭킹 요청 응답");
            Log.d(TAG, "object = " + object);

            Intent intent;
            if (from == 1)
                intent = new Intent(context, MainActivity.class);
            else
                intent = new Intent(context, SchoolRankingPushService.class);

            intent.putExtra(Global.CODE, code);
            intent.putExtra(Global.COMMAND, Global.GET_SCHOOL_RANKING);

            if (code == SocketException.SUCCESS) {
                // 성공
                JSONArray schoolArray = object.getJSONArray(Global.SCHOOL);
                ArrayList<SchoolRanking> schoolRankingList = new ArrayList<>();
                for (int i = 0; i < schoolArray.length(); i++) {
                    JSONObject schoolObject = schoolArray.getJSONObject(i);
                    SchoolRanking schoolRanking = new SchoolRanking(schoolObject);
                    schoolRankingList.add(schoolRanking);
                }
                intent.putExtra(Global.SCHOOL, schoolRankingList);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            if (from == 1)
                context.startActivity(intent);
            else
                context.startService(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 20. 로그인 응답
    private void processSignIn(JSONObject object) {
        Log.d(TAG, "로그인 응답");
        try {
            int code = object.getInt(Global.CODE);

            Intent intent = new Intent(context, LoginActivity.class);
            if (code == SocketException.SUCCESS) {
                // 성공
                JSONObject userObject = object.getJSONObject(Global.USER);
                UserEntity guest = new UserEntity(userObject);
                intent.putExtra(Global.USER, guest);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void processSingUp(JSONObject object) {
        // 회원가입 응답
        Log.d(TAG, "회원가입 응답");
        try {
            int code = object.getInt(Global.CODE);
            int userType = object.getInt(Global.USER_TYPE);

            Intent intent;
            if (userType == 0) {
                // 게스트모드
                intent = new Intent(context, LoginActivity.class);
            } else {
                // 정식모드
                intent = new Intent(context, SignUpActivity.class);
            }

            if (code == SocketException.SUCCESS) {
                // 성공
                JSONObject userObject = object.getJSONObject(Global.USER);
                UserEntity user = new UserEntity(userObject);
                intent.putExtra(Global.USER, user);
            }
            intent.putExtra(Global.COMMAND, Global.SIGN_UP);
            intent.putExtra(Global.CODE, code);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * TODO: 학교 정보
     * */
    private void processGetSchool(JSONObject object) {
        try {
            int code = object.getInt(Global.CODE);
            Log.d(TAG, "학교 정보 요청 응답");
            Log.d(TAG, "object = " + object);

            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Global.COMMAND, Global.GET_SCHOOL);
            intent.putExtra(Global.CODE, 200);

            if (code == SocketException.SUCCESS) {
                JSONArray array = object.getJSONArray(Global.SCHOOL);
                ArrayList<SchoolEntity> schoolEntities = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject schoolObject = array.getJSONObject(i);
                    schoolEntities.add(new SchoolEntity(schoolObject));
                }
                intent.putExtra(Global.SCHOOL, schoolEntities);
            }

            context.startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * TODO : 로그인 응답
     * */
    private void processLogin(JSONObject object) {
        Log.d(TAG, "로그인 응답");
        try {
            int code = object.getInt(Global.CODE);
            SocketException.printErrMsg(code);

            String id = null;
            if (code == SocketException.SUCCESS)
                id = object.getString(Global.ID);

            Intent intent = new Intent(context, LoginActivity.class);
            intent.putExtra(Global.COMMAND, Global.LOGIN);
            intent.putExtra(Global.ID, id);
            intent.putExtra(Global.CODE, code);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intent);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static Socket getSocket() {
        return socket;
    }


    private void socketConnect() {
        socket.open();
        socket.connect();
    }


    public void signUp(UserEntity userEntity) {
        // 회원가입
        Log.d(TAG, "회원가입");

        String userId = userEntity.id;
        String realName = userEntity.realName;
        int sex = userEntity.sex;
        int userType = userEntity.userType;
        String phone = userEntity.phone;
        int schoolId = userEntity.schoolId;

        Log.i(TAG, "userId = " + userId);

        try {
            JSONObject object = new JSONObject();
            object.put(Global.USER_ID, userId);
            object.put(Global.REAL_NAME, realName);
            object.put(Global.SEX, sex);
            object.put(Global.USER_TYPE, userType);
            object.put(Global.PHONE, phone);
            object.put(Global.SCHOOL_ID, schoolId);

            socket.emit(Global.SIGN_UP, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void signIn(String userId) {
        // 로그인
        Log.d(TAG, "로그인");
        Log.i(TAG, "userId = " + userId);

        try {
            JSONObject object = new JSONObject();
            object.put(Global.USER_ID, userId);

            socket.emit(Global.SIGN_IN, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * TODO: 학교 정보 요청
     * */
    public void getSchool() {
        Log.d(TAG, "학교 정보 요청");
        socket.emit(Global.GET_SCHOOL, "");
//        insertSchool();
    }


    private void insertSchool() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    String serviceKey = "AgIevc%2B9UJQ8VK0tGD%2FcO1BTMIPNnklsq7Vsa7LT%2Bu6aBTy5b42HH2r9Y4cI1mNdf%2Bp%2BZ%2B%2Bsg5Unml1IJcChuw%3D%3D";
//                    serviceKey = URLEncoder.encode(serviceKey, "UTF-8");
                    String urlStr = "http://api.data.go.kr/openapi/4e1a3cda-db21-40b3-b4f8-a1e7de2993bd?s_page=1&s_list=10000&type=xml&encoding=UTF-8&serviceKey=AgIevc%2B9UJQ8VK0tGD%2FcO1BTMIPNnklsq7Vsa7LT%2Bu6aBTy5b42HH2r9Y4cI1mNdf%2Bp%2BZ%2B%2Bsg5Unml1IJcChuw%3D%3D";
//                    urlStr = URLEncoder.encode(urlStr, "UTF-8");
                    java.net.URL url = new URL(urlStr);
                    try {
                        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                        XmlPullParser parser = factory.newPullParser();
                        parser.setInput(url.openStream(), "UTF-8");
                        int eventType = parser.getEventType();

                        final String CATEGORY = "학교급";
                        final String MIDDLE_SCHOOL = "중학교";
                        final String HIGH_SCHOOL = "고등학교";

                        final String ADDRESS = "소재지지번주소";
                        final String NAME = "학교명";
                        final String START = "com.google.gson.internal.LinkedTreeMap";

                        ArrayList<SchoolEntity> schoolEntities = new ArrayList<>();
                        SchoolEntity schoolEntity = null;

                        boolean isCategory = false;
                        boolean isAddress = false;
                        boolean isName = false;
                        boolean isTarget = false;
                        boolean isText = false;

                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            switch (eventType) {
                                case XmlPullParser.START_TAG:
                                    String tag = parser.getName();
                                    if (tag.equals(START)) {
                                        schoolEntity = new SchoolEntity();
                                        Log.d(TAG, "시작");
                                    }
                                    isText = true;
                                    break;

                                case XmlPullParser.TEXT:
                                    String text = parser.getText();
                                    if (text.equals(CATEGORY)) {
                                        isCategory = true;
                                    } else if (text.equals(ADDRESS)) {
                                        isAddress = true;
                                    } else if (text.equals(NAME)) {
                                        isName = true;
                                    } else if (isCategory) {
                                        // 학교급
                                        if (text.equals(MIDDLE_SCHOOL) || text.equals(HIGH_SCHOOL)) {
                                            schoolEntity.category = text;
                                            Log.d(TAG, "타겟임 " + text);
                                            isTarget = true;
                                            isCategory = false;
                                        } else if (isText) {
                                            Log.d(TAG, "타겟아님 " + text);
                                            isTarget = false;
                                            isCategory = false;
                                        }
                                    } else if (isAddress) {
                                        // 주소
                                        if (isTarget && isText) {
                                            Log.d(TAG, text);
                                            schoolEntity = setAddress(schoolEntity, text);
                                            isAddress = false;
                                        }
                                    } else if (isName) {
                                        if (isTarget && isText) {
                                            Log.d(TAG, text);
                                            schoolEntity.schoolname = text;
                                            isName = false;
                                        }
                                    }
                                    break;

                                case XmlPullParser.END_TAG:
                                    String endTag = parser.getName();
                                    if (endTag.equals(START)) {
                                        if (isTarget) {
                                            schoolEntity.id = schoolEntities.size() + 1;
                                            schoolEntities.add(schoolEntity);
                                            Log.d(TAG, "끝");
                                        }
                                    }

                                    isText = false;
                                    break;
                            }

                            eventType = parser.next();
                        }

                        Gson gson = new Gson();
                        String json = gson.toJson(schoolEntities);
                        JSONArray array = new JSONArray(json);
                        Log.d(TAG, "array = " + array);
                        socket.emit("insertSchool", array);
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    Log.d(TAG, "urlStr = " + urlStr);
//                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//                    httpURLConnection.setRequestMethod("GET");
//                    if (httpURLConnection != null) {
//                        int resCode = httpURLConnection.getResponseCode();
//                        if (resCode == HttpURLConnection.HTTP_OK) {
//                            StringBuilder sb = new StringBuilder();
//                            BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
//                            while (true) {
//                                String line = reader.readLine();
//                                if (line == null)
//                                    break;
//                                sb.append(line);
//                            }
//                            String school = sb.toString();
//                            Log.d(TAG, "school = " + school);
//                            reader.close();
//                            processGetSchool(school);
//                    httpURLConnection.disconnect();
//
//
//                        }
//                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    private SchoolEntity setAddress(SchoolEntity schoolEntity, String address) {
        schoolEntity.address = address;

        int firstSpace = address.indexOf(" ");
        int secondSpace = address.indexOf(" ", firstSpace + 1);

        schoolEntity.city = address.substring(0, firstSpace).trim();
        schoolEntity.gu = address.substring(firstSpace, secondSpace).trim();

        Log.d(TAG, "city = " + schoolEntity.city);
        Log.d(TAG, "gu = " + schoolEntity.gu);
        return schoolEntity;
    }


    // TODO: 15. 11. 20. 학교 랭킹 요청
    public void getSchoolRanking(int from) {
        Log.d(TAG, "학교 랭킹 요청");
        socket.emit(Global.GET_SCHOOL_RANKING, from);
    }

    // TODO: 15. 11. 20. 제품검색
    public void searchProduct(int schoolId, int sex, int category, int size) {
        Log.d(TAG, "제품 검색");
        try {
            JSONObject object = new JSONObject();
            object.put(Global.SCHOOL_ID, schoolId);
            object.put(Global.SEX, sex);
            object.put(Global.CATEGORY, category);
            object.put(Global.SIZE, size);
            Log.d(TAG, "searchProduct Object = " + object);
            socket.emit(Global.SEARCH_PRODUCT, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 20. 제품 등록
    public void insertProduct(ArrayList<ProductCardDto> productCardDtos) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(productCardDtos);
            JSONArray array = new JSONArray(json);

            JSONObject object = new JSONObject();
            object.put(Global.PRODUCT, array);
            Log.d(TAG, "insertProduct Object = " + object);
            socket.emit(Global.INSERT_PRODUCT, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 21. 유저 프로필 업데이트
    public void updateUserProfile(UserEntity user) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(user, UserEntity.class);
            JSONObject object = new JSONObject(json);
            Log.d(TAG, "updateUserProfile Object = " + object);
            socket.emit(Global.UPDATE_USER_PROFILE, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 23. 타임라인 게시글에 댓글 달기
    public void insertTimelineComment(String timelineItemId, String commentContent, int from, String userId) {
        try {
            JSONObject object = new JSONObject();
            object.put(Global.TIMELINE_ITEM_ID, timelineItemId);
            object.put(Global.COMMENT_CONTENT, commentContent);
            object.put(Global.USER_ID, userId);
            object.put(Global.FROM, from);
            Log.d(TAG, "insertTimelineComment Object = " + object);
            socket.emit(Global.INSERT_TIMELINE_COMMENT, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 23. 타임라인 게시글 댓글 불러오기
    public void getTimelineComment(String id, int from) {
        try {
            JSONObject object = new JSONObject();
            object.put(Global.ID, id);
            object.put(Global.FROM, from);
            Log.d(TAG, "getTimelineComment Object = " + object);
            socket.emit(Global.GET_TIMELINE_COMMENT, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 23. 타임라인 글 모두 불러오기
    public void getAllTimeline(int schoolId, String userId) {
        try {
            JSONObject object = new JSONObject();
            object.put(Global.SCHOOL_ID, schoolId);
            object.put(Global.USER_ID, userId);
            Log.d(TAG, "getAllTimeline Object = " + object);
            socket.emit(Global.GET_ALL_TIMELINE, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 23. 타임라인 내 글 불러오기
    public void getMyTimeline(int schoolId, String userId) {
        try {
            JSONObject object = new JSONObject();
            object.put(Global.SCHOOL_ID, schoolId);
            object.put(Global.USER_ID, userId);
            Log.d(TAG, "getMyTimeline Object = " + object);
            socket.emit(Global.GET_MY_TIMELINE, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 23. 타임라인 글 쓰기
    public void insertTimeline(int schoolId, String timelineContent, String id) {
        try {
            Log.d(TAG, "schoolId = " + schoolId);
            Log.d(TAG, "timelineContent = " + timelineContent);

            JSONObject object = new JSONObject();
            object.put(Global.SCHOOL_ID, schoolId);
            object.put(Global.USER_ID, id);
            object.put(Global.TIMELINE_CONTENT, timelineContent);
            Log.d(TAG, "insertTimeline Object = " + object);
            socket.emit(Global.INSERT_TIMELINE, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 23. 파일 지우기
    public void deleteFile(ArrayList<FileEntity> files) {
        try {
            for (int i = 0; i < files.size(); i++) {
                Log.d(TAG, i + "번째 id = " + files.get(i).id);
                Log.d(TAG, i + "번째 parent_uuid = " + files.get(i).parent_uuid);
            }

            Gson gson = new Gson();
            String json = gson.toJson(files);
            JSONArray array = new JSONArray(json);
            JSONObject object = new JSONObject();
            object.put(Global.FILE, array);
            Log.d(TAG, "deleteFile Object = " + object);
            socket.emit(Global.DELETE_FILE, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 23. 타임라인 업데이트
    public void updateTimeline(TimelineCardDto timelineCardDto) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(timelineCardDto);
            JSONObject object = new JSONObject(json);
            Log.d(TAG, "updateTimeline Object = " + object);
            socket.emit(Global.UPDATE_TIMELINE, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 24. 내 제품 요청
    public void getMyProduct(String donatorId, String receiverId) {
        try {
            JSONObject object = new JSONObject();
            object.put(TransactionEntity.PROPERTY_DONATOR_UUID, donatorId);
            object.put(TransactionEntity.PROPERTY_RECEIVER_UUID, receiverId);
            Log.d(TAG, "getMyProduct Object = " + object);
            socket.emit(Global.GET_MY_PRODUCT, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 24. 댓글 삭제 요청
    public void deleteComment(ArrayList<TimelineCommentCardDto> timelineCommentCardDtos, int from) {
        try {
            JSONObject object = new JSONObject();
            JSONArray array = new JSONArray();
            for (int i = 0; i < timelineCommentCardDtos.size(); i++) {
                TimelineCommentCardDto dto = timelineCommentCardDtos.get(i);
                JSONObject commentObject = new JSONObject();
                commentObject.put(Global.ID, dto.commentEntity.id);
                commentObject.put(Global.TIMELINE_ITEM_ID, dto.commentEntity.timeline_item_uuid);
                commentObject.put(Global.USER_ID, dto.commentEntity.user_uuid);
                array.put(commentObject);
            }
            object.put(Global.COMMENT, array);
            object.put(Global.FROM, from);
            Log.d(TAG, "deleteComment Object = " + object);
            socket.emit(Global.DELETE_COMMENT, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public  void updateTransactionStatus(int status, TransactionEntity transactionEntity) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(transactionEntity, TransactionEntity.class);
            JSONObject transJson = new JSONObject(json);

            JSONObject object = new JSONObject();
            object.put(Global.STATUS, status);
            object.put(Global.TRANSACTION, transJson);
            Log.d(TAG, "updateTransactionStatus Object = " + object);
            socket.emit(Global.UPDATE_TRANSACTION_STATUS, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 25. 제품 삭제
    public void deleteProduct(ProductCardDto productCardDto) {
        try {
            JSONObject object = new JSONObject();
            object.put(Global.PRODUCT_ID, productCardDto.productEntity.id);
            Log.d(TAG, "deleteProduct Object = " + object);
            socket.emit(Global.DELETE_PRODUCT, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 25. 제품 수정
    public void updateProduct(ProductCardDto productCardDto) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(productCardDto);
            JSONObject object = new JSONObject(json);
            Log.d(TAG, "updateProduct Object = " + object);
            socket.emit(Global.UPDATE_PRODUCT, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 25. 파일 입력
    public void insertFile(String id, String path, String fileName) {
        try {
            JSONObject object = new JSONObject();
            object.put(Global.PRODUCT_ID, id);
            object.put(Global.PATH, path);
            object.put(Global.FILE, fileName);
            Log.d(TAG, "insertFile Object = " + object);
            socket.emit(Global.INSERT_FILE, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 25. 타임라인 지우기
    public void deleteTimeline(TimelineCardDto timelineCardDto) {
        String timelineId = timelineCardDto.timelineEntity.id;
        String userId = timelineCardDto.userEntity.id;

        try {
            JSONObject object = new JSONObject();
            object.put(Global.TIMELINE_ITEM_ID, timelineId);
            object.put(Global.USER_ID, userId);
            Log.d(TAG, "deleteTimeLine Object = " + object);
            socket.emit(Global.DELETE_TIMELINE, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 25. 좋아요 지우기
    public void deleteLike(LikeEntity likeEntity) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(likeEntity);
            JSONObject object = new JSONObject(json);
            Log.d(TAG, "deleteLike Object = " + object);
            socket.emit(Global.DELETE_LIKE, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 25. 좋아요
    public void insertLike(String timelineItemId, String userId) {
        try {
            JSONObject object = new JSONObject();
            object.put(Global.TIMELINE_ITEM_ID, timelineItemId);
            object.put(Global.USER_ID, userId);
            Log.d(TAG, "insertLike Object = " + object);
            socket.emit(Global.INSERT_LIKE, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 25. 제품 요청
    public void getProduct(String productJson) {
        try {
            JSONArray array = new JSONArray(productJson);
            Log.d(TAG, "getProduct Array = " + array.toString());
            socket.emit(Global.GET_PRODUCT, array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 28.
    public void insertTransaction(ArrayList<TransactionEntity> transactionEntities) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(transactionEntities);
            JSONArray array = new JSONArray(json);
            Log.d(TAG, "insertTransaction Array = " + array.toString());
            socket.emit(Global.INSERT_TRANSACTION, array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 28. 카카오 로그인
    public void signInKakao(long id) {
        try {
            JSONObject object = new JSONObject();
            object.put(Global.ID, String.valueOf(id));
            Log.d(TAG, "signInKakao Object = " + object);
            socket.emit(Global.SIGN_IN_KAKAO, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}