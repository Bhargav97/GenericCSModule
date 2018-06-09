package com.couchsurf.bhargav.couchsurfing;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RequestsFragment extends Fragment {
    View v;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = getLayoutInflater().inflate(R.layout.request_fragment_layout,container,false);
        ((MainActivity) getActivity()).setActionBarTitle("Status");
        ((MainActivity) getActivity()).setNavItem(R.id.navstatus);


        return v;
    }
}
