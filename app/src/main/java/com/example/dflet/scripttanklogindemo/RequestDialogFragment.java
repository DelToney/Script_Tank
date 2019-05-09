package com.example.dflet.scripttanklogindemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class RequestDialogFragment extends DialogFragment {


    private OnFragmentInteractionListener mListener;

    private Context appContext;
    String userName, user_id, request_id;


    public RequestDialogFragment() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        userName = getArguments().getString("userName");
        user_id = getArguments().getString("user_id");
        request_id = getArguments().getString("request_id");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Handle Request");
        alertDialogBuilder.setMessage("Do you accept " + userName + "'s request?");

        alertDialogBuilder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ((RequestListActivity)getActivity()).handleRequest("ACCEPT_REQ", request_id, user_id);
            }
        });
        alertDialogBuilder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((RequestListActivity)getActivity()).handleRequest("REJECT_REQ", request_id, user_id);
                dialog.dismiss();
            }
        });


        return alertDialogBuilder.create();
    }

    public void setmListener(OnFragmentInteractionListener callback) {
        this.mListener = callback;
    }


    static RequestDialogFragment newInstance() {
        RequestDialogFragment fragment = new RequestDialogFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //appContext = getActivity();
        userName = getArguments().getString("userName");
        user_id = getArguments().getString("user_id");
        request_id = getArguments().getString("request_id");
        return inflater.inflate(R.layout.fragment_request_dialog, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesso <a href=
     *      * "http://developer.android.com/training/basics/fragments/communicating.html"n
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onRequestHandled(String result);
    }
}
