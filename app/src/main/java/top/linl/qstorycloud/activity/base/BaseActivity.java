package top.linl.qstorycloud.activity.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    protected void titleBarAdaptsToStatusBar(ViewGroup titleBar) {
        Context context = titleBar.getContext();
        //获取状态栏高度
        int statusBarHeight = 0;
        @SuppressLint({"DiscouragedApi", "InternalInsetResource"})
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        //适配高度
        ViewGroup.LayoutParams params = titleBar.getLayoutParams();
        params.height += statusBarHeight;
        //模拟setFitsSystemWindows(ture)填充
        titleBar.setPadding(titleBar.getPaddingLeft(), titleBar.getPaddingTop() + statusBarHeight, titleBar.getPaddingRight(), titleBar.getPaddingBottom());
    }

    protected void requestTranslucentStatusBar() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
    }
}
