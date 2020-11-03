package org.ar.beauty_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.faceunity.FURenderer;
import com.faceunity.entity.Effect;
import org.ar.beauty_android.activity.AvatarDriveActivity;
import org.ar.beauty_android.activity.BeautifyBodyActivity;
import org.ar.beauty_android.activity.FUAnimojiActivity;
import org.ar.beauty_android.activity.FUBeautyActivity;
import org.ar.beauty_android.activity.FUEffectActivity;
import org.ar.beauty_android.activity.FUHairActivity;
import org.ar.beauty_android.activity.FUMakeupActivity;
import org.ar.beauty_android.activity.LightMakeupActivity;
import org.ar.beauty_android.activity.LivePhotoDriveActivity;
import org.ar.beauty_android.activity.PosterChangeListActivity;
import org.ar.beauty_android.utils.OnMultiClickListener;
import org.ar.beauty_android.utils.ScreenUtils;
import org.ar.beauty_android.utils.ToastUtil;
import com.faceunity.utils.MiscUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 主页面，区分了SDK各个功能，并且获取权限码验证证书是否能够使用该功能
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int[] home_function_type = {
            Effect.EFFECT_TYPE_NONE,
            Effect.EFFECT_TYPE_BEAUTY_BODY,
            Effect.EFFECT_TYPE_NONE,
            Effect.EFFECT_TYPE_NORMAL,
            Effect.EFFECT_TYPE_ANIMOJI,
            Effect.EFFECT_TYPE_NONE,
            Effect.EFFECT_TYPE_NONE,
            Effect.EFFECT_TYPE_AR,
            Effect.EFFECT_TYPE_POSTER_FACE,
            Effect.EFFECT_TYPE_EXPRESSION,
            Effect.EFFECT_TYPE_MUSIC_FILTER,
            Effect.EFFECT_TYPE_BACKGROUND,
            Effect.EFFECT_TYPE_GESTURE,
            Effect.EFFECT_TYPE_FACE_WARP,
            Effect.EFFECT_TYPE_PORTRAIT_DRIVE,
            Effect.EFFECT_TYPE_NONE,
            Effect.EFFECT_TYPE_LIVE_PHOTO
    };

    // 文档：http://confluence.faceunity.com/pages/viewpage.action?pageId=10453059
    private static final String[] home_function_permissions_code = {
            "9-0",                    //美颜
            "0-32",                   // 美体
            "524288-0",               //美妆
            "6-0",                    //道具贴纸
            "16-0",                   //Animoji
            "0-8",                    //轻美妆
            "1048576-0",              //美发
            "96-0",                   //AR面具
            "8388608-0",              //海报换脸
            "2048-0",                 //表情识别
            "131072-0",               //音乐滤镜
            "256-0",                  //背景分割
            "512-0",                  //手势识别
            "65536-0",                //哈哈镜
            "32768-0",                //人像驱动
            "0-16",                   //Avatar捏脸
            "16777216-0"              //表情动图
    };

    private static final int[] home_function_name = {
            R.string.home_function_name_beauty,
            /*R.string.home_function_name_beauty_body,
            R.string.home_function_name_makeup,
            R.string.home_function_name_normal,
            R.string.home_function_name_animoji,
            R.string.home_function_name_light_makeup,
            R.string.home_function_name_hair,
            R.string.home_function_name_ar,
            R.string.home_function_name_poster_face,
            R.string.home_function_name_expression,
            R.string.home_function_name_music_filter,
            R.string.home_function_name_background,
            R.string.home_function_name_gesture,
            R.string.home_function_name_face_warp,
            R.string.home_function_name_portrait_drive,
            R.string.home_function_name_avatar,
            R.string.home_function_name_live_photo*/
    };

    private static final int[] home_function_res = {
            R.drawable.main_beauty,
            /*R.drawable.demo_icon_body,
            R.drawable.main_makeup,
            R.drawable.main_effect,
            R.drawable.main_animoji,
            R.drawable.demo_icon_texture_beauty,
            R.drawable.main_hair,
            R.drawable.main_ar_mask,
            R.drawable.main_poster_face,
            R.drawable.main_expression,
            R.drawable.main_music_fiter,
            R.drawable.main_background,
            R.drawable.main_gesture,
            R.drawable.main_face_warp,
            R.drawable.main_portrait_drive,
            R.drawable.main_avatar,
            R.drawable.main_live_photo*/
    };

    private List<Integer> hasFaceUnityPermissionsList = new ArrayList<>();
    private final boolean[] hasFaceUnityPermissions = new boolean[home_function_name.length];

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!isTaskRoot()) {
            finish();
            return;
        }
        ScreenUtils.fullScreen(this);
        MiscUtil.checkPermission(this);

        FURenderer.initFURenderer(this);

        int moduleCode0 = FURenderer.getModuleCode(0);
        int moduleCode1 = FURenderer.getModuleCode(1);
        Log.e(TAG, "ModuleCode " + moduleCode0 + " " + moduleCode1);
        for (int i = 0, count = 0; i < home_function_name.length; i++) {
            String[] codeStr = home_function_permissions_code[i].split("-");
            int code0 = Integer.valueOf(codeStr[0]);
            int code1 = Integer.valueOf(codeStr[1]);
            hasFaceUnityPermissions[i] = (moduleCode0 == 0 && moduleCode1 == 0) || ((code0 & moduleCode0) > 0 || (code1 & moduleCode1) > 0);
            if (hasFaceUnityPermissions[i]) {
                hasFaceUnityPermissionsList.add(count++, i);
            } else {
                hasFaceUnityPermissionsList.add(i);
            }
        }

        mRecyclerView = findViewById(R.id.home_recycler);
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int type = mRecyclerView.getAdapter().getItemViewType(position);
                if (type == 0) {
                    return 3;
                }
                return 1;
            }
        });
        mRecyclerView.setLayoutManager(manager);
        HomeRecyclerAdapter homeRecyclerAdapter = new HomeRecyclerAdapter();
        mRecyclerView.setAdapter(homeRecyclerAdapter);
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    class HomeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType > 0) {
                return new HomeRecyclerHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_main_recycler, parent, false));
            } else {
                return new TopHomeRecyclerHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_main_recycler_top, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int p) {
            if (viewHolder instanceof HomeRecyclerHolder) {
                HomeRecyclerHolder holder = (HomeRecyclerHolder) viewHolder;
                final int pos = p - 1;
                final int position = hasFaceUnityPermissionsList.get(pos);

                holder.homeFunctionImg.setImageResource(home_function_res[position]);
                holder.homeFunctionName.setText(home_function_name[position]);
                holder.homeFunctionName.setBackgroundResource(hasFaceUnityPermissions[position] ? R.drawable.main_recycler_item_text_background : R.drawable.main_recycler_item_text_background_unable);

                holder.itemView.setOnClickListener(new OnMultiClickListener() {
                    private long mLastClickTime;

                    @Override
                    public void onMultiClick(View v) {
                        if (!hasFaceUnityPermissions[position]) {
                            ToastUtil.showToast(MainActivity.this, R.string.sorry_no_permission);
                            return;
                        }

                        // 防止同时快速点击，因为启动相机非常慢
                        if (System.currentTimeMillis() - mLastClickTime < 300) {
                            return;
                        }

                        mLastClickTime = System.currentTimeMillis();

                        Intent intent;
                        if (home_function_res[position] == R.drawable.main_beauty) {
                            intent = new Intent(MainActivity.this, FUBeautyActivity.class);
                        } else if (home_function_res[position] == R.drawable.demo_icon_texture_beauty) {
                            intent = new Intent(MainActivity.this, LightMakeupActivity.class);
                        } else if (home_function_res[position] == R.drawable.main_makeup) {
                            intent = new Intent(MainActivity.this, FUMakeupActivity.class);
                        } else if (home_function_res[position] == R.drawable.main_hair) {
                            intent = new Intent(MainActivity.this, FUHairActivity.class);
                        } else if (home_function_res[position] == R.drawable.main_poster_face) {
                            intent = new Intent(MainActivity.this, PosterChangeListActivity.class);
                        } else if (home_function_res[position] == R.drawable.main_animoji) {
                            intent = new Intent(MainActivity.this, FUAnimojiActivity.class);
                        } else if (home_function_res[position] == R.drawable.main_live_photo) {
                            intent = new Intent(MainActivity.this, LivePhotoDriveActivity.class);
                        } else if (home_function_res[position] == R.drawable.main_avatar) {
                            intent = new Intent(MainActivity.this, AvatarDriveActivity.class);
                        } else if (home_function_res[position] == R.drawable.demo_icon_body) {
                            intent = new Intent(MainActivity.this, BeautifyBodyActivity.class);
                        } else {
                            intent = new Intent(MainActivity.this, FUEffectActivity.class);
                            intent.putExtra(FUEffectActivity.EFFECT_TYPE, home_function_type[position]);
                        }
                        startActivity(intent);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return hasFaceUnityPermissionsList.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        class HomeRecyclerHolder extends RecyclerView.ViewHolder {

            ImageView homeFunctionImg;
            TextView homeFunctionName;

            public HomeRecyclerHolder(View itemView) {
                super(itemView);
                homeFunctionImg = (ImageView) itemView.findViewById(R.id.home_recycler_img);
                homeFunctionName = (TextView) itemView.findViewById(R.id.home_recycler_text);
            }
        }

        class TopHomeRecyclerHolder extends RecyclerView.ViewHolder {

            public TopHomeRecyclerHolder(View itemView) {
                super(itemView);
            }
        }
    }

}
