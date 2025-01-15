import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import DaumPostcode from 'react-daum-postcode';
import api from '../../utils/api';
import './ResidentSignupPage.css';

const ResidentSignupPage = () => {
  const navigate = useNavigate();
  const [formData, setState] = useState({
    userName: '',
    userId: '',
    password: '',
    passwordConfirm: '',
    phone: '',
    postcode: '',
    address: '',
    detailAddress: '',
    verificationCode: '' // 인증 코드 필드 추가
  });
  
  // 기존 상태들
  const [showPassword, setShowPassword] = useState(false);
  const [showPasswordConfirm, setShowPasswordConfirm] = useState(false);
  const [showPostcode, setShowPostcode] = useState(false);
  const [emailChecked, setEmailChecked] = useState(false);
  
  // 이메일 인증 관련 상태 추가
  const [isEmailVerified, setIsEmailVerified] = useState(false);
  const [showVerificationInput, setShowVerificationInput] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setState(prev => ({
      ...prev,
      [name]: value
    }));
    if (name === 'userId') {
      setEmailChecked(false);
      setIsEmailVerified(false);
    }
  };

  // 이메일 중복 확인
const checkEmailDuplicate = async () => {
  try {
    const response = await api.checkEmail(formData.userId);
    alert(response.data.message);
    setEmailChecked(response.data.message === "사용 가능한 이메일입니다.");
    setShowVerificationInput(response.data.message === "사용 가능한 이메일입니다.");
  } catch (error) {
    console.error('이메일 중복 확인 실패:', error);
    alert('이메일 중복 확인에 실패했습니다.');
  }
};

// 인증 코드 발송
const sendVerificationCode = async () => {
  try {
    const response = await api.sendVerificationCode(formData.userId);
    alert(response.data.message);
  } catch (error) {
    console.error('인증 코드 발송 실패:', error);
    alert(error.response?.data?.message || '인증 코드 발송에 실패했습니다.');
  }
};

