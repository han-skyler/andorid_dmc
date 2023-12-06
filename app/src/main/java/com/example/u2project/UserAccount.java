package com.example.u2project;

// 리얼타임 데이터베이스
// 사용자 계정 정보 모델 클래스
public class UserAccount {

    // 추후에 저장할 것이 있다면 더 추가해서 불러올 수 있음
    private String emailID;     // 파이어베이스 고유 토큰 정보 UID
    private String password;    // 비밀번호
    private String idToken;     // uid
    private String name;        // 이름
    private String Add_si;      // 시
    private String Add_gu;      // 구
    private String Add_dong;    // 동
    private String BYear;       // 생년
    private String BMon;        // 월
    private String Bday;        // 일
    private String Age;         // 나이

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
    }

    // 리얼타임데이터베이스를 사용하기 위한 구문
    public UserAccount() { }


    // 설정하고 가져오는 과정
    public String getEmailID() {
        return emailID;
    }
    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }



    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }



    public String getIdToken() {
        return idToken;
    }
    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getAdd_si() {
        return Add_si;
    }
    public void setAdd_si(String add_si) {
        Add_si = add_si;
    }



    public String getAdd_gu() {
        return Add_gu;
    }
    public void setAdd_gu(String add_gu) {
        Add_gu = add_gu;
    }



    public String getAdd_dong() {
        return Add_dong;
    }
    public void setAdd_dong(String add_dong) {
        Add_dong = add_dong;
    }



    public String getBYear() {
        return BYear;
    }

    public void setBYear(String BYear) {
        this.BYear = BYear;
    }



    public String getBMon() {
        return BMon;
    }

    public void setBMon(String BMon) {
        this.BMon = BMon;
    }



    public String getBday() {
        return Bday;
    }

    public void setBday(String bday) {
        Bday = bday;
    }



}
