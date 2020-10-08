package com.imhc2.plumboard.mvvm.view.fragment.bottomsheets;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imhc2.plumboard.R;
import com.imhc2.plumboard.commons.eventbus.EventBus;
import com.imhc2.plumboard.commons.eventbus.Events;
import com.imhc2.plumboard.commons.querys.FirestoreQuerys;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by user on 2018-09-10.
 */

public class BankFragment extends BottomSheetDialogFragment {


    Unbinder unbinder;
    @BindView(R.id.fragment_bank_lv) ListView mLv;
    FirebaseFirestore firestore;


    public BankFragment() {

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog d = super.onCreateDialog(savedInstanceState);
        // view hierarchy is inflated after dialog is shown

        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                FrameLayout bottomSheet =d.findViewById(android.support.design.R.id.design_bottom_sheet);
                // Right here!
                final BottomSheetBehavior behaviour = BottomSheetBehavior.from(bottomSheet);
                behaviour.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                            dismiss();
                        }

                        if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                            behaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                    }
                });

            }
        });
        return d;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bank, container, false);
        unbinder = ButterKnife.bind(this, v);

        firestore = FirebaseFirestore.getInstance();

        FirestoreQuerys.INSTANCE.getBankList(firestore)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if(getActivity() !=null){
                        List<String> bankList = (List<String>) task.getResult().get("list");
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, bankList);

                        mLv.setAdapter(arrayAdapter);
                        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                EventBus.get().post(new Events.Bank(bankList.get(position)));
                                dismiss();
                            }
                        });
                    }
                }
            }
        });

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
