package br.com.dao;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.jpautil.JPAUtil;

public class DaoGeneric<E> {

	EntityManager entityManager = JPAUtil.getEntityManager();
	public void salvar(E entidade) {
		 entityManager = JPAUtil.getEntityManager();
		EntityTransaction entityTransaction  =  entityManager.getTransaction();
		entityTransaction.begin();
		
		entityManager.persist(entidade);
				
		entityTransaction.commit();
		entityManager.close();
		
	}
	
	public E merge(E entidade) {
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction entityTransaction  =  entityManager.getTransaction();
		entityTransaction.begin();
		
		E retorno = entityManager.merge(entidade);
				
		entityTransaction.commit();
		entityManager.close();
		
		return retorno;
	}
	
	
	public void deletePorId(E entidade) {
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		
		Object id = JPAUtil.getPrimaryKey(entidade);
		entityManager.createQuery("delete from "+entidade.getClass().getCanonicalName()+" where id = " +id).executeUpdate();
		
		entityTransaction.commit();
		entityManager.close();

	}
	
	public List<E> getListEntity(Class<E> entidade) {
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction entityTransaction  =  entityManager.getTransaction();
		entityTransaction.begin();

		List<E> retorno = entityManager.createQuery("from "+entidade.getName()).getResultList();
		
		entityTransaction.commit();
		entityManager.close();
		
		return retorno;
	}
	
	//  agora é preciso consultar o objeto todo para realizar o dowload da imagem
	// precisamos do nosso código
	public E consultar(Class<E> entidade, String codigo) {
		
		EntityTransaction  entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		
		// Precisamos do código para recuperar o objeto pessoa, colocando a classe direta entidade
		E objeto = entityManager.find(entidade, Long.parseLong(codigo));
		entityTransaction.commit();
		
		return objeto; 
	}
	
	
	
}
