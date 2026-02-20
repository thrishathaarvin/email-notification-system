import { Button as AntButton } from "antd";

export default function Button({ children, ...props }) {
  return <AntButton {...props}>{children}</AntButton>;
}
