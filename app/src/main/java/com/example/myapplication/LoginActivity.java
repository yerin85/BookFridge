package com.example.myapplication;

import android.app.Service;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import android.view.View;
import android.widget.Button;

import android.widget.Toast;
import android.content.Intent;

import com.example.myapplication.data.BasicResponse;
import com.example.myapplication.data.MyPageData;
import com.example.myapplication.data.UserPrivateData;
import com.example.myapplication.data.UserProfileData;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.ServiceApi;
import com.kakao.auth.ApiErrorCode;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;

import java.io.Serializable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class UserInfo implements Serializable {
    String userId;
    String nickname;
    String imagePath;

    UserInfo(String userId, String nickname, String imagePath) {
        this.userId = userId;
        this.nickname = nickname;
        this.imagePath = imagePath;
    }
}

public class LoginActivity extends AppCompatActivity {

    private Button btn_custom_login;
    private SessionCallback sessionCallback;
    ServiceApi service;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getHashKey();
        setContentView(R.layout.activity_login);
        sessionCallback = new SessionCallback();
        Session session = Session.getCurrentSession();
        session.addCallback(sessionCallback);
        Session.getCurrentSession().checkAndImplicitOpen();
        btn_custom_login = (Button) findViewById(R.id.btn_custom_login);
        btn_custom_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                session.open(AuthType.KAKAO_LOGIN_ALL, LoginActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) { //카카오 로그인 액티비티에서 넘어온 경우일 때 실행
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(sessionCallback);
    }

    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    int result = errorResult.getErrorCode();

                    if (result == ApiErrorCode.CLIENT_ERROR_CODE) {
                        Toast.makeText(getApplicationContext(), "네트워크 연결이 불안정합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "로그인 도중 오류가 발생했습니다: " + errorResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Toast.makeText(getApplicationContext(), "세션이 닫혔습니다. 다시 시도해 주세요: " + errorResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(MeV2Response result) {
                    String userId = String.valueOf(result.getId());
                    String nickname = result.getNickname();
                    String imagePath = result.getProfileImagePath();
                    service= RetrofitClient.getClient().create(ServiceApi.class);

                    //userProfile 저장
                    service.createUserProfile(new UserProfileData(userId,nickname,imagePath)).enqueue(new Callback<BasicResponse>() {
                        @Override
                        public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                            BasicResponse result = response.body();
                            if(result.getCode()!=200){//오류
                                Toast.makeText(LoginActivity.this,result.getMessage(),Toast.LENGTH_SHORT).show();
                                //종료
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<BasicResponse> call, Throwable t) {
                            Toast.makeText(LoginActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();
                            //종료
                            finish();
                        }
                    });

                    //priv에 받은 값(공개는0 비공개는1)을 string형태로 넣어야 합니다
                    service.createUserPrivate(new UserPrivateData(userId,"1")).enqueue(new Callback<BasicResponse>() {
                        @Override
                        public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                            BasicResponse result = response.body();
                            if(result.getCode()!=200){//오류
                                Toast.makeText(LoginActivity.this,result.getMessage(),Toast.LENGTH_SHORT).show();
                                //종료
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<BasicResponse> call, Throwable t) {
                            Toast.makeText(LoginActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();
                            //종료
                            finish();
                        }
                    });

                    //myPage table entry 생성
                    service.createMyPage(userId).enqueue(new Callback<BasicResponse>() {
                        @Override
                        public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                            BasicResponse result = response.body();
                            if(result.getCode()!=200){//오류
                                Toast.makeText(LoginActivity.this,result.getMessage(),Toast.LENGTH_SHORT).show();
                                //종료
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<BasicResponse> call, Throwable t) {
                            Toast.makeText(LoginActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();
                            //종료
                            finish();
                        }
                    });

                    Toast.makeText(getApplicationContext(), nickname + "님 안녕하세요", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    UserInfo userInfo = new UserInfo(userId, nickname, imagePath);
                    intent.putExtra("userInfo", userInfo);
                    startActivity(intent);
                    finish();
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException e) {
            Toast.makeText(getApplicationContext(), "로그인 도중 오류가 발생했습니다. 인터넷 연결을 확인해주세요: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
