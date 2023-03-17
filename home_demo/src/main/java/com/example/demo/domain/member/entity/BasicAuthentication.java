package com.example.demo.domain.member.entity;
/*
패스워드해쉬컨버트 : 관리자도 비번 못보게
 */
import lombok.Setter;
import javax.persistence.Column;
import javax.persistence.Convert;
import com.example.demo.domain.utility.encrypt.EncryptionUtil;
import com.example.demo.domain.utility.password.PasswordHashConverter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@ToString(callSuper = true)// 상속받은 클래스의 정보까지 출력
@NoArgsConstructor
@DiscriminatorValue(Authentication.BASIC_AUTH)//하위 클래스에 선언한다. 엔티티를 저장할 때 슈퍼타입의 구분 컬럼에 저장할 값을 지정한다.
//authenticationType의 값이  Authentication.BASIC_AUTH인 "BASIC" 일때 이 엔티티가 작동.
public class BasicAuthentication extends Authentication {

    @Setter
    @Column(nullable = false)
    @Convert(converter = PasswordHashConverter.class)//비밀번호 해시코드로 변환
    private String password;

    public BasicAuthentication (Member member, String authenticationType, String password) {
        super(member, authenticationType);
        this.password = password;//비번 해시코드로 변환해서 저장.
    }

    public boolean isRightPassword(String plainToCheck) {
        return EncryptionUtil.checkValidation(plainToCheck, password);//입력한 패스워드랑 변환된패스워드랑 같은지 확인
    }
}