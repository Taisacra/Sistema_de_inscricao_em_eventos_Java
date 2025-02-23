package br.com.nlw.events.service;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.nlw.events.dto.SubscriptionRankingByUser;
import br.com.nlw.events.dto.SubscriptionRankingItem;
import br.com.nlw.events.dto.SubscriptionResponse;
import br.com.nlw.events.exception.EventNotFoundException;
import br.com.nlw.events.exception.SubscriptionConflictException;
import br.com.nlw.events.exception.UserIndicadorNotFoundException;
import br.com.nlw.events.model.Event;
import br.com.nlw.events.model.Subscription;
import br.com.nlw.events.model.User;
import br.com.nlw.events.repository.EventRepository;
import br.com.nlw.events.repository.SubscriptionRepository;
import br.com.nlw.events.repository.UserRepository;

@Service
public class SubscriptionService {
	
	@Autowired
	private EventRepository eventRepo;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private SubscriptionRepository subRepo;
	
	public SubscriptionResponse createNewSubscription(String eventName, User user, Integer userId) {
	
		Event evt = eventRepo.findByPrettyName(eventName);//recuperar o evento pelo nome 
		if(evt == null) {//CASO ALTERNATIVO 2: Caso o evento não exista vai lança essa exception
			throw new EventNotFoundException("Evento " + eventName + " não existe");
		}
		User userRec =userRepo.findByEmail(user.getEmaill());//usuario recuperado tenta recuperar do banco
		if(userRec == null) { // CASO ALTERNATIVO 1: Se o user não existir vai gravar no banco um novo usuário
			userRec = userRepo.save(user); //gravo usuario no banco 
		}
		
		User indicador = null;
		if(userId != null) {// se ele não existir ignoro esse bloco de código e ele continua sendo null
			//TRATAMENTO: caso o user indicador existir
			indicador = userRepo.findById(userId).orElse(null);
	        if (indicador == null) {
	            throw new UserIndicadorNotFoundException("Usuário " +userId+ " indicador não existe");
	        }
		}
		
		Subscription subs = new Subscription();
		subs.setEvent(evt); //atribuir o evento que recuperei na inscrição 
		//se o usuário já exixstir vai utilizar ele na inscrição
		subs.setSubscriber(userRec); //atribuir o usuario sendo o assinante 
		subs.setIndication(indicador); 
		
		Subscription tmpSub = subRepo.findByEventAndSubscriber(evt, userRec);
		if(tmpSub != null) {//CASO ALTERNATIVO 3: Caso exista uma inscrição com o usuário passado
			throw new SubscriptionConflictException("Já existe inscrição para o usuário  " + userRec.getName()+ " no evento " + evt.getTitle());
		}
		
		Subscription res = subRepo.save(subs); // salva tudo no banco tabela inscrição 
		//retorna uma resposta de inscrição
		return new SubscriptionResponse(res.getSubscriptionNumber(), "http://codecraft.com/subscription/"+res.getEvent().getPrettyName()+"/"+res.getSubscriber().getId());
	}
	
	public List<SubscriptionRankingItem> getCompleteRanking(String prettyName){
		Event evt = eventRepo.findByPrettyName(prettyName);
		if(evt == null) {//se o evento não exixtir
			throw new EventNotFoundException("Ranking do evento " + prettyName+ "não existe!");
		}
		return subRepo.generateRanking(evt.getEventId());//se o evento existir ele retornar o ranking deste evento
	}
	
	public SubscriptionRankingByUser getRankingByUser(String prettyName, Integer userId) {
		List<SubscriptionRankingItem> ranking = getCompleteRanking(prettyName);//lista com todo ranking
													//filtro: para cada item da minha lista eu quero ver se o item no seu id é igual ao userid que passei no parâmetro (equals). Se ninguém se inscreveu pelo link dele vai ser null
		SubscriptionRankingItem item = ranking.stream().filter(i->i.userId().equals(userId)).findFirst().orElse(null);//Fazer um filtro nessa lista de ranking
		if(item == null) {//se o úsuario não estiver nesta lista
			throw new UserIndicadorNotFoundException("Não há inscrições com indicação do usuário "+userId);
		}
		// achar a posicao lista 
						//Percorrer cada um dos inteiros
		Integer posicao = IntStream.range(0, ranking.size()) 
				.filter(pos-> ranking.get(pos).userId().equals(userId))//a partir da posição(pos) na lista ranking irá verificar se é igual ao userId passado
				.findFirst().getAsInt();
		return new SubscriptionRankingByUser(item, posicao+1);
	}
}
