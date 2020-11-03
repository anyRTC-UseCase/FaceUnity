package org.ar.beauty_android.entity;


import com.faceunity.entity.Filter;
import org.ar.beauty_android.R;
import org.ar.beauty_android.utils.DecimalUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 美颜参数SharedPreferences记录,目前仅以保存数据，可改造为以SharedPreferences保存数据
 * Created by tujh on 2018/3/7.
 */
public abstract class BeautyParameterModel {
    public static final String TAG = BeautyParameterModel.class.getSimpleName();
    /**
     * 滤镜默认强度 0.4
     */
    public static final float DEFAULT_FILTER_LEVEL = 0.4f;
    public static final String STR_FILTER_LEVEL = "FilterLevel_";
    /**
     * 每个滤镜强度值。key: name, value: level
     */
    public static Map<String, Float> sFilterLevel = new HashMap<>(16);
    /**
     * 默认滤镜 自然 2
     */
    public static Filter sFilter = FilterEnum.ziran_2.create();
    /**
     * 轻美妆整体强度，key: name，value: level
     */
    public static Map<Integer, Float> sLightMakeupCombinationLevels = new HashMap<>(16);
    /**
     * 默认美发强度 0.6
     */
    public static final float HAIR_COLOR_INTENSITY = 0.6F;
    public static float[] sHairLevel = new float[14];
    // 美型默认参数
    private static final Map<Integer, Float> FACE_SHAPE_DEFAULT_PARAMS = new HashMap<>(16);

    public static float sColorLevel = 0.3f;// 美白
    public static float sBlurLevel = 0.7f; // 精细磨皮程度
    public static float sRedLevel = 0.3f;// 红润
    public static float sEyeBright = 0.0f;// 亮眼
    public static float sToothWhiten = 0.0f;// 美牙
    // 美肤默认参数
    private static final Map<Integer, Float> FACE_SKIN_DEFAULT_PARAMS = new HashMap<>(16);

    public static float sMicroPouch = 0f; // 去黑眼圈
    public static float sMicroNasolabialFolds = 0f; // 去法令纹
    public static float sMicroSmile = 0f; // 微笑嘴角
    public static float sMicroCanthus = 0f; // 眼角
    public static float sMicroPhiltrum = 0.5f; // 人中
    public static float sMicroLongNose = 0.5f; // 鼻子长度
    public static float sMicroEyeSpace = 0.5f; // 眼睛间距
    public static float sMicroEyeRotate = 0.5f; // 眼睛角度

    static {
        Arrays.fill(sHairLevel, HAIR_COLOR_INTENSITY);
    }

    public static float sCheekThinning = 0f;//瘦脸
    public static float sCheekV = 0.5f;//V脸
    public static float sCheekNarrow = 0f;//窄脸
    public static float sCheekSmall = 0f;//小脸
    public static float sEyeEnlarging = 0.4f;//大眼
    public static float sIntensityChin = 0.3f;//下巴
    public static float sIntensityForehead = 0.3f;//额头
    public static float sIntensityNose = 0.5f;//瘦鼻
    public static float sIntensityMouth = 0.4f;//嘴形

