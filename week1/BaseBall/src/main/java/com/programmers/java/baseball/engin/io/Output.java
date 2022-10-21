package com.programmers.java.baseball.engin.io;

import com.programmers.java.baseball.engin.model.BallCount;

public interface Output {
    void ballCount(BallCount bc);

    void inputError();

    void correct();
}
