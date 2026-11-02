import { InputNumber } from 'antd';
import type { InputNumberProps } from 'antd';
import { formatearNumeroCO, parsearNumeroCO } from '../utils/numeroFormato';

export default function InputMoneda(props: InputNumberProps<number>) {
  return (
    <InputNumber<number>
      style={{ width: '100%' }}
      min={0}
      formatter={(value) => formatearNumeroCO(value)}
      parser={(value) => parsearNumeroCO(value) as unknown as number}
      {...props}
    />
  );
}
