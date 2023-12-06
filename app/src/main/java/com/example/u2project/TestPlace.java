package com.example.u2project;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class TestPlace extends AppCompatActivity {

    // 주제 선택
    String Type = "OriPlace";

    TextView txQuestion, txIndex, txTotal, txAnswer;  // 질문지, 질문번호, 진행정도
    Button btnNext;
    EditText etAnswer;
    FirebaseFirestore db;
    DatabaseReference mDatabaseRef;


    // -------------주제별로 다르게 세팅할 부분-----------------
    // 몇개 문제를 랜덤하게 뽑을건지
    int choose = 3;

    // 해당 주제 문제의 총 개수
    int allQueNum = 4;

    // ----------------------------------------------------

    int score;
    int currentscore= 0;

    //-----------------------------------
    int totalIdx = 4;       // 전체 인덱스

    int currentIdx = 0; // 해당 주제 인덱스
    //-----------------------------------


    // 랜덤한 문제 번호 출력
    RandomNumbers random = new RandomNumbers();
    int[] randomQue = random.generateRandomNumbers(choose, allQueNum);

    String Answer;
    //String[] Question = new String[choose];
    public String add_dong;
    public String add_si;
    public String add_gu;
    public  String idToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_place);

        ////////// 전체 인덱스 불러오기 및 저장 //////////
        Intent intent = getIntent();
        score = intent.getIntExtra("score",0);
        totalIdx = intent.getIntExtra("totalIdx",4);

        Log.e("1.가져온 점수 : ",Integer.toString(score));
        Log.e("2.가져온 인덱스 : ",Integer.toString(totalIdx));
        //////////////////////////////////////////////

        btnNext = findViewById(R.id.btn_next);

        // Firebase Realtime Database에서 사용자 정보 가져오기
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); // 사용자의 UID
        String path = "DMC/UserAccount/" + uid; // 사용자 정보가 저장된 경로

        mDatabaseRef = FirebaseDatabase.getInstance().getReference(path);
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    add_dong = dataSnapshot.child("add_dong").getValue(String.class);
                    add_si = dataSnapshot.child("add_si").getValue(String.class);
                    add_gu = dataSnapshot.child("add_gu").getValue(String.class);
                    idToken = dataSnapshot.child("idToken").getValue(String.class);

                    // 사용자 정보를 가져온 후 UI를 업데이트
                    updateUI(choose,uid);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터 가져오기에 실패한 경우 처리
            }
        });

    }
    // 사용자 정보를 가져온 후 UI를 업데이트
    private void updateUI(int choose,String uid) {

        btnNext.setEnabled(false);

        txQuestion = findViewById(R.id.tx_question);
        txIndex = findViewById(R.id.tx_index);
        txTotal = findViewById(R.id.tx_totalindex);
        txAnswer = findViewById(R.id.tx_answer);
        btnNext = findViewById(R.id.btn_next);

        etAnswer = findViewById(R.id.et_Answer);

        txIndex.setText(Integer.toString(totalIdx));

        Log.e("2.현재 인덱스 : ", Integer.toString(currentIdx));
        Log.e("2.전체 인덱스 : ", Integer.toString(totalIdx));


        // 문제 목록 가져오기
        Answer = toAnswer(randomQue[currentIdx]);
        toQuestion(randomQue[currentIdx], Type);
        Log.e("2.정답 : ", Answer);

        txAnswer.setText(Answer);

        // EditText의 텍스트 변경 감지
        etAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 이 메서드를 사용하지 않음
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 이 메서드를 사용하지 않음
            }

            @Override
            public void afterTextChanged(Editable s) {
                // EditText의 텍스트가 변경될 때 호출됨
                if (s.toString().isEmpty()) {
                    // EditText에 값이 입력되지 않았을 때 "다음" 버튼 비활성화
                    btnNext.setEnabled(false);
                } else {
                    // EditText에 값이 입력되었을 때 "다음" 버튼 활성화
                    btnNext.setEnabled(true);
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 해당 주제의 문제 개수를 넘었을 경우 다음주제로 이동
                if(currentIdx >= choose - 1){
                    totalIdx++;

                    String userAnswer = etAnswer.getText().toString();
                    // 사용자의 답안과 정답 비교
                    if (userAnswer.equals(Answer)) {
                        score++; // 정답이면 score 증가
                        currentscore++;
                    }
                    //////////////////////////////
                    Intent intent = new Intent(TestPlace.this, TestMem.class);
                    intent.putExtra("score",score);
                    intent.putExtra("totalIdx",totalIdx);
                    //////////////////////////////
                    mDatabaseRef = FirebaseDatabase.getInstance().getReference("DMC");
                    mDatabaseRef.child("UserAccount").child(uid).child(Type).setValue(currentscore);
                    Log.e("현재 인덱스 : ", Integer.toString(currentIdx));
                    Log.e("전체 인덱스 : ", Integer.toString(totalIdx));



                    startActivity(intent);
                    finish();
                }

                else{
                    String userAnswer = etAnswer.getText().toString();
                    btnNext.setEnabled(true);

                    // 사용자의 답안과 정답 비교
                    if (userAnswer.equals(Answer)) {
                        score++; // 정답이면 score 증가
                        currentscore++;
                    }

                    // 문제 번호 증가
                    currentIdx++;
                    totalIdx++;

                    Log.e("점수 : ",Integer.toString(score));

                    //mDatabaseRef.child("DMC/UserAccount/").child(idToken+"/index").setValue(totalIdx);

                    // 문제 목록 가져오기
                    Answer = toAnswer(randomQue[currentIdx]);
                    toQuestion(randomQue[currentIdx], Type);
                    Log.e("1.정답 : ", Answer);

                    Log.e("1.현재 인덱스 : ", Integer.toString(currentIdx));
                    Log.e("1.전체 인덱스 : ", Integer.toString(totalIdx));

                    txIndex.setText(Integer.toString(totalIdx));
                    txAnswer.setText(Answer);

                    etAnswer.setText("");
                }
            }
        });
    }

    // 정답 세팅
    public String toAnswer(int i) {
        String Answer;
        switch (i) {
            case 1:
                Answer = "서울";
                return Answer;
            case 2:
                Answer = add_si;
                return Answer;
            case 3:
                Answer = add_gu;
                return Answer;
            case 4:
                Answer = add_dong;
                return Answer;
            default:
                break;
        }
        return null;
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
                            // 가져온 질문을 TextView에 표시
                            txQuestion.setText(question);
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

    // 오늘 날짜, 시간, 요일 등
    public static String Today(int i){
        // 현재 날짜와 요일 가져오기
        Calendar calendar = Calendar.getInstance();
        String Answer="";
        switch (i){
            case 1: // 오늘 요일
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // 1 (일요일) ~ 7 (토요일)
                String[] strweek = {"일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"};
                Answer = strweek[dayOfWeek - 1];
                break;
            case 2: // 현재 시간
                int hour = calendar.get(Calendar.HOUR_OF_DAY); // 0 ~ 23
                String strhour = Integer.toString(hour);
                Answer = strhour;
                break;
            case 3: // 오늘 몇월
                int month = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH는 0부터 시작하므로 +1 필요
                String strmonth = Integer.toString(month);
                Answer = strmonth;
                break;
            case 4: // 오늘 몇월
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                String strday = Integer.toString(day);
                Answer = strday;
                break;
        }
        return Answer;
    }
}