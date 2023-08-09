package br.com.repository;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.entidades.Estados;
import br.com.entidades.Pessoa;
import br.com.jpautil.JPAUtil;

@Named
public class IDAoPessoaImpl implements IDaoPessoa {
	
	@Override
	public Pessoa consultarUsuario(String login, String senha) {
	
		Pessoa pessoa = null;
		
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction entityTransaction  =  entityManager.getTransaction();
		entityTransaction.begin();
		
		pessoa = (Pessoa) entityManager.createQuery("select p from Pessoa p where p.login = '"+login+"' and p.senha = '"+senha+"'").getSingleResult();
		
		
		entityTransaction.commit();
		entityManager.close();
		
		return pessoa;
	}
	
	@Override
	public List<SelectItem> listaEstados(){
		
		List<SelectItem> seletcItems = new ArrayList<SelectItem>();
		//consulta no BD
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		
		List<Estados> estados = entityManager.createQuery("from Estados" ).getResultList();
			// transformando a lista estados, compreensível para o nosso combox JSF		
		
		for(Estados estado : estados) {
			// mostra o objeto inteiro e o valor do objeto(será mostrado para o usuário) dentro de SelectItem
			//Reitrado o id e enviando o objeto inteiro
			seletcItems.add(new SelectItem(estado, estado.getNome()));
		}
				
		//CÓDIGO ANTIGO
		//for(Estados estado : estados) {
			// mostra o objeto inteiro e o valor do objeto(será mostrado para o usuário) dentro de SelectItem
			//seletcItems.add(new SelectItem(estado.getId(), estado.getNome()));
		//}
		
		return seletcItems;
		
	}
	
	

}
