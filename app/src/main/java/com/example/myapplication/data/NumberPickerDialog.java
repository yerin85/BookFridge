package com.example.myapplication.data;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.myapplication.MainActivity;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.ServiceApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class NumberPickerDialog extends DialogFragment {
    private NumberPicker.OnValueChangeListener valueChangeListener;
    Context context;
    String userId;

    public NumberPickerDialog(Context context, String userId) {
        this.context = context;
        this.userId = userId;
    }

    @Override
    public void onCancel(DialogInterface dialog){

        SharedPreferences shared = getActivity().getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
        builder1.setCancelable(false);
        builder2.setCancelable(false);

        builder1.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                builder2.setMessage("푸시 알람을 받으시겠습니까?").setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.putBoolean("push", true);
                        editor.commit();
                    }
                }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.putBoolean("push", false);
                        editor.commit();
                    }
                }).show();
            }
        });

        builder1.setMessage("나의 노트 공개를 허용하시겠습니까?").setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editor.putBoolean("private", false);
                ServiceApi service = RetrofitClient.getClient().create(ServiceApi.class);
                service.updateUserPrivate(userId,"0").enqueue(new Callback<BasicResponse>() {
                    @Override
                    public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                        if(response.body().getCode()!=200){
                            Toast.makeText(getActivity(),response.body().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BasicResponse> call, Throwable t) {
                        Toast.makeText(getActivity(),t.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editor.putBoolean("private", true);
            }
        }).show();
        //알림 받을건지 && 공개여부 설정하자
    }

    @Override
    public void onDismiss(DialogInterface dialog){

        SharedPreferences shared = getActivity().getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
        builder1.setCancelable(false);
        builder2.setCancelable(false);

        builder1.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                builder2.setMessage("푸시 알람을 받으시겠습니까?").setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.putBoolean("push", true);
                        editor.commit();
                    }
                }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.putBoolean("push", false);
                        editor.commit();
                    }
                }).show();
            }
        });

        builder1.setMessage("나의 노트 공개를 허용하시겠습니까?").setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editor.putBoolean("private", false);
                ServiceApi service = RetrofitClient.getClient().create(ServiceApi.class);
                service.updateUserPrivate(userId,"0").enqueue(new Callback<BasicResponse>() {
                    @Override
                    public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                        if(response.body().getCode()!=200){
                            Toast.makeText(getActivity(),response.body().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BasicResponse> call, Throwable t) {
                        Toast.makeText(getActivity(),t.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editor.putBoolean("private", true);
            }
        }).show();
        //알림 받을건지 && 공개여부 설정하자
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final NumberPicker numberPicker = new NumberPicker(getActivity());

        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(100);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("월 독서 목표량을 설정해주세요");

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ServiceApi service = RetrofitClient.getClient().create(ServiceApi.class);
                service.addGoal(userId, numberPicker.getValue()).enqueue(new Callback<BasicResponse>() {
                    @Override
                    public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                        BasicResponse result = response.body();
                        if (result.getCode() != 200) {
                            Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BasicResponse> call, Throwable t) {
                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.setView(numberPicker);
        return builder.create();
    }

}