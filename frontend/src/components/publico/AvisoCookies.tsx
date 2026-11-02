import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Button, Space, Typography } from 'antd';

export const CLAVE_ACEPTACION_COOKIES = 'minmo_aviso_cookies_visto';

export default function AvisoCookies() {
  const [visible, setVisible] = useState(false);

  useEffect(() => {
    setVisible(localStorage.getItem(CLAVE_ACEPTACION_COOKIES) !== '1');
  }, []);

  if (!visible) return null;

  const cerrar = () => {
    localStorage.setItem(CLAVE_ACEPTACION_COOKIES, '1');
    setVisible(false);
  };

  return (
    <div
      role="dialog"
      aria-label="Aviso de cookies"
      style={{
        position: 'fixed',
        bottom: 0,
        left: 0,
        right: 0,
        zIndex: 1000,
        background: '#0b2733',
        color: '#fff',
        padding: '14px 16px',
        boxShadow: '0 -2px 12px rgba(0,0,0,0.2)',
      }}
    >
      <div
        style={{
          maxWidth: 960,
          margin: '0 auto',
          display: 'flex',
          flexWrap: 'wrap',
          alignItems: 'center',
          gap: 16,
          justifyContent: 'space-between',
        }}
      >
        <Typography.Text style={{ color: 'rgba(255,255,255,0.9)', fontSize: 13, maxWidth: 640 }}>
          Este sitio no utiliza cookies de rastreo ni publicidad. Solo guardamos en tu navegador la preferencia de
          haber leído este aviso. Más información en nuestra{' '}
          <Link to="/cookies" style={{ color: '#ffb066' }}>
            política de cookies
          </Link>
          .
        </Typography.Text>
        <Space>
          <Button type="primary" onClick={cerrar}>
            Entendido
          </Button>
        </Space>
      </div>
    </div>
  );
}
