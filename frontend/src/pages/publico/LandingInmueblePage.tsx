import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { Button, Carousel, Col, Empty, Form, Image, Input, Result, Row, Skeleton, Space, Tag, Typography, message } from 'antd';
import {
  BankOutlined,
  CompassOutlined,
  EnvironmentOutlined,
  ExpandOutlined,
  HomeOutlined,
  MailOutlined,
  SafetyCertificateOutlined,
  SmileOutlined,
  TeamOutlined,
  WhatsAppOutlined,
} from '@ant-design/icons';
import { landingInmuebleApi } from '../../api/publico';
import type { LandingInmueble, LandingLeadRequest } from '../../types/publico';
import { ETIQUETA_TIPO_PROPIEDAD } from '../../types/captacion';
import { NEGOCIO, construirEnlaceWhatsappNegocio } from '../../config/negocio';
import { construirEnlaceCorreo } from '../../utils/compartir';
import PublicFooter from '../../components/publico/PublicFooter';
import AvisoCookies, { CLAVE_ACEPTACION_COOKIES } from '../../components/publico/AvisoCookies';

const { Title, Paragraph, Text } = Typography;

const AZUL = '#0EA5E9';
const AZUL_OSCURO = '#0284C7';
const AZUL_SUAVE = '#E0F2FE';
const FONDO_CLARO = '#F8FAFC';

