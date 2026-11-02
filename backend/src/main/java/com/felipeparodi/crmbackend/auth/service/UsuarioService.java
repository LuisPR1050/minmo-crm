package com.felipeparodi.crmbackend.auth.service;

import com.felipeparodi.crmbackend.auth.dto.CambiarEstadoUsuarioRequest;
import com.felipeparodi.crmbackend.auth.dto.EditarUsuarioRequest;
import com.felipeparodi.crmbackend.auth.dto.UsuarioRequest;
import com.felipeparodi.crmbackend.auth.dto.UsuarioResponse;
import com.felipeparodi.crmbackend.auth.entity.RolUsuario;
import com.felipeparodi.crmbackend.auth.entity.Usuario;
import com.felipeparodi.crmbackend.auth.repository.UsuarioRepository;
import com.felipeparodi.crmbackend.atencioncliente.repository.ClienteRepository;
import com.felipeparodi.crmbackend.captacion.repository.CondicionCaptacionRepository;
import com.felipeparodi.crmbackend.captacion.repository.VisitaCaptacionRepository;
import com.felipeparodi.crmbackend.negociacion.repository.NegociacionRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClienteRepository clienteRepository;
    private final CondicionCaptacionRepository condicionCaptacionRepository;
    private final VisitaCaptacionRepository visitaCaptacionRepository;
    private final NegociacionRepository negociacionRepository;

    public UsuarioService(UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder,
                           ClienteRepository clienteRepository,
                           CondicionCaptacionRepository condicionCaptacionRepository,
                           VisitaCaptacionRepository visitaCaptacionRepository,
                           NegociacionRepository negociacionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.clienteRepository = clienteRepository;
        this.condicionCaptacionRepository = condicionCaptacionRepository;
        this.visitaCaptacionRepository = visitaCaptacionRepository;
        this.negociacionRepository = negociacionRepository;
    }

    public UsuarioResponse crear(UsuarioRequest request) {
        if (usuarioRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con el correo: " + request.getEmail());
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .rol(RolUsuario.valueOf(request.getRol()))
                .activo(true)
                .build();

        Usuario guardado = usuarioRepository.save(usuario);

        return mapearAResponse(guardado);
    }

    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public UsuarioResponse cambiarEstado(UUID id, CambiarEstadoUsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + id));

        usuario.setActivo(request.isActivo());

        Usuario actualizado = usuarioRepository.save(usuario);

        return mapearAResponse(actualizado);
    }

    public UsuarioResponse actualizar(UUID id, EditarUsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + id));

        usuarioRepository.findByEmailIgnoreCase(request.getEmail())
                .filter(otro -> !otro.getId().equals(id))
                .ifPresent(otro -> {
                    throw new IllegalArgumentException("Ya existe un usuario con el correo: " + request.getEmail());
                });

        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setTelefono(request.getTelefono());
        usuario.setRol(RolUsuario.valueOf(request.getRol()));

        Usuario actualizado = usuarioRepository.save(usuario);

        return mapearAResponse(actualizado);
    }

    public void eliminar(UUID id, UUID usuarioActualId) {
        if (id.equals(usuarioActualId)) {
            throw new IllegalArgumentException("No puedes eliminar tu propio usuario");
        }

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + id));

        if (clienteRepository.existsByAsesorId(id) || condicionCaptacionRepository.existsByAsesorId(id)
                || visitaCaptacionRepository.existsByAsesorId(id) || negociacionRepository.existsByAsesorId(id)) {
            throw new IllegalArgumentException(
                    "No se puede eliminar este usuario porque tiene clientes, captaciones, visitas o negociaciones "
                            + "asignadas. Desactivalo en vez de eliminarlo para conservar ese historial.");
        }

        usuarioRepository.delete(usuario);
    }

    private UsuarioResponse mapearAResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .telefono(usuario.getTelefono())
                .rol(usuario.getRol().name())
                .activo(usuario.isActivo())
                .createdAt(usuario.getCreatedAt())
                .build();
    }
}
