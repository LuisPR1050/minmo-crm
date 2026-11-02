import { Typography } from 'antd';
import LegalLayout from './LegalLayout';
import { NEGOCIO } from '../../config/negocio';

const { Paragraph, Title } = Typography;

export default function TerminosCondicionesPage() {
  return (
    <LegalLayout titulo="Términos y condiciones de uso">
      <Paragraph type="secondary">Última actualización: {new Date().toLocaleDateString('es-CO')}</Paragraph>

      <Title level={4}>1. Naturaleza del sitio</Title>
      <Paragraph>
        Este sitio es operado por {NEGOCIO.nombreResponsable} ({NEGOCIO.calidad.toLowerCase()}) y tiene como único
        propósito mostrar información pública de inmuebles en oferta. No es una plataforma de comercio electrónico:
        no procesa pagos, reservas ni transacciones en línea. Cualquier acuerdo, visita, reserva o negociación sobre
        un inmueble se realiza directamente entre las partes, por fuera de esta página (WhatsApp, correo, teléfono o
        en persona).
      </Paragraph>

      <Title level={4}>2. Información referencial, no una oferta vinculante</Title>
      <Paragraph>
        Los datos publicados sobre cada inmueble (precio, área, número de habitaciones, ubicación, disponibilidad,
        descripción y demás características) son suministrados por el propietario o el asesor a cargo y tienen
        carácter meramente informativo y referencial. Pueden contener imprecisiones involuntarias o cambiar sin
        previo aviso (por ejemplo, si el inmueble ya fue vendido o arrendado). Te recomendamos verificar siempre la
        información directamente con el asesor antes de tomar cualquier decisión.
      </Paragraph>
      <Paragraph>
        Ninguna descripción, fotografía o video publicado constituye una garantía sobre el estado, condiciones
        legales o físicas del inmueble. Dichas condiciones deben verificarse de forma independiente antes de
        cualquier negociación.
      </Paragraph>

      <Title level={4}>3. Propiedad de fotografías y videos</Title>
      <Paragraph>
        Las fotografías y videos publicados pertenecen a {NEGOCIO.nombreResponsable} o se publican con autorización
        del propietario del inmueble. Está prohibida su reproducción, distribución, descarga masiva o uso comercial
        sin autorización previa y expresa.
      </Paragraph>

      <Title level={4}>4. Sobre reembolsos y anticipos</Title>
      <Paragraph>
        Esta página no cobra ni recibe pagos, señas o anticipos de ningún tipo. En consecuencia, no existe una
        política de reembolsos aplicable a este sitio. Cualquier pago, arras o anticipo relacionado con un inmueble
        se acuerda directamente entre las partes por fuera de esta plataforma y se rige por lo que ambas acuerden por
        escrito en ese momento.
      </Paragraph>

      <Title level={4}>5. Uso aceptable</Title>
      <Paragraph>
        Te comprometes a usar este sitio de forma lícita y a no intentar extraer, reproducir o distribuir su
        contenido de forma masiva o automatizada sin autorización.
      </Paragraph>

      <Title level={4}>6. Legislación aplicable</Title>
      <Paragraph>
        Estos términos se rigen por las leyes de la República de Colombia, incluyendo la Ley 1581 de 2012 en materia
        de protección de datos personales.
      </Paragraph>

      <Title level={4}>7. Contacto</Title>
      <Paragraph>
        Para dudas sobre estos términos, escríbenos a <a href={`mailto:${NEGOCIO.email}`}>{NEGOCIO.email}</a>.
      </Paragraph>
    </LegalLayout>
  );
}
