import type { ReactNode } from 'react';
import { useNavigate } from 'react-router-dom';
import { Typography } from 'antd';
import { ArrowLeftOutlined, HomeOutlined } from '@ant-design/icons';
import PublicFooter from '../../components/publico/PublicFooter';

const { Title } = Typography;

export default function LegalLayout({ titulo, children }: { titulo: string; children: ReactNode }) {
  const navigate = useNavigate();

  return (
    <div style={{ minHeight: '100vh', background: '#f7f4ec', display: 'flex', flexDirection: 'column' }}>
      <header style={{ background: '#fff', borderBottom: '1px solid #e8e2d6', padding: '14px 16px' }}>
        <div style={{ maxWidth: 720, margin: '0 auto', display: 'flex', alignItems: 'center', gap: 12 }}>
          <HomeOutlined style={{ fontSize: 18, color: '#0b4f6c' }} aria-hidden />
          <span style={{ fontWeight: 700, color: '#0b4f6c' }}>MInmo</span>
        </div>
      </header>

      <main style={{ flex: 1, maxWidth: 720, margin: '0 auto', padding: '32px 16px 48px', width: '100%' }}>
        <button
          type="button"
          onClick={() => navigate(-1)}
          style={{
            display: 'inline-flex',
            alignItems: 'center',
            gap: 6,
            marginBottom: 16,
            border: 'none',
            background: 'none',
            color: '#0b4f6c',
            cursor: 'pointer',
            padding: 0,
            font: 'inherit',
          }}
        >
          <ArrowLeftOutlined /> Volver
        </button>
        <Title level={2}>{titulo}</Title>
        <Typography>{children}</Typography>
      </main>

      <PublicFooter />
    </div>
  );
}
