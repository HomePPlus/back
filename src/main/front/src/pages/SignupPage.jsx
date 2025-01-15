// SignupPage.jsx
import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import './SignupPage.css';

const SignupPage = () => {
  const [agreements, setAgreements] = useState({
    terms: false,
    privacy: false
  });

  const handleAgreementChange = (type) => {
    setAgreements(prev => ({
      ...prev,
      [type]: !prev[type]
    }));
  };

  return (
    <div className="signup-container">
      <header className="header">
        <div className="logo">
          <h1>당신의 안전한 주택</h1>
        </div>
        <nav className="main-nav">
          <ul>
            <li><Link to="/intro">소개</Link></li>
            <li><Link to="/report">신고게시판</Link></li>
            <li><Link to="/info">정보 공개</Link></li>
            <li><Link to="/communication">소통</Link></li>
          </ul>
        </nav>
      </header>

      <div className="breadcrumb">
        <span>통합회원</span> &gt; <span>회원가입</span>
      </div>

      <div className="signup-steps">
        <div className="step active">
          <div className="step-number">1단계</div>
          <div className="step-name">약관동의</div>
        </div>
        <div className="step">
          <div className="step-number">2단계</div>
          <div className="step-name">유형선택</div>
        </div>
        <div className="step">
          <div className="step-number">3단계</div>
          <div className="step-name">정보입력</div>
        </div>
        <div className="step">
          <div className="step-number">4단계</div>
          <div className="step-name">가입완료</div>
        </div>
      </div>

      <div className="notice-section">
        <h3>주의사항</h3>
        <p className="notice-text">
          다른 사람의 주민등록번호 또는 아이디/비밀번호를 부정하게 사용한 경우는 
          <span className="highlight">5년 이하의 징역이나 5000만원 이하의 벌금</span>
          이 부과됩니다. 개인정보 보호법에 따라 회원정보 보유기간은 최종 로그인 날짜로 부터 4년이며, 4년 후 자동 탈퇴처리 됩니다.
          (아래 개인정보 수집 및 이용약관 참조)
        </p>
      </div>

      <div className="agreement-section">
        <div className="agreement-item">
          <input
            type="checkbox"
            id="terms"
            checked={agreements.terms}
            onChange={() => handleAgreementChange('terms')}
          />
          <label htmlFor="terms">이용약관에 동의하십니까?(필수)</label>
        </div>

        <div className="agreement-item">
          <input
            type="checkbox"
            id="privacy"
            checked={agreements.privacy}
            onChange={() => handleAgreementChange('privacy')}
          />
          <label htmlFor="privacy">개인정보 수집에 동의하십니까?(필수)</label>
        </div>
      </div>

      <button 
        className="next-button"
        disabled={!agreements.terms || !agreements.privacy}
      >
        다음
      </button>

      <footer className="footer">
        <div className="contact-info">
          <p>부산민원120 콜센터</p>
          <p className="phone">051 - 120</p>
          <p className="hours">평일 08:30 ~ 18:30 야간.공휴일 당직실 전환</p>
        </div>
        <div className="address">
          (우 559988) 부산광역시 부산구 부산대로 1048(부산동)
        </div>
      </footer>
    </div>
  );
};

export default SignupPage;
