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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class TestCal extends AppCompatActivity {

    // 주제 선택
    String Type = "Calculate";

    TextView txQuestion, txIndex, txTotal, txAnswer;  // 질문지, 질문번호, 진행정도
    Button btnNext;
    EditText etAnswer;
    FirebaseFirestore db;
    DatabaseReference mDatabaseRef;

    // -------------주제별로 다르게 세팅할 부분-----------------
    // 몇개 문제를 랜덤하게 뽑을건지
    int choose = 5;

    // 해당 주제 문제의 총 개수
    int allQueNum = 9;

    // ----------------------------------------------------
    int score;
    int currentscore = 0;
    int totalIdx = 8;       // 전체 인덱스
    int currentIdx = 0; // 해당 주제 인덱스
    //-----------------------------------

    // 랜덤한 문제 번호 출력 ///////////////////////////////////
    RandomNumbers random = new RandomNumbers();
    int[] randomQue = random.generateRandomNumbers(choose, allQueNum);

    String Answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_cal);

        btnNext = findViewById(R.id.btn_next);

        // Firebase Realtime Database에서 사용자 정보 가져오기
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); // 사용자의 UID
        String path = "DMC/UserAccount/" + uid; // 사용자 정보가 저장된 경로

        // UI 업데이트
        updateUI(choose,uid);
    }

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


        ///////////////////// 정답데이터 가져오기 //////////////////////
        String num = Integer.toString(randomQue[currentIdx]);
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
                            Answer = documentSnapshot.getString("a");
                            // 가져온 질문을 TextView에 표시
                            txAnswer.setText(Answer);
                            Log.e("정답 : ",Answer);

                        } else {
                            // 문서가 존재하지 않는 경우 처리
                            txAnswer.setText("문서가 존재하지 않습니다.");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 데이터 가져오기 실패 시 처리
                        txAnswer.setText("데이터를 가져오는 데 문제가 발생했습니다.");
                    }
                });

        ////////////////// 문제 목록 가져오기 ////////////////////
        toQuestion(randomQue[currentIdx], Type);
        //Log.e("2.정답 : ", Answer);

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
                // Firebase Realtime Database에서 사용자 정보 가져오기
                //mDatabaseRef = FirebaseDatabase.getInstance().getReference(); // DatabaseReference 초기화
                // 해당 주제의 문제 개수를 넘었을 경우 다음주제로 이동
                if(currentIdx >= choose - 1){
                    totalIdx++;
                    // 사용자의 답안과 정답 비교
                    Log.e("현재 인덱스 : ", Integer.toString(currentIdx));
                    Log.e("전체 인덱스 : ", Integer.toString(totalIdx));

                    String userAnswer = etAnswer.getText().toString();
                    // 사용자의 답안과 정답 비교
                    if (userAnswer.equals(Answer)) {
                        score++; // 정답이면 score 증가
                        currentscore++;
                        Log.e("점수 : ",Integer.toString(score));
                    }

                    Intent intent = new Intent(TestCal.this, TestLan.class);

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
                    String userAnswer = etAnswer.getText().toString();
                    btnNext.setEnabled(true);

                    // 사용자의 답안과 정답 비교
                    if (userAnswer.equals(Answer)) {
                        score++; // 정답이면 score 증가
                        currentscore++;
                        Log.e("점수 : ",Integer.toString(score));
                    }

                    // 문제 번호 증가
                    currentIdx++;
                    totalIdx++;

                    String num = Integer.toString(randomQue[currentIdx]);
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
                                        Answer = documentSnapshot.getString("a");
                                        // 가져온 질문을 TextView에 표시
                                        txAnswer.setText(Answer);
                                        Log.e("정답 : ",Answer);

                                    } else {
                                        // 문서가 존재하지 않는 경우 처리
                                        txAnswer.setText("문서가 존재하지 않습니다.");
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // 데이터 가져오기 실패 시 처리
                                    txAnswer.setText("데이터를 가져오는 데 문제가 발생했습니다.");
                                }
                            });

                    // 문제 목록 가져오기
                    toQuestion(randomQue[currentIdx], Type);
                    Log.e("정답 : ", Answer);

                    Log.e("현재 인덱스 : ", Integer.toString(currentIdx));
                    Log.e("전체 인덱스 : ", Integer.toString(totalIdx));

                    txIndex.setText(Integer.toString(totalIdx));
                    txAnswer.setText(Answer);

                    etAnswer.setText("");  // 빈 문자열로 설정하여 텍스트를 삭제
                }
            }
        });
    }

    public String toAnswer(int i) {
        String num = Integer.toString(i);
        // Firebase Firestore 초기화
        db = FirebaseFirestore.getInstance();
        // 반환할 정답 문자열
        final String[] answer = new String[1];
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
                            answer[0] = documentSnapshot.getString("a");
                            // 가져온 질문을 TextView에 표시
                            txAnswer.setText(answer[0]);
                            Log.e("정답 : ", answer[0]);
                        } else {
                            // 문서가 존재하지 않는 경우 처리
                            txAnswer.setText("오류가 발생하였습니다.");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 데이터 가져오기 실패 시 처리
                        txAnswer.setText("오류가 발생하였습니다.");
                    }
                });

        return answer[0];
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
}