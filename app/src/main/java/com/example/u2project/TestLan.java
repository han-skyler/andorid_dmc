package com.example.u2project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class TestLan extends AppCompatActivity {

    String Type = "Language";

    TextView txQuestion, txIndex, txTotal, txAnswer, tx1, tx2, tx3;  // 질문지, 질문번호, 진행정도
    Button btn1,btn2,btn3;
    String etAnswer;
    FirebaseFirestore db;
    DatabaseReference mDatabaseRef;

    // -------------주제별로 다르게 세팅할 부분-----------------
    // 몇개 문제를 랜덤하게 뽑을건지
    int choose = 3;

    // 해당 주제 문제의 총 개수
    int allQueNum = 5;

    // ----------------------------------------------------
    int score;
    int currentscore = 0;
    int totalIdx = 13;       // 전체 인덱스
    int currentIdx = 0; // 해당 주제 인덱스
    //-----------------------------------

    // 랜덤한 문제 번호 출력 ///////////////////////////////////
    RandomNumbers random = new RandomNumbers();
    int[] randomQue = random.generateRandomNumbers(choose, allQueNum);

    String Answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_lan);

        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);

        ////////// 전체 인덱스 불러오기 및 저장 //////////
        Intent intent = getIntent();

        int score = intent.getIntExtra("score",0);
        int totalIdx = intent.getIntExtra("totalIdx",13);

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
        txAnswer = findViewById(R.id.tx_answer);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);

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

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userAnswer = "1";
                if(currentIdx >= choose - 1){
                    totalIdx++;
                    // 사용자의 답안과 정답 비교
                    Log.e("현재 인덱스 : ", Integer.toString(currentIdx));
                    Log.e("전체 인덱스 : ", Integer.toString(totalIdx));

                    // 사용자의 답안과 정답 비교
                    if (userAnswer.equals(Answer)) {
                        score++; // 정답이면 score 증가
                        currentscore++;
                        Log.e("점수 : ",Integer.toString(score));
                    }
                    Log.e("점수 : ",Integer.toString(score));
                    Intent intent = new Intent(TestLan.this, TestRecall.class);

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
                    // 사용자의 답안과 정답 비교
                    if (userAnswer.equals(Answer)) {
                        score++; // 정답이면 score 증가
                        currentscore++;
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
                    Log.e("점수 : ",Integer.toString(score));
                    // 문제 목록 가져오기
                    toQuestion(randomQue[currentIdx], Type);
                    Log.e("정답 : ", Answer);


                    Log.e("현재 인덱스 : ", Integer.toString(currentIdx));
                    Log.e("전체 인덱스 : ", Integer.toString(totalIdx));

                    txIndex.setText(Integer.toString(totalIdx));
                    txAnswer.setText(Answer);

                    // 빈 문자열로 설정하여 텍스트를 삭제
                }
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userAnswer = "2";
                if(currentIdx >= choose - 1){
                    totalIdx++;
                    // 사용자의 답안과 정답 비교
                    Log.e("현재 인덱스 : ", Integer.toString(currentIdx));
                    Log.e("전체 인덱스 : ", Integer.toString(totalIdx));
                    // 사용자의 답안과 정답 비교
                    if (userAnswer.equals(Answer)) {
                        score++; // 정답이면 score 증가
                        currentscore++;
                    }

                    Intent intent = new Intent(TestLan.this, TestRecall.class);

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
                    // 사용자의 답안과 정답 비교
                    if (userAnswer.equals(Answer)) {
                        score++; // 정답이면 score 증가
                        currentscore++;
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

                    // 빈 문자열로 설정하여 텍스트를 삭제
                }
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userAnswer = "3";
                if(currentIdx >= choose - 1){
                    totalIdx++;
                    // 사용자의 답안과 정답 비교
                    Log.e("현재 인덱스 : ", Integer.toString(currentIdx));
                    Log.e("전체 인덱스 : ", Integer.toString(totalIdx));

                    // 사용자의 답안과 정답 비교
                    if (userAnswer.equals(Answer)) {
                        score++; // 정답이면 score 증가
                        currentscore++;
                    }

                    Intent intent = new Intent(TestLan.this, TestRecall.class);

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
                    // 사용자의 답안과 정답 비교
                    if (userAnswer.equals(Answer)) {
                        score++; // 정답이면 score 증가
                        currentscore++;
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

                    // 빈 문자열로 설정하여 텍스트를 삭제
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