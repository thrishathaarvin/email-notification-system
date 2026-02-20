import { useEffect, useState } from "react";
import { Table, Button, Modal, Input, Form, message } from "antd";
import { getTemplates, deleteTemplate } from "../api/templateApi";
import { Link } from "react-router-dom";
import { sendEmail } from "../api/emailApi";

export default function Templates() {
  const [templates, setTemplates] = useState([]);
  const [loading, setLoading] = useState(false);
  const [selectedTemplate, setSelectedTemplate] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [sending, setSending] = useState(false);

  const [form] = Form.useForm();

  const fetchTemplates = async () => {
    setLoading(true);
    try {
      const data = await getTemplates();
      setTemplates(data);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTemplates();
  }, []);

  const handleDelete = async (id) => {
    await deleteTemplate(id);
    message.success("Template deleted successfully");
    fetchTemplates();
  };

  // Open modal and prefill subject/body from template
  const handleUse = (template) => {
    setSelectedTemplate(template);
    form.setFieldsValue({
      to: "",
      from: "",
      subject: template.subject,
      body: template.body,
    });
    setIsModalOpen(true);
  };

  const handleModalClose = () => {
    setIsModalOpen(false);
    setSelectedTemplate(null);
    form.resetFields();
  };

  // Normal send: closes modal
  const handleSend = async () => {
    const values = form.getFieldsValue();
    if (!values.to || !values.from) {
      message.error("Please enter both To and From fields");
      return;
    }

    try {
      setSending(true);
      await sendEmail({
        to: values.to,
        from: values.from,
        subject: values.subject,
        body: values.body,
      });
      message.success("Email sent successfully!");
      handleModalClose();
    } catch (err) {
      console.error(err);
      message.error("Failed to send email.");
    } finally {
      setSending(false);
    }
  };

  // Test send: sends to "from" email only, modal stays open
  const handleTest = async () => {
    const values = form.getFieldsValue();
    if (!values.from) {
      message.error("From email is required for test.");
      return;
    }

    try {
      setSending(true);
      await sendEmail({
        ...values,
        to: values.from, // override recipient
      });
      message.success("Test email sent successfully!");
      // ⚡ Do NOT close modal or reset form
    } catch (err) {
      console.error(err);
      message.error("Failed to send test email.");
    } finally {
      setSending(false);
    }
  };

  const columns = [
    { title: "Name", dataIndex: "name", key: "name" },
    { title: "Subject", dataIndex: "subject", key: "subject" },
    {
      title: "Actions",
      key: "actions",
      render: (_, record) => (
        <>
          <Button
            style={{ marginRight: 8 }}
            type="primary"
            onClick={() => handleUse(record)}
          >
            Use
          </Button>
          <Button danger onClick={() => handleDelete(record.id)}>
            Delete
          </Button>
        </>
      ),
    },
  ];

  return (
    <div>
      <h2>Templates</h2>
      <Button type="primary" style={{ marginBottom: 16 }}>
        <Link to="/newtemplate">New Template</Link>
      </Button>

      <Table
        columns={columns}
        dataSource={templates}
        rowKey="id"
        loading={loading}
        pagination={{ pageSize: 10 }}
      />

      <Modal
        title={`Compose Email - ${selectedTemplate?.name || ""}`}
        open={isModalOpen}
        onCancel={handleModalClose}
        footer={[
          <Button key="cancel" onClick={handleModalClose}>
            Cancel
          </Button>,
          <Button key="test" type="default" loading={sending} onClick={handleTest}>
            Test
          </Button>,
          <Button key="send" type="primary" loading={sending} onClick={handleSend}>
            Send
          </Button>,
        ]}
      >
        <Form form={form} layout="vertical">
          <Form.Item
            label="To"
            name="to"
            rules={[{ required: true, message: "Enter recipient email" }]}
          >
            <Input placeholder="Recipient email" />
          </Form.Item>
          <Form.Item
            label="From"
            name="from"
            rules={[{ required: true, message: "Enter your email" }]}
          >
            <Input placeholder="Your email" />
          </Form.Item>
          <Form.Item label="Subject" name="subject">
            <Input />
          </Form.Item>
          <Form.Item label="Body" name="body">
            <Input.TextArea rows={6} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
