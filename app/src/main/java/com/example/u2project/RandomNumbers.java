package com.example.u2project;

import android.util.Log;

import java.util.Random;

public class RandomNumbers {
    public int[] generateRandomNumbers(int count, int max) {
        int[] shuffledNumbers = new int[max];
        int[] randomNumbers = new int[count];
        Random random = new Random();

        // 1부터 max까지의 숫자로 배열 초기화
        for (int i = 0; i < max; i++) {
            shuffledNumbers[i] = i + 1;
        }

        // Fisher-Yates 셔플링 알고리즘을 사용하여 배열 무작위 섞음
        for (int i = max - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = shuffledNumbers[i];
            shuffledNumbers[i] = shuffledNumbers[j];
            shuffledNumbers[j] = temp;
        }

        // 처음 count개의 숫자만 선택
        for (int i = 0; i < count; i++) {
            randomNumbers[i] = shuffledNumbers[i];
            Log.e("랜덤숫자 : ",Integer.toString(randomNumbers[i]));
        }

        return randomNumbers;
    }
}
