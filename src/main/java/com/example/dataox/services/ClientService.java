package com.example.dataox.services;

import com.example.dataox.dto.ClientRequest;
import com.example.dataox.entities.Client;
import com.example.dataox.repo.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Client getClientById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + id));
    }

    public Client createClient(ClientRequest clientRequest) {
        Client client = Client.builder()
                .name(clientRequest.getName())
                .email(clientRequest.getEmail())
                .address(clientRequest.getAddress())
                .active(true)
                .profit(BigDecimal.ZERO)
                .build();
        return clientRepository.save(client);
    }

    public Client updateClient(Long id, ClientRequest clientDetails) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + id));
        client.setName(clientDetails.getName());
        client.setEmail(clientDetails.getEmail());
        client.setAddress(clientDetails.getAddress());
        client.setActive(clientDetails.isActive());
        client.setUpdatedAt(LocalDateTime.now());
        return clientRepository.save(client);
    }

    @Transactional
    public void deactivateClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + id));
        client.setActive(false);
        client.setDeactivatedAt(LocalDateTime.now());
        clientRepository.save(client);
    }

    public List<Client> searchClients(String name, String email, String address) {
        if (name != null && name.length() < 3) {
            throw new IllegalArgumentException("Name must be at least 3 characters long");
        }
        if (email != null && email.length() < 3) {
            throw new IllegalArgumentException("Email must be at least 3 characters long");
        }
        if (address != null && address.length() < 3) {
            throw new IllegalArgumentException("Address must be at least 3 characters long");
        }

        return clientRepository.searchClients(name, email, address);
    }

    public List<Client> findClientsByProfitRange(BigDecimal minProfit, BigDecimal maxProfit) {
        if (minProfit == null) minProfit = BigDecimal.valueOf(-1_000_000);
        if (maxProfit == null) maxProfit = BigDecimal.valueOf(1_000_000);

        return clientRepository.findByProfitRange(minProfit, maxProfit);
    }

    public BigDecimal calculateClientProfit(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found with id: " + clientId));
        return client.getProfit() != null ? client.getProfit() : BigDecimal.ZERO;
    }
}
