import { Navigate, Route, Routes } from 'react-router-dom';
import AppLayout from './layouts/AppLayout';
import RutaProtegida from './components/RutaProtegida';
import LoginPage from './pages/auth/LoginPage';
import DashboardPage from './pages/dashboard/DashboardPage';
import PipelineCaptacionPage from './pages/captacion/PipelineCaptacionPage';
import InmuebleDetallePage from './pages/captacion/InmuebleDetallePage';
import PropietariosPage from './pages/captacion/PropietariosPage';
import PropietarioDetallePage from './pages/captacion/PropietarioDetallePage';
import PipelineClientesPage from './pages/atencioncliente/PipelineClientesPage';
import ClienteDetallePage from './pages/atencioncliente/ClienteDetallePage';
import NegociacionesPage from './pages/negociacion/NegociacionesPage';
import NegociacionDetallePage from './pages/negociacion/NegociacionDetallePage';
import DocumentacionPage from './pages/documentacion/DocumentacionPage';
import MarketingPage from './pages/marketing/MarketingPage';
import UsuariosPage from './pages/usuarios/UsuariosPage';
import LandingInmueblePage from './pages/publico/LandingInmueblePage';
import PoliticaPrivacidadPage from './pages/legal/PoliticaPrivacidadPage';
import TerminosCondicionesPage from './pages/legal/TerminosCondicionesPage';
import PoliticaCookiesPage from './pages/legal/PoliticaCookiesPage';

function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/p/:id" element={<LandingInmueblePage />} />
      <Route path="/privacidad" element={<PoliticaPrivacidadPage />} />
      <Route path="/terminos" element={<TerminosCondicionesPage />} />
      <Route path="/cookies" element={<PoliticaCookiesPage />} />

      <Route element={<RutaProtegida />}>
        <Route element={<AppLayout />}>
          <Route path="/" element={<DashboardPage />} />
          <Route path="/captacion/inmuebles" element={<PipelineCaptacionPage />} />
          <Route path="/captacion/inmuebles/:id" element={<InmuebleDetallePage />} />
          <Route path="/captacion/propietarios" element={<PropietariosPage />} />
          <Route path="/captacion/propietarios/:id" element={<PropietarioDetallePage />} />
          <Route path="/atencion-cliente" element={<PipelineClientesPage />} />
          <Route path="/atencion-cliente/:id" element={<ClienteDetallePage />} />
          <Route path="/negociacion" element={<NegociacionesPage />} />
          <Route path="/negociacion/:id" element={<NegociacionDetallePage />} />
          <Route path="/documentacion" element={<DocumentacionPage />} />
          <Route path="/marketing" element={<MarketingPage />} />

          <Route element={<RutaProtegida soloAdmin />}>
            <Route path="/usuarios" element={<UsuariosPage />} />
          </Route>

          <Route path="*" element={<Navigate to="/captacion/inmuebles" replace />} />
        </Route>
      </Route>
    </Routes>
  );
}

export default App;
