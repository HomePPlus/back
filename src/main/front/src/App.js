// App.js

import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import SignupPage from './pages/SignupPage';
import SignupTypePage from './pages/SignupTypePage';
import ResidentSignupPage from './pages/ResidentSignupPage/ResidentSignupPage';
import InspectorSignupPage from './pages/InspectorSignupPage';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/signup" element={<SignupPage />} />
        <Route path="/signup/type" element={<SignupTypePage />} />
        <Route path="/signup/resident" element={<ResidentSignupPage />} />
        <Route path="/signup/inspector" element={<InspectorSignupPage />} />
      </Routes>
    </Router>
  );
}
export default App;
