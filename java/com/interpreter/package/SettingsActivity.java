package ru.avroraventures.russiansinglanguage;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import java.util.ArrayList;
import ru.avroraventures.russiansinglanguage.db.DatabaseHandler;
import ru.avroraventures.russiansinglanguage.db.PairVariant;
import ru.avroraventures.russiansinglanguage.db.StatisticsItem;
import ru.avroraventures.russiansinglanguage.tools.AutoResizeTextView;
import ru.avroraventures.russiansinglanguage.tools.VoiceSpinner;

public class SettingsActivity extends MyActivity implements OnClickListener {
    private static final String TAG = SettingsActivity.class.getSimpleName();
    public static Switch switchSound;
    public static SeekBar volumeSeekBar;
    private ArrayAdapter<StatisticsItem> adapter;
    private ImageView clearStatisticsButton;
    private boolean isFirstTimeSpinnerItemSelected;
    private ArrayList<StatisticsItem> list;
    private ListView listView;
    private TextView noDataTextView;
    private AutoResizeTextView statisticsColumnTitle;
    private Spinner statisticsSpinner;
    private LinearLayout statisticsTableLinearLayout;
    private String[] statisticsTitles;

    class ViewHolder {
        TextView column1TextView;
        TextView column2TextView;
        TextView column3TextView;
        TextView column4TextView;

