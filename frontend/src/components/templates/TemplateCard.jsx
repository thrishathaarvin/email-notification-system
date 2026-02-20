import { Card } from "antd";

export default function TemplateCard({ template }) {
  return (
    <Card title={template.templateName} style={{ marginBottom: 12 }}>
      <p><strong>Subject:</strong> {template.subjectLine}</p>
      <p>{template.contentBody}</p>
    </Card>
  );
}
