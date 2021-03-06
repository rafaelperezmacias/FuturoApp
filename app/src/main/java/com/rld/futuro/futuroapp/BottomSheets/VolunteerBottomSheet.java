package com.rld.futuro.futuroapp.BottomSheets;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.rld.futuro.futuroapp.CameraPreview;
import com.rld.futuro.futuroapp.Fragments.VolunteerBS.ContactFragment;
import com.rld.futuro.futuroapp.Fragments.VolunteerBS.OtherFragment;
import com.rld.futuro.futuroapp.Fragments.VolunteerBS.PersonalFragment;
import com.rld.futuro.futuroapp.Fragments.VolunteerBS.PolicyFragment;
import com.rld.futuro.futuroapp.Fragments.VolunteerBS.SectionFragment;
import com.rld.futuro.futuroapp.MainActivity;
import com.rld.futuro.futuroapp.Models.FileManager;
import com.rld.futuro.futuroapp.Models.Volunteer;
import com.rld.futuro.futuroapp.R;

import java.util.ArrayList;

public class VolunteerBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetBehavior bottomSheetBehavior;

    private FragmentManager fragmentManager;
    private Fragment currentFragment;

    private Volunteer volunteer;

    private MainActivity mainActivity;

    public VolunteerBottomSheet(MainActivity mainActivity)
    {
        volunteer = new Volunteer();
        this.mainActivity = mainActivity;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog bottomSheet = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.fragment_volunter_bottom_sheet, null);
        bottomSheet.setContentView(view);
        bottomSheetBehavior = BottomSheetBehavior.from((View) (view.getParent()));
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if( BottomSheetBehavior.STATE_HIDDEN == i ) {
                    dismiss();
                }
            }

            @Override public void onSlide(@NonNull View view, float v) { }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Toolbar toolbar = view.findViewById(R.id.volunter_bs_toolbar);
            toolbar.setElevation(12.0f);
        }

        MaterialButton btnSave = view.findViewById(R.id.fvbs_save_btn);
        ImageButton btnClose = view.findViewById(R.id.fvbs_close_btn);
        TextView txtSubtitle = view.findViewById(R.id.fvbs_subtitle);

        PersonalFragment personalFragment = new PersonalFragment(volunteer);
        ContactFragment contactFragment = new ContactFragment(volunteer, mainActivity);
        OtherFragment otherFragment = new OtherFragment(volunteer);
        SectionFragment sectionFragment = new SectionFragment(volunteer, mainActivity);
        PolicyFragment policyFragment = new PolicyFragment();
        currentFragment = personalFragment;
        txtSubtitle.setText(getString(R.string.fbs_step1));

        fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction().add(R.id.volunteer_bs_container, personalFragment).commit();
        fragmentManager.beginTransaction().add(R.id.volunteer_bs_container, contactFragment).hide(contactFragment).commit();
        fragmentManager.beginTransaction().add(R.id.volunteer_bs_container, otherFragment).hide(otherFragment).commit();
        fragmentManager.beginTransaction().add(R.id.volunteer_bs_container, sectionFragment).hide(sectionFragment).commit();
        fragmentManager.beginTransaction().add(R.id.volunteer_bs_container, policyFragment).hide(policyFragment).commit();

        btnClose.setOnClickListener(v -> {
            if ( currentFragment == personalFragment ) {
                new MaterialAlertDialogBuilder(mainActivity)
                        .setTitle("Alerta")
                        .setMessage("??Esta seguro de querer cancelar el proceso?")
                        .setNegativeButton("Quedarme", (dialog, which) -> {

                        })
                        .setPositiveButton("Si", (dialog, which) -> {
                            mainActivity.enableBtnCarga();
                            dismiss();
                        })
                        .show();
            } else if ( currentFragment == contactFragment ) {

                btnClose.setImageResource(R.drawable.ic_sharp_close_24);
                txtSubtitle.setText(getString(R.string.fbs_step1));
                showFragment(personalFragment, fragmentManager);

            } else if ( currentFragment == sectionFragment ) {

                txtSubtitle.setText(getString(R.string.fbs_step2));
                showFragment(contactFragment, fragmentManager);

            } else if ( currentFragment == otherFragment ) {

                txtSubtitle.setText(getString(R.string.fbs_step3));
                showFragment(sectionFragment, fragmentManager);

            } else if ( currentFragment == policyFragment ) {
                btnSave.setText(getString(R.string.fbs_continue));
                txtSubtitle.setText(getString(R.string.fbs_step4));
                showFragment(otherFragment, fragmentManager);
            }

        });

        btnSave.setOnClickListener(v -> {
            if ( currentFragment == personalFragment ) {

                if ( !personalFragment.isComplete() ) {
                    return;
                }

                showFragment(contactFragment, fragmentManager);
                personalFragment.setVolunteer();
                btnSave.setText(getString(R.string.fbs_continue));
                btnClose.setImageResource(R.drawable.ic_baseline_arrow_back_24);
                txtSubtitle.setText(getString(R.string.fbs_step2));

            } else if ( currentFragment == contactFragment ) {

                if ( !contactFragment.isComplete() ) {
                    return;
                }

                showFragment(sectionFragment, fragmentManager);
                contactFragment.getState();
                contactFragment.setVolunteer();
                sectionFragment.setState();
                sectionFragment.setInfo();
                btnSave.setText(getString(R.string.fbs_continue));
                txtSubtitle.setText(getString(R.string.fbs_step3));

            } else if ( currentFragment == sectionFragment ) {

                if ( !sectionFragment.isComplete() ) {
                    return;
                }

                showFragment(otherFragment, fragmentManager);
                sectionFragment.setVolunter();
                txtSubtitle.setText(getString(R.string.fbs_step4));
                btnSave.setText(getString(R.string.fbs_continue));
                btnClose.setImageResource(R.drawable.ic_baseline_arrow_back_24);

            } else if ( currentFragment == otherFragment ) {

                if ( !otherFragment.isComplete() ) {
                    return;
                }

                otherFragment.setVolunteer();
                showFragment(policyFragment, fragmentManager);
                btnSave.setText(getString(R.string.fbs_finish));

            } else if ( currentFragment == policyFragment ) {
                if ( policyFragment.isComplete() ) {
                    Toast.makeText(mainActivity, "Confirme las politicas de privacidad", Toast.LENGTH_SHORT).show();
                } else {
                    btnSave.setEnabled(false);
                    new MaterialAlertDialogBuilder(mainActivity)
                            .setTitle("Alerta")
                            .setMessage("??Desea tomar una fotografia de la INE (Parte frontral)?")
                            .setNegativeButton("No", (dialog, which) -> {
                                mainActivity.addVoluteerWithoutImage(volunteer);
                                dismiss();
                            })
                            .setPositiveButton("Si, tomar foto", (dialog, which) -> {
                                mainActivity.addVolunteerWithImage(volunteer);
                                dismiss();
                            })
                            .setCancelable(false)
                            .show();
                }
            }

        });

        setCancelable(false);
        return bottomSheet;
    }

    private void showFragment(Fragment fragment, FragmentManager fragmentManager) {
        fragmentManager.beginTransaction().hide(currentFragment).show(fragment).commit();
        currentFragment = fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
}
