package antifraud.model;

import antifraud.validation.ValidIp;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "suspicious_ips", indexes = {
        @Index(name = "idx_suspiciousip_ip", columnList = "ip")
})
public class SuspiciousIp {

    @Id
    @GeneratedValue
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;

    @ValidIp
    String ip;
}
