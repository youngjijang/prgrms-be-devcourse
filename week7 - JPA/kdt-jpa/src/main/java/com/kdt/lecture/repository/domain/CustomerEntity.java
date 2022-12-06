package com.kdt.lecture.repository.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity // 실제 RDB 테이블과 mapping이 되는 객체 라는 것을 명시
@Table(name = "customer") // table ㅇ어노테이션이 없으면 기본적으로 class 명과 동일한 테이블을 찾는다.
public class CustomerEntity {

    @Id // pk 명시
    private long id;
    private String firstName;
    private String lastName;


    //getter setter를 통해 entity 작동
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
