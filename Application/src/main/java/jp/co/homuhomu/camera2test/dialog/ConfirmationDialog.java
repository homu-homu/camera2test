package jp.co.homuhomu.camera2test.dialog;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import jp.co.homuhomu.camera2test.Camera2Fragment;
import jp.co.homuhomu.camera2test.R;

/**
 * Shows OK/Cancel confirmation dialog about camera permission.
 */
public class ConfirmationDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Fragment parent = getParentFragment();
        return new AlertDialog.Builder(getActivity())
                .setMessage(R.string.request_permission)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> parent.requestPermissions(
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Camera2Fragment.REQUEST_PERMISSION))
                .setNegativeButton(android.R.string.cancel,
                        (dialog, which) -> {
                            Activity activity = parent.getActivity();
                            if (activity != null) {
                                activity.finish();
                            }
                        })
                .create();
    }
}
