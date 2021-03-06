package com.senac.cl.service;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import com.senac.cl.enums.tipoAcao;
import com.senac.cl.modelos.Pessoa;
import com.senac.cl.repository.PessoaRepository;
import com.senac.cl.transactional.Transactional;
/**
 * 
 * @author Pedro
 * @since 27/08/2016
 */
public class PessoaService {

	@Inject
	private PessoaRepository repositorio;
	@Inject
	private PessoaHistoricoService service;

	
/**
 * Metodo de Salvar - Aplica Regras de Negocio
 * @param pessoa
 */
	@Transactional
	public void salvar(Pessoa pessoa) {
		if (pessoa.getIdPessoa() == null) {
			pessoa.setNormal(true);
			pessoa.setDataUltimoLogin(Calendar.getInstance());
			pessoa.setDataUltimoLogin(Calendar.getInstance());
			repositorio.inserir(pessoa);
			service.atualizarHistorico(pessoa,tipoAcao.INCLUIR);

		} else {
			pessoa.setNormal(true);
			pessoa.setDataUltimoLogin(Calendar.getInstance());
			pessoa.setDataUltimoLogin(Calendar.getInstance());
			repositorio.atualizar(pessoa);
			service.atualizarHistorico(pessoa,tipoAcao.ALTERACAO);
		}
	}
/**
 * Metodo De listar
 * @return
 */
	@Transactional
	public List<Pessoa> carregarPessoasDoBancoDeDados() {
		return repositorio.todasAsPessoas();
	}
/**
 * Metodo de Deletar - Aplica Regras de Negocio
 * @param pessoa
 */
	@Transactional
	public void deletar(Pessoa pessoa) {
		repositorio.remover(pessoa);
		service.atualizarHistorico(pessoa,tipoAcao.DELETAR);
		
	}
	/**
	 * Conta o numeor de pessoas cadastradas no momento na aplica��o
	 * @return
	 */
	public int buscaNumeroDeusuarioCadastradosAplicacao(){
		int numero =repositorio.contaTodasAsPessoasAplicacao();
		return numero;
	}
	
	/**
	 * Metodo que retorna pessoa pelo seu ID
	 * 
	 * @param id
	 * @return
	 */
	@Transactional
	public Pessoa buscaPessoaPeloId(Long id) {

		return repositorio.buscarPeloId(id);
	}
	
}
