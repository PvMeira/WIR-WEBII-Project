package com.senac.cl.enums;
/**
 * 
 * @author Pedro
 *
 */
public enum tipoAcao {

	
	INCLUIR ("I","INCLUIR"),
	DELETAR ("D","DELETAR"),
	ALTERACAO ("A","ALTERACAO");
 

private String sigla;
private String nome;
	
tipoAcao(String tipo,String nome){
	this.sigla =tipo;
	this.nome = nome;
}

public String getSigla() {
	return sigla;
}

public void setSigla(String sigla) {
	this.sigla = sigla;
}

public String getNome() {
	return nome;
}

public void setNome(String nome) {
	this.nome = nome;
}

}