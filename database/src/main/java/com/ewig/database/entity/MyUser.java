package com.ewig.database.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("user")
public class MyUser {
    @Id
    long id;
    @Column("full_name")
    String fullName;
    String email;
    String phone;
    String password;
    @Column("user_role")
    int userRole;
}
