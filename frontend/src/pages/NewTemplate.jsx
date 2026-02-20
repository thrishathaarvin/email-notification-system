import { useState } from "react";
import { Form, Input, Button, message } from "antd";
import { createTemplate } from "../api/templateApi";
import { useNavigate } from "react-router-dom";

export default function NewTemplate() {
  const [loading, setLoading] = useState(false);
  const [form] = Form.useForm();
  const navigate = useNavigate();

  const handleCreate = async () => {
    try {
      const values = await form.validateFields();
      setLoading(true);
      await createTemplate(values);
      message.success("Template created successfully!");
      navigate("/templates");
    } catch (err) {
      console.error(err);
      message.error("Failed to create template.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h2>New Template</h2>
      <Form layout="vertical" form={form}>
        <Form.Item
          label="Name"
          name="name"
          rules={[{ required: true, message: "Template name required" }]}
        >
          <Input id="name"/>
        </Form.Item>

        <Form.Item
          label="Subject"
          name="subject"
          rules={[{ required: true, message: "Subject required" }]}
        >
          <Input id="sub"/>
        </Form.Item>

        <Form.Item
          label="Body"
          name="body"
          rules={[{ required: true, message: "Body required" }]}
        >
          <Input.TextArea rows={6} id="body"/>
        </Form.Item>

        <Button type="primary" onClick={handleCreate} loading={loading}>
          Create Template
        </Button>
      </Form>
    </div>
  );
}
