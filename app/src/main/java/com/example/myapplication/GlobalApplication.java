package com.example.myapplication;
import android.app.Application;
import android.content.Context;

import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;

public class GlobalApplication extends Application {

    private static volatile GlobalApplication instance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        KakaoSDK.init(new KakaoSDKAdapter());
    }

    public static final GlobalApplication getGlobalApplicationContext() {
        if (instance == null)
            throw new IllegalStateException("this application does not inherit com.kakao.GlobalApplication");
        return instance;
    }

    protected static class KakaoSDKAdapter extends KakaoAdapter {
        /**
         * Session Config에 대해서는 default값들이 존재한다.
         * 필요한 상황에서만 override해서 사용하면 됨.
         *
         * @return Session의 설정값.
         */

        @Override
        public ISessionConfig getSessionConfig() {
            return new ISessionConfig() {

                @Override
                public AuthType[] getAuthTypes() {

                    // 로그인시 인증받을 타입을 지정한다. 지정하지 않을 시 가능한 모든 옵션이 지정된다.

                    //1.KAKAO_TALK :  kakaotalk으로 login을 하고 싶을때 지정.
                    //2.KAKAO_STORY : kakaostory으로 login을 하고 싶을때 지정.
                    //3.KAKAO_ACCOUNT :  웹뷰 Dialog를 통해 카카오 계정연결을 제공하고 싶을경우 지정.
                    //4.KAKAO_TALK_EXCLUDE_NATIVE_LOGIN : 카카오톡으로만 로그인을 유도하고 싶으면서 계정이 없을때 계정생성을 위한
                    //버튼도 같이 제공을 하고 싶다면 지정.KAKAO_TALK과 중복 지정불가.
                    //5.KAKAO_LOGIN_ALL : 모든 로그인방식을 사용하고 싶을때 지정.

                    return new AuthType[]{AuthType.KAKAO_TALK};
                }


                @Override
                public boolean isUsingWebviewTimer() {
                    return false;
                }
                @Override
                public boolean isSecureMode() {
                    return false;
                }
                @Override
                public ApprovalType getApprovalType() {
                    return ApprovalType.INDIVIDUAL;
                    //일반 사용자가 아닌 Kakao와 제휴된 앱에서 사용되는 값으로, 값을 채워주지 않을경우 ApprovalType.INDIVIDUAL 값을 사용하게 된다.

                }
                @Override
                public boolean isSaveFormData() {
                    // Kakao SDK 에서 사용되는 WebView에서 email 입력폼에서 data를 save할지여부를 결정한다. Default true.

                    return true;
                }
            };
        }

        //Application이 가지고있는 정보를 얻기위한 interface.
        @Override
        public IApplicationConfig getApplicationConfig() {
            return new IApplicationConfig() {

                // 현재 최상단에 위치하고 있는 Activity. topActivity가 아니거나 ApplicationContext를 넣는다면
                // SDK내에서의 Dialog Popup등이 동작하지 않을 수 있습니다.


                @Override
                public Context getApplicationContext() {
                    return GlobalApplication.getGlobalApplicationContext();
                }
            };
        }
    }




    /**
     * 애플리케이션 종료시 singleton 어플리케이션 객체 초기화한다.
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        instance = null;
    }

}