const formatearMoneda = (valor?: number) =>
  valor != null ? valor.toLocaleString('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }) : undefined;

function idDeYoutube(url: string): string | null {
  const match = url.match(/(?:youtube\.com\/watch\?v=|youtu\.be\/|youtube\.com\/embed\/|youtube\.com\/shorts\/)([\w-]{11})/);
  return match ? match[1] : null;
}

function idDeVimeo(url: string): string | null {
  const match = url.match(/vimeo\.com\/(?:video\/)?(\d+)/);
  return match ? match[1] : null;
}

function VideoEmbebido({ url, origen }: { url: string; origen: 'LINK_EXTERNO' | 'ARCHIVO_SUBIDO' }) {
  if (origen === 'ARCHIVO_SUBIDO') {
    return (
      // eslint-disable-next-line jsx-a11y/media-has-caption
      <video controls style={{ width: '100%', borderRadius: 12, background: '#000' }}>
        <source src={url} />
      </video>
    );
  }

  const youtubeId = idDeYoutube(url);
  if (youtubeId) {
    return (
      <div style={{ position: 'relative', paddingTop: '56.25%', borderRadius: 12, overflow: 'hidden' }}>
        <iframe
          src={`https://www.youtube.com/embed/${youtubeId}`}
          title="Video del inmueble"
          allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
          allowFullScreen
          style={{ position: 'absolute', inset: 0, width: '100%', height: '100%', border: 0 }}
        />
      </div>
    );
  }

  const vimeoId = idDeVimeo(url);
  if (vimeoId) {
    return (
      <div style={{ position: 'relative', paddingTop: '56.25%', borderRadius: 12, overflow: 'hidden' }}>
        <iframe
          src={`https://player.vimeo.com/video/${vimeoId}`}
          title="Video del inmueble"
          allow="autoplay; fullscreen; picture-in-picture"
          allowFullScreen
          style={{ position: 'absolute', inset: 0, width: '100%', height: '100%', border: 0 }}
        />
      </div>
    );
  }

  if (/\.(mp4|webm|ogg)(\?.*)?$/i.test(url)) {
    return (
      // eslint-disable-next-line jsx-a11y/media-has-caption
      <video controls style={{ width: '100%', borderRadius: 12, background: '#000' }}>
        <source src={url} />
      </video>
    );
  }

  return (
    <a href={url} target="_blank" rel="noreferrer">
      Ver video
    </a>
  );
}

const BENEFICIOS = [
  {
    icono: <SmileOutlined />,
    titulo: 'Un lugar para tu historia',
    texto: 'Imagina las mañanas, las reuniones familiares y los momentos que vas a construir aquí.',
  },
  {
    icono: <TeamOutlined />,
    titulo: 'Acompañamiento en cada paso',
    texto: 'Un asesor te guía desde la primera visita hasta la firma, sin presión ni letra pequeña.',
  },
  {
    icono: <SafetyCertificateOutlined />,
    titulo: 'Decisión informada',
    texto: 'Fotos reales, información clara y respuestas directas para que decidas con confianza.',
  },
];

function tituloInmueble(inmueble: LandingInmueble) {
  const ubicacion = inmueble.zona ? `en ${inmueble.zona}` : inmueble.ciudad ? `en ${inmueble.ciudad}` : '';
  const tipo =
    ETIQUETA_TIPO_PROPIEDAD[inmueble.tipoPropiedad as keyof typeof ETIQUETA_TIPO_PROPIEDAD] ?? inmueble.tipoPropiedad;
  return `${tipo} ${ubicacion}`.trim();
}

export default function LandingInmueblePage() {
  const { id } = useParams<{ id: string }>();
  const [inmueble, setInmueble] = useState<LandingInmueble | null>(null);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState(false);
  const [enviandoLead, setEnviandoLead] = useState(false);
  const [leadEnviado, setLeadEnviado] = useState(false);
  const [avisoCookiesVisible, setAvisoCookiesVisible] = useState(false);
  const [formLead] = Form.useForm<LandingLeadRequest>();

  useEffect(() => {
    setAvisoCookiesVisible(localStorage.getItem(CLAVE_ACEPTACION_COOKIES) !== '1');
  }, []);

  useEffect(() => {
    if (!id) return;
    setCargando(true);
    landingInmuebleApi
      .obtener(id)
      .then(setInmueble)
      .catch(() => setError(true))
      .finally(() => setCargando(false));
  }, [id]);

  useEffect(() => {
    if (!inmueble) return;
    const titulo = `${tituloInmueble(inmueble)} — MInmo`;
    document.title = titulo;

    const descripcion = inmueble.ganchoEmocional || inmueble.descripcionPublica || 'Conoce esta propiedad y agenda tu visita.';
    let metaDescripcion = document.querySelector('meta[name="description"]');
    if (!metaDescripcion) {
      metaDescripcion = document.createElement('meta');
      metaDescripcion.setAttribute('name', 'description');
      document.head.appendChild(metaDescripcion);
    }
    metaDescripcion.setAttribute('content', descripcion.slice(0, 200));

    return () => {
      document.title = 'MInmo';
    };
  }, [inmueble]);

  const mensajeWhatsapp = inmueble
    ? `Hola, vi la publicación del ${tituloInmueble(inmueble)} (código ${inmueble.codigo}) y me interesa saber más.`
    : '';

  const fotoPrincipal = inmueble?.fotos.find((f) => f.esPrincipal) ?? inmueble?.fotos[0];
  const fotosSecundarias = inmueble?.fotos.filter((f) => f.id !== fotoPrincipal?.id) ?? [];

  const handleEnviarLead = async (values: LandingLeadRequest) => {
    if (!id) return;
    setEnviandoLead(true);
    try {
      await landingInmuebleApi.enviarLead(id, values);
      setLeadEnviado(true);
    } catch {
      message.error('No se pudo enviar tu solicitud. Intenta de nuevo o contáctanos por WhatsApp.');
    } finally {
      setEnviandoLead(false);
    }
  };

  return (
    <div style={{ minHeight: '100vh', background: FONDO_CLARO, display: 'flex', flexDirection: 'column' }}>
      <style>{`
        .landing-carousel-dots li button { background: rgba(255,255,255,0.5) !important; }
        .landing-carousel-dots li.slick-active button { background: #fff !important; }
        .ant-carousel .slick-prev, .ant-carousel .slick-next { color: #fff; z-index: 5; }
        .ant-carousel .slick-prev::before, .ant-carousel .slick-next::before { opacity: 0.85; }
        .landing-galeria-carrusel::-webkit-scrollbar { height: 6px; }
        .landing-galeria-carrusel::-webkit-scrollbar-thumb { background: ${AZUL_SUAVE}; border-radius: 999px; }
      `}</style>
      <header style={{ background: AZUL, padding: '14px 16px' }}>
        <div style={{ maxWidth: 960, margin: '0 auto' }}>
          <span style={{ fontWeight: 700, fontSize: 18, color: '#fff', letterSpacing: 0.3 }}>MInmo</span>
        </div>
      </header>

      {!cargando && inmueble && fotoPrincipal && (
        <div
          style={{
            position: 'relative',
            width: '100%',
            height: 'min(70vh, 560px)',
            minHeight: 320,
            overflow: 'hidden',
          }}
        >
          <Carousel
            autoplay
            autoplaySpeed={5500}
            arrows
            dots={{ className: 'landing-carousel-dots' }}
            style={{ position: 'absolute', inset: 0 }}
          >
            {inmueble.fotos.map((foto, index) => (
              <div key={foto.id}>
                <img
                  src={foto.url}
                  alt={`${tituloInmueble(inmueble)}, código ${inmueble.codigo}`}
                  style={{ width: '100%', height: 'min(70vh, 560px)', objectFit: 'cover', display: 'block' }}
                  loading={index === 0 ? 'eager' : 'lazy'}
                  fetchPriority={index === 0 ? 'high' : 'auto'}
                />
              </div>
            ))}
          </Carousel>
          <div
            style={{
              position: 'absolute',
              inset: 0,
              background: 'linear-gradient(180deg, rgba(0,0,0,0.05) 0%, rgba(0,0,0,0.15) 45%, rgba(0,0,0,0.78) 100%)',
            }}
          />
          <div
            style={{
              position: 'absolute',
              left: 0,
              right: 0,
              bottom: 0,
              padding: '28px 16px',
              maxWidth: 960,
              margin: '0 auto',
            }}
          >
            <Space size={10} wrap style={{ marginBottom: 10 }}>
              <Tag color={AZUL} style={{ fontSize: 13, padding: '3px 12px', border: 'none' }}>
                <EnvironmentOutlined /> {[inmueble.zona, inmueble.ciudad].filter(Boolean).join(', ') || 'Ubicación'}
              </Tag>
              <Tag style={{ fontSize: 13, padding: '3px 12px', background: 'rgba(255,255,255,0.15)', color: '#fff', border: 'none' }}>
                Código {inmueble.codigo}
              </Tag>
            </Space>
            <Title level={1} style={{ margin: 0, color: '#fff', fontSize: 'clamp(26px, 5vw, 42px)', lineHeight: 1.15 }}>
              {tituloInmueble(inmueble)}
            </Title>
            {inmueble.ganchoEmocional && (
              <Paragraph
                style={{
                  margin: '10px 0 0',
                  fontSize: 'clamp(16px, 2.4vw, 20px)',
                  color: AZUL_SUAVE,
                  fontStyle: 'italic',
                  maxWidth: 640,
                }}
              >
                {inmueble.ganchoEmocional}
              </Paragraph>
            )}
            {inmueble.precio != null && (
              <div
                style={{
                  display: 'inline-block',
                  marginTop: 16,
                  background: '#fff',
                  color: AZUL_OSCURO,
                  fontWeight: 700,
                  fontSize: 'clamp(18px, 2.6vw, 24px)',
                  padding: '8px 18px',
                  borderRadius: 999,
                }}
              >
                {formatearMoneda(inmueble.precio)}
              </div>
            )}
          </div>
        </div>
      )}

      <main style={{ flex: 1, maxWidth: 960, margin: '0 auto', padding: '24px 16px 64px', width: '100%' }}>
        {cargando && <Skeleton active paragraph={{ rows: 8 }} />}

        {!cargando && (error || !inmueble) && (
          <div style={{ paddingTop: 80, textAlign: 'center' }}>
            <Empty description="Esta propiedad ya no está disponible o el link no es válido" />
          </div>
        )}

        {!cargando && inmueble && (
          <Space orientation="vertical" size={24} style={{ width: '100%' }}>
            <Row gutter={[20, 20]}>
              {BENEFICIOS.map((beneficio) => (
                <Col key={beneficio.titulo} xs={24} sm={8}>
                  <div
                    style={{
                      height: '100%',
                      background: '#fff',
                      borderRadius: 16,
                      padding: '22px 20px',
                      boxShadow: '0 4px 18px rgba(14,165,233,0.12)',
                    }}
                  >
                    <div
                      style={{
                        width: 44,
                        height: 44,
                        borderRadius: '50%',
                        background: AZUL_SUAVE,
                        color: AZUL_OSCURO,
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        fontSize: 20,
                        marginBottom: 14,
                      }}
                    >
                      {beneficio.icono}
                    </div>
                    <Text strong style={{ fontSize: 16, color: '#102a43', display: 'block' }}>
                      {beneficio.titulo}
                    </Text>
                    <Paragraph style={{ marginTop: 6, marginBottom: 0, color: '#595959', fontSize: 14.5 }}>
                      {beneficio.texto}
                    </Paragraph>
                  </div>
                </Col>
              ))}
            </Row>

            {!fotoPrincipal && (
              <div>
                <Space align="center" wrap>
                  <HomeOutlined style={{ fontSize: 22, color: AZUL_OSCURO }} aria-hidden />
                  <Title level={2} style={{ margin: 0 }}>
                    {tituloInmueble(inmueble)}
                  </Title>
                </Space>
                <Space size={16} wrap style={{ marginTop: 8, color: 'rgba(0,0,0,0.65)' }}>
                  <span>
                    <EnvironmentOutlined /> {[inmueble.zona, inmueble.ciudad].filter(Boolean).join(', ') || 'Ubicación no especificada'}
                  </span>
                  <span>Código {inmueble.codigo}</span>
                </Space>
                {inmueble.ganchoEmocional && (
                  <Paragraph
                    italic
                    style={{ margin: '12px 0 0', fontSize: 18, color: AZUL_OSCURO, borderLeft: `3px solid ${AZUL}`, paddingLeft: 12 }}
                  >
                    {inmueble.ganchoEmocional}
                  </Paragraph>
                )}
                {inmueble.precio != null && (
                  <Title level={3} style={{ margin: '12px 0 0', color: AZUL_OSCURO }}>
                    {formatearMoneda(inmueble.precio)}
                  </Title>
                )}
              </div>
            )}

            {fotosSecundarias.length > 0 && (
              <div>
                <Text strong style={{ fontSize: 13, color: '#8c8c8c', letterSpacing: 0.3 }}>
                  GALERÍA
                </Text>
                <Image.PreviewGroup>
                  <div
                    className="landing-galeria-carrusel"
                    style={{
                      display: 'flex',
                      gap: 10,
                      marginTop: 10,
                      overflowX: 'auto',
                      scrollSnapType: 'x mandatory',
                      paddingBottom: 6,
                    }}
                  >
                    {fotosSecundarias.map((foto, index) => (
                      <div key={foto.id} style={{ flex: '0 0 auto', scrollSnapAlign: 'start' }}>
                        <Image
                          src={foto.url}
                          alt={`Foto ${index + 2} de ${tituloInmueble(inmueble)}, código ${inmueble.codigo}`}
                          style={{ borderRadius: 12, objectFit: 'cover', height: 180, width: 240 }}
                          height={180}
                          width={240}
                          loading="lazy"
                        />
                      </div>
                    ))}
                  </div>
                </Image.PreviewGroup>
              </div>
            )}

            {inmueble.descripcionPublica && (
              <Paragraph
                style={{
                  whiteSpace: 'pre-line',
                  fontSize: 16.5,
                  lineHeight: 1.7,
                  color: '#262626',
                  margin: 0,
                }}
              >
                {inmueble.descripcionPublica}
              </Paragraph>
            )}

            <div>
              <Text strong style={{ fontSize: 13, color: '#8c8c8c', letterSpacing: 0.3 }}>
                CARACTERÍSTICAS
              </Text>
              <div style={{ marginTop: 10 }}>
                <Space size={[10, 10]} wrap>
                  {inmueble.areaConstruida != null && (
                    <Tag icon={<ExpandOutlined />} style={{ padding: '4px 10px', fontSize: 13.5 }}>
                      {inmueble.areaConstruida} m² construidos
                    </Tag>
                  )}
                  {inmueble.areaTotal != null && inmueble.areaConstruida == null && (
                    <Tag icon={<ExpandOutlined />} style={{ padding: '4px 10px', fontSize: 13.5 }}>
                      {inmueble.areaTotal} m²
                    </Tag>
                  )}
                  {inmueble.numeroHabitaciones != null && (
                    <Tag icon={<HomeOutlined />} style={{ padding: '4px 10px', fontSize: 13.5 }}>
                      {inmueble.numeroHabitaciones} habitaciones
                    </Tag>
                  )}
                  {inmueble.numeroBanos != null && (
                    <Tag icon={<BankOutlined />} style={{ padding: '4px 10px', fontSize: 13.5 }}>
                      {inmueble.numeroBanos} baños
                    </Tag>
                  )}
                  {inmueble.piso != null && (
                    <Tag icon={<CompassOutlined />} style={{ padding: '4px 10px', fontSize: 13.5 }}>
                      Piso {inmueble.piso}
                    </Tag>
                  )}
                  {inmueble.estratoSocioeconomico != null && (
                    <Tag style={{ padding: '4px 10px', fontSize: 13.5 }}>Estrato {inmueble.estratoSocioeconomico}</Tag>
                  )}
                </Space>
              </div>
            </div>

            {inmueble.caracteristicas.length > 0 && (
              <div>
                <Text strong style={{ fontSize: 13, color: '#8c8c8c', letterSpacing: 0.3 }}>
                  CARACTERÍSTICAS Y ZONAS COMUNES
                </Text>
                <div style={{ marginTop: 10 }}>
                  <Space size={[10, 10]} wrap>
                    {inmueble.caracteristicas.map((caracteristica) => (
                      <Tag key={caracteristica} style={{ padding: '4px 10px', fontSize: 13.5 }}>
                        {caracteristica}
                      </Tag>
                    ))}
                  </Space>
                </div>
              </div>
            )}

            {inmueble.videos.length > 0 && (
              <div>
                <Title level={4}>Videos</Title>
                <Space orientation="vertical" size={16} style={{ width: '100%' }}>
                  {inmueble.videos.map((video) => (
                    <VideoEmbebido key={video.id} url={video.url} origen={video.origen} />
                  ))}
                </Space>
              </div>
            )}

            <div
              style={{
                background: `linear-gradient(135deg, ${AZUL} 0%, ${AZUL_OSCURO} 100%)`,
                borderRadius: 14,
                padding: 24,
                color: '#fff',
              }}
            >
              <Title level={4} style={{ marginTop: 0, color: '#fff' }}>
                ¿Te interesa esta propiedad?
              </Title>
              <Paragraph style={{ marginBottom: 16, color: 'rgba(255,255,255,0.85)' }}>
                Escríbenos y con gusto te damos más información o coordinamos una visita.
              </Paragraph>
              <Space wrap>
                <Button
                  type="primary"
                  size="large"
                  icon={<WhatsAppOutlined />}
                  href={construirEnlaceWhatsappNegocio(mensajeWhatsapp)}
                  target="_blank"
                  rel="noreferrer"
                >
                  Contactar por WhatsApp
                </Button>
                <Button
                  size="large"
                  icon={<MailOutlined />}
                  href={construirEnlaceCorreo(
                    NEGOCIO.email,
                    `Consulta por ${tituloInmueble(inmueble)}`,
                    mensajeWhatsapp,
                  )}
                >
                  Escribir un correo
                </Button>
              </Space>

              <div style={{ background: '#fff', borderRadius: 10, padding: 18, marginTop: 20 }}>
                {leadEnviado ? (
                  <Result
                    status="success"
                    title="¡Listo! Ya tenemos tus datos"
                    subTitle="Un asesor te contactará pronto."
                    style={{ padding: '12px 0' }}
                  />
                ) : (
                  <>
                    <Text strong style={{ fontSize: 13.5 }}>
                      O deja tus datos y te contactamos nosotros
                    </Text>
                    <Form form={formLead} layout="vertical" onFinish={handleEnviarLead} style={{ marginTop: 12 }}>
                      <Row gutter={8}>
                        <Col xs={24} sm={12}>
                          <Form.Item
                            name="nombre"
                            label="Nombre"
                            rules={[{ required: true, message: 'Escribe tu nombre' }]}
                          >
                            <Input placeholder="Tu nombre" />
                          </Form.Item>
                        </Col>
                        <Col xs={24} sm={12}>
                          <Form.Item
                            name="telefono"
                            label="Teléfono"
                            rules={[{ required: true, message: 'Escribe tu teléfono' }]}
                          >
                            <Input placeholder="Tu teléfono" />
                          </Form.Item>
                        </Col>
                      </Row>
                      <Form.Item
                        name="correoElectronico"
                        label="Correo (opcional)"
                        rules={[{ type: 'email', message: 'Escribe un correo válido' }]}
                      >
                        <Input placeholder="tu@correo.com" />
                      </Form.Item>
                      <Form.Item name="mensaje" label="Mensaje (opcional)">
                        <Input.TextArea rows={2} placeholder="Cuéntanos qué te interesa saber" />
                      </Form.Item>
                      <Button type="primary" htmlType="submit" loading={enviandoLead} block>
                        Enviar
                      </Button>
                    </Form>
                  </>
                )}
              </div>
            </div>

            <Text type="secondary" style={{ fontSize: 12 }}>
              Información de carácter referencial, sujeta a cambios sin previo aviso. Consulta los{' '}
              <a href="/terminos">términos y condiciones</a>.
            </Text>
          </Space>
        )}
      </main>

      {!cargando && inmueble && (
        <a
          href={construirEnlaceWhatsappNegocio(mensajeWhatsapp)}
          target="_blank"
          rel="noreferrer"
          aria-label="Contactar por WhatsApp"
          style={{
            position: 'fixed',
            right: 20,
            bottom: avisoCookiesVisible ? 92 : 20,
            width: 56,
            height: 56,
            borderRadius: '50%',
            background: '#25D366',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            boxShadow: '0 4px 14px rgba(0,0,0,0.25)',
            zIndex: 20,
          }}
        >
          <WhatsAppOutlined style={{ fontSize: 28, color: '#fff' }} />
        </a>
      )}

      <PublicFooter />
      <AvisoCookies />
    </div>
  );
}
