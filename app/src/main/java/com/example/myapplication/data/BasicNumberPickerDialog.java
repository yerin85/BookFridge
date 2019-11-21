package com.example.myapplication.data;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.ServiceApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BasicNumberPickerDialog extends DialogFragment {
    private NumberPicker.OnValueChangeListener valueChangeListener;
    Context context;
    String userId;

    public BasicNumberPickerDialog(Context context, String userId) {
        this.context = context;
        this.userId = userId;
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