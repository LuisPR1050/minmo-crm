import { Typography } from 'antd';
import LegalLayout from './LegalLayout';
import { NEGOCIO } from '../../config/negocio';

const { Paragraph, Title } = Typography;

export default function PoliticaCookiesPage() {
  return (
    <LegalLayout titulo="Política de cookies">
      <Paragraph type="secondary">Última actualización: {new Date().toLocaleDateString('es-CO')}</Paragraph>

      <Title level={4}>1. Qué usamos</Title>
      <Paragraph>
        Este sitio no utiliza cookies de rastreo, publicidad ni analítica de terceros (por ejemplo, no usamos Google
        Analytics ni píxeles de redes sociales en esta página).
      </Paragraph>

      <Title level={4}>2. Almacenamiento local estrictamente necesario</Title>
      <Paragraph>
        Guardamos únicamente una preferencia técnica en el almacenamiento local de tu navegador (no una cookie de
        terceros) para recordar que ya viste el aviso de cookies y no mostrártelo de nuevo en cada visita. Esta
        información no sale de tu dispositivo, no se envía a nuestros servidores y no se usa para identificarte ni
        para seguimiento publicitario.
      </Paragraph>

      <Title level={4}>3. Contenido embebido de terceros</Title>
      <Paragraph>
        Cuando un inmueble tiene video, este puede reproducirse a través de YouTube o Vimeo embebidos directamente en
        la página. Al reproducir ese video, YouTube o Vimeo pueden aplicar sus propias cookies conforme a sus propias
        políticas de privacidad, ajenas a este sitio:
      </Paragraph>
      <ul>
        <li>
          <a href="https://policies.google.com/privacy" target="_blank" rel="noreferrer">
            Política de privacidad de Google/YouTube
          </a>
        </li>
        <li>
          <a href="https://vimeo.com/privacy" target="_blank" rel="noreferrer">
            Política de privacidad de Vimeo
          </a>
        </li>
      </ul>

      <Title level={4}>4. Cómo eliminar esta preferencia</Title>
      <Paragraph>
        Puedes borrar la preferencia guardada en cualquier momento eliminando los datos de sitio almacenados por tu
        navegador para este dominio, desde la configuración de tu navegador.
      </Paragraph>

      <Title level={4}>5. Contacto</Title>
      <Paragraph>
        Si tienes dudas sobre esta política, escríbenos a <a href={`mailto:${NEGOCIO.email}`}>{NEGOCIO.email}</a>.
      </Paragraph>
    </LegalLayout>
  );
}
