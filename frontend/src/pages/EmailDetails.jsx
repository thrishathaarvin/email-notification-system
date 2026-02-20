// src/pages/EmailDetails.jsx
import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Card, Button, Spin, Tag } from "antd";
import { getEmailById } from "../api/emailApi";

export default function EmailDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [email, setEmail] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchEmail = async () => {
      try {
        const data = await getEmailById(id);
        setEmail(data);
      } catch (err) {
        console.error("Error fetching email:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchEmail();
  }, [id]);

  if (loading) return <Spin description="Loading..." style={{ marginTop: 50 }} />;


  if (!email)
    return (
      <div style={{ marginTop: 50 }}>
        <p>Email not found.</p>
        <Button onClick={() => navigate("/emails")}>Back</Button>
      </div>
    );

  const { recipientEmail, fromEmail, subject, body, deliveryStatus, createdAt } = email;

  const getStatusColor = (status) => {
    switch (status) {
      case "SENT":
        return "green";
      case "FAILED":
        return "red";
      case "OPENED":
        return "blue";
      case "DELIVERED":
        return "cyan";
      case "CREATED":
        return "orange";
      default:
        return "gray";
    }
  };

  return (
    <div style={{ maxWidth: 700, margin: "50px auto" }}>
      <Button style={{ marginBottom: 16 }} onClick={() => navigate("/emails")}>
        Back
      </Button>

      <Card title={`Email Details - ${subject || "No Subject"}`}>
        <p>
          <strong>From:</strong> {fromEmail}
        </p>
        <p>
          <strong>To:</strong> {recipientEmail}
        </p>
        <p>
          <strong>Subject:</strong> {subject}
        </p>
        <p>
          <strong>Status:</strong>{" "}
          <Tag color={getStatusColor(deliveryStatus)}>{deliveryStatus}</Tag>
        </p>
        <p>
          <strong>Created At:</strong> {new Date(createdAt).toLocaleString()}
        </p>
        <p>
          <strong>Body:</strong>
        </p>
        <div
          style={{
            padding: 16,
            background: "#f5f5f5",
            borderRadius: 6,
            whiteSpace: "pre-wrap",
          }}
        >
          {body}
        </div>
      </Card>
    </div>
  );
}
