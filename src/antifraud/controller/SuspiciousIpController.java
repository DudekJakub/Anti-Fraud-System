package antifraud.controller;

import antifraud.exception.SuspiciousIpNotFoundException;
import antifraud.mapper.SuspiciousIpMapper;
import antifraud.model.SuspiciousIp;
import antifraud.model.request.SuspiciousIpRequest;
import antifraud.model.response.SuspiciousIpCreateResponse;
import antifraud.model.response.SuspiciousIpDeleteResponse;
import antifraud.service.SuspiciousIpService;
import antifraud.validation.ValidIp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/antifraud/suspicious-ip")
@AllArgsConstructor
@Validated
@PreAuthorize("hasAuthority('SUPPORT')")
public class SuspiciousIpController {

    SuspiciousIpService suspiciousIpService;

    @GetMapping
    List<SuspiciousIp> listSuspiciousIps() {
        return suspiciousIpService.listSuspiciousIps();
    }

    @PostMapping
    SuspiciousIpCreateResponse createSuspiciousIp(@Valid @RequestBody SuspiciousIpRequest request) {
        SuspiciousIp ip = SuspiciousIpMapper.requestToSuspiciousIp(request);

        log.info("Request = {}", request);
        log.info("Ip = {}", ip);

        SuspiciousIp savedIp = suspiciousIpService.register(ip)
                .orElseThrow(SuspiciousIpNotFoundException::new);

        return SuspiciousIpMapper.suspiciousIpToCreateResponse(savedIp);
    }

    @DeleteMapping("/{ip}")
    SuspiciousIpDeleteResponse deleteSuspiciousIp(@PathVariable @ValidIp String ip) {
        if (suspiciousIpService.delete(ip)) {
            return new SuspiciousIpDeleteResponse(ip);
        }
        throw new SuspiciousIpNotFoundException();
    }

    @DeleteMapping
    void deleteSuspiciousIp() {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }
}
