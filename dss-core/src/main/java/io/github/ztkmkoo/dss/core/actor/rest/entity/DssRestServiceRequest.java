package io.github.ztkmkoo.dss.core.actor.rest.entity;

import java.io.Serializable;

/**
 * Project: dss
 * Created by: @ztkmkoo(ztkmkoo@gmail.com)
 * Date: 20. 3. 15. 오전 1:13
 */
public interface DssRestServiceRequest<S extends Serializable> extends Serializable {
    S getBody();
}
