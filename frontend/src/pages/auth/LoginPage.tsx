import { useState } from 'react';
import { isAxiosError } from 'axios';
import { Button, Card, Form, Input, Typography, message } from 'antd';
import { LockOutlined, UserOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { authApi } from '../../api/auth';
import { useAuth } from '../../context/AuthContext';
import type { LoginRequest } from '../../types/auth';

const { Title, Text } = Typography;

export default function LoginPage() {
  const [enviando, setEnviando] = useState(false);
  const { iniciarSesion } = useAuth();
  const navigate = useNavigate();

  const handleLogin = async (values: LoginRequest) => {
    setEnviando(true);
    try {
      const respuesta = await authApi.login({
        email: values.email.trim(),
        password: values.password.trim(),
      });
      iniciarSesion(respuesta.token, {
        usuarioId: respuesta.usuarioId,
        nombre: respuesta.nombre,
        email: respuesta.email,
        rol: respuesta.rol,
      });
      navigate('/', { replace: true });
    } catch (error) {
      if (isAxiosError(error) && !error.response) {
        message.error('No se pudo conectar con el servidor. Verifica tu conexión a la red.');
      } else if (isAxiosError(error) && error.response?.status === 401) {
        message.error('Correo o contraseña incorrectos');
      } else if (isAxiosError(error)) {
        const detalle = (error.response?.data as { mensaje?: string; error?: string } | undefined);
        message.error(
          `Error ${error.response?.status ?? '?'}: ${detalle?.mensaje ?? detalle?.error ?? error.message}`,
          8,
        );
      } else {
        message.error(`Error inesperado: ${error instanceof Error ? error.message : String(error)}`, 8);
      }
    } finally {
      setEnviando(false);
    }
  };

  return (
    <div
      style={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background: '#f7f4ec',
        padding: 16,
      }}
    >
      <div style={{ width: '100%', maxWidth: 380, display: 'flex', flexDirection: 'column', gap: 16 }}>
        <Card style={{ width: '100%' }}>
          <div style={{ textAlign: 'center', marginBottom: 24 }}>
            <Title level={3} style={{ marginBottom: 4 }}>
              MInmo
            </Title>
            <Text type="secondary">Inicia sesión para continuar</Text>
          </div>
          <Form layout="vertical" onFinish={handleLogin}>
            <Form.Item name="email" label="Correo" rules={[{ required: true, message: 'El correo es obligatorio' }]}>
              <Input
                prefix={<UserOutlined />}
                placeholder="correo@ejemplo.com"
                autoFocus
                type="email"
                inputMode="email"
                autoCapitalize="none"
                autoCorrect="off"
                autoComplete="username"
              />
            </Form.Item>
            <Form.Item
              name="password"
              label="Contraseña"
              rules={[{ required: true, message: 'La contraseña es obligatoria' }]}
            >
              <Input.Password
                prefix={<LockOutlined />}
                placeholder="••••••••"
                autoCapitalize="none"
                autoCorrect="off"
                autoComplete="current-password"
              />
            </Form.Item>
            <Form.Item>
              <Button type="primary" htmlType="submit" loading={enviando} block>
                Iniciar sesión
              </Button>
            </Form.Item>
          </Form>
        </Card>
        <Text type="secondary" style={{ textAlign: 'center', fontSize: 12 }}>
          MInmo © {new Date().getFullYear()} — Felipe Parodi · Diseño y desarrollo: Luis Peña
        </Text>
      </div>
    </div>
  );
}