        ViewHolder() {
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMyTitle(getString(R.string.settigsTitle));
        setContentView(R.layout.settings);
        this.clearStatisticsButton = (ImageView) findViewById(R.id.clearStatisticsButton);
        this.statisticsSpinner = (Spinner) findViewById(R.id.statisticsSpinner);
        this.noDataTextView = (TextView) findViewById(R.id.noDataTextView);
        this.statisticsTableLinearLayout = (LinearLayout) findViewById(R.id.statisticsTableLinearLayout);
        this.listView = (ListView) findViewById(R.id.listView);
        this.statisticsTitles = getResources().getStringArray(R.array.statisticsTitles);
        this.statisticsColumnTitle = (AutoResizeTextView) findViewById(R.id.statisticsColumnTitle);
        AutoResizeTextView statisticsColumnTitle2 = (AutoResizeTextView) findViewById(R.id.statisticsColumnTitle2);
        AutoResizeTextView statisticsColumnTitle3 = (AutoResizeTextView) findViewById(R.id.statisticsColumnTitle3);
        ((AutoResizeTextView) findViewById(R.id.statisticsColumnTitle1)).setEtalonText(statisticsColumnTitle2.getText());
        statisticsColumnTitle3.setEtalonText(statisticsColumnTitle2.getText());
        this.clearStatisticsButton.setOnClickListener(this);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/PTM55F.ttf");
        switchSound = (Switch) findViewById(R.id.switchSound);
        switchSound.setChecked(MyApplication.getIsSoundOn());
        switchSound.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MyApplication.setIsSoundOn(isChecked);
                try {
                    if (MyApplication.getIsSoundOn()) {
                        MyActivity.action_sound.setIcon(MyApplication.context.getResources().getDrawable(R.drawable.sound_on_));
                    } else {
                        MyActivity.action_sound.setIcon(MyApplication.context.getResources().getDrawable(R.drawable.sound_off_));
                    }
                } catch (Exception e) {
                    Log.e(SettingsActivity.TAG, "### menu " + e.toString());
                }
            }
        });
        Switch switchEffects = (Switch) findViewById(R.id.switchEffects);
        switchEffects.setChecked(MyApplication.getIsEffectsOn());
        switchEffects.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MyApplication.setIsEffectsOn(isChecked);
            }
        });
        Switch switchVibration = (Switch) findViewById(R.id.switchVibration);
        switchVibration.setChecked(MyApplication.getIsVibrationOn());
        switchVibration.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MyApplication.setIsVibrationOn(isChecked);
            }
        });
        VoiceSpinner spinnerVoices_menu_items = (VoiceSpinner) findViewById(R.id.spinnerVoices_menu_items);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.voices_menu_items, 17367043);
        adapter.setDropDownViewResource(17367050);
        spinnerVoices_menu_items.setAdapter(adapter);
        this.statisticsSpinner.setSelection(MyApplication.getCurrentStatisticsSpinnerItem());
        this.statisticsSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView arg0, View arg1, int arg2, long arg3) {
                Log.i(SettingsActivity.TAG, "### statistics onItemSelected - " + arg2);
                if (arg2 != MyApplication.getCurrentStatisticsSpinnerItem()) {
                    MyApplication.setCurrentStatisticsSpinnerItem(arg2);
                    SettingsActivity.this.refreshStatisticsViews();
                }
            }

            public void onNothingSelected(AdapterView arg0) {
            }
        });
        spinnerVoices_menu_items.setSelection(MyApplication.getCurrentVoices_menu_item());
        spinnerVoices_menu_items.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView arg0, View arg1, int arg2, long arg3) {
                Log.i(SettingsActivity.TAG, "### voice onItemSelected - " + arg2 + ", isFirstTimeSpinnerItemSelected=" + SettingsActivity.this.isFirstTimeSpinnerItemSelected);
                MyApplication.setCurrentVoices_menu_item(arg2);
                if (SettingsActivity.this.isFirstTimeSpinnerItemSelected) {
                    SettingsActivity.this.isFirstTimeSpinnerItemSelected = false;
                } else {
                    MyApplication.playSample();
                }
            }

            public void onNothingSelected(AdapterView arg0) {
            }
        });
        AudioManager audioManager = (AudioManager) getSystemService("audio");
        volumeSeekBar = (SeekBar) findViewById(R.id.volumeSeekBar);
        volumeSeekBar.setMax(audioManager.getStreamMaxVolume(3));
        volumeSeekBar.setProgress(MyApplication.getAppVolume());
        volumeSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                int seekBarProgress = seekBar.getProgress();
                Log.i(SettingsActivity.TAG, "### volume onStopTrackingTouch, seekBarProgress=" + seekBarProgress);
                MyApplication.setAppVolume(seekBarProgress, false);
            }
        });
        Answers.getInstance().logContentView(new ContentViewEvent().putContentId("Settings"));
    }

    private void refreshStatisticsViews() {
        boolean isStatisticsExisted;
        Log.i(TAG, "### statistics refreshStatisticsViews");
        this.list = new DatabaseHandler(this).getStatistics(this.statisticsSpinner.getSelectedItemPosition());
        if (this.list == null || this.list.size() <= 0) {
            isStatisticsExisted = false;
        } else {
            isStatisticsExisted = true;
        }
        if (isStatisticsExisted) {
            this.statisticsSpinner.setVisibility(0);
            this.noDataTextView.setVisibility(8);
            this.clearStatisticsButton.setVisibility(0);
            this.statisticsTableLinearLayout.setVisibility(0);
            initListAdapter();
            return;
        }
        this.statisticsSpinner.setVisibility(8);
        this.noDataTextView.setVisibility(0);
        this.clearStatisticsButton.setVisibility(4);
        this.statisticsTableLinearLayout.setVisibility(8);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clearStatisticsButton /*2131492944*/:
                Builder builder = new Builder(this);
                builder.setTitle(getString(R.string.clearStatisticsDialogTitle)).setMessage(getString(R.string.clearStatisticsDialogQuestion)).setCancelable(true).setPositiveButton(getString(R.string.clearStatisticsDialogYes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int i;
                        new DatabaseHandler(SettingsActivity.this).deleteAllItems();
                        SettingsActivity.this.refreshStatisticsViews();
                        for (i = 0; i < MyApplication.group.length; i++) {
                            MyApplication.saveScoreForGroup(i, 0);
                        }
                        for (i = 0; i < MyApplication.gamesNames.length; i++) {
                            MyApplication.saveScoreForGame(i, 0);
                        }
                        dialog.cancel();
                    }
                }).setNegativeButton(getString(R.string.clearStatisticsDialogNo), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                builder.create().show();
                return;
            default:
                return;
        }
    }

    protected void onResume() {
        super.onResume();
        this.isFirstTimeSpinnerItemSelected = true;
        refreshStatisticsViews();
    }

    private void initListAdapter() {
        if (this.list != null) {
            Log.i(TAG, "### statistics list=" + this.list.toString());
            this.statisticsColumnTitle.setText(this.statisticsTitles[this.statisticsSpinner.getSelectedItemPosition()]);
            this.adapter = new ArrayAdapter<StatisticsItem>(this, R.layout.my_simple_list_item_1, this.list) {
                public View getView(int position, View convertView, ViewGroup parent) {
                    ViewHolder holder;
                    StatisticsItem statisticsItem = (StatisticsItem) SettingsActivity.this.adapter.getItem(position);
                    if (convertView == null) {
                        convertView = SettingsActivity.this.getLayoutInflater().inflate(R.layout.my_simple_list_item_1, null);
                        holder = new ViewHolder();
                        holder.column1TextView = (TextView) convertView.findViewById(R.id.column1TextView);
                        holder.column2TextView = (TextView) convertView.findViewById(R.id.column2TextView);
                        holder.column3TextView = (TextView) convertView.findViewById(R.id.column3TextView);
                        holder.column4TextView = (TextView) convertView.findViewById(R.id.column4TextView);
                        convertView.setTag(holder);
                    } else {
                        holder = (ViewHolder) convertView.getTag();
                    }
                    switch (SettingsActivity.this.statisticsSpinner.getSelectedItemPosition()) {
                        case PairVariant.SHOWED /*0*/:
                            if (position >= MyApplication.groupsTitles.length) {
                                holder.column1TextView.setText(MyApplication.context.getString(R.string.total));
                                break;
                            }
                            holder.column1TextView.setText(MyApplication.groupsTitles[position]);
                            break;
                        case PairVariant.RIGHT /*1*/:
                            if (position >= MyApplication.all_letters.length) {
                                holder.column1TextView.setText(MyApplication.context.getString(R.string.total));
                                break;
                            }
                            holder.column1TextView.setText(MyApplication.all_letters[position]);
                            break;
                        case 2:
                            if (position >= MyApplication.gamesNames.length) {
                                holder.column1TextView.setText(MyApplication.context.getString(R.string.total));
                                break;
                            }
                            holder.column1TextView.setText(MyApplication.gamesNames[position]);
                            break;
                    }
                    if (statisticsItem.allAnswers > 0) {
                        holder.column2TextView.setText(String.valueOf(statisticsItem.rightAnswers) + "/" + String.valueOf(statisticsItem.allAnswers));
                        holder.column3TextView.setText(String.valueOf(statisticsItem.getAverageTimeOfAnswers()));
                    } else {
                        holder.column2TextView.setText("-");
                        holder.column3TextView.setText("-");
                    }
                    if (SettingsActivity.this.statisticsSpinner.getSelectedItemPosition() != 2 || statisticsItem.indexOfGroupOrLetter == -1) {
                        holder.column4TextView.setText(String.valueOf(statisticsItem.getPercentage()));
                    } else {
                        holder.column4TextView.setText("-");
                    }
                    return convertView;
                }
            };
            this.listView.setAdapter(this.adapter);
        }
    }
}
