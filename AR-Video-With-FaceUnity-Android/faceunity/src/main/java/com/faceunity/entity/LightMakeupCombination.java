package com.faceunity.entity;

import java.util.List;

/**
 * 轻美妆妆容组合
 *
 * @author Richie on 2018.11.15
 */
public class LightMakeupCombination {
    // 无妆
    public static final int FACE_MAKEUP_TYPE_NONE = -1;
    // 口红
    public static final int FACE_MAKEUP_TYPE_LIPSTICK = 0;
    // 腮红
    public static final int FACE_MAKEUP_TYPE_BLUSHER = 1;
    // 眉毛
    public static final int FACE_MAKEUP_TYPE_EYEBROW = 2;
    // 眼影
    public static final int FACE_MAKEUP_TYPE_EYE_SHADOW = 3;
    // 眼线
    public static final int FACE_MAKEUP_TYPE_EYE_LINER = 4;
    // 睫毛
    public static final int FACE_MAKEUP_TYPE_EYELASH = 5;
    // 美瞳
    public static final int FACE_MAKEUP_TYPE_EYE_PUPIL = 6;

    private List<LightMakeupItem> mMakeupItems;
    private int nameId;
    private int iconId;

    public LightMakeupCombination(List<LightMakeupItem> makeupItems, int nameId, int iconId) {
        mMakeupItems = makeupItems;
        this.nameId = nameId;
        this.iconId = iconId;
    }

    public List<LightMakeupItem> getMakeupItems() {
        return mMakeupItems;
    }

    public void setMakeupItems(List<LightMakeupItem> makeupItems) {
        mMakeupItems = makeupItems;
    }

    public int getNameId() {
        return nameId;
    }

    public void setNameId(int nameId) {
        this.nameId = nameId;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    @Override
    public String toString() {
        return "LightMakeupCombination{" +
                "MakeupItems=" + mMakeupItems +
                ", name='" + nameId + '\'' +
                ", iconId=" + iconId +
                '}';
    }
}
