package jp.co.homuhomu.camera2test;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;

import jp.co.homuhomu.camera2test.controller.Camera2Controller;
import jp.co.homuhomu.camera2test.dialog.ConfirmationDialog;
import jp.co.homuhomu.camera2test.dialog.ErrorDialog;

public class Camera2Fragment extends Fragment {

    private static final String FRAGMENT_DIALOG = "dialog";
    public static final int REQUEST_PERMISSION = 1;
    /**
     * An {@link AutoFitTextureView} for camera preview.
     */
    private AutoFitTextureView textureView;


    private Camera2Controller camera2Controller;
    private File file;

    public static Camera2Fragment newInstance() {
        Bundle args = new Bundle();
        Camera2Fragment fragment = new Camera2Fragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera2_basic, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        textureView = view.findViewById(R.id.texture);
        camera2Controller = new Camera2Controller(textureView, getActivity());
        view.findViewById(R.id.picture).setOnClickListener(v -> onClickCameraButton());
        view.findViewById(R.id.info).setOnClickListener(v -> onClickInfoButton());
        view.findViewById(R.id.previewShot).setOnClickListener(v -> camera2Controller.takePreviewShot());
        camera2Controller.setOnTakePreviewListener(() -> CameraActivity.showToast("preview taken"));
    }

    private void onClickCameraButton() {
        camera2Controller.takePicture();
    }

    private void onClickInfoButton() {
        Activity activity = getActivity();
        if (null != activity) {
            new AlertDialog.Builder(activity)
                    .setMessage(R.string.intro_message)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ErrorDialog.newInstance(getString(R.string.request_permission)).show(getFragmentManager(), FRAGMENT_DIALOG);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        camera2Controller.startBackgroundThread();
        if (textureView.isAvailable()) {
            camera2Controller.openCamera(textureView.getWidth(), textureView.getHeight());
        } else {
            textureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) &&
                shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new ConfirmationDialog().show(getChildFragmentManager(), FRAGMENT_DIALOG);
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        }
    }

    /**
     * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a
     * {@link TextureView}.
     */
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermission();
                    return;
                }
            }
            camera2Controller.openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
            camera2Controller.configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        }
    };

    @Override
    public void onPause() {
        camera2Controller.closeCamera();
        camera2Controller.stopBackgroundThread();
        super.onPause();
    }


}
