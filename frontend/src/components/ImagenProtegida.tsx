import { useEffect, useState } from 'react';
import { Image, Skeleton } from 'antd';
import type { ImageProps } from 'antd';
import { obtenerBlobUrl } from '../utils/archivosProtegidos';

interface Props extends Omit<ImageProps, 'src'> {
  url: string;
}

// Wrapper de antd <Image> para fotos servidas desde /uploads/**, que ahora
// exige sesion en el backend y por lo tanto no puede cargarse con un <img src>
// directo (el navegador no adjunta el token). Pide el archivo con el token via
// axios y lo muestra como blob URL.
export default function ImagenProtegida({ url, height, width, style, ...resto }: Props) {
  const [blobUrl, setBlobUrl] = useState<string | undefined>(undefined);

  useEffect(() => {
    let cancelado = false;
    let objectUrl: string | undefined;

    obtenerBlobUrl(url)
      .then((creado) => {
        if (cancelado) {
          URL.revokeObjectURL(creado);
          return;
        }
        objectUrl = creado;
        setBlobUrl(creado);
      })
      .catch(() => {
        if (!cancelado) setBlobUrl(undefined);
      });

    return () => {
      cancelado = true;
      if (objectUrl) URL.revokeObjectURL(objectUrl);
    };
  }, [url]);

  if (!blobUrl) {
    return <Skeleton.Image active style={{ width: width ?? '100%', height: height ?? 110, ...style }} />;
  }

  return <Image src={blobUrl} height={height} width={width} style={style} {...resto} />;
}
