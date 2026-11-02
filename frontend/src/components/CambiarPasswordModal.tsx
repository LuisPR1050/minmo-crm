import { useState } from 'react';
import { Form, Input, Modal, message } from 'antd';
import { AxiosError } from 'axios';
import { authApi } from '../api/auth';
import type { CambiarPasswordRequest } from '../types/auth';

interface Props {
  open: boolean;
  onClose: () => void;
}

interface FormValues extends CambiarPasswordRequest {
  confirmarPassword: string;
}

export default function CambiarPasswordModal({ open, onClose }: Props) {
  const [guardando, setGuardando] = useState(false);
  const [form] = Form.useForm<FormValues>();

  const handleSubmit = async (values: FormValues) => {
    setGuardando(true);
    try {
      await authApi.cambiarPassword({
        passwordActual: values.passwordActual,
        passwordNueva: values.passwordNueva,
      });
      message.success('Contraseña actualizada correctamente');
      form.resetFields();
      onClose();
    } catch (error) {
      if (error instanceof AxiosError && error.response?.status === 401) {
        message.error('La contraseña actual no es correcta');
      } else {
        message.error('No se pudo cambiar la contraseña');
      }
    } finally {
      setGuardando(false);
    }
  };

  return (
    <Modal
      title="Cambiar contraseña"
      open={open}
      onCancel={() => {
        form.resetFields();
        onClose();
      }}
      onOk={() => form.submit()}
      confirmLoading={guardando}
      okText="Guardar"
      cancelText="Cancelar"
    >
      <Form form={form} layout="vertical" onFinish={handleSubmit}>
        <Form.Item
          name="passwordActual"
          label="Contraseña actual"
          rules={[{ required: true, message: 'Ingresa tu contraseña actual' }]}
        >
          <Input.Password autoFocus />
        </Form.Item>
        <Form.Item
          name="passwordNueva"
          label="Contraseña nueva"
          rules={[
            { required: true, message: 'Ingresa la nueva contraseña' },
            { min: 8, message: 'Debe tener al menos 8 caracteres' },
          ]}
        >
          <Input.Password />
        </Form.Item>
        <Form.Item
          name="confirmarPassword"
          label="Confirmar contraseña nueva"
          dependencies={['passwordNueva']}
          rules={[
            { required: true, message: 'Confirma la nueva contraseña' },
            ({ getFieldValue }) => ({
              validator(_, value) {
                if (!value || getFieldValue('passwordNueva') === value) {
                  return Promise.resolve();
                }
                return Promise.reject(new Error('Las contraseñas no coinciden'));
              },
            }),
          ]}
        >
          <Input.Password />
        </Form.Item>
      </Form>
    </Modal>
  );
}
