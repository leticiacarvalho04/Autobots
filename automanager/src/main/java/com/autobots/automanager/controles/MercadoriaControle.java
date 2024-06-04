package com.autobots.automanager.controles;

import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Mercadoria;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.modelo.adicionadorLink.AdicionadorLinkEmpresa;
import com.autobots.automanager.modelo.adicionadorLink.AdicionadorLinkMercadoria;
import com.autobots.automanager.modelo.adicionadorLink.AdicionadorLinkUsuario;
import com.autobots.automanager.modelo.adicionadorLink.AdicionadorLinkVenda;
import com.autobots.automanager.modelo.atualizadores.MercadoriaAtualizadora;
import com.autobots.automanager.modelo.selecionadores.MercadoriaSelecionadora;
import com.autobots.automanager.repositorios.EmpresaRepositorio;
import com.autobots.automanager.repositorios.MercadoriaRepositorio;
import com.autobots.automanager.repositorios.UsuarioRepositorio;
import com.autobots.automanager.repositorios.VendaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/mercadoria")
public class MercadoriaControle {
	
	@Autowired
	public MercadoriaRepositorio repositorio;
	
	@Autowired
	public MercadoriaSelecionadora selecionador;
	
	@Autowired
	public AdicionadorLinkMercadoria adicionadorLink;
	
	@Autowired
	public UsuarioRepositorio usuarioRepositorio;
	
	@Autowired
	private VendaRepositorio vendaRepositorio;
	
	@Autowired
	private AdicionadorLinkVenda adicionadorLinkVenda;
	
	@Autowired
	private EmpresaRepositorio empresaRepositorio;
	
	@Autowired
	private AdicionadorLinkEmpresa adicionadorLinkEmpresa;
	
	@Autowired
	private AdicionadorLinkUsuario adicionadorLinkUsuario;
	
	@GetMapping("/{id}")
	public Mercadoria buscarPorId(Long id) {
        Mercadoria mercadoria = repositorio.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		adicionadorLink.adicionarLink(mercadoria);
		return mercadoria;
    }
	
	@GetMapping
	public List<Mercadoria> obterMercadoria(){
        List<Mercadoria> mercadoria = repositorio.findAll();
		adicionadorLink.adicionarLink(mercadoria);
        return mercadoria;
    }
	
	@PostMapping("/cadastro/venda/{idVenda}")
	public ResponseEntity<Mercadoria> cadastrarMercadoria(@RequestBody Mercadoria mercadoria, @PathVariable Long idVenda) {
		Venda venda = vendaRepositorio.getById(idVenda);
		venda.getMercadorias().add(mercadoria);
		vendaRepositorio.save(venda);
		adicionadorLinkVenda.adicionarLink(venda);
		repositorio.save(mercadoria);
		adicionadorLink.adicionarLink(mercadoria);
		return new ResponseEntity<Mercadoria>(mercadoria, HttpStatus.CREATED);
	}
	
	@PostMapping("/cadastro/empresa/{idEmpresa}")
	public ResponseEntity<Mercadoria> cadastrarMercadoriaEmpresa(@RequestBody Mercadoria mercadoria, @PathVariable Long idEmpresa) {
		Empresa empresa = empresaRepositorio.findById(idEmpresa).orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
		empresa.getMercadorias().add(mercadoria);
		empresaRepositorio.save(empresa);
		repositorio.save(mercadoria);
		adicionadorLinkEmpresa.adicionarLink(empresa);
		adicionadorLink.adicionarLink(mercadoria);
		return new ResponseEntity<Mercadoria>(mercadoria, HttpStatus.CREATED);
	}
	
	@PostMapping("/cadastro/usuario/{idUsuario}")
	public ResponseEntity<Mercadoria> cadastrarMercadoriaUsuario(@RequestBody Mercadoria mercadoria, @PathVariable Long idUsuario) {
		Usuario usuario = usuarioRepositorio.findById(idUsuario).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
		usuario.getMercadorias().add(mercadoria);
		usuarioRepositorio.save(usuario);
		adicionadorLinkUsuario.adicionarLink(usuario);
		repositorio.save(mercadoria);
		adicionadorLink.adicionarLink(mercadoria);
		return new ResponseEntity<Mercadoria>(mercadoria, HttpStatus.CREATED);
	}
	
	@PutMapping("/atualizar")
	public void atualizarMercadoria(@RequestBody Mercadoria mercadoria) {
        Mercadoria mercadoriaId = repositorio.getById(mercadoria.getId());
		MercadoriaAtualizadora atualizador = new MercadoriaAtualizadora();
        atualizador.atualizar(mercadoriaId, mercadoria);
        repositorio.save(mercadoria);
    }
	
	@DeleteMapping("/excluir/{id}")
	public void excluirMercadoria(@PathVariable Long id) {
		Mercadoria mercadoria = repositorio.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		List<Usuario> usuarios = usuarioRepositorio.findAll();
		for (Usuario usuario : usuarios) {
			if (usuario.getMercadorias().contains(mercadoria)) {
				usuario.getMercadorias().remove(mercadoria);
				usuarioRepositorio.save(usuario);
			}
		}

		repositorio.delete(mercadoria);
	}
}
