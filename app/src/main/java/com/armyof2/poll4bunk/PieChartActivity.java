package com.armyof2.poll4bunk;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import java.util.ArrayList;

import static com.armyof2.poll4bunk.MainActivity.BUNK_NO;
import static com.armyof2.poll4bunk.MainActivity.BUNK_UNDEC;
import static com.armyof2.poll4bunk.MainActivity.BUNK_YES;
import static com.armyof2.poll4bunk.MainActivity.BUNK_YES80;
import static com.armyof2.poll4bunk.MainActivity.addDataSet;
import static com.armyof2.poll4bunk.MainActivity.i;
import static com.armyof2.poll4bunk.MainActivity.j;
import static com.armyof2.poll4bunk.MainActivity.k;
import static com.armyof2.poll4bunk.MainActivity.l;
import static com.armyof2.poll4bunk.MainActivity.pieChart;
import static com.armyof2.poll4bunk.MainActivity.x;
import static com.armyof2.poll4bunk.MainActivity.yData;


public class PieChartActivity extends Job {

    String yes = i;
    String no = j;
    String undec = l;
    String yes80 = k;
    ArrayList<String> bunkingYes = BUNK_YES;
    ArrayList<String> bunkingNo = BUNK_NO;
    ArrayList<String> bunkingYes80 = BUNK_YES80;
    ArrayList<String> bunkingUndec = BUNK_UNDEC;
    public static final String TAG = "my_job_tag";
    Intent intent;

    //pie chart references/variables



    @Override
    @NonNull
    protected Result onRunJob(Params params) {
        if(x <= 40) {
            //update pie chart values
            Log.d("TAG", "Pie Chart Values Updated");

            yData = new int[]{Integer.parseInt(i), Integer.parseInt(j), Integer.parseInt(k), Integer.parseInt(l)};
            addDataSet(pieChart, yData);

            scheduleJob();
            x++;
        }
            return Result.SUCCESS;
    }

    public static void scheduleJob() {
        new JobRequest.Builder(PieChartActivity.TAG)
                .setExecutionWindow(2_000L, 40_000L) //Every 2 seconds for 40 seconds
                .startNow()
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .build()
                .schedule();
    }
}
