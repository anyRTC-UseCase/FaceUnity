package org.ar.beauty_android.activity;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;

import com.faceunity.FURenderer;
import org.ar.beauty_android.R;
import org.ar.beauty_android.ui.control.BeautyControlView;

import org.ar.rtc.Constants;
import org.ar.rtc.IRtcEngineEventHandler;
import org.ar.rtc.RtcEngine;
import org.ar.rtc.VideoEncoderConfiguration;
import org.ar.rtc.mediaio.IVideoFrameConsumer;
import org.ar.rtc.mediaio.IVideoSource;
import org.ar.rtc.video.VideoCanvas;

/**
 * 美颜界面
 * Created by tujh on 2018/1/31.
 */

public class FUBeautyActivity extends FUBaseActivity {
    public final static String TAG = FUBeautyActivity.class.getSimpleName();
    private BeautyControlView mBeautyControlView;

    private String userId = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));

    private String CHANNEL_NAME = "808081";
    //APPID 需要去官网进行注册
    private String APPID = "";
    private RtcEngine mRtcEngine;
    private IVideoFrameConsumer consumer;

    @Override
    protected void onCreate() {
        mBottomViewStub.setLayoutResource(R.layout.layout_fu_beauty);
        mBeautyControlView = (BeautyControlView) mBottomViewStub.inflate();
        mBeautyControlView.setOnFUControlListener(mFURenderer);
        mBeautyControlView.setOnBottomAnimatorChangeListener(new BeautyControlView.OnBottomAnimatorChangeListener() {
            private int px166 = getResources().getDimensionPixelSize(R.dimen.x160);
            private int px156 = getResources().getDimensionPixelSize(R.dimen.x156);
            private int px402 = getResources().getDimensionPixelSize(R.dimen.x402);
            private int diff = px402 - px156;

            @Override
            public void onBottomAnimatorChangeListener(float showRate) {
                // 收起 1-->0，弹出 0-->1
                double v = px166 * (1 - showRate * 0.265);
                mTakePicBtn.setDrawWidth((int) v);
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mTakePicBtn.getLayoutParams();
                params.bottomMargin = (int) (px156 + diff * showRate);
                mTakePicBtn.setLayoutParams(params);
                mTakePicBtn.setDrawWidth((int) (getResources().getDimensionPixelSize(R.dimen.x166)
                        * (1 - showRate * 0.265)));
            }
        });
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mTakePicBtn.getLayoutParams();
        params.bottomToTop = ConstraintLayout.LayoutParams.UNSET;
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        params.bottomMargin = getResources().getDimensionPixelSize(R.dimen.x156);
        int size = getResources().getDimensionPixelSize(R.dimen.x160);
        mTakePicBtn.setLayoutParams(params);
        mTakePicBtn.setDrawWidth(size);
        mTakePicBtn.bringToFront();
        initializeEngine();
        setupVideoConfig();
        joinChannel();
        setupLocalVideo();
    }

    @Override
    public int onDrawFrame(byte[] cameraNV21Byte, int cameraTextureId, int cameraWidth, int cameraHeight, float[] mvpMatrix, float[] texMatrix, long timeStamp) {

        if (mRtcEngine != null) {
            if (consumer != null) {
                mFURenderer.onDrawFrame(cameraNV21Byte, cameraWidth, cameraHeight);
                consumer.consumeByteArrayFrame(cameraNV21Byte, 3, cameraWidth, cameraHeight, 270, timeStamp);
            }
//            ARVideoFrame videoFrame =new ARVideoFrame();
//            videoFrame.buf=cameraNV21Byte;
//            videoFrame.format=ARVideoFrame.FORMAT_NV21;
//            videoFrame.bufType=ARVideoFrame.BUFFER_TYPE_BUFFER;
//            videoFrame.stride=cameraWidth;
//            videoFrame.height=cameraHeight;
//            videoFrame.rotation=270;
//            videoFrame.timeStamp=timeStamp;
//            mRtcEngine.pushExternalVideoFrame(videoFrame);
            Log.d("sendData", cameraNV21Byte.length + "");
        }
        return super.onDrawFrame(cameraNV21Byte, cameraTextureId, cameraWidth, cameraHeight, mvpMatrix, texMatrix, timeStamp);
    }

    private void joinChannel() {
        mRtcEngine.joinChannel("", CHANNEL_NAME, "Extra Optional Data", userId);
    }

    private void initializeEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), APPID, mRtcEventHandler);
