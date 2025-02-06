package com.example.projet;

import org.opencv.core.Core;

public class OpenCVTest {
    static {
        System.loadLibrary("opencv_java4100");
    }
    public static void main(String[] args) {
        System.out.println("OpenCV version: " + Core.VERSION);
    }
}
