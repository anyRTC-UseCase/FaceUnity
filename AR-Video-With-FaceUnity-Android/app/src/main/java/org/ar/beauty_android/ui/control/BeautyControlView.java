package org.ar.beauty_android.ui.control;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.faceunity.OnFUControlListener;
import com.faceunity.entity.Filter;
import org.ar.beauty_android.R;
import org.ar.beauty_android.entity.BeautyParameterModel;
import org.ar.beauty_android.entity.FilterEnum;
import org.ar.beauty_android.ui.CheckGroup;
import org.ar.beauty_android.ui.TouchStateImageView;
import org.ar.beauty_android.ui.beautybox.BaseBeautyBox;
import org.ar.beauty_android.ui.beautybox.BeautyBoxGroup;
import org.ar.beauty_android.ui.dialog.BaseDialogFragment;
import org.ar.beauty_android.ui.dialog.ConfirmDialogFragment;
import org.ar.beauty_android.ui.seekbar.DiscreteSeekBar;
import org.ar.beauty_android.utils.DecimalUtils;
import org.ar.beauty_android.utils.OnMultiClickListener;
import org.ar.beauty_android.utils.ToastUtil;

import java.util.List;

import static org.ar.beauty_android.entity.BeautyParameterModel.DEFAULT_FILTER_LEVEL;
import static org.ar.beauty_android.entity.BeautyParameterModel.STR_FILTER_LEVEL;
import static org.ar.beauty_android.entity.BeautyParameterModel.getValue;
import static org.ar.beauty_android.entity.BeautyParameterModel.isOpen;
import static org.ar.beauty_android.entity.BeautyParameterModel.sFilter;
import static org.ar.beauty_android.entity.BeautyParameterModel.sFilterLevel;
import static org.ar.beauty_android.entity.BeautyParameterModel.setValue;

/**
 * 美颜
 * Created by tujh on 2017/8/15.
 */
public class BeautyControlView extends FrameLayout implements TouchStateImageView.OnTouchStateListener {
    private static final String TAG = "BeautyControlView";
    private Context mContext;

    private OnFUControlListener mOnFUControlListener;

    public void setOnFUControlListener(@NonNull OnFUControlListener onFUControlListener) {
        mOnFUControlListener = onFUControlListener;
    }

    private CheckGroup mBottomCheckGroup;
    private FrameLayout mFlFaceSkinItems;
    private BeautyBoxGroup mSkinBeautyBoxGroup;
    private BeautyBoxGroup mShapeBeautyBoxGroup;
    private FrameLayout mFlFaceShapeItems;
    private ImageView mIvRecoverFaceShape;
    private TextView mTvRecoverFaceShape;
    private ImageView mIvRecoverFaceSkin;
    private TextView mTvRecoverFaceSkin;
    private View mBottomView;
    private RecyclerView mFilterRecyclerView;
    private FilterRecyclerAdapter mFilterRecyclerAdapter;
    private DiscreteSeekBar mBeautySeekBar;
    private TouchStateImageView mIvCompare;
    private boolean isShown;
    private List<Filter> mFilters;
    // 默认选中第三个粉嫩
    private int mFilterPositionSelect = 2;

    public BeautyControlView(Context context) {
        this(context, null);
    }

