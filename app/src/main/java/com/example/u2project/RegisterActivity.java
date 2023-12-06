package com.example.u2project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

// 회원가입
public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;     // 파이어베이스 인증
    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스
    private EditText mEtEmail, mEtPwd,mEtName, mEtAdd_si,mEtAdd_gu,mEtAdd_dong,mEtBYear,mEtBMon,mEtBDay;      // 회원가입 입력 필드
    private ImageView mBtnRegister;         // 회원가입 버튼

    Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("DMC");

        mEtEmail = findViewById(R.id.et_email);      // 유저 이메일
        mEtPwd = findViewById(R.id.et_pwd);          // 유저 비밀번호

        mEtName = findViewById(R.id.et_name);   // 유저 이름

        mEtAdd_si = findViewById(R.id.et_add_si);       // 유저 사는 지역
        mEtAdd_gu = findViewById(R.id.et_add_gu);       // 유저 사는 구
        mEtAdd_dong = findViewById(R.id.et_add_dong);   // 유저 사는 동

        mEtBYear = findViewById(R.id.et_byear); // 생년
        mEtBMon = findViewById(R.id.et_bmon);   // 월
        mEtBDay = findViewById(R.id.et_bday);   // 일


        mBtnRegister = findViewById(R.id.btn_register);

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원가입 처리 시작
                String strEmail = mEtEmail.getText().toString();        // 이메일
                String strPwd = mEtPwd.getText().toString();            // 비밀번호
                String strName = mEtName.getText().toString();          // 이름
                String strAdd_si = mEtAdd_si.getText().toString();      // 시
                String strAdd_gu = mEtAdd_gu.getText().toString();      // 구
                String strAdd_dong = mEtAdd_dong.getText().toString();  // 동
                String strBYear = mEtBYear.getText().toString();        // 생년
                String strBMon = mEtBMon.getText().toString();          // 월
                String strBday = mEtBDay.getText().toString();          // 일

                int Age = Integer.valueOf(strBYear);
                Age = calendar.get(Calendar.YEAR) - Age + 1;
                String strAge = Integer.toString(Age);
                // FirebaseAuth 진행
                mFirebaseAuth.createUserWithEmailAndPassword(strEmail,strPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // 로그인 성공
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

                            UserAccount account = new UserAccount();
                            account.setIdToken(firebaseUser.getUid());
                            account.setEmailID(firebaseUser.getEmail());
                            account.setPassword(strPwd);

                            // 추가 정보 설정
                            account.setName(strName);
                            account.setAdd_si(strAdd_si);
                            account.setAdd_gu(strAdd_gu);
                            account.setAdd_dong(strAdd_dong);
                            account.setBYear(strBYear);
                            account.setBMon(strBMon);
                            account.setBday(strBday);
                            account.setAge(strAge);

                            // Firebase Realtime Database에 데이터 저장
                            mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);

                            Toast.makeText(RegisterActivity.this, "회원가입에 성공하셨습니다 :)", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(RegisterActivity.this, "회원가입에 실패하셨습니다 :(", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

    }

}