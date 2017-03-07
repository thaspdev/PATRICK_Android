package com.thaspdev.mypatrick;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.VideoView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {//@link CamAndControlFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {//@link CamAndControlFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CamAndControlFragment extends Fragment implements View.OnTouchListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    /*private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";*/

    // TODO: Rename and change types of parameters
    /*private String mParam1;
    private String mParam2;*/

    private OnFragmentInteractionListener mListener;
    boolean dernier_haut_était_appuyé = false;
    boolean dernier_bas_était_appuyé = false;
    boolean dernier_rag_était_appuyé = false;
    boolean dernier_rca_était_appuyé = false;

    public CamAndControlFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * //@param param1 Parameter 1.
     * //@param param2 Parameter 2.
     * //@return A new instance of fragment CamAndControlFragment.
     */
    // TODO: Rename and change types and number of parameters
    /*public static CamAndControlFragment newInstance(String param1, String param2) {
        CamAndControlFragment fragment = new CamAndControlFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_cam_and_control, container, false);

        Button haut = (Button) v.findViewById(R.id.robotHaut);
        Button bas = (Button) v.findViewById(R.id.robotBas);
        Button rag = (Button) v.findViewById(R.id.robotAiguilles);
        Button rca = (Button) v.findViewById(R.id.robotContraireAiguilles);

        /*VideoView cameraFeed = (VideoView) v.findViewById(R.id.cameraFeed);

        cameraFeed.setVideoURI(Uri.parse("http://192.168.1.28:16000"));
        cameraFeed.start();*/


        haut.setOnTouchListener(this);
        bas.setOnTouchListener(this);
        rag.setOnTouchListener(this);
        rca.setOnTouchListener(this);

        return v;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            switch (v.getId()) {
                case R.id.robotHaut: {
                    if (!dernier_haut_était_appuyé) {
                        mListener.onCamButtonClicked("robotHaut", true);
                        dernier_haut_était_appuyé = true;
                    }
                    break;
                }

                case R.id.robotBas: {
                    if (!dernier_haut_était_appuyé) {
                        mListener.onCamButtonClicked("robotBas", true);
                        dernier_haut_était_appuyé = true;
                    }
                    break;
                }

                case R.id.robotAiguilles: {
                    if (!dernier_rag_était_appuyé) {
                        mListener.onCamButtonClicked("robotAiguilles", true);
                        dernier_rag_était_appuyé = true;
                    }
                    break;
                }

                case R.id.robotContraireAiguilles: {
                    if (!dernier_rca_était_appuyé) {
                        mListener.onCamButtonClicked("robotContraireAiguilles", true);
                        dernier_rca_était_appuyé = true;
                    }
                    break;
                }
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
            switch (v.getId()) {
                case R.id.robotHaut: {
                    if (dernier_haut_était_appuyé) {
                        mListener.onCamButtonClicked("robotHaut", false);
                        dernier_haut_était_appuyé = false;
                    }
                    break;
                }

                case R.id.robotBas: {
                    if(dernier_haut_était_appuyé) {
                        mListener.onCamButtonClicked("robotBas", false);
                        dernier_haut_était_appuyé = false;
                    }
                    break;
                }

                case R.id.robotAiguilles: {
                    if (dernier_rag_était_appuyé) {
                        mListener.onCamButtonClicked("robotAiguilles", false);
                        dernier_rag_était_appuyé = false;
                    }
                    break;
                }

                case R.id.robotContraireAiguilles: {
                    if (dernier_rca_était_appuyé) {
                        mListener.onCamButtonClicked("robotContraireAiguilles", false);
                        dernier_rca_était_appuyé = false;
                    }
                    break;
                }
            }
        }
        return true;
    }


    // TODO: Rename method, update argument and hook method into UI event
    /*public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

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
        /*mListener = null;*/
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.onCamAndControlCreate();
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onCamAndControlCreate();
        void onCamButtonClicked(String butName,boolean appuyé);//Si appuyé == true cela veut dire que le bouton est pressé, sinon il est relaché
    }
}
