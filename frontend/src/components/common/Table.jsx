import { Table as AntTable } from "antd";

export default function Table({ columns, data, ...props }) {
  return <AntTable columns={columns} dataSource={data} {...props} />;
}
