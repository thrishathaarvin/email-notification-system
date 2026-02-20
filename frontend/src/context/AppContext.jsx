import { createContext, useState } from "react";
import { message } from "antd";

export const AppContext = createContext();

export function AppProvider({ children }) {
  // Global state
  const [username, setUsername] = useState("Thrishatha");

  // Notification helper
  const notify = (type, content) => {
    switch (type) {
      case "success":
        message.success(content);
        break;
      case "error":
        message.error(content);
        break;
      case "info":
        message.info(content);
        break;
      case "warning":
        message.warning(content);
        break;
      default:
        message.info(content);
    }
  };

  return (
    <AppContext.Provider value={{ username, setUsername, notify }}>
      {children}
    </AppContext.Provider>
  );
}
