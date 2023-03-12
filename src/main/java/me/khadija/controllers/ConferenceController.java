package me.khadija.controllers;

import me.khadija.models.Conference;
import me.khadija.services.ConferenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("conferences")
public class ConferenceController {

    private final ConferenceService conferenceService;

    public ConferenceController(ConferenceService conferenceService) {
        this.conferenceService = conferenceService;
    }

    @GetMapping(path = {"/all", "/", ""})
    public ResponseEntity<List<Conference>> all(){
        return ResponseEntity.ok(conferenceService.fetchAll());
    }

    @GetMapping("/find/{name}")
    public ResponseEntity<Conference> byName(@PathVariable("name") String name){
        final Conference conference = conferenceService.fetch(name);
        if (conference == null)
            return ResponseEntity.notFound()
                    .header("error",
                            "Conference with the name " + name + " doesnt exist!")
                    .build();

        return ResponseEntity.ok(conference);
    }

    @DeleteMapping("/delete/{name}")
    public ResponseEntity<Conference> delete(@PathVariable("name")String name){
        final Conference conference = conferenceService.delete(name);
        if (conference == null)
            return ResponseEntity.notFound()
                    .header("error",
                            "Conference with the name " + name + " doesnt exist!")
                    .build();
        return ResponseEntity.ok(conference);
    }

}
