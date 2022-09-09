package com.example.restfulweb.user;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
//@JsonIgnoreProperties(value = {"password","ssn"}) // -> Json에서 제외시킬 Properties 설정
@NoArgsConstructor
@JsonFilter("UserInfo")
@ApiModel(description = "사용자 상세 정보를 윈한 도메인 객체")
@Entity
public class User {
    @Id
    @GeneratedValue
    private Integer id;
    @Size(min=2, message = "Name은 두글자 이상 입력해주세요")
    @ApiModelProperty(notes = "사용자 이름을 입력해 주세요.")
    private String name;
    @Past
    @ApiModelProperty(notes = "사용자의 등록일을 입력해 주세요.")
    private Date joinDate;

//    @JsonIgnore -> Json 출력 값에서 이 항목을 뺸 나머지 항목 출력
    @ApiModelProperty(notes = "사용자의 패스워드를 입력해 주세요.")
    private String password;
//    @JsonIgnore -> Json 출력 값에서 이 항목을 뺸 나머지 항목 출력
    @ApiModelProperty(notes = "사용자의 주민등록번호를 입력해 주세요.")
    private String ssn;

    @OneToMany(mappedBy = "user")
    private List<Post> posts;

    public User(int id, String name, Date joinDate, String password, String ssn) {
        this.id = id;
        this.name = name;
        this.joinDate = joinDate;
        this.password = password;
        this.ssn = ssn;
    }
}
