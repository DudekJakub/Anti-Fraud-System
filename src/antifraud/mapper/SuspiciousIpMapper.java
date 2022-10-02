package antifraud.mapper;

import antifraud.model.SuspiciousIp;
import antifraud.model.request.SuspiciousIpRequest;
import antifraud.model.response.SuspiciousIpCreateResponse;

public class SuspiciousIpMapper {

    public static SuspiciousIp requestToSuspiciousIp(SuspiciousIpRequest ipRequest) {
        return SuspiciousIp.builder()
                .ip(ipRequest.getIp())
                .build();
    }

    public static SuspiciousIpCreateResponse suspiciousIpToCreateResponse(SuspiciousIp ip) {
        return SuspiciousIpCreateResponse.builder()
                .id(ip.getId())
                .ip(ip.getIp())
                .build();
    }
}