// 인증 코드 확인
const verifyCode = async () => {
  try {
    const response = await api.verifyEmail(formData.verificationCode);
    alert(response.data.message);
    // 인증 성공 시 상태 업데이트
    setIsEmailVerified(true);
  } catch (error) {
    console.error('인증 코드 확인 실패:', error);
    alert(error.response?.data?.message || '인증 코드 확인에 실패했습니다.');
    setIsEmailVerified(false);
  }
};



  // handlePostcode 함수 정의 추가
  const handlePostcode = (data) => {
    // 주소 데이터 처리
    let fullAddress = data.address;
    let extraAddress = '';

    if (data.addressType === 'R') {
      if (data.bname !== '') {
        extraAddress += data.bname;
      }
      if (data.buildingName !== '') {
        extraAddress += extraAddress !== '' ? `, ${data.buildingName}` : data.buildingName;
      }
      fullAddress += extraAddress !== '' ? ` (${extraAddress})` : '';
    }

    // 상태 업데이트
    setState(prev => ({
      ...prev,
      postcode: data.zonecode,
      address: fullAddress
    }));
    
    // 주소 검색 창 닫기
    setShowPostcode(false);
  };


  const handleSubmit = async (e) => {
    e.preventDefault();
      
    if (!emailChecked) {
      alert('이메일 중복확인을 해주세요.');
      return;
    }
  
    if (!isEmailVerified) {
      alert('이메일 인증을 완료해주세요.');
      return;
    }
  
    const passwordRegex = /^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$()*/~^{}])[a-zA-Z0-9!@#$()*/~^{}]{8,24}$/;
    if (!passwordRegex.test(formData.password)) {
      alert('비밀번호는 영문, 숫자, 특수문자(!@#$()*/~^{})를 포함하여 8-24자리로 입력해주세요.');
      return;
    }
  
    if (formData.password !== formData.passwordConfirm) {
      alert('비밀번호가 일치하지 않습니다.');
      return;
    }
  
    try {
      const response = await api.registerResident({
        userName: formData.userName,
        email: formData.userId,
        password: formData.password,
        confirmPassword: formData.passwordConfirm,
        phone: formData.phone,
        detailAddress: formData.detailAddress
      });
      
      // 스프링에서 오는 메시지를 직접 표시
      alert(response.data.message);
      if (response.data.message === "회원가입이 완료되었습니다.") {
        navigate('/signup/complete');
      }
    } catch (error) {
      console.error('회원가입 실패:', error);
      // 에러 메시지도 스프링에서 오는 그대로 표시
      alert(error.response?.data || '회원가입에 실패했습니다.');
    }
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

      <div className="signup-steps">
        <div className="step">1단계 약관동의</div>
        <div className="step">2단계 유형선택</div>
        <div className="step active">3단계 정보입력</div>
        <div className="step">4단계 가입완료</div>
      </div>

      <form onSubmit={handleSubmit} className="signup-form">
        <h3>필수 정보입력</h3>
        
        <div className="form-group">
          <label>회원이름</label>
          <input
            type="text"
            name="userName"
            value={formData.userName}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-group">
      <label>아이디 (이메일 주소)</label>
      <div className="input-with-button">
        <input
          type="email"
          name="userId"
          value={formData.userId}
          onChange={handleChange}
          placeholder="예시: aivle2024@gmail.com"
          required
        />
        <button 
          type="button" 
          className="check-button" 
          onClick={checkEmailDuplicate}
        >
          이메일 중복확인
        </button>
      </div>
      {emailChecked && !isEmailVerified && (
        <div className="verification-section">
          <button 
            type="button" 
            className="send-verification" 
            onClick={sendVerificationCode}
          >
            인증코드 받기
          </button>
          <div className="verification-input">
            <input
              type="text"
              name="verificationCode"
              value={formData.verificationCode}
              onChange={handleChange}
              placeholder="인증코드 입력"
            />
            <button 
              type="button" 
              className="verify-button"
              onClick={verifyCode}
            >
              확인
            </button>
          </div>
        </div>
      )}
      <p className="help-text">
        아이디는 공백없이 영문(소문자)으로 시작하고, 영문(소문자), 숫자로만 이루어진 이메일(email) 형식만 허용됩니다.
      </p>
    </div>
    <div className="form-group">
      <label>비밀번호</label>
      <div className="input-with-button">
        <input
          type={showPassword ? "text" : "password"}
          name="password"
          value={formData.password}
          onChange={handleChange}
          required
        />
        <button 
          type="button" 
          onClick={() => setShowPassword(!showPassword)}
          className="show-password"
        >
          비밀번호 표시
        </button>
      </div>
      <p className="help-text">
        비밀번호는 영문 + 숫자 + 특수문자, 3가지 조합으로 설정해주세요.<br />
        비밀번호는 8자리 이상, 24자리 이하 이어야함<br />
        사용 가능한 특수문자 : ! @ # $ ( ) * ? / ~ ^ {"{}"} 
      </p>
    </div>

        <div className="form-group">
          <label>비밀번호 확인</label>
          <div className="input-with-button">
            <input
              type={showPasswordConfirm ? "text" : "password"}
              name="passwordConfirm"
              value={formData.passwordConfirm}
              onChange={handleChange}
              required
            />
            <button 
              type="button" 
              onClick={() => setShowPasswordConfirm(!showPasswordConfirm)}
              className="show-password"
            >
              비밀번호 표시
            </button>
          </div>
        </div>

        <div className="form-group">
          <label>전화번호</label>
          <input
            type="tel"
            name="phone"
            value={formData.phone}
            onChange={handleChange}
            placeholder="전화번호 입력( - 제외)"
            required
          />
        </div>

        <div className="form-group">
          <label>주소</label>
          <div className="address-inputs">
            <input
              type="text"
              name="postcode"
              value={formData.postcode}
              placeholder="우편번호"
              readOnly
            />
            <button 
              type="button" 
              className="address-search" 
              onClick={() => setShowPostcode(true)}
            >
              주소찾기
            </button>

            {showPostcode && (
              <div className="postcode-modal">
                <DaumPostcode 
                  onComplete={handlePostcode}
                  autoClose
                />
              </div>
            )}
            <input
              type="text"
              name="address"
              value={formData.address}
              placeholder="도로명주소"
              readOnly
            />
            <input
              type="text"
              name="detailAddress"
              value={formData.detailAddress}
              onChange={handleChange}
              placeholder="상세주소"
            />
          </div>
        </div>

        <div className="button-group">
          <button type="button" onClick={() => navigate(-1)} className="cancel-button">
            취소
          </button>
          <button type="submit" className="submit-button">
            가입신청
          </button>
        </div>
      </form>

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

export default ResidentSignupPage;
