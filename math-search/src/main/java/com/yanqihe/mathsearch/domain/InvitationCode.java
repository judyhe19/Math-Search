// Invitation code class
package com.yanqihe.mathsearch.domain;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class InvitationCode {
    private Integer id;
    private String invitationCode;
    private Timestamp invitationExpire;
    private Integer adminId;
}
