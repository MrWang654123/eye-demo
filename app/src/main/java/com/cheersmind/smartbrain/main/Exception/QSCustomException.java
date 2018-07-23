package com.cheersmind.smartbrain.main.Exception;

/**
 * Created by goodm on 2017/4/15.
 */
public class QSCustomException extends Exception {
    //private String message; // a detailed message
//    public QSCustomException (String message) {
//        this.message = message;
//    }
    public QSCustomException() {
        super();
    }

    public QSCustomException(String detailMessage) {
        super(detailMessage);
    }
}
