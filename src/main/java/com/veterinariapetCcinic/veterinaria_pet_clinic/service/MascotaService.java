package com.veterinariapetCcinic.veterinaria_pet_clinic.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.veterinariapetCcinic.veterinaria_pet_clinic.Model.Cliente;
import com.veterinariapetCcinic.veterinaria_pet_clinic.Model.Mascota;
import com.veterinariapetCcinic.veterinaria_pet_clinic.repository.ClienteRepository;
import com.veterinariapetCcinic.veterinaria_pet_clinic.repository.MascotaRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class MascotaService {

    private final MascotaRepository mascotaRepository;
    private final ClienteRepository clienteRepository;

    public MascotaService(MascotaRepository mascotaRepository, ClienteRepository clienteRepository) {
        this.mascotaRepository = mascotaRepository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public Mascota guardar(Mascota mascota) {
        return mascotaRepository.save(mascota);
    }

    @Transactional
    public Mascota registrarMascota(Long clienteId, Mascota mascota) {
        Cliente cliente = clienteRepository.findById(clienteId)
            .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con ID: " + clienteId));
        mascota.setCliente(cliente);
        return mascotaRepository.save(mascota);
    }

    @Transactional
    public Mascota actualizar(Mascota mascotaActualizada) {
        Mascota existente = buscarPorId(mascotaActualizada.getId());
        
        existente.setNombre(mascotaActualizada.getNombre());
        existente.setEspecie(mascotaActualizada.getEspecie());
        existente.setRaza(mascotaActualizada.getRaza());
        existente.setSexo(mascotaActualizada.getSexo());
        existente.setFechaNacimiento(mascotaActualizada.getFechaNacimiento());
        // No se setea edad porque es calculada automáticamente
        existente.setPeso(mascotaActualizada.getPeso());
        existente.setColor(mascotaActualizada.getColor());
        existente.setAlergias(mascotaActualizada.getAlergias());
        existente.setEstado(mascotaActualizada.getEstado());
        existente.setObservaciones(mascotaActualizada.getObservaciones());
        existente.setMicrochip(mascotaActualizada.getMicrochip());
        existente.setEsterilizado(mascotaActualizada.getEsterilizado());
        existente.setFotoUrl(mascotaActualizada.getFotoUrl());
        
        return mascotaRepository.save(existente);
    }

    public Mascota buscarPorId(Long id) {
        return mascotaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Mascota no encontrada con ID: " + id));
    }

    public List<Mascota> listarTodos() {
        return mascotaRepository.findAll();
    }

    public List<Mascota> buscarPorCliente(Long clienteId) {
        return mascotaRepository.findByClienteId(clienteId);
    }

    public List<Mascota> buscarPorNombre(String nombre) {
        return mascotaRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Mascota> buscarPorEspecie(String especie) {
        return mascotaRepository.findByEspecie(especie);
    }

    public long contarMascotas() {
        return mascotaRepository.count();
    }

    public long contarMascotasPorCliente(Long clienteId) {
        return mascotaRepository.countByClienteId(clienteId);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!mascotaRepository.existsById(id)) {
            throw new EntityNotFoundException("Mascota no encontrada con ID: " + id);
        }
        mascotaRepository.deleteById(id);
    }
}