    public BeautyControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BeautyControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mFilters = FilterEnum.getFiltersByFilterType();
        LayoutInflater.from(context).inflate(R.layout.layout_beauty_control, this);
        initView();
    }

    private void initView() {
        mBottomView = findViewById(R.id.cl_bottom_view);
        mBottomView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mIvCompare = findViewById(R.id.iv_compare);
        mIvCompare.setOnTouchStateListener(this);
        initViewBottomRadio();
        initViewSkinBeauty();
        initViewFaceShape();
        initViewFilterRecycler();
        initViewTop();
    }

    public void onResume() {
        updateViewSkinBeauty();
        updateViewFaceShape();
        updateViewFilterRecycler();
        hideBottomLayoutAnimator();
    }

    @Override
    public boolean isShown() {
        return isShown;
    }

    private void initViewBottomRadio() {
        mBottomCheckGroup = findViewById(R.id.beauty_radio_group);
        mBottomCheckGroup.setOnCheckedChangeListener(new CheckGroup.OnCheckedChangeListener() {
            private int checkedIdOld = View.NO_ID;

            @Override
            public void onCheckedChanged(CheckGroup group, int checkedId) {
                clickViewBottomRadio(checkedId);
                if (checkedId != View.NO_ID) {
                    switch (checkedId) {
                        case R.id.beauty_radio_skin_beauty: {
                            seekToSeekBar(mSkinBeautyBoxGroup.getCheckedBeautyBoxId());
                        }
                        break;
                        case R.id.beauty_radio_face_shape: {
                            seekToSeekBar(mShapeBeautyBoxGroup.getCheckedBeautyBoxId());
                        }
                        break;
                        case R.id.beauty_radio_filter: {
                            Float valueObj = sFilterLevel.get(STR_FILTER_LEVEL + sFilter.getName());
                            if (valueObj == null) {
                                valueObj = DEFAULT_FILTER_LEVEL;
                            }
                            if (mFilterPositionSelect > 0) {
                                seekToSeekBar(valueObj);
                            } else {
                                mBeautySeekBar.setVisibility(INVISIBLE);
                            }
                        }
                        break;
                        default:
                    }
                }
                if ((checkedId == View.NO_ID || checkedId == checkedIdOld) && checkedIdOld != View.NO_ID) {
                    int endHeight = (int) getResources().getDimension(R.dimen.x1);
                    int startHeight = (int) getResources().getDimension(R.dimen.x268);
                    changeBottomLayoutAnimator(startHeight, endHeight);
                    mIvCompare.setVisibility(INVISIBLE);
                    isShown = false;
                } else if (checkedId != View.NO_ID && checkedIdOld == View.NO_ID) {
                    int startHeight = (int) getResources().getDimension(R.dimen.x1);
                    int endHeight = (int) getResources().getDimension(R.dimen.x268);
                    changeBottomLayoutAnimator(startHeight, endHeight);
                    isShown = true;
                }
                checkedIdOld = checkedId;
            }
        });
    }

    private void updateViewSkinBeauty() {
        onChangeFaceBeautyLevel(R.id.beauty_box_blur_level);
        onChangeFaceBeautyLevel(R.id.beauty_box_color_level);
        onChangeFaceBeautyLevel(R.id.beauty_box_red_level);
        onChangeFaceBeautyLevel(R.id.beauty_box_pouch);
        onChangeFaceBeautyLevel(R.id.beauty_box_nasolabial);
        onChangeFaceBeautyLevel(R.id.beauty_box_eye_bright);
        onChangeFaceBeautyLevel(R.id.beauty_box_tooth_whiten);
    }

    private void initViewSkinBeauty() {
        mFlFaceSkinItems = findViewById(R.id.fl_face_skin_items);
        mIvRecoverFaceSkin = findViewById(R.id.iv_recover_face_skin);
        mIvRecoverFaceSkin.setOnClickListener(new OnMultiClickListener() {
            @Override
            protected void onMultiClick(View v) {
                ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance(mContext.getString(R.string.dialog_reset_avatar_model), new BaseDialogFragment.OnClickListener() {
                    @Override
                    public void onConfirm() {
                        // recover params
                        BeautyParameterModel.recoverFaceSkinToDefValue();
                        updateViewSkinBeauty();
                        int checkedId = mSkinBeautyBoxGroup.getCheckedBeautyBoxId();
                        seekToSeekBar(checkedId);
                        setRecoverFaceSkinEnable(false);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                confirmDialogFragment.show(((FragmentActivity) mContext).getSupportFragmentManager(), "ConfirmDialogFragmentReset");
            }
        });
        mTvRecoverFaceSkin = findViewById(R.id.tv_recover_face_skin);

        mSkinBeautyBoxGroup = findViewById(R.id.beauty_group_skin_beauty);
        mSkinBeautyBoxGroup.setOnCheckedChangeListener(new BeautyBoxGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(BeautyBoxGroup group, int checkedId) {
                mBeautySeekBar.setVisibility(INVISIBLE);
                seekToSeekBar(checkedId);
                onChangeFaceBeautyLevel(checkedId);
            }
        });
        checkFaceSkinChanged();
    }

    private void updateViewFaceShape() {
        onChangeFaceBeautyLevel(R.id.beauty_box_eye_enlarge);
        onChangeFaceBeautyLevel(R.id.beauty_box_cheek_thinning);
        onChangeFaceBeautyLevel(R.id.beauty_box_cheek_v);
        onChangeFaceBeautyLevel(R.id.beauty_box_cheek_narrow);
        onChangeFaceBeautyLevel(R.id.beauty_box_cheek_small);
        onChangeFaceBeautyLevel(R.id.beauty_box_intensity_chin);
        onChangeFaceBeautyLevel(R.id.beauty_box_intensity_forehead);
        onChangeFaceBeautyLevel(R.id.beauty_box_intensity_nose);
        onChangeFaceBeautyLevel(R.id.beauty_box_intensity_mouth);
        onChangeFaceBeautyLevel(R.id.beauty_box_canthus);
        onChangeFaceBeautyLevel(R.id.beauty_box_eye_space);
        onChangeFaceBeautyLevel(R.id.beauty_box_eye_rotate);
        onChangeFaceBeautyLevel(R.id.beauty_box_long_nose);
        onChangeFaceBeautyLevel(R.id.beauty_box_philtrum);
        onChangeFaceBeautyLevel(R.id.beauty_box_smile);
    }

    private void initViewFilterRecycler() {
        mFilterRecyclerView = findViewById(R.id.filter_recycle_view);
        mFilterRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mFilterRecyclerView.setAdapter(mFilterRecyclerAdapter = new FilterRecyclerAdapter());
        ((SimpleItemAnimator) mFilterRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    private void updateViewFilterRecycler() {
        mFilterRecyclerAdapter.setFilter(sFilter);
        mOnFUControlListener.onFilterNameSelected(sFilter.getName());
        float filterLevel = getFilterLevel(sFilter.getName());
        mOnFUControlListener.onFilterLevelSelected(filterLevel);
    }

    private void initViewFaceShape() {
        mFlFaceShapeItems = findViewById(R.id.fl_face_shape_items);
        mIvRecoverFaceShape = findViewById(R.id.iv_recover_face_shape);
        mIvRecoverFaceShape.setOnClickListener(new OnMultiClickListener() {
            @Override
            protected void onMultiClick(View v) {
                ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance(mContext.getString(R.string.dialog_reset_avatar_model), new BaseDialogFragment.OnClickListener() {
                    @Override
                    public void onConfirm() {
                        // recover params
                        BeautyParameterModel.recoverFaceShapeToDefValue();
                        updateViewFaceShape();
                        seekToSeekBar(mShapeBeautyBoxGroup.getCheckedBeautyBoxId());
                        setRecoverFaceShapeEnable(false);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                confirmDialogFragment.show(((FragmentActivity) mContext).getSupportFragmentManager(), "ConfirmDialogFragmentReset");
            }
        });
        mTvRecoverFaceShape = findViewById(R.id.tv_recover_face_shape);
        mShapeBeautyBoxGroup = findViewById(R.id.beauty_group_face_shape);
        mShapeBeautyBoxGroup.setOnCheckedChangeListener(new BeautyBoxGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(BeautyBoxGroup group, int checkedId) {
                mBeautySeekBar.setVisibility(GONE);
                seekToSeekBar(checkedId);
                onChangeFaceBeautyLevel(checkedId);
            }
        });
        checkFaceShapeChanged();
    }

    private void setRecoverFaceShapeEnable(boolean enable) {
        if (enable) {
            mIvRecoverFaceShape.setAlpha(1f);
            mTvRecoverFaceShape.setAlpha(1f);
        } else {
            mIvRecoverFaceShape.setAlpha(0.6f);
            mTvRecoverFaceShape.setAlpha(0.6f);
        }
        mIvRecoverFaceShape.setEnabled(enable);
        mTvRecoverFaceShape.setEnabled(enable);
    }

    private void setRecoverFaceSkinEnable(boolean enable) {
        if (enable) {
            mIvRecoverFaceSkin.setAlpha(1f);
            mTvRecoverFaceSkin.setAlpha(1f);
        } else {
            mIvRecoverFaceSkin.setAlpha(0.6f);
            mTvRecoverFaceSkin.setAlpha(0.6f);
        }
        mIvRecoverFaceSkin.setEnabled(enable);
        mTvRecoverFaceSkin.setEnabled(enable);
    }

    private void onChangeFaceBeautyLevel(int viewId) {
        if (viewId == View.NO_ID) {
            return;
        }
        View view = findViewById(viewId);
        if (view instanceof BaseBeautyBox) {
            boolean open = isOpen(viewId);
            ((BaseBeautyBox) view).setOpen(open);
        }
        if (mOnFUControlListener == null) {
            return;
        }
        switch (viewId) {
            case R.id.beauty_box_blur_level:
                Log.i(TAG, "onChangeFaceBeautyLevel: dong -1->");
                mOnFUControlListener.onBlurLevelSelected(getValue(viewId));
                break;
            case R.id.beauty_box_color_level:
                mOnFUControlListener.onColorLevelSelected(getValue(viewId));
                break;
            case R.id.beauty_box_red_level:
                mOnFUControlListener.onRedLevelSelected(getValue(viewId));
                break;
            case R.id.beauty_box_pouch:
                mOnFUControlListener.setRemovePouchStrength(getValue(viewId));
                break;
            case R.id.beauty_box_nasolabial:
                mOnFUControlListener.setRemoveNasolabialFoldsStrength(getValue(viewId));
                break;
            case R.id.beauty_box_eye_bright:
                mOnFUControlListener.onEyeBrightSelected(getValue(viewId));
                break;
            case R.id.beauty_box_tooth_whiten:
                mOnFUControlListener.onToothWhitenSelected(getValue(viewId));
                break;
            case R.id.beauty_box_eye_enlarge:
                mOnFUControlListener.onEyeEnlargeSelected(getValue(viewId));
                break;
            case R.id.beauty_box_cheek_thinning:
                mOnFUControlListener.onCheekThinningSelected(getValue(viewId));
                break;
            case R.id.beauty_box_cheek_narrow:
                mOnFUControlListener.onCheekNarrowSelected(getValue(viewId));
                break;
            case R.id.beauty_box_cheek_v:
                mOnFUControlListener.onCheekVSelected(getValue(viewId));
                break;
            case R.id.beauty_box_cheek_small:
                mOnFUControlListener.onCheekSmallSelected(getValue(viewId));
                break;
            case R.id.beauty_box_intensity_chin:
                mOnFUControlListener.onIntensityChinSelected(getValue(viewId));
                break;
            case R.id.beauty_box_intensity_forehead:
                mOnFUControlListener.onIntensityForeheadSelected(getValue(viewId));
                break;
            case R.id.beauty_box_intensity_nose:
                mOnFUControlListener.onIntensityNoseSelected(getValue(viewId));
                break;
            case R.id.beauty_box_intensity_mouth:
                mOnFUControlListener.onIntensityMouthSelected(getValue(viewId));
                break;
            case R.id.beauty_box_canthus:
                mOnFUControlListener.setCanthusIntensity(getValue(viewId));
                break;
            case R.id.beauty_box_eye_space:
                mOnFUControlListener.setEyeSpaceIntensity(getValue(viewId));
                break;
            case R.id.beauty_box_eye_rotate:
                mOnFUControlListener.setEyeRotateIntensity(getValue(viewId));
                break;
            case R.id.beauty_box_long_nose:
                mOnFUControlListener.setLongNoseIntensity(getValue(viewId));
                break;
            case R.id.beauty_box_philtrum:
                mOnFUControlListener.setPhiltrumIntensity(getValue(viewId));
                break;
            case R.id.beauty_box_smile:
                mOnFUControlListener.setSmileIntensity(getValue(viewId));
                break;
            default:
        }
    }

    private void initViewTop() {
        mBeautySeekBar = findViewById(R.id.beauty_seek_bar);
        mBeautySeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnSimpleProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                if (!fromUser) {
                    return;
                }

                float valueF = 1.0f * (value - seekBar.getMin()) / 100;
                int checkedCheckBoxId = mBottomCheckGroup.getCheckedCheckBoxId();
                if (checkedCheckBoxId == R.id.beauty_radio_skin_beauty) {
                    Log.i(TAG, "onProgressChanged: dong -1-> val="+value);
                    int skinCheckedId = mSkinBeautyBoxGroup.getCheckedBeautyBoxId();
                    setValue(skinCheckedId, valueF);
                    onChangeFaceBeautyLevel(skinCheckedId);
                    checkFaceSkinChanged();
                } else if (checkedCheckBoxId == R.id.beauty_radio_face_shape) {
                    Log.i(TAG, "onProgressChanged: dong -2-> val="+valueF+",id ="+mShapeBeautyBoxGroup.getCheckedBeautyBoxId());
                    setValue(mShapeBeautyBoxGroup.getCheckedBeautyBoxId(), valueF);
                    onChangeFaceBeautyLevel(mShapeBeautyBoxGroup.getCheckedBeautyBoxId());
                    checkFaceShapeChanged();
                } else if (checkedCheckBoxId == R.id.beauty_radio_filter) {
                    Log.i(TAG, "onProgressChanged: dong -3-> val="+value);
                    mFilterRecyclerAdapter.setFilterLevels(valueF);
                }
            }
        });
    }

    private void checkFaceShapeChanged() {
        if (BeautyParameterModel.checkIfFaceShapeChanged()) {
            setRecoverFaceShapeEnable(true);
        } else {
            setRecoverFaceShapeEnable(false);
        }
    }

    private void checkFaceSkinChanged() {
        if (BeautyParameterModel.checkIfFaceSkinChanged()) {
            setRecoverFaceSkinEnable(true);
        } else {
            setRecoverFaceSkinEnable(false);
        }
    }

    /**
     * 点击底部 tab
     *
     * @param viewId
     */
    private void clickViewBottomRadio(int viewId) {
        mFlFaceShapeItems.setVisibility(GONE);
        mFlFaceSkinItems.setVisibility(GONE);
        mFilterRecyclerView.setVisibility(GONE);
        mBeautySeekBar.setVisibility(GONE);
        if (viewId == R.id.beauty_radio_skin_beauty) {
            mFlFaceSkinItems.setVisibility(VISIBLE);
            mIvCompare.setVisibility(VISIBLE);
        } else if (viewId == R.id.beauty_radio_face_shape) {
            mFlFaceShapeItems.setVisibility(VISIBLE);
            int id = mShapeBeautyBoxGroup.getCheckedBeautyBoxId();
            seekToSeekBar(id);
            mIvCompare.setVisibility(VISIBLE);
        } else if (viewId == R.id.beauty_radio_filter) {
            mFilterRecyclerView.setVisibility(VISIBLE);
            mFilterRecyclerAdapter.setFilterProgress();
            mIvCompare.setVisibility(VISIBLE);
        }
    }

    private void seekToSeekBar(float value) {
        seekToSeekBar(value, 0, 100);
    }

    private void seekToSeekBar(float value, int min, int max) {
        mBeautySeekBar.setVisibility(VISIBLE);
        mBeautySeekBar.setMin(min);
        mBeautySeekBar.setMax(max);
        mBeautySeekBar.setProgress((int) (value * (max - min) + min));
    }

    private void seekToSeekBar(int checkedId) {
        if (checkedId == View.NO_ID) {
            return;
        }

        float value = getValue(checkedId);
        int min = 0;
        int max = 100;
        if (checkedId == R.id.beauty_box_intensity_chin || checkedId == R.id.beauty_box_intensity_forehead
                || checkedId == R.id.beauty_box_intensity_mouth || checkedId == R.id.beauty_box_long_nose
                || checkedId == R.id.beauty_box_eye_space || checkedId == R.id.beauty_box_eye_rotate
                || checkedId == R.id.beauty_box_philtrum) {
            min = -50;
            max = 50;
        }
        seekToSeekBar(value, min, max);
    }

    private void changeBottomLayoutAnimator(final int startHeight, final int endHeight) {
        if (mBottomLayoutAnimator != null && mBottomLayoutAnimator.isRunning()) {
            mBottomLayoutAnimator.end();
        }
        mBottomLayoutAnimator = ValueAnimator.ofInt(startHeight, endHeight).setDuration(150);
        mBottomLayoutAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int height = (int) animation.getAnimatedValue();
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mBottomView.getLayoutParams();
                params.height = height;
                mBottomView.setLayoutParams(params);
                if (mOnBottomAnimatorChangeListener != null) {
                    float showRate = 1.0f * (height - startHeight) / (endHeight - startHeight);
                    mOnBottomAnimatorChangeListener.onBottomAnimatorChangeListener(startHeight > endHeight ? 1 - showRate : showRate);
                }
                if (DecimalUtils.floatEquals(animation.getAnimatedFraction(), 1.0f) && startHeight < endHeight) {
                    mIvCompare.setVisibility(VISIBLE);
                }
            }
        });
        mBottomLayoutAnimator.start();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mOnFUControlListener == null) {
            return false;
        }
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            v.setAlpha(0.7f);
            mOnFUControlListener.setBeautificationOn(false);
        } else if (action == MotionEvent.ACTION_UP) {
            v.setAlpha(1f);
            mOnFUControlListener.setBeautificationOn(true);
        }
        return true;
    }

    public interface OnBottomAnimatorChangeListener {
        void onBottomAnimatorChangeListener(float showRate);
    }

    public void setOnBottomAnimatorChangeListener(OnBottomAnimatorChangeListener onBottomAnimatorChangeListener) {
        mOnBottomAnimatorChangeListener = onBottomAnimatorChangeListener;
    }

    private OnBottomAnimatorChangeListener mOnBottomAnimatorChangeListener;

    private ValueAnimator mBottomLayoutAnimator;

    public void hideBottomLayoutAnimator() {
        mBottomCheckGroup.check(View.NO_ID);
    }

    public void setFilterLevel(String filterName, float faceBeautyFilterLevel) {
        sFilterLevel.put(STR_FILTER_LEVEL + filterName, faceBeautyFilterLevel);
        if (mOnFUControlListener != null) {
            mOnFUControlListener.onFilterLevelSelected(faceBeautyFilterLevel);
        }
    }

    public float getFilterLevel(String filterName) {
        String key = STR_FILTER_LEVEL + filterName;
        Float level = sFilterLevel.get(key);
        if (level == null) {
            level = BeautyParameterModel.DEFAULT_FILTER_LEVEL;
            sFilterLevel.put(key, level);
        }
        setFilterLevel(filterName, level);
        return level;
    }

    class FilterRecyclerAdapter extends RecyclerView.Adapter<FilterRecyclerAdapter.HomeRecyclerHolder> {

        @Override
        public FilterRecyclerAdapter.HomeRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new FilterRecyclerAdapter.HomeRecyclerHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_beauty_control_recycler, parent, false));
        }

        @Override
        public void onBindViewHolder(FilterRecyclerAdapter.HomeRecyclerHolder holder, final int position) {
            final List<Filter> filters = mFilters;
            holder.filterImg.setImageResource(filters.get(position).getIconId());
            holder.filterName.setText(filters.get(position).getNameId());
            if (mFilterPositionSelect == position) {
                holder.filterImg.setBackgroundResource(R.drawable.control_filter_select);
                holder.filterName.setSelected(true);
            } else {
                holder.filterImg.setBackgroundResource(0);
                holder.filterName.setSelected(false);
            }
            holder.itemView.setOnClickListener(new OnMultiClickListener() {
                @Override
                protected void onMultiClick(View v) {
                    mFilterPositionSelect = position;
                    setFilterProgress();
                    notifyDataSetChanged();
                    if (mOnFUControlListener != null) {
                        sFilter = filters.get(mFilterPositionSelect);
                        mOnFUControlListener.onFilterNameSelected(sFilter.getName());
                        ToastUtil.showNormalToast(mContext, sFilter.getNameId());
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mFilters.size();
        }

        public void setFilterLevels(float filterLevels) {
            if (mFilterPositionSelect >= 0) {
                setFilterLevel(mFilters.get(mFilterPositionSelect).getName(), filterLevels);
            }
        }

        public void setFilter(Filter filter) {
            mFilterPositionSelect = mFilters.indexOf(filter);
        }

        public void setFilterProgress() {
            if (mFilterPositionSelect > 0) {
                seekToSeekBar(getFilterLevel(mFilters.get(mFilterPositionSelect).getName()));
            } else {
                mBeautySeekBar.setVisibility(INVISIBLE);
            }
        }

        class HomeRecyclerHolder extends RecyclerView.ViewHolder {

            ImageView filterImg;
            TextView filterName;

            public HomeRecyclerHolder(View itemView) {
                super(itemView);
                filterImg = itemView.findViewById(R.id.control_recycler_img);
                filterName = itemView.findViewById(R.id.control_recycler_text);
            }
        }
    }

}