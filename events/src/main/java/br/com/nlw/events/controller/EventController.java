package br.com.nlw.events.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.nlw.events.model.Event;
import br.com.nlw.events.service.EventService;

@RestController
public class EventController {
	
	@Autowired
	private EventService eventService;
	
	@PostMapping("/events")
	public Event addNewEvent(@RequestBody Event newEvent) {
		return eventService.addNewEvent(newEvent);
	}
	
	@GetMapping("/events")
	public List<Event> getAllEvents(){
		return eventService.getAllEvents();
	}
	
	@GetMapping("/events/{prettyName}")
	       //Retorna uma entidade de resposta cujo corpo dela pode ser um Event. Permite manipular um codigo de estatus HTTP
	public ResponseEntity<Event> getEventByPrettyName(@PathVariable String prettyName) {
		Event evt = eventService.getByPrettyName(prettyName);
		if(evt != null) { //evento existe no banco de dados
			return ResponseEntity.ok().body(evt);
		}
		return ResponseEntity.notFound().build();
	}
}
