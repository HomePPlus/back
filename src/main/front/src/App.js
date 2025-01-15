// App.js
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import SignupPage from './pages/SignupPage';

function App() {
  return (
    <Router>
        <Routes>
          <Route path="/" element={<SignupPage />} />
          <Route path="/signup" element={<SignupPage />} />
        {/* 다른 라우트들... */}
      </Routes>
    </Router>
  );
}

export default App;
