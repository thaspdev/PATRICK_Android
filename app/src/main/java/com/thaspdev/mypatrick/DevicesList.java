package com.thaspdev.mypatrick;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {//@link DevicesList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {//@link DevicesList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DevicesList extends Fragment{
    /*// TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    */
    private OnFragmentInteractionListener mListener;

    public DevicesList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * //@param param1 Parameter 1.
     * //@param param2 Parameter 2.
     * //@return A new instance of fragment DevicesList.
     */
    // TODO: Rename and change types and number of parameters
    /*public static DevicesList newInstance(String param1, String param2) {
        DevicesList fragment = new DevicesList();
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
    /*/@Override
    public void onPause(){
        super.onPause();
    }*/

    @Override
    public void onResume(){
        super.onResume();
        ArrayAdapter aa = mListener.onDevListFragmentCreate();//On appelle cette méthode qui est définie dans MainActivity et qui va s'occuper de créer une connexion bluetooth ainsi que d'afficher les appareils disponibles dans la ListView
        if (aa != null){
            ListView lv = (ListView) getView().findViewById(R.id.devListView);
            lv.setAdapter(aa);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        mListener.onDevListFragmentPause();//Cet fonction va "désenregistrer" le broadcast receiver dès que l'on détruira le Fragment, c-à-d quand on change de Fragment, quand on met l'appli en pause (equiv. onPause), ou quand on détruit l'appli (equiv. onDestroy)
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_devices_list, container, false);
        ListView lv = (ListView) rootView.findViewById(R.id.devListView);
        lv.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick (AdapterView<?> l, View v,int position, long id){
                mListener.onDevListItemClicked(l,v,position,id);
            }
        });
        // Inflate the layout for this fragment
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        /*if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }*/
    }

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
        ArrayAdapter onDevListFragmentCreate();//Cette méthode sera utilisée dans la méthode onCreate, donc qd on crée un fragment DevicesList
        void onDevListFragmentPause();
        void onDevListItemClicked(AdapterView<?> l, View v,int position, long id);
    }
    /*@Override
    public void onListItemClick(ListView l, View v, int position, long id){
        mListener.onDevListFragmentCreate();
    }*/

}
