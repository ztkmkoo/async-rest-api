package io.github.ztkmkoo.dss.core.network.tcp.property;

import java.io.Serializable;

import io.github.ztkmkoo.dss.core.network.DssChannelProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DssTcpChannelProperty implements Serializable, DssChannelProperty {

    private static final long serialVersionUID = -6904414304979344025L;
    private final String host;
    private final int port;

    @Builder
    public DssTcpChannelProperty(String host, int port) {
        this.host = host;
        this.port = port;
    }
}
