package org.ar.beauty_android.activity;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;

import com.faceunity.FURenderer;
import org.ar.beauty_android.R;
import org.ar.beauty_android.ui.SwitchConfig;
import org.ar.beauty_android.ui.control.BeautifyBodyControlView;

/**
 * 美体界面
 *
 * @author Richie on 2019.07.31
 */
public class BeautifyBodyActivity extends FUBaseActivity {
    public static final String TAG = "BeautifyBodyActivity";

    @Override
    protected void onCreate() {
        mBottomViewStub.setLayoutResource(R.layout.layout_fu_beautify_body);
        BeautifyBodyControlView view = (BeautifyBodyControlView) mBottomViewStub.inflate();
        view.setOnFUControlListener(mFURenderer);

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mTakePicBtn.getLayoutParams();
        params.bottomToTop = ConstraintLayout.LayoutParams.UNSET;
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        params.bottomMargin = getResources().getDimensionPixelSize(R.dimen.x304);
        int size = getResources().getDimensionPixelSize(R.dimen.x122);
        mTakePicBtn.setLayoutParams(params);
        mTakePicBtn.setDrawWidth(size);
        mIsTrackingText.setText(R.string.toast_not_detect_body);
    }

    @Override
    protected FURenderer initFURenderer() {
        return new FURenderer
                .Builder(this)
                .maxFaces(1)
                .inputImageOrientation(mFrontCameraOrientation)
                .setUseBeautifyBody(true)
                .setLoadAiHumanPose(true)
                .inputTextureType(FURenderer.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE)
                .setOnFUDebugListener(this)
                .setOnTrackingStatusChangedListener(this)
                .build();
    }

    @Override
    protected boolean isOpenPhotoVideo() {
        return SwitchConfig.ENABLE_LOAD_EXTERNAL_FILE_TO_BODY;
    }

    @Override
    protected void onSelectPhotoVideoClick() {
        super.onSelectPhotoVideoClick();
        Intent intent = new Intent(BeautifyBodyActivity.this, SelectDataActivity.class);
        intent.putExtra(SelectDataActivity.SELECT_DATA_KEY, BeautifyBodyActivity.TAG);
        startActivity(intent);
    }
}
