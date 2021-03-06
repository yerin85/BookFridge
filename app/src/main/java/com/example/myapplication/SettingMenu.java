package com.example.myapplication;

import androidx.fragment.app.Fragment;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.example.myapplication.data.BasicResponse;
import com.example.myapplication.data.UserGenreData;
import com.example.myapplication.data.UserGenreResponse;
import com.example.myapplication.data.UserInfo;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.ServiceApi;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingMenu extends Fragment {
    UserInfo userInfo;
    ServiceApi service;

    Boolean notification;

    public SettingMenu() {
        // Required empty public constructor
    }

    public static Fragment newInstance(UserInfo userInfo) {
        SettingMenu settingMenu = new SettingMenu();
        Bundle bundle = new Bundle();
        bundle.putSerializable("userInfo", userInfo);
        settingMenu.setArguments(bundle);
        return settingMenu;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_setting_menu, container, false);

        userInfo = (UserInfo) getArguments().getSerializable("userInfo");

        TextView button_logout = v.findViewById(R.id.text_logout);
        TextView button_exit = v.findViewById(R.id.text_delete);

        TextView button_genre = v.findViewById(R.id.select_gerne);
        Switch switchPrivate = v.findViewById(R.id.switch1);
        Switch switchPush = v.findViewById(R.id.switch2);

        service = RetrofitClient.getClient().create(ServiceApi.class);

        SharedPreferences shared = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
        if(shared.getBoolean("push",false)){
            switchPush.setChecked(true);
        }else{
            switchPush.setChecked(false);
        }
        if(shared.getBoolean("private",true)){
            switchPrivate.setChecked(false);
        }
        else{
            switchPrivate.setChecked(true);
        }

        button_genre.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showDialog();
               }
            }, 100);

            }
        });

        button_logout.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
            }
        });

        button_exit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setMessage("탈퇴하시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() {
                                    @Override
                                    public void onFailure(ErrorResult errorResult) {
                                        int result = errorResult.getErrorCode();

                                    }

                                    @Override
                                    public void onSessionClosed(ErrorResult errorResult) {
                                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onNotSignedUp() {
                                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onSuccess(Long result) {
                                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                });

                                service.subUserProfile(userInfo.userId).enqueue(new Callback<BasicResponse>() {
                                    @Override
                                    public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                                        BasicResponse result = response.body();
                                        if (result.getCode() != 200) {
                                            Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<BasicResponse> call, Throwable t) {
                                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        switchPush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferences.Editor editor = shared.edit();
                if (isChecked) {
                    Toast.makeText(getContext(), "푸시 알림에 동의하셨습니다", Toast.LENGTH_LONG).show();
                    editor.putBoolean("push",true);
                } else {
                    Toast.makeText(getContext(), "푸시 알림을 차단합니다", Toast.LENGTH_LONG).show();
                    editor.putBoolean("push",false);
                }
                editor.commit();
            }
        });

        switchPrivate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferences.Editor editor = shared.edit();
                if (isChecked) {
                    service.updateUserPrivate(userInfo.userId, "0").enqueue(new Callback<BasicResponse>() {
                        @Override
                        public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                            BasicResponse result = response.body();
                            if (result.getCode() != 200) {
                                Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getContext(), "공개로 전환하였습니다", Toast.LENGTH_SHORT).show();
                                editor.putBoolean("private",false);
                                editor.commit();
                            }
                        }

                        @Override
                        public void onFailure(Call<BasicResponse> call, Throwable t) {
                            Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    service.updateUserPrivate(userInfo.userId, "1").enqueue(new Callback<BasicResponse>() {
                        @Override
                        public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                            BasicResponse result = response.body();
                            if (result.getCode() != 200) {
                                Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getContext(), "비공개로 전환하였습니다", Toast.LENGTH_SHORT).show();
                                editor.putBoolean("private",true);
                                editor.commit();
                            }
                        }
                        @Override
                        public void onFailure(Call<BasicResponse> call, Throwable t) {
                            Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        // Inflate the layout for this fragment
        return v;
    }

    public void showDialog() {
        final List<String> ListItems = new ArrayList<String>();
        ListItems.add("만화");
        ListItems.add("SF");
        ListItems.add("추리");
        ListItems.add("고전");
        ListItems.add("액션");
        ListItems.add("판타지");
        ListItems.add("희곡");
        ListItems.add("에세이");
        ListItems.add("시");
        ListItems.add("무협");

        final List<String> GenreList = new ArrayList<>();
        GenreList.add("comics");
        GenreList.add("sf");
        GenreList.add("mystery");
        GenreList.add("classical");
        GenreList.add("action");
        GenreList.add("fantasy");
        GenreList.add("theatrical");
        GenreList.add("essay");
        GenreList.add("poem");
        GenreList.add("martialArt");

        final CharSequence[] items = ListItems.toArray(new String[ListItems.size()]);
        final boolean[] checkArray = new boolean[10];

        service.getUserGenre(userInfo.userId).enqueue(new Callback<ArrayList<UserGenreResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<UserGenreResponse>> call, Response<ArrayList<UserGenreResponse>> response) {
                ArrayList<UserGenreResponse> userGenres = response.body();

                for(int i=0; i<userGenres.size();i++){
                    if(GenreList.contains(userGenres.get(i).getGenre())){
                        checkArray[GenreList.indexOf(userGenres.get(i).getGenre())]=true;
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<UserGenreResponse>> call, Throwable t) {

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("장르 변경");
        builder.setMultiChoiceItems(items, checkArray, new DialogInterface.OnMultiChoiceClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean isChecked) {

                if (isChecked) {
                    checkArray[i]=isChecked;
                }
                else if (checkArray[i]) {
                    checkArray[i]=false;
                }
            }
        });

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                for(int j=0;j<10;j++){
                    String s = GenreList.get(j);

                    if(checkArray[j]){
                        service.addUserGenre(new UserGenreData(userInfo.userId,s)).enqueue(new Callback<BasicResponse>() {
                            @Override
                            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                                BasicResponse result = response.body();
                            }
                            @Override
                            public void onFailure(Call<BasicResponse> call, Throwable t) {
                            }
                        });
                    }else{
                        service.subUserGenre(new UserGenreData(userInfo.userId,s)).enqueue(new Callback<BasicResponse>() {
                            @Override
                            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                                BasicResponse result = response.body();
                            }
                            @Override
                            public void onFailure(Call<BasicResponse> call, Throwable t) {
                            }
                        });

                    }
                }
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();
    }

}

