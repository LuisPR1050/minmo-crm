import { Link } from 'react-router-dom';
import { Space, Typography } from 'antd';
import { MailOutlined, PhoneOutlined } from '@ant-design/icons';
import { NEGOCIO } from '../../config/negocio';

const { Text } = Typography;

export default function PublicFooter() {
  const año = new Date().getFullYear();

  return (
    <footer
      style={{
        borderTop: '1px solid #e8e2d6',
        marginTop: 48,
        padding: '24px 16px',
        background: '#fff',
      }}
    >
      <div style={{ maxWidth: 960, margin: '0 auto' }}>
        <Space orientation="vertical" size={8} style={{ width: '100%' }}>
          <Text strong>{NEGOCIO.nombreResponsable}</Text>
          <Text type="secondary" style={{ fontSize: 13 }}>
            {NEGOCIO.calidad} · {NEGOCIO.pais}
          </Text>

          <Space size={16} wrap style={{ marginTop: 4 }}>
            <a href={`mailto:${NEGOCIO.email}`} style={{ fontSize: 13 }}>
              <MailOutlined /> {NEGOCIO.email}
            </a>
            <a href={`tel:+${NEGOCIO.whatsapp}`} style={{ fontSize: 13 }}>
              <PhoneOutlined /> {NEGOCIO.telefonoVisible}
            </a>
          </Space>

          <Space size={16} wrap style={{ marginTop: 12, fontSize: 13 }}>
            <Link to="/privacidad">Aviso de privacidad</Link>
            <Link to="/terminos">Términos y condiciones</Link>
            <Link to="/cookies">Política de cookies</Link>
          </Space>

          <Text type="secondary" style={{ fontSize: 12, marginTop: 8 }}>
            © {año} {NEGOCIO.nombreResponsable}. Todos los derechos reservados sobre el contenido, fotografías y
            videos publicados.
          </Text>
        </Space>
      </div>
    </footer>
  );
}
