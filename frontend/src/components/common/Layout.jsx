import { Layout as AntLayout, Menu, Avatar, Dropdown } from "antd";
import { Link, useLocation } from "react-router-dom";
import { useContext } from "react";
import { AppContext } from "../../context/AppContext";

const { Header, Content, Footer } = AntLayout;

export default function Layout({ children }) {
  const { username } = useContext(AppContext);
  const location = useLocation();

  const menuItems = [
    { key: "/dashboard", label: <Link to="/dashboard">Dashboard</Link> },
    { key: "/emails", label: <Link to="/emails">Emails</Link> },
    { key: "/templates", label: <Link to="/templates">Templates</Link> },
    { key: "/newtemplate", label: <Link to="/newtemplate">New Template</Link> },
  ];

  const userMenuItems = [{ key: "logout", label: "Logout" }];

  return (
    <AntLayout style={{ minHeight: "100vh", display: "flex", flexDirection: "column" }}>
      <Header style={{ display: "flex", alignItems: "center", justifyContent: "space-between" }}>
        <div style={{ color: "#fff", fontWeight: "bold", fontSize: 18 }}>
          Email Notification System
        </div>

        <Menu
          theme="dark"
          mode="horizontal"
          selectedKeys={[location.pathname]}
          items={menuItems}
          style={{ flex: 1, marginLeft: 20 }}
        />

        <Dropdown menu={{ items: userMenuItems }}>
          <div style={{ color: "#fff", cursor: "pointer", display: "flex", alignItems: "center" }}>
            <Avatar style={{ marginRight: 8 }}>{username?.charAt(0)}</Avatar>
            {username}
          </div>
        </Dropdown>
      </Header>

      <Content
        style={{
          padding: "20px 50px",
          flex: 1,
          minHeight: "calc(100vh - 64px - 70px)",
          backgroundColor: "#f0f2f5",
        }}
      >
        {children}
      </Content>

      <Footer style={{ textAlign: "center" }}>
        Email Notification System ©2026
      </Footer>
    </AntLayout>
  );
}
