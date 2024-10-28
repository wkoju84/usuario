package com.william.usuario.business;

import com.william.usuario.business.converter.UsuarioConverter;
import com.william.usuario.business.dtos.EnderecoDTO;
import com.william.usuario.business.dtos.TelefoneDTO;
import com.william.usuario.business.dtos.UsuarioDTO;
import com.william.usuario.infrastructure.entities.Endereco;
import com.william.usuario.infrastructure.entities.Telefone;
import com.william.usuario.infrastructure.entities.Usuario;
import com.william.usuario.infrastructure.exceptions.ConflictException;
import com.william.usuario.infrastructure.exceptions.ResourceNotFoundException;
import com.william.usuario.infrastructure.repositories.EnderecoRepository;
import com.william.usuario.infrastructure.repositories.TelefoneRepository;
import com.william.usuario.infrastructure.repositories.UsuarioRepository;
import com.william.usuario.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EnderecoRepository enderecoRepository;
    private final TelefoneRepository telefoneRepository;



    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO){
        emailExiste(usuarioDTO.getEmail());
        usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));
    }

    public void emailExiste(String email){
        try {

            boolean existe = verificaEmailExistente(email);
            if (existe){
                throw new ConflictException("Email já cadastrado!" + email);
            }
        }catch (ConflictException e){
            throw new ConflictException("Email já cadastrado!" + e.getCause());
        }
    }

    public boolean verificaEmailExistente(String email){
        return usuarioRepository.existsByEmail(email);
    }

    public UsuarioDTO buscarUsuarioPorEmail(String email){
        try {
            return usuarioConverter.paraUsuarioDTO(usuarioRepository.findByEmail(email).orElseThrow(
                    ()-> new ResourceNotFoundException("Email não encontrado" + email)));

        }
        catch (ResourceNotFoundException e){
            throw new ResourceNotFoundException("Email não encontrado! " + email);
        }
    }

    public void deletaUsuarioPorEmail(String email){
        usuarioRepository.deleteByEmail(email);
    }

    public UsuarioDTO atualizaDadosUsuario(String token, UsuarioDTO dto){
        //Aqui buscamos o email através do token (tirar a obrigatoriedade do email)
        String email = jwtUtil.extrairEmailToken(token.substring(7));

        //Criptografia de senha
        dto.setSenha(dto.getSenha() != null ? passwordEncoder.encode(dto.getSenha()) : null);

        //Busca os dados do usuário no banco de dados
        Usuario usuarioEntity = usuarioRepository.findByEmail(email).orElseThrow(()->
                new ResourceNotFoundException("Email não localizado."));

        //Mescla os dados que recebemos na requisição DTO com os dados do banco de dados
        Usuario usuario = usuarioConverter.updateUsuario(dto, usuarioEntity);

        //Salva os dados convertidos e depois retorna e converte para usuarioDTO
        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));
    }

    public EnderecoDTO atualizaEndereco(Long idEndereco, EnderecoDTO enderecoDTO){

        Endereco entity = enderecoRepository.findById(idEndereco).orElseThrow(()->
                new ResourceNotFoundException("Id não encontrado! " + idEndereco));

        Endereco endereco = usuarioConverter.updateEndereco(enderecoDTO, entity);

        return usuarioConverter.paraEnderecoDTO(enderecoRepository.save(endereco));

    }

    public TelefoneDTO atualizaTelefone(Long idTelefone, TelefoneDTO dto){

        Telefone entity = telefoneRepository.findById(idTelefone).orElseThrow(()->
                new ResourceNotFoundException("Id não encontrado! " + idTelefone));

        Telefone telefone = usuarioConverter.updateTelefone(dto, entity);

        return usuarioConverter.paraTelefoneDTO(telefoneRepository.save(telefone));
    }

    public EnderecoDTO cadastraEndereco(String token, EnderecoDTO dto){

        String email = jwtUtil.extrairEmailToken(token.substring(7));
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(()->
                new ResourceNotFoundException("Email não localizado! " + email));

        Endereco endereco = usuarioConverter.paraEnderecoEntity(dto, usuario.getId());
        Endereco enderecoEntity = enderecoRepository.save(endereco);
        return usuarioConverter.paraEnderecoDTO(enderecoEntity);
    }

    public TelefoneDTO cadastraTelefone(String token, TelefoneDTO dto){

        String email = jwtUtil.extrairEmailToken(token.substring(7));
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(()->
                new ResourceNotFoundException("Email não localizado! " + email));

        Telefone telefone = usuarioConverter.paraTelefoneEntity(dto, usuario.getId());
        return usuarioConverter.paraTelefoneDTO(telefoneRepository.save(telefone));
    }
}
