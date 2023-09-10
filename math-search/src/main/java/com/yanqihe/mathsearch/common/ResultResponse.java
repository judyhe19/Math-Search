// Result Response class - all response from services will be returned in a ResultResponse object
package com.yanqihe.mathsearch.common;

import lombok.Data;

@Data
public class ResultResponse<T> {
    int code = 0; // success/error code
    String message = "success"; // message - will include error message if service methods failed
    T data; // data containing the returned object/array list/array from the database
}