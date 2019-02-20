package com.app.shovelerapp.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.app.shovelerapp.R;
import com.app.shovelerapp.activity.UploadPhotoNotification;

/**
 * Created by supriya.n on 15-06-2016.
 */
public class JobDoneNotification extends Fragment {
    private Button mAddJobPicButton,mGoToJobStatusButton;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.notification_xml, container, false);
        mAddJobPicButton= (Button) v.findViewById(R.id.add_job_done_pic);
        mGoToJobStatusButton= (Button) v.findViewById(R.id.go_to_job_status);
        mAddJobPicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Fragment fragment = new UploadPhotoNotification();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.container, fragment,"UploadPhotoNotification");
                fragmentTransaction.addToBackStack(UploadPhotoNotification.class.getName());
                fragmentTransaction.commit();*/
            }
        });

        mGoToJobStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Fragment fragment = new JobStatusFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.container, fragment,"JobStatusFragment");
                fragmentTransaction.addToBackStack(JobStatusFragment.class.getName());
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentTransaction.commit();*/
            }
        });

        return v;
    }
}
