package com.couchsurf.bhargav.couchsurfing;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class GuestOrHostFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.guest_or_host_layout, container, false);
        Button host = v.findViewById(R.id.buttonGH1);
        Button traveller = v.findViewById(R.id.buttonGH2);
        final LinearLayout mainLayout = v.findViewById(R.id.mainLayoutGHFragment);
        host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GraphDFSFragment.setMode(false);
                ExtraInfoForm.setUserType(true);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragContainerImage, new TravellerImageView(), "TI").commit();
                ExtraInfoForm.setColor(getActivity().getWindow(),getActivity(),R.color.traveller);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragContainerUserInfo, new HostInfoFragment(), "HINP").commit();
                // getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragContainerImage, new GraphDFSInputOneFragment(), "INPUTONE").commit();
            }
        });
        traveller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GraphDFSFragment.setMode(true);
                ExtraInfoForm.setUserType(false);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragContainerImage, new TravellerImageView(), "TI").commit();
                ExtraInfoForm.setColor(getActivity().getWindow(),getActivity(),R.color.traveller);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragContainerUserInfo, new TravellerInfoFragment(), "TINP").commit();
            }
        });
        return v;
    }
}
