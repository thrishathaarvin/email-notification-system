import { Modal as AntModal } from "antd";

export default function Modal({ children, ...props }) {
  return <AntModal {...props}>{children}</AntModal>;
}
