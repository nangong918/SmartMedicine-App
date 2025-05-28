package com.czy.customviewlib.view;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.czy.customviewlib.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GlobalDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GlobalDialogFragment extends DialogFragment {

    public static GlobalDialogFragment newInstance(String message) {
        GlobalDialogFragment fragment = new GlobalDialogFragment();
        Bundle args = new Bundle();
        args.putString("message", message);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_global_dialog, null);
        TextView messageTextView = view.findViewById(R.id.dialog_message);
        Button okButton = view.findViewById(R.id.button_ok);

        // 设置消息
        String message = getArguments() != null ? getArguments().getString("message") : "Default Message";
        messageTextView.setText(message);

        okButton.setOnClickListener(v -> dismiss());

        builder.setView(view);
        return builder.create();
    }
}