import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import { ConfigProvider } from 'antd'
import esES from 'antd/locale/es_ES'
import './index.css'
import App from './App.tsx'
import { AuthProvider } from './context/AuthContext.tsx'
import ErrorBoundary from './components/ErrorBoundary.tsx'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ConfigProvider
      locale={esES}
      theme={{
        token: {
          colorPrimary: '#0b4f6c',
          colorLink: '#0b4f6c',
          colorTextSecondary: '#595959',
          colorBorderSecondary: '#e8e2d6',
          colorBgLayout: '#f7f4ec',
          borderRadius: 8,
          boxShadowTertiary: '0 1px 4px rgba(0, 0, 0, 0.06)',
          fontFamily:
            "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif",
        },
        components: {
          Layout: {
            siderBg: '#083950',
            triggerBg: '#062a3a',
            bodyBg: '#f7f4ec',
          },
          Menu: {
            darkItemBg: '#083950',
            darkItemSelectedBg: '#ff7a00',
            darkItemColor: 'rgba(255, 255, 255, 0.85)',
            darkItemSelectedColor: '#ffffff',
            darkItemHoverBg: 'rgba(255, 122, 0, 0.15)',
          },
          Table: {
            headerBg: '#faf8f2',
            headerColor: '#595959',
            borderColor: '#e8e2d6',
          },
          Card: {
            headerFontSize: 15,
          },
        },
      }}
    >
      <ErrorBoundary>
        <BrowserRouter>
          <AuthProvider>
            <App />
          </AuthProvider>
        </BrowserRouter>
      </ErrorBoundary>
    </ConfigProvider>
  </StrictMode>,
)
