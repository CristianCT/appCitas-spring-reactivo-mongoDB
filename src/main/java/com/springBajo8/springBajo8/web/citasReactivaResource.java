package com.springBajo8.springBajo8.web;


import com.springBajo8.springBajo8.domain.citasDTOReactiva;
import com.springBajo8.springBajo8.service.IcitasReactivaService;
import com.springBajo8.springBajo8.service.impl.citasReactivaServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
public class citasReactivaResource {

    @Autowired
    private IcitasReactivaService icitasReactivaService;

    @PostMapping("/citasReactivas")
    @ResponseStatus(HttpStatus.CREATED)
    private Mono<citasDTOReactiva> save(@RequestBody citasDTOReactiva citasDTOReactiva) {
        return this.icitasReactivaService.save(citasDTOReactiva);
    }

    @DeleteMapping("/citasReactivas/{id}")
    private Mono<ResponseEntity<citasDTOReactiva>> delete(@PathVariable("id") String id) {
        return this.icitasReactivaService.delete(id)
                .flatMap(citasDTOReactiva -> Mono.just(ResponseEntity.ok(citasDTOReactiva)))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));

    }

    @PutMapping("/citasReactivas/{id}")
    private Mono<ResponseEntity<citasDTOReactiva>> update(@PathVariable("id") String id, @RequestBody citasDTOReactiva citasDTOReactiva) {
        return this.icitasReactivaService.update(id, citasDTOReactiva)
                .flatMap(citasDTOReactiva1 -> Mono.just(ResponseEntity.ok(citasDTOReactiva1)))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));

    }

    @GetMapping("/citasReactivas/{idPaciente}/byidPaciente")
    private Flux<citasDTOReactiva> findAllByidPaciente(@PathVariable("idPaciente") String idPaciente) {
        return this.icitasReactivaService.findByIdPaciente(idPaciente);
    }

    @GetMapping(value = "/citasReactivas")
    private Flux<citasDTOReactiva> findAll() {
        return this.icitasReactivaService.findAll();
    }

    @PutMapping(value = "/cancelarcita/{idPaciente}")
    private Flux<citasDTOReactiva> cancel(@PathVariable("idPaciente") String idPaciente){
        Flux<citasDTOReactiva> paciente = this.icitasReactivaService.findByIdPaciente(idPaciente);

        return paciente.flatMap(paciente1 -> {
            paciente1.setEstadoReservaCita(false);
            return save(paciente1);
        });
    }

    @PutMapping(value = "/citasReactivas/cancelarcita/{idCita}")
    private Mono<citasDTOReactiva> cancelCita(@PathVariable("idCita") String idCita){
        Mono<citasDTOReactiva> paciente = this.icitasReactivaService.findById(idCita);

        return paciente.flatMap(paciente1 -> {
            paciente1.setEstadoReservaCita(false);
            return save(paciente1);
        });
    }

    @PutMapping(value = "/citasReactivas/reactivarcita/{idCita}")
    private Mono<citasDTOReactiva> reactivarCita(@PathVariable("idCita") String idCita){
        Mono<citasDTOReactiva> paciente = this.icitasReactivaService.findById(idCita);

        return paciente.flatMap(paciente1 -> {
            paciente1.setEstadoReservaCita(true);
            return save(paciente1);
        });
    }

    @GetMapping(value = "/citasReactivas/{fechaReservaCita}/{horaReservaCita}")
    private Flux<citasDTOReactiva> findByFechaReservaCita(@PathVariable("fechaReservaCita") String fechaReservaCita, @PathVariable("horaReservaCita") String horaReservaCita){
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate fechaReservaCitaLocalDate = LocalDate.parse(fechaReservaCita, format);

        return this.icitasReactivaService
                .findByFechaReservaCita(fechaReservaCitaLocalDate)
                .filter(cita -> cita.getHoraReservaCita().equals(horaReservaCita));
    }

    @GetMapping(value = "/citasReactivas/{idPaciente}/medico")
    private Mono<String> findAndMedico(@PathVariable("idPaciente") String idPaciente){
        citasDTOReactiva paciente = this.icitasReactivaService.findByIdPaciente(idPaciente).blockFirst();

        return Mono.just(paciente.getNombreMedico().concat(" ").concat(paciente.getApellidosMedico()));
    }

    @GetMapping(value = "/citasReactivas/{idPaciente}/padecimientos")
    private Flux<String> findByIdAndPadecimientos(@PathVariable("idPaciente") String idPaciente){
        return this.icitasReactivaService.findByIdPaciente(idPaciente).flatMap(paciente -> {
            return Mono.just(paciente.getPadecimiento());
        });
    }

    @GetMapping(value = "/citasReactivas/{idCita}")
    private Mono<citasDTOReactiva> findCitaById(@PathVariable("idCita") String idCita){
        return this.icitasReactivaService.findById(idCita);
    }
}
