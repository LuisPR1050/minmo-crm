import { Typography } from 'antd';
import LegalLayout from './LegalLayout';
import { NEGOCIO } from '../../config/negocio';

const { Paragraph, Title } = Typography;

export default function PoliticaPrivacidadPage() {
  return (
    <LegalLayout titulo="Aviso de privacidad y tratamiento de datos personales">
      <Paragraph type="secondary">Última actualización: {new Date().toLocaleDateString('es-CO')}</Paragraph>

      <Title level={4}>1. Responsable del tratamiento</Title>
      <Paragraph>
        {NEGOCIO.nombreResponsable}, {NEGOCIO.calidad.toLowerCase()}, con domicilio en {NEGOCIO.pais}, es responsable
        del tratamiento de los datos personales que se recolecten a través de este sitio, conforme a la Ley 1581 de
        2012 y el Decreto 1377 de 2013 (régimen de protección de datos personales en Colombia).
      </Paragraph>

      <Title level={4}>2. Qué información recolectamos</Title>
      <Paragraph>
        Esta página muestra información pública de un inmueble en oferta. No contiene formularios ni solicita datos
        personales al visitante. Solo se procesan los datos técnicos mínimos e indispensables para servir el
        contenido (por ejemplo, la dirección IP y el tipo de navegador registrados de forma estándar por el servidor
        web por motivos de seguridad y disponibilidad del servicio).
      </Paragraph>
      <Paragraph>
        Si decides contactarnos por WhatsApp, correo electrónico o teléfono, los datos que compartas en esa
        conversación (nombre, número, mensaje) los recibimos directamente por ese canal y los usamos únicamente para
        responder tu consulta sobre el inmueble. Esas plataformas (WhatsApp, tu proveedor de correo) tienen sus
        propias políticas de privacidad, ajenas a este sitio.
      </Paragraph>

      <Title level={4}>3. Principio de minimización de datos</Title>
      <Paragraph>
        Solo solicitamos y usamos los datos estrictamente necesarios para atender tu consulta sobre un inmueble. No
        vendemos, alquilamos ni compartimos tus datos personales con terceros para fines comerciales o publicitarios.
      </Paragraph>

      <Title level={4}>4. Tus derechos (Habeas Data)</Title>
      <Paragraph>
        Como titular de datos personales tienes derecho a conocer, actualizar, rectificar y solicitar la supresión de
        tus datos, así como a revocar la autorización otorgada, cuando corresponda. Puedes ejercer estos derechos
        escribiendo a{' '}
        <a href={`mailto:${NEGOCIO.email}`}>{NEGOCIO.email}</a>.
      </Paragraph>

      <Title level={4}>5. Seguridad</Title>
      <Paragraph>
        Aplicamos medidas razonables para proteger la información que se transmite a través de este sitio (conexión
        cifrada HTTPS) y no almacenamos datos personales sensibles en esta página pública.
      </Paragraph>

      <Title level={4}>6. Cambios a este aviso</Title>
      <Paragraph>
        Este aviso puede actualizarse para reflejar cambios en el sitio o en la normativa aplicable. La fecha de
        última actualización aparece al inicio de esta página.
      </Paragraph>
    </LegalLayout>
  );
}
