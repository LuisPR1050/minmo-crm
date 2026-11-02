import { useState } from 'react';
import { Avatar, Dropdown, Grid, Layout, Menu, Space, Tag, Typography, theme } from 'antd';
import {
  DashboardOutlined,
  HomeOutlined,
  TeamOutlined,
  FileTextOutlined,
  SolutionOutlined,
  NotificationOutlined,
  UserOutlined,
  LogoutOutlined,
  IdcardOutlined,
  KeyOutlined,
} from '@ant-design/icons';
import { Link, Outlet, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import CambiarPasswordModal from '../components/CambiarPasswordModal';
import NotificacionesBell from '../components/NotificacionesBell';

const { Header, Sider, Content, Footer } = Layout;
const { Text } = Typography;

export default function AppLayout() {
  const [collapsed, setCollapsed] = useState(false);
  const [modalPasswordAbierto, setModalPasswordAbierto] = useState(false);
  const location = useLocation();
  const navigate = useNavigate();
  const { usuario, cerrarSesion } = useAuth();
  const {
    token: { colorBgContainer },
  } = theme.useToken();
  const screens = Grid.useBreakpoint();
  const esMovil = !screens.sm;

  const menuItems = [
    { key: '/', icon: <DashboardOutlined />, label: <Link to="/">Inicio</Link> },
    {
      key: '/captacion/propietarios',
      icon: <TeamOutlined />,
      label: <Link to="/captacion/propietarios">Propietarios</Link>,
    },
    { key: '/captacion/inmuebles', icon: <HomeOutlined />, label: <Link to="/captacion/inmuebles">Inmuebles</Link> },
    { key: '/atencion-cliente', icon: <SolutionOutlined />, label: <Link to="/atencion-cliente">Atención al cliente</Link> },
    { key: '/negociacion', icon: <FileTextOutlined />, label: <Link to="/negociacion">Negociación</Link> },
    { key: '/documentacion', icon: <FileTextOutlined />, label: <Link to="/documentacion">Documentación</Link> },
    { key: '/marketing', icon: <NotificationOutlined />, label: <Link to="/marketing">Marketing</Link> },
    ...(usuario?.rol === 'ADMIN'
      ? [{ key: '/usuarios', icon: <IdcardOutlined />, label: <Link to="/usuarios">Usuarios</Link> }]
      : []),
  ];

  const handleLogout = () => {
    cerrarSesion();
    navigate('/login', { replace: true });
  };

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider
        collapsible
        collapsed={collapsed}
        onCollapse={setCollapsed}
        breakpoint="lg"
        collapsedWidth={esMovil ? 0 : 80}
        zeroWidthTriggerStyle={{ top: 12 }}
      >
        <div
          style={{
            height: 48,
            margin: 16,
            color: '#ff7a00',
            fontWeight: 600,
            fontSize: collapsed ? 16 : 18,
            textAlign: 'center',
            whiteSpace: 'nowrap',
            overflow: 'hidden',
          }}
        >
          MInmo
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[location.pathname]}
          items={menuItems}
        />
      </Sider>
      <Layout style={{ minWidth: 0 }}>
        <Header
          style={{
            padding: esMovil ? '0 12px' : '0 24px',
            background: colorBgContainer,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            gap: 8,
          }}
        >
          <h3
            style={{
              margin: 0,
              overflow: 'hidden',
              textOverflow: 'ellipsis',
              whiteSpace: 'nowrap',
              fontSize: esMovil ? 15 : undefined,
            }}
          >
            {esMovil ? 'MInmo' : 'MInmo — Panel de gestión inmobiliaria'}
          </h3>
          <Space size={esMovil ? 8 : 16}>
            <NotificacionesBell />
            <Dropdown
              menu={{
                items: [
                  {
                    key: 'cambiar-password',
                    icon: <KeyOutlined />,
                    label: 'Cambiar contraseña',
                    onClick: () => setModalPasswordAbierto(true),
                  },
                  { key: 'logout', icon: <LogoutOutlined />, label: 'Cerrar sesión', onClick: handleLogout },
                ],
              }}
            >
              <Space style={{ cursor: 'pointer' }}>
                <Avatar icon={<UserOutlined />} />
                {!esMovil && (
                  <Space orientation="vertical" size={0}>
                    <Text strong>{usuario?.nombre}</Text>
                    <Tag color={usuario?.rol === 'ADMIN' ? 'purple' : 'blue'} style={{ marginRight: 0 }}>
                      {usuario?.rol}
                    </Tag>
                  </Space>
                )}
              </Space>
            </Dropdown>
          </Space>
        </Header>
        <Content
          style={{
            margin: esMovil ? '12px 8px' : '24px 16px',
            padding: esMovil ? 12 : 24,
            background: colorBgContainer,
            minHeight: 280,
            minWidth: 0,
          }}
        >
          <Outlet />
        </Content>
        <Footer style={{ textAlign: 'center', padding: '12px 24px', fontSize: 12, color: 'rgba(0, 0, 0, 0.45)' }}>
          MInmo © {new Date().getFullYear()} — Felipe Parodi · Diseño y desarrollo: Luis Peña
        </Footer>
      </Layout>
      <CambiarPasswordModal open={modalPasswordAbierto} onClose={() => setModalPasswordAbierto(false)} />
    </Layout>
  );
}
