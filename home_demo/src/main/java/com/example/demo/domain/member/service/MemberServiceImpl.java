package com.example.demo.domain.member.service;

import com.example.demo.domain.member.entity.Member;
import com.example.demo.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.demo.domain.member.entity.Authentication;
import com.example.demo.domain.member.entity.BasicAuthentication;
import com.example.demo.domain.member.service.request.MemberLoginRequest;
import com.example.demo.domain.member.repository.AuthenticationRepository;
import com.example.demo.domain.member.service.request.MemberRegisterRequest;
import java.util.UUID;
import java.util.Optional;
import com.example.demo.domain.security.service.RedisService;
import com.example.demo.domain.member.service.request.EmailMatchRequest;
import com.example.demo.domain.member.service.request.EmailPasswordRequest;
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    final private MemberRepository memberRepository;
    final private AuthenticationRepository authenticationRepository;
    final private RedisService redisService;
    @Override
    public Boolean emailValidation(String email) {
        Optional<Member> maybeMember = memberRepository.findByEmail(email);
        if (maybeMember.isPresent()) {
            return false;
        }
        return true;
    }

    @Override
    public Boolean signUp(MemberRegisterRequest memberRegisterRequest) {
        final Member member = memberRegisterRequest.toMember();
        memberRepository.save(member);

        final BasicAuthentication authentication = new BasicAuthentication(
                member,
                Authentication.BASIC_AUTH,//authenticationType칼럼에 값을 멤버변수 BASIC_AUTH의 값을 넣음
                memberRegisterRequest.getPassword()
        );
        authenticationRepository.save(authentication);

        return true;
    }

    @Override
    public String signIn(MemberLoginRequest memberLoginRequest) {
        Optional<Member> maybeMember =
                memberRepository.findByEmail(memberLoginRequest.getEmail());

        System.out.println("loginRequest: " + memberLoginRequest);
        System.out.println("maybeMember.isPresent(): " + maybeMember.isPresent());

        if (maybeMember.isPresent()) {//이메일이 존재할경우
            Member member = maybeMember.get();

            System.out.println("사용자가 입력한 비번: " + memberLoginRequest.getPassword());
            System.out.println("비밀번호 일치 검사: " + member.isRightPassword(memberLoginRequest.getPassword()));

            if (!member.isRightPassword(memberLoginRequest.getPassword())) {
                System.out.println("잘 들어오나 ?");
                throw new RuntimeException("이메일 및 비밀번호 입력이 잘못되었습니다!");
                //있는 이메일인데 비번이 잘못입력됨
            }
            //비번이 제대로 입력됨(로그인성공)
            UUID userToken = UUID.randomUUID();

            // redis 처리 필요
            redisService.deleteByKey(userToken.toString());
            redisService.setKeyAndValue(userToken.toString(), member.getId());
            //레디스에 토큰:유저ID 입력
            return userToken.toString();
        }

        throw new RuntimeException("가입된 사용자가 아닙니다!");
    }

    @Override
    public Boolean applyNewPassword(EmailPasswordRequest emailPasswordRequest) {
        Optional<Authentication> maybeAuthentication = authenticationRepository.findByEmail(emailPasswordRequest.getEmail());
        if (!maybeAuthentication.isPresent()){ //인증정보가 존재하지 않을 경우
            System.out.println("인증정보가없습니다.");
            return false;
        }
        System.out.println("인증정보가 존재합니다.");
        BasicAuthentication authentication = (BasicAuthentication)maybeAuthentication.get();
        System.out.println("emailPasswordRequest.getPassword():" + emailPasswordRequest.getPassword());
        authentication.setPassword(emailPasswordRequest.getPassword());
        authenticationRepository.save(authentication);
        System.out.println("비번변경완료");
        return true;
    }

    @Override
    public Boolean emailMatch(EmailMatchRequest toEmailMatchRequest) {
        Optional<Member> maybeMember = memberRepository.findByEmail(toEmailMatchRequest.getEmail());
        if (!maybeMember.isPresent()){//이메일이 존재하지 않을경우
            return false;
        }

        return true;//이미존재하는 이메일
    }
}