import { useEffect, useState } from "react";
import { Card, Row, Col } from "antd";
import { getDashboardData } from "../api/reportApi";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
  LineChart,
  Line,
  Legend,
  ResponsiveContainer,
} from "recharts";

export default function Dashboard() {
  const [dailyCounts, setDailyCounts] = useState([]);
  const [statusCounts, setStatusCounts] = useState([]);
  const [summary, setSummary] = useState({ sent: 0, failed: 0 });

  const fetchData = async () => {
    try {
      const data = await getDashboardData();

      setDailyCounts(
        Object.entries(data.dailyCounts || {}).map(([date, count]) => ({
          date,
          count,
        }))
      );

      setStatusCounts(
        Object.entries(data.statusCounts || {}).map(([status, count]) => ({
          status,
          count,
        }))
      );

      setSummary({ sent: data.sent || 0, failed: data.failed || 0 });
    } catch (err) {
      console.error("Failed to fetch dashboard data:", err);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  return (
    <div style={{ padding: 24 }}>
      <h2 style={{ marginBottom: 24 }}>Dashboard</h2>

      {/* Summary Cards */}
      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col xs={24} sm={12}>
          <Card
            title="Sent Emails"
            bordered={false}
            style={{
              textAlign: "center",
              backgroundColor: "#f6ffed",
              fontWeight: 600,
              fontSize: 24,
            }}
          >
            {summary.sent}
          </Card>
        </Col>
        <Col xs={24} sm={12}>
          <Card
            title="Failed Emails"
            bordered={false}
            style={{
              textAlign: "center",
              backgroundColor: "#fff1f0",
              fontWeight: 600,
              fontSize: 24,
            }}
          >
            {summary.failed}
          </Card>
        </Col>
      </Row>

      {/* Charts */}
      <Row gutter={16}>
        <Col xs={24} lg={12}>
  <Card title="Daily Email Counts (Last 7 Days)" bordered={false} style={{ height: "100%" }}>
    <ResponsiveContainer width="100%" height={300}>
      <LineChart data={dailyCounts} margin={{ top: 20, right: 20, left: -10, bottom: 20 }}>
        <CartesianGrid stroke="#eee" strokeDasharray="5 5" />
        <XAxis 
          dataKey="date" 
          tick={{ fontSize: 12 }} 
          angle={-45} 
          textAnchor="end" 
        />
        <YAxis allowDecimals={false} />
        <Tooltip />
        <Legend />
        <Line type="monotone" dataKey="count" stroke="#8884d8" strokeWidth={2} />
      </LineChart>
    </ResponsiveContainer>
  </Card>
</Col>

<Col xs={24} lg={12}>
  <Card title="Email Status Counts" bordered={false} style={{ height: "100%" }}>
    <ResponsiveContainer width="100%" height={300}>
      <BarChart data={statusCounts} margin={{ top: 20, right: 20, left: -10, bottom: 20 }}>
        <CartesianGrid stroke="#ccc" />
        <XAxis dataKey="status" tick={{ fontSize: 12 }} />
        <YAxis allowDecimals={false} />
        <Tooltip />
        <Legend />
        <Bar dataKey="count" fill="#82ca9d" barSize={40} radius={[6, 6, 0, 0]} />
      </BarChart>
    </ResponsiveContainer>
  </Card>
</Col>

      </Row>
    </div>
  );
}
