import { useEffect, useState, useContext } from "react";
import { Tag, Table, Button, Select } from "antd";
import { getEmails, deleteEmail } from "../api/emailApi";
import { AppContext } from "../context/AppContext";
import ComposeEmailModal from "../components/emails/ComposeEmailModal";
import { useNavigate } from "react-router-dom";
const { Option } = Select;

export default function Emails() {
  const { username } = useContext(AppContext);
  const [emails, setEmails] = useState([]);
  const [loading, setLoading] = useState(false);
  const [statusFilter, setStatusFilter] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);
  const navigate = useNavigate();

  const fetchEmails = async () => {
    setLoading(true);
    try {
      const data = await getEmails({ status: statusFilter });
      setEmails(data);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchEmails();
  }, [statusFilter]);

  const handleDelete = (id) => {
    Modal.confirm({
      title: "Delete Email?",
      content: "This will permanently delete the email record.",
      onOk: async () => {
        await deleteEmail(id);
        fetchEmails();
      },
    });
  };

  const columns = [
    { title: "Recipient", dataIndex: "recipientEmail", key: "recipient" },
    { title: "Subject", dataIndex: "subject", key: "subject" },
    {
    title: "Status",
    dataIndex: "deliveryStatus",
    key: "status",
    render: (status) => {
      let color = "gray";

      switch (status) {
        case "SENT":
          color = "green";
          break;
        case "FAILED":
          color = "red";
          break;
        case "OPENED":
          color = "blue";
          break;
        case "DELIVERED":
          color = "cyan";
          break;
        case "CREATED":
          color = "orange";
          break;
        default:
          color = "gray";
      }

      return <Tag color={color}>{status}</Tag>;
    },
  },
    { title: "Created At", dataIndex: "createdAt", key: "createdAt" },
    {
    title: "Actions",
    key: "actions",
    render: (_, record) => (
      <div style={{ display: "flex", gap: "8px" }}>
        <Button
          type="default"
          onClick={() => navigate(`/emails/${record.id}`)}
        >
          View
        </Button>
        <Button danger onClick={() => handleDelete(record.id)}>
          Delete
        </Button>
      </div>
    ),
  },
  ];

  return (
    <div>
      <h2>Emails</h2>

      <div style={{ marginBottom: 16, display: "flex", gap: 8 }}>
        <Button type="primary" onClick={() => setModalOpen(true)}>
          Compose Email
        </Button>

        <Select
          style={{ width: 200 }}
          placeholder="Filter by status"
          allowClear
          onChange={setStatusFilter}
        >
          <Option value="CREATED">Created</Option>
          <Option value="SENT">Sent</Option>
          <Option value="FAILED">Failed</Option>
          <Option value="OPENED">Opened</Option>
          <Option value="DELIVERED">Delivered</Option>
        </Select>
      </div>

      <Table
        columns={columns}
        dataSource={emails}
        rowKey="id"
        loading={loading}
        pagination={{ pageSize: 10 }}
      />

      <ComposeEmailModal
        visible={modalOpen}
        onClose={() => setModalOpen(false)}
        onSent={fetchEmails}
      />
    </div>
  );
}
