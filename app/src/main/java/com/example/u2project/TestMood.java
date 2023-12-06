package com.example.u2project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class TestMood extends AppCompatActivity {
    String Type = "Mood";
    TextView txQuestion, txIndex, txTotal, tx1, tx2, tx3;  // 질문지, 질문번호, 진행정도
    ImageView btn1;
    ImageView btn2;
    ImageView btn3;

    Button btn_next;
    FirebaseFirestore db;
    DatabaseReference mDatabaseRef;

    // -------------주제별로 다르게 세팅할 부분-----------------
    // 몇개 문제를 랜덤하게 뽑을건지
    int choose = 4;

    // 해당 주제 문제의 총 개수
    int allQueNum = 8;

    // ----------------------------------------------------
    int score;
    int currentscore = 0;
    int temp;
    int totalIdx = 19;       // 전체 인덱스
    int currentIdx = 0; // 해당 주제 인덱스
    //-----------------------------------

    // 랜덤한 문제 번호 출력 ///////////////////////////////////
    RandomNumbers random = new RandomNumbers();
    int[] randomQue = random.generateRandomNumbers(choose, allQueNum);

    String Answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_mood);

        ////////// 전체 인덱스 불러오기 및 저장 //////////
        Intent intent = getIntent();

        int score = intent.getIntExtra("score",0);
        int totalIdx = intent.getIntExtra("totalIdx",19);

        Log.e("가져온 점수 : ",Integer.toString(score));
        Log.e("가져온 인덱스 : ",Integer.toString(totalIdx));
        //////////////////////////////////////////////

        // Firebase Realtime Database에서 사용자 정보 가져오기
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); // 사용자의 UID
        String path = "DMC/UserAccount/" + uid; // 사용자 정보가 저장된 경로

        // UI 업데이트
        updateUI(choose,uid);
    }

    private void updateUI(int choose,String uid) {

        txQuestion = findViewById(R.id.tx_question);
        tx1 = findViewById(R.id.tx1);
        tx2 = findViewById(R.id.tx2);
        tx3 = findViewById(R.id.tx3);
        txIndex = findViewById(R.id.tx_index);
        txTotal = findViewById(R.id.tx_totalindex);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn_next = findViewById(R.id.btn_next);

        txIndex.setText(Integer.toString(totalIdx));

        Log.e("2.현재 인덱스 : ", Integer.toString(currentIdx));
        Log.e("2.전체 인덱스 : ", Integer.toString(totalIdx));

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 모든 ImageView를 초기화 (선택 해제)
                btn1.setSelected(false);
                btn2.setSelected(false);
                btn3.setSelected(false);

                // 클릭한 ImageView를 선택
                v.setSelected(true);

                if (btn1.isSelected()) {
                    // btn1이 선택됨
                    btn1.setImageResource(R.drawable.circle_fill);
                    btn2.setImageResource(R.drawable.semo_e);
                    btn3.setImageResource(R.drawable.x_e);
                    temp = 0;
                } else if (btn2.isSelected()) {
                    // btn2가 선택됨
                    btn1.setImageResource(R.drawable.circle_e);
                    btn2.setImageResource(R.drawable.semo_fill);
                    btn3.setImageResource(R.drawable.x_e);
                    temp = 1;
                } else if (btn3.isSelected()) {
                    // btn3이 선택됨
                    btn1.setImageResource(R.drawable.circle_e);
                    btn2.setImageResource(R.drawable.semo_e);
                    btn3.setImageResource(R.drawable.x_fill);
                    temp = 2;
                }
                // 버튼 상태를 업데이트
                if (btn1.isSelected() || btn2.isSelected() || btn3.isSelected()) {
                    btn_next.setEnabled(true); // 선택된 버튼이 있으면 "다음" 버튼 활성화
                } else {
                    btn_next.setEnabled(false); // 선택된 버튼이 없으면 "다음" 버튼 비활성화
                }
            }
        };

        btn1.setOnClickListener(clickListener);
        btn2.setOnClickListener(clickListener);
        btn3.setOnClickListener(clickListener);





        ////////////////// 문제 목록 가져오기 ////////////////////
        toQuestion(randomQue[currentIdx], Type);
        //Log.e("2.정답 : ", Answer);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn1.setImageResource(R.drawable.circle_e);
                btn2.setImageResource(R.drawable.semo_e);
                btn3.setImageResource(R.drawable.x_e);
                Log.e("temp : ",Integer.toString(temp));
                // Firebase Realtime Database에서 사용자 정보 가져오기
                //mDatabaseRef = FirebaseDatabase.getInstance().getReference(); // DatabaseReference 초기화
                // 해당 주제의 문제 개수를 넘었을 경우 다음주제로 이동
                if(currentIdx >= choose - 1){
                    totalIdx++;
                    // 사용자의 답안과 정답 비교
                    Log.e("현재 인덱스 : ", Integer.toString(currentIdx));
                    Log.e("전체 인덱스 : ", Integer.toString(totalIdx));

                    score = score + temp;
                    currentscore = currentscore + temp;

                    Log.e("점수 : ",Integer.toString(score));
                    // 사용자의 답안과 정답 비교

                    Intent intent = new Intent(TestMood.this, TestEva.class);

                    ////////////////////////////////////////////////////////////
                    intent.putExtra("score",score);
                    intent.putExtra("totalIdx",totalIdx);
                    ////////////////////////////////////////////////////////////
                    mDatabaseRef = FirebaseDatabase.getInstance().getReference("DMC");
                    mDatabaseRef.child("UserAccount").child(uid).child(Type).setValue(currentscore);
                    startActivity(intent);
                    finish();
                }
                else{
                    score = score + temp;
                    currentscore = currentscore + temp;
                    Log.e("점수 : ",Integer.toString(score));

                    // 문제 번호 증가
                    currentIdx++;
                    totalIdx++;

                    toQuestion(randomQue[currentIdx], Type);

                    Log.e("현재 인덱스 : ", Integer.toString(currentIdx));
                    Log.e("전체 인덱스 : ", Integer.toString(totalIdx));

                    txIndex.setText(Integer.toString(totalIdx));
                }
            }
        });
    }

    // 문제를 데이터베이스에서 가져옴
    public void toQuestion(int i, String Type) {
        String num = Integer.toString(i);
        // Firebase Firestore 초기화
        db = FirebaseFirestore.getInstance();
        // Firestore에서 데이터 가져오기
        // 파이어스토어에서 문서 가져오기
        db.collection(Type)
                .document(num)  // questionId는 문서의 고유 식별자입니다.
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // 문서가 존재하는 경우
                            String question = documentSnapshot.getString("q");
                            String p1 = documentSnapshot.getString("p1");
                            String p2 = documentSnapshot.getString("p2");
                            String p3 = documentSnapshot.getString("p3");

                            // 가져온 질문을 TextView에 표시
                            txQuestion.setText(question);
                            tx1.setText(p1);
                            tx2.setText(p2);
                            tx3.setText(p3);

                            Log.e("문제 : ",question);

                        } else {
                            // 문서가 존재하지 않는 경우 처리
                            txQuestion.setText("문서가 존재하지 않습니다.");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 데이터 가져오기 실패 시 처리
                        txQuestion.setText("데이터를 가져오는 데 문제가 발생했습니다.");
                    }
                });
    }
}