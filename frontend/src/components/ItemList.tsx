import { Empty, Skeleton } from 'antd';
import type { ReactNode } from 'react';

interface Props<T> {
  items: T[];
  rowKey: (item: T) => string;
  renderItem: (item: T) => ReactNode;
  loading?: boolean;
  emptyDescription?: string;
  emptyImage?: ReactNode;
  size?: 'small' | 'default';
}

/**
 * Reemplazo liviano del componente `List` de AntD (deprecado desde v6, sin
 * reemplazo directo). Dibuja filas separadas por un borde inferior, igual
 * que el estilo por defecto de `List`, sin depender del componente en si.
 */
export default function ItemList<T>({ items, rowKey, renderItem, loading, emptyDescription, emptyImage, size = 'default' }: Props<T>) {
  if (loading) {
    return <Skeleton active />;
  }

  if (items.length === 0) {
    return <Empty description={emptyDescription ?? 'Sin elementos'} image={emptyImage} />;
  }

  const paddingY = size === 'small' ? 8 : 12;

  return (
    <div>
      {items.map((item, index) => (
        <div
          key={rowKey(item)}
          style={{
            padding: `${paddingY}px 0`,
            borderBottom: index < items.length - 1 ? '1px solid rgba(5, 5, 5, 0.06)' : 'none',
          }}
        >
          {renderItem(item)}
        </div>
      ))}
    </div>
  );
}
