package antifraud.service;

import antifraud.business.ip.IP;
import antifraud.persistance.IPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class IPService {
    @Autowired
    private IPRepository repository;

    public IP saveIP(IP ip) {
        if (exists(ip.getIp())) throw new ResponseStatusException(HttpStatus.CONFLICT);
        return repository.save(ip);
    }

    public boolean exists(String ip) {
        Optional<IP> fromDb = repository.findIPByIp(ip);
        return fromDb.isPresent();
    }

    public IP getIP(String ip) {
        Optional<IP> fromDb = repository.findIPByIp(ip);
        return fromDb.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    public void deleteIP(String ip) {
        if (!exists(ip)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        repository.deleteByIp(ip);
    }

    public List<IP> getAllIP() {
        Iterable<IP> all = repository.findAll();
        return StreamSupport.stream(all.spliterator(), false).collect(Collectors.toList());
    }
}
