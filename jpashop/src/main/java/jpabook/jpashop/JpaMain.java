package jpabook.jpashop;

import jpabook.jpashop.domain.Book;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try{
            /*
            // 아무튼 핵심은 양방향보다는 단방향을 잘 설계하자!
            Order order = new Order();
            order.addOrderItem(new OrderItem());
            */

            // 상속관계 매핑

            Book book = new Book();
            book.setName("JPA");
            book.setAuthor("김영한");
            em.persist(book);


            tx.commit();
        }catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
