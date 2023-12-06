package com.example.u2project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TestRecall extends AppCompatActivity {

    String Type = "Memory";
    TextView tx_index;
    EditText et_Answer1, et_Answer2, et_Answer3;
    Button btn_next;

    DatabaseReference mDatabaseRef;

    ////////// 전체 인덱스 불러오기 및 저장 //////////
    Intent intent = getIntent();
    int score;
    int currentscore = 0;
    int totalIdx = 16;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_recall);

        Intent intent = new Intent();
        score = intent.getIntExtra("score",0);

        // Firebase Realtime Database에서 사용자 정보 가져오기
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); // 사용자의 UID
        String path = "DMC/UserAccount/" + uid; // 사용자 정보가 저장된 경로

        updateUI(0,uid);
    }


    private void updateUI(int choose,String uid) {
        btn_next = findViewById(R.id.btn_next);
        tx_index = findViewById(R.id.tx_index);

        tx_index.setText(String.valueOf(totalIdx));

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                et_Answer1 = findViewById(R.id.et_Answer1);
                et_Answer2 = findViewById(R.id.et_Answer2);
                et_Answer3 = findViewById(R.id.et_Answer3);

                Log.e("가져온 점수 : ",Integer.toString(score));
                Log.e("가져온 인덱스 : ",Integer.toString(totalIdx));
                //////////////////////////////////////////////

                // EditText로부터 사용자의 입력을 가져옵니다.
                String userAnswer1 = et_Answer1.getText().toString().trim();
                String userAnswer2 = et_Answer2.getText().toString().trim();
                String userAnswer3 = et_Answer3.getText().toString().trim();

                // 정답 배열
                String[] Answer = {"호랑이", "감자", "자동차"};

                // 사용자의 입력이 정답 배열에 포함되는지 확인
                boolean isCorrect = false;
                int temp = 0;

                if (userAnswer1.isEmpty() || userAnswer2.isEmpty() || userAnswer3.isEmpty()) {
                    // 사용자가 아무 입력도 하지 않은 경우
                    isCorrect = false;
                } else {
                    isCorrect = true;
                }


                // 사용자의 입력이 정답 배열에 포함된 경우에만 점수 추가
                if (isCorrect) {
                    for (String answer : Answer) {
                        if (userAnswer1.equals(answer)) {
                            temp++;
                        }
                        if (userAnswer2.equals(answer)) {
                            temp++;
                        }
                        if(userAnswer3.equals(answer)){
                            temp++;
                        }
                    }
                    if(temp == 3){
                        score += 3;
                        currentscore += 3;
                    }
                }else {
                    currentscore = 0;
                }

                totalIdx++;

                Intent intent1 = new Intent();
                intent1 = new Intent(TestRecall.this, TestPer.class);

                ////////////////////////////////////////////////////////////
                intent1.putExtra("score",score);
                intent1.putExtra("totalIdx",totalIdx);
                ////////////////////////////////////////////////////////////
                mDatabaseRef = FirebaseDatabase.getInstance().getReference("DMC");
                mDatabaseRef.child("UserAccount").child(uid).child(Type).setValue(currentscore);
                startActivity(intent1);
                finish();

            }
        });

    }
}