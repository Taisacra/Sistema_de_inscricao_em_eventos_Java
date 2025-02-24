package br.com.nlw.events.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="tbl_subscription")
public class Subscription {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "subscription_number")
	private Integer subscriptionNumber;
	
	@ManyToOne  //múltiplas inscrições para o mesmo evento
	@JoinColumn(name = "event_id") // dentro da tabela inscrição qual o nome da coluna que faz essa ligação entre a tabela inscrição e evento 
	private Event event;
	
	@ManyToOne // múltiplas inscrições para um único usuário
	@JoinColumn(name="subscribed_user_id")
	private User subscriber;
	
	@ManyToOne
	@JoinColumn(name="indication_user_id", nullable = true) // posso ter um caso que ninguem indicou por isso nullable
	private User indication;

	public Integer getSubscriptionNumber() {
		return subscriptionNumber;
	}

	public void setSubscriptionNumber(Integer subscriptionNumber) {
		this.subscriptionNumber = subscriptionNumber;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public User getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(User subscriber) {
		this.subscriber = subscriber;
	}

	public User getIndication() {
		return indication;
	}

	public void setIndication(User indication) {
		this.indication = indication;
	}
	
}
