package com.subject.piu.main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TableRow;

import com.subject.piu.CommonParams;
import com.subject.piu.R;

import java.util.HashSet;
import java.util.Set;

/**
 * メイン画面のViewPagerに表示するフラグメントのクラス
 */
public class MainSwitchFragment extends Fragment {
    // デバッグ用のタグ
    private static final String TAG = "MainSwitchFragment";

    // 呼びだされたMainActivityのインスタンス
    private MainActivity mainActivity;
    
    // このフラグメントが持つSwitchの集合
    private Set<Switch> switches;

    // このフラグメントを示すタブの番号
    private int position;

    /**
     * このフラグメントのインスタンスを新規生成する
     * @param mainActivity メイン画面のアクティビティ
     * @param position このフラグメントを示すタブの番号
     * @return このフラグメントのインスタンス
     */
    static Fragment newInstance(MainActivity mainActivity, int position) {
        MainSwitchFragment thisFragment = new MainSwitchFragment();
        thisFragment.mainActivity = mainActivity;
        thisFragment.position = position;
        thisFragment.switches = new HashSet<>();
        return thisFragment;
    }

    /**
     * このフラグメントのビューを生成する
     * @param inflater 生成するビューのインフレーター
     * @param container インフレーターでinflateする際に要するViewGroup
     * @param savedInstanceState バンドルインスタンス
     * @return このフラグメントのビュー
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // ログ出力
        Log.d(TAG, "onCreateView:position=" + position);

        // MainActivityのSharedPreferenceインスタンスを取得
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mainActivity);

        // 1dpの大きさをpx単位で計算
        float dp = mainActivity.getResources().getDisplayMetrics().density;

        // このフラグメントのビューをinflateする
        View thisView = inflater.inflate(R.layout.fragment_switch, container, false);

        // Switchを格納するレイアウトを取得
        LinearLayout switchLayout = thisView.findViewById(R.id.switchLayout);

        // 各Switchをレイアウトに格納し、その集合にセット
        switch (position) {
        case 0:
            // このフラグメントが「ステップ」に対応する場合
            for (int i = 0; i < CommonParams.stepChecks.length; i++) {
                final int idx = i;

                Switch s = new Switch(mainActivity);
                s.setText(CommonParams.STEPS[i]);
                s.setChecked(CommonParams.stepChecks[i]);
                s.setEnabled(!mainActivity.isWaited.get());
                s.setPadding((int) (20 * dp), (int) (10 * dp), (int) (20 * dp), (int) (10 * dp));
                s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // チェック状態を更新
                        CommonParams.stepChecks[idx] = isChecked;
                        // チェック状態をSharedPreferenceに保存
                        sp.edit()
                                .putBoolean("stepChecks[" + idx + "]", isChecked)
                                .apply();
                    }
                });

                switches.add(s);

                switchLayout.addView(s, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            }
            break;
        case 1:
            // このフラグメントが「難易度」に対応する場合
            for (int i = 0; i < CommonParams.difficultyChecks.length; i++) {
                final int idx = i;

                Switch s = new Switch(mainActivity);
                s.setText(String.valueOf(i + 1));
                s.setChecked(CommonParams.difficultyChecks[i]);
                s.setEnabled(!mainActivity.isWaited.get());
                s.setPadding((int) (20 * dp), (int) (10 * dp), (int) (20 * dp), (int) (10 * dp));
                s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // チェック状態を更新
                        CommonParams.difficultyChecks[idx] = isChecked;
                        // チェック状態をSharedPreferenceに保存
                        sp.edit()
                                .putBoolean("difficultyChecks[" + idx + "]", isChecked)
                                .apply();
                    }
                });

                switches.add(s);

                switchLayout.addView(s, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            }
            break;
        case 2:
            // このフラグメントが「種別」に対応する場合
            for (int i = 0; i < CommonParams.typeChecks.length; i++) {
                final int idx = i;

                Switch s = new Switch(mainActivity);
                s.setText(CommonParams.TYPES[i]);
                s.setChecked(CommonParams.typeChecks[i]);
                s.setEnabled(!mainActivity.isWaited.get());
                s.setPadding((int) (20 * dp), (int) (10 * dp), (int) (20 * dp), (int) (10 * dp));
                s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // チェック状態を更新
                        CommonParams.typeChecks[idx] = isChecked;
                        // チェック状態をSharedPreferenceに保存
                        sp.edit()
                                .putBoolean("typeChecks[" + idx + "]", isChecked)
                                .apply();
                    }
                });

                switches.add(s);

                switchLayout.addView(s, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            }
            break;
        case 3:
            // このフラグメントが「シリーズ」に対応する場合
            for (int i = 0; i < CommonParams.seriesChecks.length; i++) {
                final int idx = i;

                Switch s = new Switch(mainActivity);
                s.setText(CommonParams.SERIES[i]);
                s.setChecked(CommonParams.seriesChecks[i]);
                s.setEnabled(!mainActivity.isWaited.get());
                s.setPadding((int) (20 * dp), (int) (10 * dp), (int) (20 * dp), (int) (10 * dp));
                s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // チェック状態を更新
                        CommonParams.seriesChecks[idx] = isChecked;
                        // チェック状態をSharedPreferenceに保存
                        sp.edit()
                                .putBoolean("seriesChecks[" + idx + "]", isChecked)
                                .apply();
                    }
                });

                switches.add(s);

                switchLayout.addView(s, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            }
            break;
        case 4:
            // このフラグメントが「カテゴリー」に対応する場合
            for (int i = 0; i < CommonParams.categoryChecks.length; i++) {
                final int idx = i;

                Switch s = new Switch(mainActivity);
                s.setText(CommonParams.CATEGORIES[i]);
                s.setChecked(CommonParams.categoryChecks[i]);
                s.setEnabled(!mainActivity.isWaited.get());
                s.setPadding((int) (20 * dp), (int) (10 * dp), (int) (20 * dp), (int) (10 * dp));
                s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // チェック状態を更新
                        CommonParams.categoryChecks[idx] = isChecked;
                        // チェック状態をSharedPreferenceに保存
                        sp.edit()
                                .putBoolean("categoryChecks[" + idx + "]", isChecked)
                                .apply();
                    }
                });

                switches.add(s);

                switchLayout.addView(s, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            }
            break;
        case 5:
            // このフラグメントが「その他」に対応する場合
            Switch ppUnlockedStepSwitch = new Switch(mainActivity);
            ppUnlockedStepSwitch.setText(CommonParams.PP_UNLOCKED_STEP);
            ppUnlockedStepSwitch.setChecked(CommonParams.ppUnlockedStepCheck);
            ppUnlockedStepSwitch.setEnabled(!mainActivity.isWaited.get());
            ppUnlockedStepSwitch.setPadding((int) (20 * dp), (int) (10 * dp), (int) (20 * dp), (int) (10 * dp));
            ppUnlockedStepSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // チェック状態を更新
                    CommonParams.ppUnlockedStepCheck = isChecked;
                    // チェック状態をSharedPreferenceに保存
                    sp.edit()
                            .putBoolean("ppUnlockedStepCheck", isChecked)
                            .apply();
                }
            });

            Switch amPassOnlyUsedStepSwitch = new Switch(mainActivity);
            amPassOnlyUsedStepSwitch.setText(CommonParams.AM_PASS_ONLY_USED_STEP);
            amPassOnlyUsedStepSwitch.setChecked(CommonParams.amPassOnlyUsedStepCheck);
            amPassOnlyUsedStepSwitch.setEnabled(!mainActivity.isWaited.get());
            amPassOnlyUsedStepSwitch.setPadding((int) (20 * dp), (int) (10 * dp), (int) (20 * dp), (int) (10 * dp));
            amPassOnlyUsedStepSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // チェック状態を更新
                    CommonParams.amPassOnlyUsedStepCheck = isChecked;
                    // チェック状態をSharedPreferenceに保存
                    sp.edit()
                            .putBoolean("amPassOnlyUsedStepCheck", isChecked)
                            .apply();
                }
            });

            switches.add(ppUnlockedStepSwitch);
            switches.add(amPassOnlyUsedStepSwitch);

            switchLayout.addView(ppUnlockedStepSwitch, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            switchLayout.addView(amPassOnlyUsedStepSwitch, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            break;
        default:
            throw new IllegalArgumentException("position is out between 0 to 5.");
        }

        // レイアウトに格納したすべてのSwitchを、その集合にセット
        mainActivity.createdSwitches.addAll(switches);

        return thisView;
    }

    /**
     * このフラグメントのビューを破棄する
     */
    @Override
    public void onDestroyView() {
        // ログ出力
        Log.d(TAG, "onDestroyView:position=" + position);

        // このフラグメント上で破棄されるSwitchを、その集合から除去する
        mainActivity.createdSwitches.removeAll(switches);
        switches.clear();
        
        super.onDestroyView();
    }
}
