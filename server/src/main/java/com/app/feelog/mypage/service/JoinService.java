package com.app.feelog.mypage.service;

import com.app.feelog.domain.dto.MemberDTO;
import com.app.feelog.mypage.repository.JoinLogDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class JoinService implements ToDTO {
    private final JoinLogDAO joinLogDAO;

    // 2025.04.17 조승찬 :: 아이디로 멤버 정보 가져오기
    public Optional<MemberDTO> getMemberByEmail(String memberEmail) {

        return Optional.ofNullable(
                toMemberDTO(joinLogDAO.getMemberByEmail(memberEmail)
                        .orElse(null)));
    }

    // 2025.04.17 조승찬 :: 카카오 회원가입시 멤버 이메일, 닉네임 저장
    public void kakaoJoin(MemberDTO memberDTO) {
        joinLogDAO.kakaoJoin(memberDTO.toVO());
    }

    // 2025.04.18 조승찬 :: 닉네임으로 멤버 정보 가져오기
    public Optional<Object> getMemberByNickname(String memberNickname) {
        return Optional.ofNullable(
                toMemberDTO(joinLogDAO.getMemberByNickname(memberNickname)
                        .orElse(null)));
    }

    // 2025.04.18 조승찬 :: 이메일 회원가입시 멤버 이메일, 닉네임, 비밀번호 저장
    public void emailJoin(MemberDTO memberDTO) {
        joinLogDAO.emailJoin(memberDTO.toVO());
    }

    public void insertMemberTask(Long id) {
        joinLogDAO.insertMemberTask(id);
    }

}
