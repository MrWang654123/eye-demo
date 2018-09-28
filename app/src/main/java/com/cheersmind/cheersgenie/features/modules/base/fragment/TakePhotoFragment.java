package com.cheersmind.cheersgenie.features.modules.base.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.cheersmind.cheersgenie.R;

import org.devio.takephoto.app.TakePhoto;
import org.devio.takephoto.app.TakePhotoImpl;
import org.devio.takephoto.compress.CompressConfig;
import org.devio.takephoto.model.CropOptions;
import org.devio.takephoto.model.InvokeParam;
import org.devio.takephoto.model.LubanOptions;
import org.devio.takephoto.model.TContextWrap;
import org.devio.takephoto.model.TResult;
import org.devio.takephoto.model.TakePhotoOptions;
import org.devio.takephoto.permission.InvokeListener;
import org.devio.takephoto.permission.PermissionManager;
import org.devio.takephoto.permission.TakePhotoInvocationHandler;

import java.io.File;

/**
 * 拍照或从相册选择图片的基础类
 */
public abstract class TakePhotoFragment extends LazyLoadFragment implements TakePhoto.TakeResultListener, InvokeListener {

    private static final String TAG = org.devio.takephoto.app.TakePhotoFragment.class.getName();
    private InvokeParam invokeParam;
    private TakePhoto takePhoto;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(getActivity(), type, invokeParam, this);
    }

    /**
     * 获取TakePhoto实例
     *
     * @return
     */
    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        return takePhoto;
    }

    @Override
    public void takeSuccess(TResult result) {
        Log.i(TAG, "takeSuccess：" + result.getImage().getCompressPath());
    }

    @Override
    public void takeFail(TResult result, String msg) {
        Log.i(TAG, "takeFail:" + msg);
    }

    @Override
    public void takeCancel() {
        Log.i(TAG, getResources().getString(org.devio.takephoto.R.string.msg_operation_canceled));
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

    //选择照片方式：从相册中选取
    public static final int TAKE_PHOTOS_TYPE_PICK_PHOTO = 1;
    //选择照片方式：拍照
    public static final int TAKE_PHOTOS_TYPE_CAMERA = 2;


    /**
     * 弹出选择修改头像方式的对话框
     */
    protected void popupModifyProfileWindows(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.popup_window_modify_profile, null);
        final Dialog dialog = new Dialog(context, R.style.base_dialog_bottom_full);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setContentView(view);

        WindowManager.LayoutParams lp = window.getAttributes(); // 获取对话框当前的参数值
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//宽度占满屏幕
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        window.setWindowAnimations(R.style.BottomDialog_Animation);//动画
        dialog.show();

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        //从相册中选取
        Button btnPhotos = view.findViewById(R.id.btn_photos);
        //拍照
        Button btnCamera = view.findViewById(R.id.btn_camera);
        //取消
        Button btnCancel = view.findViewById(R.id.btn_cancel);

        btnPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //相册
                doPickPhotos(TAKE_PHOTOS_TYPE_PICK_PHOTO, getTakePhoto());
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //拍照
                doPickPhotos(TAKE_PHOTOS_TYPE_CAMERA, getTakePhoto());
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }


    /**
     * 从相册选取照片
     * @param type 1：从相册中选取；2、拍照
     * @param takePhoto
     */
    protected void doPickPhotos(int type, TakePhoto takePhoto) {
        File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".png");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Uri imageUri = Uri.fromFile(file);

        configCompress(takePhoto);
        configTakePhotoOption(takePhoto);

        //从相册中选取
        if (type == TAKE_PHOTOS_TYPE_PICK_PHOTO) {
            int limit = 1;
            if (limit > 1) {
                //是否裁切
                boolean doCrop = true;
                if (doCrop) {
                    takePhoto.onPickMultipleWithCrop(limit, getCropOptions());
                } else {
                    takePhoto.onPickMultiple(limit);
                }
                return;
            }

            //从文件选，否则从相册选
            boolean isFromFile = false;
            if (isFromFile) {
                //是否裁切
                boolean doCrop = true;
                if (doCrop) {
                    takePhoto.onPickFromDocumentsWithCrop(imageUri, getCropOptions());
                } else {
                    takePhoto.onPickFromDocuments();
                }
                return;
            } else {
                //是否裁切
                boolean doCrop = true;
                if (doCrop) {
                    takePhoto.onPickFromGalleryWithCrop(imageUri, getCropOptions());
                } else {
                    takePhoto.onPickFromGallery();
                }
            }

        } else {
            //拍照
            //是否裁切
            boolean doCrop = true;
            if (doCrop) {
                takePhoto.onPickFromCaptureWithCrop(imageUri, getCropOptions());
            } else {
                takePhoto.onPickFromCapture(imageUri);
            }
        }

    }


    /**
     * 照片压缩配置
     * @param takePhoto
     */
    protected void configCompress(TakePhoto takePhoto) {
        //最大体积
        int maxSize = 1024000;//B
        //宽高
        int width = 800;
        int height = 800;
        //显示压缩进度条
        boolean showProgressBar = false;
        //拍照后保存原图
        boolean enableRawFile = true;
        CompressConfig config;
        //使用TakePhoto自带压缩工具
        boolean userDefault = true;
        if (userDefault) {
            config = new CompressConfig.Builder().setMaxSize(maxSize)
                    .setMaxPixel(width >= height ? width : height)
                    .enableReserveRaw(enableRawFile)
                    .create();
        } else {
            LubanOptions option = new LubanOptions.Builder().setMaxHeight(height).setMaxWidth(width).setMaxSize(maxSize).create();
            config = CompressConfig.ofLuban(option);
            config.enableReserveRaw(enableRawFile);
        }

        takePhoto.onEnableCompress(config, showProgressBar);

    }

    /**
     * 照片其他配置
     * @param takePhoto
     */
    protected void configTakePhotoOption(TakePhoto takePhoto) {
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        //使用TakePhoto自带相册
        builder.setWithOwnGallery(false);
        //纠正拍照的照片旋转角度
        builder.setCorrectImage(true);
        takePhoto.setTakePhotoOptions(builder.create());

    }

    /**
     * 照片裁剪配置
     * @return
     */
    protected CropOptions getCropOptions() {

        int height = 800;
        int width = 800;
        //是否使用TakePhoto自带裁剪工具
        boolean withWonCrop = true;

        CropOptions.Builder builder = new CropOptions.Builder();
        //宽和高
        builder.setAspectX(width).setAspectY(height);
        //宽或者高
//        builder.setOutputX(width).setOutputY(height);
        builder.setWithOwnCrop(withWonCrop);
        return builder.create();
    }


}
