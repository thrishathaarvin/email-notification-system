import { message } from "antd";

export function useNotification() {
  const notifySuccess = (text) => message.success(text);
  const notifyError = (text) => message.error(text);

  return { notifySuccess, notifyError };
}
