package com.example.u2project;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TestResult extends AppCompatActivity {

    Long Time, Place, Cal, Lan,Mem, Per, Mood, Eva;
    Long total;
    DatabaseReference userRef;
    Button btn_next;
    BarChart Barchart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);

        // Firebase Realtime Database에서 사용자 정보 가져오기
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); // 사용자의 UID
        String path = "DMC/UserAccount/" + uid; // 사용자 정보가 저장된 경로

        userRef = FirebaseDatabase.getInstance().getReference(path);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Time = dataSnapshot.child("OriTime").getValue(Long.class);
                    Place = dataSnapshot.child("OriPlace").getValue(Long.class);
                    Mem = dataSnapshot.child("Memory").getValue(Long.class);
                    Lan = dataSnapshot.child("Language").getValue(Long.class);
                    Per = dataSnapshot.child("Perception").getValue(Long.class);
                    Mood = dataSnapshot.child("Mood").getValue(Long.class);
                    Eva = dataSnapshot.child("Evaluation").getValue(Long.class);
                    Cal = dataSnapshot.child("Calculate").getValue(Long.class);

                    updateUI(uid);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터 가져오기에 실패한 경우 처리
            }
        });
    }

    private void updateUI(String uid) {
        Barchart = new BarChart(this);
        Barchart = findViewById(R.id.barchart);

        // 데이터 생성
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, Time));
        entries.add(new BarEntry(1, Place));
        entries.add(new BarEntry(2, Cal));
        entries.add(new BarEntry(3, Lan));
        entries.add(new BarEntry(4, Mem));
        entries.add(new BarEntry(5, Per));
        entries.add(new BarEntry(6, Mood));
        entries.add(new BarEntry(7, Eva));

        BarDataSet dataSet = new BarDataSet(entries, "Scores");

        // 데이터 색상 설정
        dataSet.setColors(Color.rgb(55, 132, 224));

        BarData data = new BarData(dataSet);

        // X 축 레이블 설정
        final String[] labels = {"지남력(시간)","지남력(장소)","기억등록/회상","주의집중 및 계산","언어","시각적 지각 능력","기분","주관적 평가"};
        XAxis xAxis = Barchart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

        Barchart.setData(data);
        Barchart.setFitBars(true);
        Barchart.getDescription().setEnabled(false);
        Barchart.animateY(2000);
        Barchart.invalidate();

        btn_next = findViewById(R.id.btn_next);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ImageView (btn_send) 클릭 시 동작할 코드
                total = Time+ Place+ Cal+ Lan+Mem+ Per+ Mood+ Eva;
                userRef = FirebaseDatabase.getInstance().getReference("DMC");
                userRef.child("UserAccount").child(uid).child("Result").setValue(total);
                Intent intent = new Intent(TestResult.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

}