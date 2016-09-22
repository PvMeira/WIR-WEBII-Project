package com.senac.cl.service;

import java.util.Calendar;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import com.senac.cl.modelos.ListaCustomizada;
import com.senac.cl.modelos.Livro;
import com.senac.cl.modelos.Pessoa;
import com.senac.cl.repository.LivroRepository;
import com.senac.cl.transactional.Transactional;

public class LivroService {

	@Inject
	LivroRepository livroRepository;
	
	@Inject
	ListaCustomizadaService listaCustomizadaService;

	private HttpSession ses = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
	
	private static final String OBS ="Livro Publico, nome do dono :";

	/**
	 * Salva um livro
	 * 
	 * @param entidade
	 */
	@Transactional
	public void salvar(Livro entidade, boolean var) {
		Pessoa pessoaDaSecao = (Pessoa) this.ses.getAttribute("user");

		entidade.setDono(pessoaDaSecao);
		entidade.setDataUltimaLeitura(Calendar.getInstance());
		entidade.setDataUpload(Calendar.getInstance());
		entidade.setLivroAtivo(true);
		entidade.setPublico(var);

		// Verifica se alguma campo n�o foi preenchido
		this.verificaCampos(entidade);
		livroRepository.inserir(entidade);
	}

	/**
	 * Atualiza a entidade livro recebida
	 * 
	 * @param entidade
	 */
	@Transactional
	public void atualizar(Livro entidade) {
		entidade.setDataUpload(Calendar.getInstance());
		livroRepository.atualizar(entidade);
	}

	/**
	 * Atualiza a boolean que torna o livro p�blico
	 * 
	 * @param entidade
	 */
	@Transactional
	public void atualizarATransferenciaParaPublico(Livro entidade) {
		entidade.setPublico(Boolean.TRUE);
		livroRepository.atualizar(entidade);
	}
	
	/**
	 * Criar um novo livro que � uma copia 
	 * do livro publico
	 * @param entidade
	 */
	@Transactional
	public void copiaLivroPublicoParaContaUsuarioLogado(Livro entidade){
		Pessoa pessoaDaSecao = (Pessoa) this.ses.getAttribute("user");
		
		Livro novo = new Livro();
		
		novo.setArquivo(entidade.getArquivo());
		novo.setAutor(entidade.getAutor());
		novo.setDataUltimaLeitura(Calendar.getInstance());
		novo.setDataUpload(Calendar.getInstance());
		novo.setDono(pessoaDaSecao);
		novo.setJaFoiLido(Boolean.FALSE);
		novo.setLivroAtivo(Boolean.TRUE);
		novo.setObservacao(OBS.concat(entidade.getDono().getNome()));
		novo.setPaginas(entidade.getPaginas());
		novo.setPontuacao(entidade.getPontuacao());
		novo.setTitulo(entidade.getTitulo());
		novo.setPublico(Boolean.FALSE);
		
		livroRepository.inserir(novo);
	}

	/**
	 * verifica Se todos os campo obrigatorios est�o preenchidos
	 */
	private void verificaCampos(Livro livro) {
		if (livro.getTitulo() == null) {
			throw new RuntimeException("Campo Autor est� nulo");
		}
		if (livro.getDono() == null) {
			throw new RuntimeException("Problema Com o login, contado suporte");
		}
		if (livro.getPaginas() == 0) {
			throw new RuntimeException("Campo paginas est� nulo");
		}
		if (livro.getAutor() == null) {
			throw new RuntimeException("Campo autor est� nulo");
		}
	}

	/**
	 * Remove o livro selecionado da lista
	 * 
	 * @param livro
	 */
	@Transactional
	public void deletar(Livro livro) {
		this.verificaExisteListaCustomizada(livro);
		livroRepository.deletar(livro);

	}
	private void verificaExisteListaCustomizada(Livro ed){
		List<ListaCustomizada> lista = listaCustomizadaService.listarListasCustomizadas();
		if(lista != null){
			for (ListaCustomizada listaCustomizada : lista) {
				List<Livro> livro = listaCustomizada.getLivro();
				for (Livro livro2 : livro) {
					if(livro2.equals(ed)){
						this.listaCustomizadaService.removeListaCustomizadaPeloLivro(listaCustomizada);
					}
				}
			}
		}
	}
	
	/**
	 * Conta livros  cadastrados
	 * @return
	 */
	@Transactional
	public int contaLivrosTotais(){
		return this.livroRepository.contaLivrosCadastrados();
	}
	
	/**
	 * Conta livros publicos cadastrados
	 * @return
	 */
	@Transactional
	public int contaLivrosPublicosTotais(){
		return this.livroRepository.contaLivrosPublicosCadastrados();
	}

	/**
	 * 
	 * @return
	 */
	@Transactional
	public List<Livro> listarTodosRegistros() {
		return livroRepository.todosOsRegistros();
	}

	/**
	 * retorna os livros da pessoa logada
	 * 
	 * @return
	 */
	public List<Livro> listarTodosLivrosDoUsuario() {
		Pessoa pessoaDaSecao = (Pessoa) this.ses.getAttribute("user");
		Long idParaPesquisa = pessoaDaSecao.getIdPessoa();
		List<Livro> lista = this.livroRepository.todosOsRegistrosDoUsuario(idParaPesquisa);
		return lista;
	}

	/**
	 * retorna todos os livros marcados como publicos
	 * 
	 * @return
	 */
	public List<Livro> listaTodosLivrosPublicos() {
		List<Livro> lista = this.livroRepository.todosOsRegistrosPublicos();
		return lista;
	}

	/**
	 * Retorna o auto complete
	 * 
	 * @param s
	 * @return
	 */
	public List<Livro> listarLivrosAutoComplete(String s) {
		List<Livro> lista = this.livroRepository.listarLivrosAutoComplete(s);
		return lista;
	}

	/**
	 * Retorna o autocomplete para a transferencia onde os livros que tem
	 * publico = false ser�o mostrado
	 * 
	 * @param s
	 * @return
	 */
	public List<Livro> listarLivrosAutoCompleteTransferir(String s) {
		List<Livro> lista = this.livroRepository.listarLivrosAutoCompleteTransferir(s);
		return lista;
	}

	/**
	 * @return the ses
	 */
	public HttpSession getSes() {
		return ses;
	}

	/**
	 * @param ses
	 *            the ses to set
	 */
	public void setSes(HttpSession ses) {
		this.ses = ses;
	}

}
