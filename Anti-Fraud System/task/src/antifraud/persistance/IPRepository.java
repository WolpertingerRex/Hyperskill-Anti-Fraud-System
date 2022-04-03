package antifraud.persistance;

import antifraud.business.ip.IP;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPRepository extends CrudRepository<IP, Long> {
    Optional<IP> findIPByIp(String ip);
    void deleteByIp(String ip);
}
