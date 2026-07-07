import { BrowserRouter, Routes, Route } from "react-router-dom";
import Login from "./features/auth/login";
import Register from "./features/auth/registration";
import Dashboard from "./pages/Dashboard";
import { PaymentSuccessPage, PaymentCancelPage } from "./features/payments";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/payment/success" element={<PaymentSuccessPage />} />
        <Route path="/payment/cancel" element={<PaymentCancelPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
