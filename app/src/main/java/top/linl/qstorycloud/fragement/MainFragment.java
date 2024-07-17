package top.linl.qstorycloud.fragement;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import top.linl.qstorycloud.R;
import top.linl.qstorycloud.db.helper.CommonDBHelper;


public class MainFragment extends Fragment {
    public static final String SAFE_MODE = "safe_mode";
    public static final String CLEAN_DATA = "clean_data";
    private TextView tvAppLabel;
    private CardView cardViewGithub;
    private CardView cardTutorial;
    private CheckBox cbSafeMode;

    private CommonDBHelper dbHelper;
    private CheckBox cbCleanData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initDB();
        initView(rootView);
        initUI();
        initListener();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initUI();
    }

    private void initDB() {
        dbHelper = CommonDBHelper.getInstance(getContext());
        String safeMode = dbHelper.query(SAFE_MODE);
        if (safeMode == null) {
            dbHelper.insert(SAFE_MODE, String.valueOf(false));
        }

    }

    private void initView(View root) {
        tvAppLabel = root.findViewById(R.id.tv_app_label);
        cardViewGithub = root.findViewById(R.id.card_view_github);
        cardTutorial = root.findViewById(R.id.card_tutorial);
        cbSafeMode = root.findViewById(R.id.cb_safe_mode);
        cbCleanData = root.findViewById(R.id.cb_clean_data);
    }

    private void initUI() {
        cbSafeMode.setChecked(Boolean.parseBoolean(dbHelper.query(SAFE_MODE)));
        String cleanData = dbHelper.query(CLEAN_DATA);
        cbCleanData.setChecked(cleanData != null);
    }

    private void initListener() {
        cardViewGithub.setOnClickListener(v -> {
            //跳转到浏览器打开
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.setData(Uri.parse("https://github.com/Suzhelan/QStoryCloud"));
            startActivity(intent);
        });
        cbSafeMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dbHelper.update(SAFE_MODE, String.valueOf(isChecked));
        });
        cbCleanData.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                //插入清空数据的标记
                dbHelper.insert(CLEAN_DATA, "true");
            } else {
                dbHelper.delete(CLEAN_DATA);
            }
        });
    }
}
