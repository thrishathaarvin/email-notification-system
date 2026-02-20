import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import Layout from "../components/common/Layout";
import Dashboard from "../pages/Dashboard";
import Emails from "../pages/Emails";
import Templates from "../pages/Templates";
import NewTemplate from "../pages/NewTemplate";
import EmailDetails from "../pages/EmailDetails";

export default function AppRouter() {
  return (
    <Router>
      <Layout>
        <Routes>
          <Route path="/" element={<Navigate to="/dashboard" />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/emails" element={<Emails />} />
          <Route path="/templates" element={<Templates />} />
          <Route path="newtemplate" element={<NewTemplate />} />
          <Route path="/emails/:id" element={<EmailDetails />} />

        </Routes>
      </Layout>
    </Router>
  );
}
