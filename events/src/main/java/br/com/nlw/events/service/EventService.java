package br.com.nlw.events.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.nlw.events.model.Event;
import br.com.nlw.events.repository.EventRepository;

@Service
public class EventService {
	
	@Autowired
	private EventRepository eventRepo;
	
	public Event addNewEvent(Event event) {
		//gerando o pretty name
		event.setPrettyName(event.getTitle().toLowerCase().replace(" ", "-"));
		return eventRepo.save(event);
	}
	
	public List<Event> getAllEvents(){
		return (List<Event>)eventRepo.findAll();
	}
	
	public Event getByPrettyName(String prettyName) {
		return eventRepo.findByPrettyName(prettyName);
	}
	
}
