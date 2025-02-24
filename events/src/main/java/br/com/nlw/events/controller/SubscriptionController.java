package br.com.nlw.events.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.nlw.events.dto.ErrorMessage;
import br.com.nlw.events.dto.SubscriptionResponse;
import br.com.nlw.events.exception.EventNotFoundException;
import br.com.nlw.events.exception.SubscriptionConflictException;
import br.com.nlw.events.exception.UserIndicadorNotFoundException;
import br.com.nlw.events.model.User;
import br.com.nlw.events.service.SubscriptionService;

@RestController
public class SubscriptionController {
	
	@Autowired
	private SubscriptionService SubService;
	
	 @PostMapping({"/subscription/{prettyName}", "/subscription/{prettyName}/{userId}"})
	// anotação @PathVariable indica que o {prettyName} vai vim da URL e os outros 2 parametros vai vim do corpo da requisição
	public ResponseEntity<?> createSubscription(@PathVariable String prettyName, 
												@RequestBody User subscriber,
												@PathVariable(required = false) Integer userId){
		try {
		SubscriptionResponse res = SubService.createNewSubscription(prettyName, subscriber, userId);
			if(res!=null) {
				return ResponseEntity.ok(res);
			}
		}catch(EventNotFoundException ex) {
			//ao capturar a exception do serviço, vai ser tratado nesse catch, gerando o cod 404
			return ResponseEntity.status(404).body(new ErrorMessage(ex.getMessage()));
			
		}catch(SubscriptionConflictException ex){
			return ResponseEntity.status(409).body(new ErrorMessage(ex.getMessage()));
			
		}catch(UserIndicadorNotFoundException ex) {
			return ResponseEntity.status(404).body(new ErrorMessage(ex.getMessage()));
		}
		
		return ResponseEntity.badRequest().build();
	}
	
	 @GetMapping("/subscription/{prettyName}/ranking")
	 public ResponseEntity<?>generateRankingByEvent(@PathVariable String prettyName){
		 try {
			 return ResponseEntity.ok(SubService.getCompleteRanking(prettyName).subList(0, 3));
		 }catch(EventNotFoundException ex) {
			return ResponseEntity.status(404).body(new ErrorMessage(ex.getMessage()));
		 }
	 }
	 
	 @GetMapping("/subscription/{prettyName}/ranking/{userId}")
	 public ResponseEntity<?> genereteRankingByEventAndUser(@PathVariable String prettyName,
			 												@PathVariable Integer userId){
		 try{
			 return ResponseEntity.ok(SubService.getRankingByUser(prettyName, userId));
		 }catch(Exception ex){
			 return ResponseEntity.status(404).body(new ErrorMessage(ex.getMessage()));
		 }
	 }
	
}