//            JSONObject jsonParams = new JSONObject();
//            try {
//                jsonParams.put("Cmd", "ConfPriCloudAddr");
//                jsonParams.put("ServerAdd", "pro.gateway.agrtc.cn");
//                jsonParams.put("Port", 6080);
//                mRtcEngine.setParameters(jsonParams.toString());
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onJoinChannelSuccess(String channel, final String uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("ANYRTC", "加入房间成功啦~ 你的ID是：" + (uid));
                }
            });
        }


        @Override
        public void onUserJoined(String uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }

        @Override
        public void onFirstRemoteVideoDecoded(final String uid, int width, int height, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setupRemoteVideo(uid);
                }
            });
        }

        @Override
        public void onUserOffline(final String uid, int reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    removeRemoteVideo(uid);
                }
            });
        }

        @Override
        public void onRemoteVideoStateChanged(String uid, int state, int reason, int elapsed) {
            super.onRemoteVideoStateChanged(uid, state, reason, elapsed);

        }

        @Override
        public void onRemoteAudioStateChanged(String uid, int state, int reason, int elapsed) {
            super.onRemoteAudioStateChanged(uid, state, reason, elapsed);
        }

        @Override
        public void onWarning(int warn) {
            super.onWarning(warn);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }

        @Override
        public void onError(int err) {
            super.onError(err);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }
    };

    private void setupLocalVideo() {
        TextureView mLocalView = RtcEngine.CreateRendererView(getBaseContext());
        arVideoGroup.addView("local",mLocalView,true);
        mRtcEngine.setupLocalVideo(new VideoCanvas(mLocalView, Constants.RENDER_MODE_HIDDEN,CHANNEL_NAME, userId,Constants.VIDEO_MIRROR_MODE_AUTO));
        mRtcEngine.startPreview();
    }

    private void removeLocal(){
        arVideoGroup.removeView("local");
    }

    private void leaveChannel() {
        if (mRtcEngine!=null) {
            mRtcEngine.leaveChannel();
        }
    }

    private void setupRemoteVideo(String uid) {
        if (arVideoGroup.getM_list_video().size()<=3){
            TextureView mRemoteView = RtcEngine.CreateRendererView(getBaseContext());
            arVideoGroup.addView(uid,mRemoteView,true);
            mRtcEngine.setRemoteVideoStreamType(uid,0);
            mRtcEngine.setupRemoteVideo(new VideoCanvas(mRemoteView, Constants.RENDER_MODE_HIDDEN,CHANNEL_NAME, uid,Constants.VIDEO_MIRROR_MODE_DISABLED));
        }else {
            TextureView mRemoteView = RtcEngine.CreateRendererView(getBaseContext());
            arVideoGroup.addView(uid,mRemoteView,false);
            mRtcEngine.setRemoteVideoStreamType(uid,1);
            mRtcEngine.setupRemoteVideo(new VideoCanvas(mRemoteView, Constants.RENDER_MODE_HIDDEN,CHANNEL_NAME, uid,Constants.VIDEO_MIRROR_MODE_DISABLED));
        }
    }

    private void removeRemoteVideo(String uid) {
        arVideoGroup.removeView(uid);
    }

    private void setupVideoConfig() {
        mRtcEngine.setVideoSource(new VideoSource());
        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
    }

    public class VideoSource implements IVideoSource {

        public VideoSource() {
        }

        @Override
        public boolean onInitialize(IVideoFrameConsumer con) {
            consumer = con;
            Log.d("VideoSource", "onInitialize");
            return true;
        }

        @Override
        public boolean onStart() {
            Log.d("VideoSource", "onStart");
            return true;
        }

        @Override
        public void onStop() {
            Log.d("VideoSource", "onStop");
        }

        @Override
        public void onDispose() {
            consumer = null;
            Log.d("VideoSource", "onDispose");
        }

        @Override
        public int getBufferType() {
            return 1;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mBeautyControlView.isShown()) {
            mBeautyControlView.hideBottomLayoutAnimator();
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected FURenderer initFURenderer() {
        return new FURenderer
                .Builder(this)
                .maxFaces(4)
                .inputImageOrientation(mFrontCameraOrientation)
                .inputTextureType(FURenderer.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE)
                .setOnFUDebugListener(this)
                .setOnTrackingStatusChangedListener(this)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBeautyControlView != null) {
            mBeautyControlView.onResume();
        }
    }

    @Override
    protected boolean isOpenPhotoVideo() {
        return true;
    }

    @Override
    protected boolean isOpenResolutionChange() {
        return true;
    }

    @Override
    protected void onSelectPhotoVideoClick() {
        super.onSelectPhotoVideoClick();
        Intent intent = new Intent(FUBeautyActivity.this, SelectDataActivity.class);
        intent.putExtra(SelectDataActivity.SELECT_DATA_KEY, TAG);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        leaveChannel();
    }
}
