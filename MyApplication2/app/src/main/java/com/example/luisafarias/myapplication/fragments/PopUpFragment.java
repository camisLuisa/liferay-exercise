package com.example.luisafarias.myapplication.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.luisafarias.myapplication.MainActivity;
import com.example.luisafarias.myapplication.model.Feed;
import com.wedeploy.android.auth.Authorization;
import com.wedeploy.android.auth.TokenAuthorization;

/**
 * Created by luisafarias on 10/10/17.
 */

public class PopUpFragment extends DialogFragment {


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this._feed = getArguments().getParcelable("feed");
        String nome = _feed.get_nome();
        String token = getArguments().getString("token");
        _authorization = new TokenAuthorization(token);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("O que deseja fazer com"+nome)
                .setPositiveButton("Editar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(_feed != null && _authorization != null){
                            MainActivity activity = (MainActivity) getActivity();
                            activity.goAddNewFeed(getView());
                        }
                    }
                })
                .setNegativeButton("excluir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }

    private Authorization _authorization;
    private Feed _feed;
}
