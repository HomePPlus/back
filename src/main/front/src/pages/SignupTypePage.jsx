// SignupTypePage.jsx
import React from 'react';
import { useNavigate, Link } from 'react-router-dom';
import './SignupTypePage.css';

const SignupTypePage = () => {
    const navigate = useNavigate();
  
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
  
        <div className="signup-steps">
          <div className="step">
            <div className="step-number">1단계</div>
            <div className="step-name">약관동의</div>
          </div>
          <div className="step active">
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
  
        <div className="type-selection">
          <h3>회원가입 유형을 선택해주세요</h3>
          <div className="type-options">
            <div 
              className="type-option resident"
              onClick={() => navigate('/signup/resident')}
            >
              <img src="/images/resident-icon.png" alt="입주민" />
              <h4>입주민</h4>
              <p>일반적인 회원가입 입니다.</p>
            </div>
            <div 
              className="type-option manager"
              onClick={() => navigate('/signup/manager')}
            >
              <img src="/images/manager-icon.png" alt="관리자" />
              <h4>관리자</h4>
              <p>관리자 전용 회원가입 입니다.</p>
            </div>
          </div>
        </div>
  
        <footer className="footer">
          <div className="contact-info">
            <p>부산민원120 콜센터</p>
            <p className="phone">051 - 120</p>
            <p className="hours">평일 08:30 ~ 18:30 야간.공휴일 당직실 전환</p>
          </div>
        </footer>
      </div>
    );
};

export default SignupTypePage;
