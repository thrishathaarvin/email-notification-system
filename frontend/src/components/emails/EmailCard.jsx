import { Card, Tag } from "antd";

export default function EmailCard({ email }) {
  return (
    <Card style={{ marginBottom: 12 }}>
      <div style={{ display: "flex", justifyContent: "space-between" }}>
        <div>
          <strong>To:</strong> {email.recipientEmail} <br />
          <strong>Subject:</strong> {email.subject}
        </div>
        <Tag color={email.deliveryStatus === "SENT" ? "green" : "red"}>
          {email.deliveryStatus}
        </Tag>
      </div>
    </Card>
  );
}