    static {
        // 美型
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_cheek_thinning, sCheekThinning);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_cheek_narrow, sCheekNarrow);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_cheek_small, sCheekSmall);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_cheek_v, sCheekV);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_eye_enlarge, sEyeEnlarging);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_intensity_chin, sIntensityChin);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_intensity_forehead, sIntensityForehead);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_intensity_nose, sIntensityNose);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_intensity_mouth, sIntensityMouth);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_canthus, sMicroCanthus);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_eye_space, sMicroEyeSpace);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_eye_rotate, sMicroEyeRotate);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_long_nose, sMicroLongNose);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_philtrum, sMicroPhiltrum);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_smile, sMicroSmile);

        // 美肤
        FACE_SKIN_DEFAULT_PARAMS.put(R.id.beauty_box_blur_level, sBlurLevel);
        FACE_SKIN_DEFAULT_PARAMS.put(R.id.beauty_box_color_level, sColorLevel);
        FACE_SKIN_DEFAULT_PARAMS.put(R.id.beauty_box_red_level, sRedLevel);
        FACE_SKIN_DEFAULT_PARAMS.put(R.id.beauty_box_pouch, sMicroPouch);
        FACE_SKIN_DEFAULT_PARAMS.put(R.id.beauty_box_nasolabial, sMicroNasolabialFolds);
        FACE_SKIN_DEFAULT_PARAMS.put(R.id.beauty_box_eye_bright, sEyeBright);
        FACE_SKIN_DEFAULT_PARAMS.put(R.id.beauty_box_tooth_whiten, sToothWhiten);
    }

    /**
     * 美颜效果是否打开
     *
     * @param checkId
     * @return
     */
    public static boolean isOpen(int checkId) {
        switch (checkId) {
            case R.id.beauty_box_blur_level:
                return sBlurLevel > 0;
            case R.id.beauty_box_color_level:
                return sColorLevel > 0;
            case R.id.beauty_box_red_level:
                return sRedLevel > 0;
            case R.id.beauty_box_pouch:
                return sMicroPouch > 0;
            case R.id.beauty_box_nasolabial:
                return sMicroNasolabialFolds > 0;
            case R.id.beauty_box_eye_bright:
                return sEyeBright > 0;
            case R.id.beauty_box_tooth_whiten:
                return sToothWhiten != 0;
            case R.id.beauty_box_eye_enlarge:
                return sEyeEnlarging > 0;
            case R.id.beauty_box_cheek_thinning:
                return sCheekThinning > 0;
            case R.id.beauty_box_cheek_narrow:
                return sCheekNarrow > 0;
            case R.id.beauty_box_cheek_v:
                return sCheekV > 0;
            case R.id.beauty_box_cheek_small:
                return sCheekSmall > 0;
            case R.id.beauty_box_intensity_chin:
                return !DecimalUtils.floatEquals(sIntensityChin, 0.5f);
            case R.id.beauty_box_intensity_forehead:
                return !DecimalUtils.floatEquals(sIntensityForehead, 0.5f);
            case R.id.beauty_box_intensity_nose:
                return sIntensityNose > 0;
            case R.id.beauty_box_intensity_mouth:
                return !DecimalUtils.floatEquals(sIntensityMouth, 0.5f);
            case R.id.beauty_box_smile:
                return sMicroSmile > 0;
            case R.id.beauty_box_canthus:
                return sMicroCanthus > 0;
            case R.id.beauty_box_philtrum:
                return !DecimalUtils.floatEquals(sMicroPhiltrum, 0.5f);
            case R.id.beauty_box_long_nose:
                return !DecimalUtils.floatEquals(sMicroLongNose, 0.5f);
            case R.id.beauty_box_eye_space:
                return !DecimalUtils.floatEquals(sMicroEyeSpace, 0.5f);
            case R.id.beauty_box_eye_rotate:
                return !DecimalUtils.floatEquals(sMicroEyeRotate, 0.5f);
            default:
                return true;
        }
    }

    /**
     * 获取美颜的参数值
     *
     * @param checkId
     * @return
     */
    public static float getValue(int checkId) {
        switch (checkId) {
            case R.id.beauty_box_blur_level:
                return sBlurLevel;
            case R.id.beauty_box_color_level:
                return sColorLevel;
            case R.id.beauty_box_red_level:
                return sRedLevel;
            case R.id.beauty_box_pouch:
                return sMicroPouch;
            case R.id.beauty_box_nasolabial:
                return sMicroNasolabialFolds;
            case R.id.beauty_box_eye_bright:
                return sEyeBright;
            case R.id.beauty_box_tooth_whiten:
                return sToothWhiten;
            case R.id.beauty_box_eye_enlarge:
                return sEyeEnlarging;
            case R.id.beauty_box_cheek_thinning:
                return sCheekThinning;
            case R.id.beauty_box_cheek_narrow:
                return sCheekNarrow;
            case R.id.beauty_box_cheek_v:
                return sCheekV;
            case R.id.beauty_box_cheek_small:
                return sCheekSmall;
            case R.id.beauty_box_intensity_chin:
                return sIntensityChin;
            case R.id.beauty_box_intensity_forehead:
                return sIntensityForehead;
            case R.id.beauty_box_intensity_nose:
                return sIntensityNose;
            case R.id.beauty_box_intensity_mouth:
                return sIntensityMouth;
            case R.id.beauty_box_smile:
                return sMicroSmile;
            case R.id.beauty_box_canthus:
                return sMicroCanthus;
            case R.id.beauty_box_philtrum:
                return sMicroPhiltrum;
            case R.id.beauty_box_long_nose:
                return sMicroLongNose;
            case R.id.beauty_box_eye_space:
                return sMicroEyeSpace;
            case R.id.beauty_box_eye_rotate:
                return sMicroEyeRotate;
            default:
                return 0;
        }
    }

    /**
     * 设置美颜的参数值
     *
     * @param checkId
     * @param value
     */
    public static void setValue(int checkId, float value) {
        switch (checkId) {
            case R.id.beauty_box_blur_level:
                sBlurLevel = value;
                break;
            case R.id.beauty_box_color_level:
                sColorLevel = value;
                break;
            case R.id.beauty_box_red_level:
                sRedLevel = value;
                break;
            case R.id.beauty_box_pouch:
                sMicroPouch = value;
                break;
            case R.id.beauty_box_nasolabial:
                sMicroNasolabialFolds = value;
                break;
            case R.id.beauty_box_eye_bright:
                sEyeBright = value;
                break;
            case R.id.beauty_box_tooth_whiten:
                sToothWhiten = value;
                break;
            case R.id.beauty_box_eye_enlarge:
                sEyeEnlarging = value;
                break;
            case R.id.beauty_box_cheek_thinning:
                sCheekThinning = value;
                break;
            case R.id.beauty_box_cheek_v:
                sCheekV = value;
                break;
            case R.id.beauty_box_cheek_narrow:
                sCheekNarrow = value;
                break;
            case R.id.beauty_box_cheek_small:
                sCheekSmall = value;
                break;
            case R.id.beauty_box_intensity_chin:
                sIntensityChin = value;
                break;
            case R.id.beauty_box_intensity_forehead:
                sIntensityForehead = value;
                break;
            case R.id.beauty_box_intensity_nose:
                sIntensityNose = value;
                break;
            case R.id.beauty_box_intensity_mouth:
                sIntensityMouth = value;
                break;
            case R.id.beauty_box_smile:
                sMicroSmile = value;
            case R.id.beauty_box_canthus:
                sMicroCanthus = value;
                break;
            case R.id.beauty_box_philtrum:
                sMicroPhiltrum = value;
                break;
            case R.id.beauty_box_long_nose:
                sMicroLongNose = value;
                break;
            case R.id.beauty_box_eye_space:
                sMicroEyeSpace = value;
                break;
            case R.id.beauty_box_eye_rotate:
                sMicroEyeRotate = value;
                break;
            default:
        }
    }

    /**
     * 默认的美型参数是否被修改过
     *
     * @return
     */
    public static boolean checkIfFaceShapeChanged() {
        if (Float.compare(sCheekNarrow, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_cheek_narrow)) != 0) {
            return true;
        }
        if (Float.compare(sCheekSmall, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_cheek_small)) != 0) {
            return true;
        }
        if (Float.compare(sCheekV, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_cheek_v)) != 0) {
            return true;
        }
        if (Float.compare(sCheekThinning, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_cheek_thinning)) != 0) {
            return true;
        }
        if (Float.compare(sEyeEnlarging, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_eye_enlarge)) != 0) {
            return true;
        }
        if (Float.compare(sIntensityNose, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_intensity_nose)) != 0) {
            return true;
        }
        if (Float.compare(sIntensityChin, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_intensity_chin)) != 0) {
            return true;
        }
        if (Float.compare(sIntensityMouth, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_intensity_mouth)) != 0) {
            return true;
        }
        if (Float.compare(sIntensityForehead, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_intensity_forehead)) != 0) {
            return true;
        }
        if (Float.compare(sMicroCanthus, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_canthus)) != 0) {
            return true;
        }
        if (Float.compare(sMicroEyeSpace, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_eye_space)) != 0) {
            return true;
        }
        if (Float.compare(sMicroEyeRotate, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_eye_rotate)) != 0) {
            return true;
        }
        if (Float.compare(sMicroLongNose, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_long_nose)) != 0) {
            return true;
        }
        if (Float.compare(sMicroPhiltrum, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_philtrum)) != 0) {
            return true;
        }
        if (Float.compare(sMicroSmile, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_smile)) != 0) {
            return true;
        }
        return false;
    }

    /**
     * 默认的美肤参数是否被修改过
     *
     * @return
     */
    public static boolean checkIfFaceSkinChanged() {
        if (Float.compare(sColorLevel, FACE_SKIN_DEFAULT_PARAMS.get(R.id.beauty_box_color_level)) != 0) {
            return true;
        }
        if (Float.compare(sRedLevel, FACE_SKIN_DEFAULT_PARAMS.get(R.id.beauty_box_red_level)) != 0) {
            return true;
        }
        if (Float.compare(sMicroPouch, FACE_SKIN_DEFAULT_PARAMS.get(R.id.beauty_box_pouch)) != 0) {
            return true;
        }
        if (Float.compare(sMicroNasolabialFolds, FACE_SKIN_DEFAULT_PARAMS.get(R.id.beauty_box_nasolabial)) != 0) {
            return true;
        }
        if (Float.compare(sEyeBright, FACE_SKIN_DEFAULT_PARAMS.get(R.id.beauty_box_eye_bright)) != 0) {
            return true;
        }
        if (Float.compare(sToothWhiten, FACE_SKIN_DEFAULT_PARAMS.get(R.id.beauty_box_tooth_whiten)) != 0) {
            return true;
        }
        if (Float.compare(sBlurLevel, FACE_SKIN_DEFAULT_PARAMS.get(R.id.beauty_box_blur_level)) != 0) {
            return true;
        }
        return false;
    }

    /**
     * 恢复美型的默认值
     */
    public static void recoverFaceShapeToDefValue() {
        sCheekNarrow = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_cheek_narrow);
        sCheekSmall = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_cheek_small);
        sCheekV = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_cheek_v);
        sCheekThinning = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_cheek_thinning);
        sEyeEnlarging = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_eye_enlarge);
        sIntensityNose = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_intensity_nose);
        sIntensityMouth = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_intensity_mouth);
        sIntensityForehead = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_intensity_forehead);
        sIntensityChin = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_intensity_chin);
        sMicroCanthus = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_canthus);
        sMicroEyeSpace = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_eye_space);
        sMicroEyeRotate = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_eye_rotate);
        sMicroLongNose = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_long_nose);
        sMicroPhiltrum = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_philtrum);
        sMicroSmile = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_smile);
    }

    /**
     * 恢复美肤的默认值
     */
    public static void recoverFaceSkinToDefValue() {
        sBlurLevel = FACE_SKIN_DEFAULT_PARAMS.get(R.id.beauty_box_blur_level);
        sColorLevel = FACE_SKIN_DEFAULT_PARAMS.get(R.id.beauty_box_color_level);
        sRedLevel = FACE_SKIN_DEFAULT_PARAMS.get(R.id.beauty_box_red_level);
        sMicroPouch = FACE_SKIN_DEFAULT_PARAMS.get(R.id.beauty_box_pouch);
        sMicroNasolabialFolds = FACE_SKIN_DEFAULT_PARAMS.get(R.id.beauty_box_nasolabial);
        sEyeBright = FACE_SKIN_DEFAULT_PARAMS.get(R.id.beauty_box_eye_bright);
        sToothWhiten = FACE_SKIN_DEFAULT_PARAMS.get(R.id.beauty_box_tooth_whiten);
    }

}
