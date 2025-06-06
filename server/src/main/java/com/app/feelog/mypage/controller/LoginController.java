// 2025.04.17 조승찬   ::  로그인

package com.app.feelog.mypage.controller;

import com.app.feelog.domain.dto.MemberDTO;
import com.app.feelog.mypage.service.EmailService;
import com.app.feelog.mypage.service.LoginService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/login")
@Slf4j
public class LoginController {
    private final LoginService loginService;
    private final EmailService emailService;

    @GetMapping("/login")
    public String getLogin() {
        return "login/login";
    }

    @GetMapping("/email-login")
    public String getEmailLogin() {
        return "login/email-login";
    }

    // 2025.04.19 조승찬 :: 이메일 로그인 처리
    @ResponseBody
    @PostMapping("/email-login")
    public ResponseEntity<String> postEmailLogin(@RequestBody MemberDTO memberDTO, HttpSession session) {

        log.info("login " + memberDTO.toVO());
        if (!loginService.getMemberByEmailAndPassword(memberDTO).isPresent()) {
            log.info("memberDTO is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("로그인 실패");
        }

        // 로그인 성공 처리
        MemberDTO member = loginService.getMemberByEmailAndPassword(memberDTO).get();
        log.info("member type " + member.getMemberType());

        session.setAttribute("memberStatus", "email");
        session.setAttribute("member", member);

        String redirectUrl = (String) session.getAttribute("redirectAfterLogin");

        if (redirectUrl != null) {
            session.removeAttribute("redirectAfterLogin");
            return ResponseEntity.status(HttpStatus.OK) // 성공 상태 200 반환
                    .body(redirectUrl); // 리다이렉션 URL 반환
        }

        if ("ADMIN".equals(member.getMemberType().toString())) {
            session.removeAttribute("redirectAfterLogin");
            return ResponseEntity.status(HttpStatus.OK) // 성공 상태 200 반환
                    .body("/admin/admin"); // 리다이렉션 URL 반환
        }

        // 기본 리다이렉션 설정
        return ResponseEntity.status(HttpStatus.OK)
                .body("/"); // 기본 URL 반환
    }

    // 2025.04.19 조승찬 :: 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        if (session.getAttribute("memberStatus") != null) {
            if (session.getAttribute("memberStatus").equals("kakao")) {

                String kakaoLogoutUrl = "https://kauth.kakao.com/oauth/logout?"
                        + "client_id=" + "1f0eec6d921e399c7940b9689a7896e3"
                        + "&logout_redirect_uri=" + "http://feelog.shop/login/login";
            }

            session.invalidate();
            log.info("로그아웃 완료");
        }

        return "redirect:/";
    }

    // 2025.04.19 조승찬 :: 비밀번호 재설정 화면
    @GetMapping("/password-issue")
    public String getPasswordIssue() {
        return "login/password-issue";
    }

    // 2025.04.19 조승찬 :: 비밀번호 재설정
    @PostMapping("/password-issue")
    public ResponseEntity<String> postPasswordIssue(@RequestBody MemberDTO memberDTO, HttpServletResponse response,
                                                    HttpSession session) throws MessagingException {

        // 이메일이 같은 멤버 찾기
        if (!loginService.getMemberByEmail(memberDTO.getMemberEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("존재하지 않는 회원입니다.");
        }

        // 확인 메일 보내기
        emailService.sendPasswordIssue(memberDTO.getMemberEmail(), response);

        // 로그인 상태
        MemberDTO member = loginService.getMemberByEmail(memberDTO.getMemberEmail()).get();
        session.setAttribute("memberStatus", "email");
        session.setAttribute("member", member);

        return ResponseEntity.status(HttpStatus.OK)
                .body("발급 성공"); // 기본 URL 반환
    }


    //    이메일로 받은 토큰 대조 후 일치하면 마이페이지의 설정 페이지로
    @GetMapping("confirm")
    public String confirm(@CookieValue(name = "token", required = false) String token,
                          @SessionAttribute(name = "member", required = false) MemberDTO member,
                          @RequestParam String code,
                          HttpServletResponse response, Model model) {
        log.info("token " + token);

        // 토큰이 만료되었을 때(유효기간이 지났을 때)
        if (token == null || token.isEmpty()) {
            log.info("token is null");
            model.addAttribute("errorMessage", "초대 링크가 만료되었습니다.");
            return "/login/password-issue";
        }

        // 발급받은 토큰이 동일하다면 쿠키 초기화
        if (token.equals(code)) {
            log.info("token = code");
            Cookie cookie = new Cookie("token", "");
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);

            // 프로필 설정 화면으로 이동
            return "redirect:/myPage/setting-profile";

        }

        log.info("정보 불일치. 비밀번호 재발급로 이동합니다.");
        model.addAttribute("errorMessage", "정보가 불일치합니다.");
        return "/login/password-issue";
    }

}
