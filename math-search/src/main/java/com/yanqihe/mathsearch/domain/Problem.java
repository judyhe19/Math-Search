// Problem class
package com.yanqihe.mathsearch.domain;

import lombok.Data;

@Data
public class Problem {
    private Integer id;
    private String questionText;
    private String questionImage;
    private String questionLatexCode;
    private String answerText;
    private String answerImage;
    private String answerLatexCode;
    private Integer ownerId;
}