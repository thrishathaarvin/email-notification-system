import { useState, useContext } from "react";
import { Modal, Form, Input, Button } from "antd";
import { sendEmail } from "../../api/emailApi";
import { AppContext } from "../../context/AppContext";

export default function ComposeEmailModal({ visible, onClose, onSent }) {
  const { notify } = useContext(AppContext);
  const [loading, setLoading] = useState(false);
  const [form] = Form.useForm();

  // Normal send: closes modal, resets form
  const handleSend = async () => {
    try {
      const values = await form.validateFields();
      setLoading(true);

      await sendEmail(values);
      notify("success", "Email sent successfully!");
      form.resetFields();
      onSent(); // refresh table
      onClose();
    } catch (err) {
      console.error(err);
      notify("error", "Failed to send email.");
    } finally {
      setLoading(false);
    }
  };

  // Test send: sends to "from" email only, modal stays open
  const handleTest = async () => {
    try {
      const values = await form.validateFields();
      if (!values.from) {
        notify("error", "From email is required for test.");
        return;
      }
      setLoading(true);

      await sendEmail({ ...values, to: values.from }); // override 'to' with 'from'
      notify("success", "Test email sent successfully!");
      // ⚡ Do NOT reset form or close modal
    } catch (err) {
      console.error(err);
      notify("error", "Test email failed.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      title="Compose Email"
      open={visible}
      onCancel={onClose}
      footer={[
        <Button key="cancel" onClick={onClose}>
          Cancel
        </Button>,
        <Button key="test" type="default" loading={loading} onClick={handleTest}>
          Test
        </Button>,
        <Button key="send" type="primary" loading={loading} onClick={handleSend}>
          Send
        </Button>,
      ]}
    >
      <Form layout="vertical" form={form}>
        <Form.Item
          label="From"
          name="from"
          rules={[{ required: true, message: "Sender email is required" }]}
        >
          <Input placeholder="noreply@yourapp.com" />
        </Form.Item>

        <Form.Item
          label="To"
          name="to"
          rules={[{ required: true, message: "Recipient email is required" }]}
        >
          <Input placeholder="recipient@example.com" />
        </Form.Item>

        <Form.Item
          label="Subject"
          name="subject"
          rules={[{ required: true, message: "Subject is required" }]}
        >
          <Input />
        </Form.Item>

        <Form.Item
          label="Body"
          name="body"
          rules={[{ required: true, message: "Email body is required" }]}
        >
          <Input.TextArea rows={5} />
        </Form.Item>
      </Form>
    </Modal>
  );
}
