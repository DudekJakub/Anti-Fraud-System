package antifraud.service;

import antifraud.exception.RecordAlreadyExistsException;
import antifraud.model.SuspiciousIp;
import antifraud.repository.SuspiciousIpRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SuspiciousIpService {

    SuspiciousIpRepository suspiciousIpRepository;

    public List<SuspiciousIp> listSuspiciousIps() {
        return suspiciousIpRepository.findAll(
                Sort.sort(SuspiciousIp.class).by(SuspiciousIp::getId).ascending()
        );
    }

    @Transactional
    public Optional<SuspiciousIp> register(SuspiciousIp ip) {

        if (suspiciousIpRepository.existsByIp(ip.getIp())) {
            throw new RecordAlreadyExistsException();
        }

        return Optional.of(suspiciousIpRepository.save(ip));
    }

    @Transactional
    public boolean delete(String ip) {
        return suspiciousIpRepository.deleteByIp(ip) == 1;
    }
}
